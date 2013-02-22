package lab4;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Driver class for paging simulation.
public class Driver {
  private List<Process> plist = new ArrayList<Process>();
  private List<Process> readyQueue = new ArrayList<Process>();
  private int machineSize = 0;
  private int pageSize = 0;
  private int processSize = 0;
  private int jobMix = 0;
  private int numRefsEachProc = 0;
  private String algorithm = "";
  private int mode = 0;
  private int quantum = 3;
  private Scanner sc;

  // Constructor that reads in all the input data and initialize the scanner
  // for reading random numbers.
  public Driver(String[] args, String file) {
    readInput(args);
    FileInputStream input = null;
    try {
      input = new FileInputStream(file);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    if(input != null) {
      sc = new Scanner(input);
    }
  }

  // Method that simulate pageing.
  public void simulatPaging() {
    createProcesses();
    int numOfFrames = (int)(Math.ceil((double) machineSize / pageSize));
    Pager pager = new Pager(numOfFrames, pageSize);

    // At the beginning, put all the processes into the ready queue.
    for(int i = 0; i < plist.size(); i++) {
      readyQueue.add(i, plist.get(i));
    }

    int currentTime = 0;
    while(readyQueue.size() > 0) {
      Process process = getNextProcess();
      //System.out.println(process.getNextRef());
      
      for(int i = 0; i < quantum; i++) {
        int pageId = pager.decidePageId(process);
        // Search the frame table to see if the required page is already
        // loaded. It returns a non-negative frame id if the page is loaded,
        // and -1 if there is a page fault.
        int frameId = pager.checkPageFault(pageId, process.getId());
        if(frameId == -1) {
          // There is a page fault. Continue to check to see if there is any
          // free frame for page insertion. The anyFreeFrame() method is
          // similar to checkPageFault: it returns a non-negative frame id
          // of a free frame, or -1 if there is no free frame.
          frameId = pager.anyFreeFrame();
          if(frameId == -1) {
            if(algorithm.equals("random")) {
              int rn = readNextRandomNumber();
              pager.setRandomNumber(rn);
            }
            pager.evictAndInsert(algorithm, pageId, process.getId(),
                currentTime, plist, mode);
          } else {
            pager.insertPage(frameId, pageId, process.getId(),
                currentTime, plist);
            
          }
        } else {
          // No page fault, and the required page is already loaded in
          // frame frameId.
          pager.updatePageMatrix(frameId);
        }
        
        process.decreaseRefLeft();
        currentTime++;
        calculateNextRef(process);

        // If the process is done, break out the inner loop because it won't
        // use up its quantum.
        if(process.getRefLeft() == 0) {
          break;
        }
      }
      // If the process hasn't finished, put it to the back of the ready queue.
      if(process.getRefLeft() > 0) {
        readyQueue.add(process);
      }
    }
  }

  // Method that simulates paging in debugging mode.
  public void simulatePagingDebug() {
    createProcesses();
    int numOfFrames = (int)(Math.ceil((double) machineSize / pageSize));
    Pager pager = new Pager(numOfFrames, pageSize);

    // At the beginning, put all the processes into the ready queue.
    for(int i = 0; i < plist.size(); i++) {
      readyQueue.add(i, plist.get(i));
    }

    int currentTime = 0;
    while(readyQueue.size() > 0) {
      Process process = getNextProcess();
      //System.out.println(process.getNextRef());
      
      for(int i = 0; i < quantum; i++) {
        int pageId = pager.decidePageId(process);

        // Search the frame table to see if the required page is already
        // loaded. It returns a non-negative frame id if the page is loaded,
        // and -1 if there is a page fault.
        int frameId = pager.checkPageFault(pageId, process.getId());
        System.out.print(process.getId() + " references word " + process.getNextRef() + " (page " + pageId + ") at time " + (currentTime+1));
        if(frameId == -1) {
          // There is a page fault. Continue to check to see if there is any
          // free frame for page insertion. The anyFreeFrame() method is
          // similar to checkPageFault: it returns a non-negative frame id
          // of a free frame, or -1 if there is no free frame.
          frameId = pager.anyFreeFrame();
          System.out.print(": Fault, ");
          if(frameId == -1) {
            if(algorithm.equals("random")) {
              int rn = readNextRandomNumber();
              if(mode == 2) {
                System.out.print(process.getId() +
                    " uses random number: " + rn + ", ");
              }
              pager.setRandomNumber(rn);
            }
            pager.evictAndInsert(algorithm, pageId, process.getId(),
                currentTime, plist, mode);
          } else {
            pager.insertPage(frameId, pageId, process.getId(),
                currentTime, plist);
            System.out.print("using free frame " + frameId);
          }
        } else {
          // No page fault, and the required page is already loaded in
          // frame frameId.
          System.out.print(" Hit in frame " + frameId);
          pager.updatePageMatrix(frameId);
        }
        System.out.println();

        process.decreaseRefLeft();
        currentTime++;
        calculateNextRef(process);
        // If the process is done, break out the inner loop because it won't
        // use up its quantum.
        if(process.getRefLeft() == 0) {
          break;
        }
      }
      // If the process hasn't finished, put it to the back of the ready queue.
      if(process.getRefLeft() > 0) {
        readyQueue.add(process);
      }
    }
  }

  // Method to insert a process to the back of the ready queue.
  public void enqueue(Process process) {
    readyQueue.add(process);
  }

  // Method to retrieve the head element from the ready queue.
  public Process dequeue() {
    Process process = readyQueue.get(0);
    readyQueue.remove(0);
    return process;
  }

  // Method to get the next ready process.
  public Process getNextProcess() {
    return dequeue();
  }

  // Method to calculate the next reference for a process.
  public void calculateNextRef(Process process) {
    int currentRef = process.getNextRef();
    int rn = readNextRandomNumber();
    if(mode == 2) {
      System.out.println(process.getId() + " uses random number: " + rn);
    }
    double y = rn / (Integer.MAX_VALUE + 1d);
    if(y < process.getFracA()) {
      // If it's probability A, do case 1.
      process.setNextRef((currentRef + 1) % process.getSize());
    } else if(y < (process.getFracA() + process.getFracB())) {
      // If it's probability B, do case 2.
      process.setNextRef((currentRef - 5 + process.getSize()) %
          process.getSize());
    } else if(y < (process.getFracA() + process.getFracB() +
          process.getFracC())) {
      // If it's probability C, do case 3.
      process.setNextRef((currentRef + 4) % process.getSize());
    } else {
      // If it's probability 1-A-B-C, do case 4.
      rn = readNextRandomNumber();
      if(mode == 2) {
        System.out.println("uses random number: " + rn);
      }
      process.setNextRef(rn % process.getSize());
    }
  }

  // Method to read the input data.
  public void readInput(String[] args) {
    machineSize = Integer.parseInt(args[0]);
    pageSize = Integer.parseInt(args[1]);
    processSize = Integer.parseInt(args[2]);
    jobMix = Integer.parseInt(args[3]);
    numRefsEachProc = Integer.parseInt(args[4]);
    algorithm = args[5].toLowerCase();
    mode = Integer.parseInt(args[6]);
  }

  // Method to create processes based on the argument "job mix".
  public void createProcesses() {
    if(jobMix == 1) {
      Process process = new Process(1, processSize);
      process.setFracA(1);
      process.setNextRef(111 * process.getId() % process.getSize());
      process.setRefLeft(numRefsEachProc);
      plist.add(process);
    } else if(jobMix == 2) {
      for(int i = 0; i < 4; i++) {
        Process process = new Process(i + 1, processSize);
        process.setFracA(1);
        process.setNextRef(111 * process.getId() % process.getSize());
        process.setRefLeft(numRefsEachProc);
        plist.add(process);
      }
    } else if(jobMix == 3) {
      for(int i = 0; i < 4; i++) {
        Process process = new Process(i + 1, processSize);
        process.setNextRef(111 * process.getId() % process.getSize());
        process.setRefLeft(numRefsEachProc);
        plist.add(process);
      }
    } else {
      Process process = new Process(1, processSize);
      process.setFracA(0.75);
      process.setFracB(0.25);
      process.setNextRef(111 % process.getSize());
      process.setRefLeft(numRefsEachProc);
      plist.add(process);
      
      process = new Process(2, processSize);
      process.setFracA(0.75);
      process.setFracC(0.25);
      process.setNextRef(111 * 2 % process.getSize());
      process.setRefLeft(numRefsEachProc);
      plist.add(process);
      
      process = new Process(3, processSize);
      process.setFracA(0.75);
      process.setFracB(0.125);
      process.setFracC(0.125);
      process.setNextRef(111 * 3 % process.getSize());
      process.setRefLeft(numRefsEachProc);
      plist.add(process);
      
      process = new Process(4, processSize);
      process.setFracA(0.5);
      process.setFracB(0.125);
      process.setFracC(0.125);
      process.setNextRef(111 * 4 % process.getSize());
      process.setRefLeft(numRefsEachProc);
      plist.add(process);
    }
  }

  // Method to read the next random number.
  public int readNextRandomNumber() {
    int rn = sc.nextInt();
    return rn;
  }

  // Method to display result.
  public void displayResult() {
    System.out.println("The machine size is " + machineSize + ".");
    System.out.println("The page size is " + pageSize + ".");
    System.out.println("The process size is " + processSize + ".");
    System.out.println("The job mix number is " + jobMix + ".");
    System.out.println("The number of references per process is " + numRefsEachProc + ".");
    System.out.println("The replacement algorithm is " + algorithm + ".");
    System.out.println("The level of debugging output " + mode + ".");
    System.out.println();

    double totalResTime = 0;
    int totalEviction = 0;
    int totalPageFault = 0;
    for(int i = 0; i < plist.size(); i++) {
      Process process = plist.get(i);
      process.calcAverResTime();
      if(process.getAverResTime() > 0) {
        System.out.println("Process " + process.getId() + " had " +
            process.getNumOfPagefault() + " faults and " +
            process.getAverResTime() +
            " average residency.");
      } else {
        System.out.println("Process " + process.getId() + " had " +
            process.getNumOfPagefault() + " faults.");
        System.out.println(
            "    With no evictions, the average residency is undefined.");
      }
      totalResTime += process.getSum();
      totalEviction += process.getNumOfEviction();
      totalPageFault += process.getNumOfPagefault();
    }

    System.out.println();
    if(totalEviction > 0) {
      System.out.println("The total number of faults is " + totalPageFault +
          " and the overall average residency is " +
          (totalResTime / totalEviction));
    } else {
      System.out.println("The total number of faults is " +
          totalPageFault + ".");
      System.out.println(
          "    With no evictions, the overall averyge residency " +
          "is undefined.");
    }
  }
}
