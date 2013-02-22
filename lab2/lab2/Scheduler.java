package lab2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Scheduler {
  private List<Process> processes = new ArrayList<Process>();
  private List<Process> originalList = new ArrayList<Process>();
  private String procInfoFile = "";
  private String randomNumberFile = "";
  private AlgorithmInfo fcfs = new AlgorithmInfo();
  private AlgorithmInfo rr = new AlgorithmInfo();
  private AlgorithmInfo uniprogramming = new AlgorithmInfo();
  private AlgorithmInfo hprn = new AlgorithmInfo();
  
  public Scheduler(String procInfoFile, String randomNumberFile) {
    this.procInfoFile = procInfoFile;
    this.randomNumberFile = randomNumberFile;
    
    readInput();
    sortByCreationTime(processes);
  }

  // Method to read the input file of process information and create a list
  // of process.
  private void readInput() {
    FileInputStream input = null;
    try {
      input = new FileInputStream(procInfoFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    if(input != null) {
      Scanner sc = new Scanner(input);
      int numOfProcs = sc.nextInt();
      for(int i = 0; i < numOfProcs; i++) {
        String str = "";
        int num = 0;
        Process process = new Process();

        // Read in the creation time.
        str = sc.next();
        num = Integer.parseInt(str.substring(1));
        process.setCreationTime(num);
        // Read in the CPU burst.
        str = sc.next();
        num = Integer.parseInt(str);
        process.setCPUBurst(num);
        // Read in the total CPU needed time.
        str = sc.next();
        num = Integer.parseInt(str);
        process.setTotalCPUTime(num);
        process.setCPUTimeRemaining(num);
        // Read in the IO burst.
        str = sc.next();
        num = Integer.parseInt(str.substring(0, str.length() - 1));
        process.setIOBurst(num);

        processes.add(process);
        originalList.add(process);
      }
    }
  }

  // Method to sort the list of process based on their arrival time.
  private void sortByCreationTime(List<Process> plist) {
    for(int i = 1; i < plist.size(); i++) {
      for(int j = i; j > 0; j--) {
        if(plist.get(j).getCreationTime() < plist.get(j - 1).getCreationTime()) {
          Process temp = plist.get(j);
          plist.set(j, plist.get(j - 1));
          plist.set(j - 1, temp);
        }
      }
    }

    // Set the priority of each process. This will be used
    // to determine their priority for joining the ready queue.
    for(int i = 0; i < plist.size(); i++) {
      plist.get(i).setPriority(i);
    }
  }

  // Method to sort the processes by their priority. This method is used to
  // determine the processes' priority for joining the ready queue.
  private void sortByPriority(List<Process> plist) {
    for(int i = 1; i < plist.size(); i++) {
      for(int j = i; j > 0; j--) {
        if(plist.get(j).getPriority() < plist.get(j - 1).getPriority()) {
          Process temp = plist.get(j);
          plist.set(j, plist.get(j - 1));
          plist.set(j - 1, temp);
        }
      }
    }
  }

  // Method to perform FCFS scheduling.
  public void fcfsScheduling() {
    resetProcessList();
    ReadyQueue readyQueue = new ReadyQueue();
    BlockList blockList = new BlockList();
    List<Process> intermediateList = new ArrayList<Process>();

    FileInputStream randomInput = null;
    try {
      randomInput = new FileInputStream(randomNumberFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    Scanner sc = new Scanner(randomInput);
    int jobsRemaining = processes.size();
    int currentTime = 0;
    Process runningProcess = null;
    while(jobsRemaining > 0) {
      // Pick new coming processes.
      for(int i = 0; i < processes.size(); i++) {
        Process process = processes.get(i);
        if(process.getStatus() == Process.INACTIVE &&
            process.getCreationTime() <= currentTime) {
          intermediateList.add(process);
          process.setStatus(Process.READY);
        }
      }

      // Unblocked the processes which have finished I/O.
      if(blockList.getSize() > 0) {
        blockList.unblockProcesses(intermediateList);
      }

      // Sort the intermediate list before joining its processes to the ready
      // queue.
      sortByPriority(intermediateList);
      for(int i = 0; i < intermediateList.size(); i++) {
        readyQueue.enqueue(intermediateList.get(i));
      }
      // Clear the intermediate list for subsequent use.
      intermediateList.clear();

      // If currently there is no running process, pick one from the ready
      // queue if the queue is non-empty.
      if(runningProcess == null && readyQueue.getSize() > 0) {
        runningProcess = readyQueue.dequeue();
        runningProcess.setStatus(Process.RUNNING);
        runningProcess.setCurrentCPUBurst(randomOS(runningProcess.getCPUBurst(), sc));
        if(runningProcess.getCurrentCPUBurst() > runningProcess.getCPUTimeRemaining()) {
          runningProcess.setCurrentCPUBurst(runningProcess.getCPUTimeRemaining());
        }
      }

      // Time collapsed is 1 time unit. Process runs, I/O performs, and others wait.
      int timeCollapsed = 1;
      currentTime += 1;
      // Update the block list.
      if(blockList.getSize() > 0) {
        blockList.updateIOTime(timeCollapsed);
        // Update the I/O used time.
        fcfs.setIoUsedTime(fcfs.getIoUsedTime() + 1);
      } else {
        // Otherwise update the I/O idle time.
        fcfs.setIoIdleTime(fcfs.getIoIdleTime() + 1);
      }
      // Update the ready queue's processes' wait time.
      readyQueue.updateWaitTime(timeCollapsed);
      // If there is a running process, it runs for 1 time unit.
      if(runningProcess != null) {
        runningProcess.setCurrentCPUBurst(runningProcess.getCurrentCPUBurst() - 1);
        runningProcess.setCPUTimeRemaining(runningProcess.getCPUTimeRemaining() - 1);
        // Terminate or block the running process after it
        // finishes its current CPU burst.
        if(runningProcess.getCPUTimeRemaining() == 0) {
          runningProcess.setFinishingTime(currentTime);
          runningProcess.setTurnAroundTime(runningProcess.getFinishingTime() -
              runningProcess.getCreationTime());
          jobsRemaining--;
          fcfs.increaseJobsDone();
          runningProcess.setStatus(Process.TERMINATED);
          runningProcess = null;
        } else if(runningProcess.getCurrentCPUBurst() == 0){
          runningProcess.setCurrentIOBurst(randomOS(runningProcess.getIOBurst(), sc));
          blockList.addProcess(runningProcess);
          runningProcess = null;
        }
        // Update the CPU used time.
        fcfs.setCpuUsedTime(fcfs.getCpuUsedTime() + 1);
      } else {
        // Otherwise update the CPU idle time.
        fcfs.setCpuIdleTime(fcfs.getCpuIdleTime() + 1);
      }
    }
    
    // Sets the finishing time.
    fcfs.setFinishingTime(currentTime);
    fcfs.calcThroughput();
    fcfs.calcCPUUtilization();
    fcfs.calcIOUtilization();
    fcfs.calcAvrgTurnaroundTime(processes);
    fcfs.calcAvrgWaitTime(processes);
  }

  // Method to do RR scheduling with quantum being 2.
  public void rrScheduling() {
    resetProcessList();
    ReadyQueue readyQueue = new ReadyQueue();
    BlockList blockList = new BlockList();
    int quantum = 2;
    List<Process> intermediateList = new ArrayList<Process>();

    FileInputStream randomInput = null;
    try {
      randomInput = new FileInputStream(randomNumberFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    Scanner sc = new Scanner(randomInput);
    int jobsRemaining = processes.size();
    int currentTime = 0;
    Process runningProcess = null;
    
    while(jobsRemaining > 0) {
      // Pick new coming processes.
      for(int i = 0; i < processes.size(); i++) {
        Process process = processes.get(i);
        if(process.getStatus() == Process.INACTIVE &&
            process.getCreationTime() <= currentTime) {
          intermediateList.add(process);
          process.setStatus(Process.READY);
        }
      }

      // Unblocked the processes which have finished I/O.
      blockList.unblockProcesses(intermediateList);  

      // Sort the intermediate list before joining its processes to the ready
      // queue.
      sortByPriority(intermediateList);
      for(int i = 0; i < intermediateList.size(); i++) {
        readyQueue.enqueue(intermediateList.get(i));
      }
      // Clear the intermediate list for subsequent use.
      intermediateList.clear();

      if(runningProcess == null && readyQueue.getSize() > 0) {
        runningProcess = readyQueue.dequeue();
        runningProcess.setStatus(Process.RUNNING);
        // If the process doesn't have remained current CPU burst, generate a
        // new one using a random number; otherwise just use its remaining
        // current CPU burst. This is different from FCFS.
        if(runningProcess.getCurrentCPUBurst() == 0) {
          runningProcess.setCurrentCPUBurst(randomOS(runningProcess.getCPUBurst(), sc));
          if(runningProcess.getCurrentCPUBurst() > runningProcess.getCPUTimeRemaining()) {
            runningProcess.setCurrentCPUBurst(runningProcess.getCPUTimeRemaining());
          }
        }
      }
      
      // Time collapsed is 1 time unit. Process runs, I/O performs, and others wait.
      int timeCollapsed = 1;
      currentTime += 1;
      // Update the block list.
      if(blockList.getSize() > 0) {
        blockList.updateIOTime(timeCollapsed);
        // Update the I/O used time.
        rr.setIoUsedTime(rr.getIoUsedTime() + 1);
      } else {
        // Update the I/O idle time.
        rr.setIoIdleTime(rr.getIoIdleTime() + 1);
      }
      // Update the ready queue's processes' wait time.
      readyQueue.updateWaitTime(timeCollapsed);

      // If there is a running process, it runs for 1 time unit.
      if(runningProcess != null) {
        runningProcess.setCurrentCPUBurst(runningProcess.getCurrentCPUBurst() - 1);
        runningProcess.setCPUTimeRemaining(runningProcess.getCPUTimeRemaining() - 1);
        quantum -= 1;
        // Terminate or block the running process after it
        // finishes its current CPU burst.
        if(runningProcess.getCPUTimeRemaining() == 0) {
          runningProcess.setFinishingTime(currentTime);
          runningProcess.setTurnAroundTime(runningProcess.getFinishingTime() -
              runningProcess.getCreationTime());
          jobsRemaining--;
          rr.increaseJobsDone();
          runningProcess.setStatus(Process.TERMINATED);
          runningProcess = null;
          quantum = 2;
        } else if(runningProcess.getCurrentCPUBurst() == 0){
          runningProcess.setCurrentIOBurst(randomOS(runningProcess.getIOBurst(), sc));
          blockList.addProcess(runningProcess);
          runningProcess = null;
          quantum = 2;
        } else if(quantum == 0) {
          // Preempt the running process.
          runningProcess.setStatus(Process.READY);
          intermediateList.add(runningProcess);
          runningProcess = null;
          quantum = 2;
        }

        // Update the CPU used time.
        rr.setCpuUsedTime(rr.getCpuUsedTime() + 1);
      } else {
        rr.setCpuIdleTime(rr.getCpuIdleTime() + 1);
      }
    }

    // Sets the finishing time.
    rr.setFinishingTime(currentTime);
    rr.calcThroughput();
    rr.calcCPUUtilization();
    rr.calcIOUtilization();
    rr.calcAvrgTurnaroundTime(processes);
    rr.calcAvrgWaitTime(processes);
  }

  // Method to do uniprogramming scheduling.
  public void uniprogramming() {
    resetProcessList();
    ReadyQueue readyQueue = new ReadyQueue();
    FileInputStream randomInput = null;
    try {
      randomInput = new FileInputStream(randomNumberFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    Scanner sc = new Scanner(randomInput);
    int jobsRemaining = processes.size();
    int currentTime = 0;
    Process runningProcess = null;
    while(jobsRemaining > 0) {
      for(int i = 0; i < processes.size(); i++) {
        Process process = processes.get(i);
        if(process.getStatus() == Process.INACTIVE &&
           process.getCreationTime() <= currentTime) {
          readyQueue.enqueue(process);
          process.setStatus(Process.READY);
        }
      }

      if(runningProcess == null) {
        runningProcess = readyQueue.dequeue();
      }
      // Set CPU burst for the process.
      if(runningProcess.getStatus() == Process.READY) {
        runningProcess.setCurrentCPUBurst(randomOS(runningProcess.getCPUBurst(), sc));
        if(runningProcess.getCurrentCPUBurst() > runningProcess.getCPUTimeRemaining()) {
          runningProcess.setCurrentCPUBurst(runningProcess.getCPUTimeRemaining());
        }
        runningProcess.setStatus(Process.RUNNING);
      }
      
      // The process runs for 1 time unit.
      currentTime += 1;
      int timeCollapsed = 1;
      if(runningProcess.getStatus() == Process.RUNNING) {
        uniprogramming.increaseCPUUsedTime();
        uniprogramming.increaseIOIdleTime();
        runningProcess.setCPUTimeRemaining(runningProcess.getCPUTimeRemaining() - 1);
        runningProcess.setCurrentCPUBurst(runningProcess.getCurrentCPUBurst() - 1);
        if(runningProcess.getCPUTimeRemaining() == 0) {
          runningProcess.setStatus(Process.TERMINATED);
          runningProcess.setFinishingTime(currentTime);
          runningProcess.setTurnAroundTime(runningProcess.getFinishingTime() -
              runningProcess.getCreationTime());
          runningProcess = null;
          jobsRemaining -= 1;
          uniprogramming.increaseJobsDone();
        } else if(runningProcess.getCurrentCPUBurst() == 0){
          runningProcess.setStatus(Process.BLOCKED);
          runningProcess.setCurrentIOBurst(randomOS(runningProcess.getIOBurst(), sc));
        }
      } else if(runningProcess.getStatus() == Process.BLOCKED) {
        uniprogramming.increaseCPUIdleTime();
        uniprogramming.increaseIOUsedTime();
        runningProcess.setCurrentIOBurst(runningProcess.getCurrentIOBurst() - 1);
        runningProcess.setIOTime(runningProcess.getIOTime() + 1);
        if(runningProcess.getCurrentIOBurst() == 0) {
          runningProcess.setStatus(Process.READY);
        }
      }
      readyQueue.updateWaitTime(timeCollapsed);
    }
    // Sets the finishing time.
    uniprogramming.setFinishingTime(currentTime);
    uniprogramming.calcThroughput();
    uniprogramming.calcCPUUtilization();
    uniprogramming.calcIOUtilization();
    uniprogramming.calcAvrgTurnaroundTime(processes);
    uniprogramming.calcAvrgWaitTime(processes);
  }

  // Method to do HPRN scheduling.
  public void hprnScheduling() {
    resetProcessList();
    List<Process> readyQueue = new ArrayList<Process>();
    BlockList blockList = new BlockList();
    FileInputStream randomInput = null;
    try {
      randomInput = new FileInputStream(randomNumberFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    Scanner sc = new Scanner(randomInput);
    int jobsRemaining = processes.size();
    int currentTime = 0;
    Process runningProcess = null;
      
    while(jobsRemaining > 0) {
      for(int i = 0; i < processes.size(); i++) {
        Process process = processes.get(i);
        if(process.getStatus() == Process.INACTIVE &&
           process.getCreationTime() <= currentTime) {
          process.setStatus(Process.READY);
          readyQueue.add(process);
        }
      }

      // Unblocked the processes which have finished I/O.
      blockList.unblockProcesses(readyQueue);

      for(int i = 0; i < readyQueue.size(); i++) {
        Process process = readyQueue.get(i);
        process.calcPenaltyRatio();
      }

      this.sortByPriority(readyQueue);
      this.sortByHPR(readyQueue);

      if(runningProcess == null && readyQueue.size() > 0) {
        runningProcess = readyQueue.get(0);
        readyQueue.remove(0);
        runningProcess.setStatus(Process.RUNNING);
        runningProcess.setCurrentCPUBurst(randomOS(runningProcess.getCPUBurst(), sc));
        if(runningProcess.getCurrentCPUBurst() > runningProcess.getCPUTimeRemaining()) {
          runningProcess.setCurrentCPUBurst(runningProcess.getCPUTimeRemaining());
        }
      }
      
      // Time collapsed is 1 time unit.
      int timeCollapsed = 1;
      currentTime += 1;
      // Update the ready queue's processes' wait time.
      for(int i = 0; i < readyQueue.size(); i++) {
        Process process = readyQueue.get(i);
        process.setWaitTime(process.getWaitTime() + timeCollapsed);
      }
      // Update the block list.
      if(blockList.getSize() > 0) {
        blockList.updateIOTime(timeCollapsed);
        // Update the I/O used time.
        hprn.setIoUsedTime(hprn.getIoUsedTime() + 1);
      } else {
        // Update the I/O idle time.
        hprn.setIoIdleTime(hprn.getIoIdleTime() + 1);
      }

      // If there is a running process, it runs for 1 time unit.
      if(runningProcess != null) {
        runningProcess.setCurrentCPUBurst(runningProcess.getCurrentCPUBurst() - 1);
        runningProcess.setCPUTimeRemaining(runningProcess.getCPUTimeRemaining() - 1);
        // Terminate or block the running process after it
        // finishes its current CPU burst.
        if(runningProcess.getCPUTimeRemaining() == 0) {
          runningProcess.setFinishingTime(currentTime);
          runningProcess.setTurnAroundTime(runningProcess.getFinishingTime() -
              runningProcess.getCreationTime());
          jobsRemaining--;
          hprn.increaseJobsDone();
          runningProcess.setStatus(Process.TERMINATED);
          runningProcess = null;
        } else if(runningProcess.getCurrentCPUBurst() == 0){
          runningProcess.setCurrentIOBurst(randomOS(runningProcess.getIOBurst(), sc));
          blockList.addProcess(runningProcess);
          runningProcess = null;
        }

        // Update the CPU used time.
        hprn.setCpuUsedTime(hprn.getCpuUsedTime() + 1);
      } else {
        hprn.setCpuIdleTime(hprn.getCpuIdleTime() + 1);
      }
    }

    // Sets the finishing time.
    hprn.setFinishingTime(currentTime);
    hprn.calcThroughput();
    hprn.calcCPUUtilization();
    hprn.calcIOUtilization();
    hprn.calcAvrgTurnaroundTime(processes);
    hprn.calcAvrgWaitTime(processes);
  }

  public void sortByHPR(List<Process> plist) {
    for(int i = 1; i < plist.size(); i++) {
      for(int j = i; j > 0; j--) {
        if(plist.get(j).getPenaltyRatio() > plist.get(j - 1).getPenaltyRatio()) {
          Process temp = plist.get(j);
          plist.set(j, plist.get(j - 1));
          plist.set(j - 1, temp);
        }
      }
    }
  }

  public int randomOS(int base, Scanner sc) {
    int num = sc.nextInt();
    return (num % base) + 1;
  }

  // Method to reset process list.
  private void resetProcessList() {
    for(int i = 0; i < processes.size(); i++) {
      processes.get(i).resetInfo();
    }
  }

  public void printInputInfo() {
    String str1 = "The original input was: " + processes.size() + " ";
    String str2 = "The (sorted) input is:  " + processes.size() + " ";
    for(int i = 0; i < processes.size(); i++) {
      Process proc = originalList.get(i);
      str1 += "( " + proc.getCreationTime() + " " +
          proc.getCPUBurst() + " " +
          proc.getTotalCPUTime() + " " +
          proc.getIOBurst() + " ) ";
      proc = processes.get(i);
      str2 += "( " + proc.getCreationTime() + " " +
          proc.getCPUBurst() + " " +
          proc.getTotalCPUTime() + " " +
          proc.getIOBurst() + " ) ";
    }
    System.out.println(str1);
    System.out.println(str2);
  }

  protected void printProcessInfo() {
    for(int i = 0; i < processes.size(); i++) {
      Process proc = processes.get(i);
      System.out.println("Process " + i + ":");
      System.out.println("(A,B,C,IO) = (" + 
          proc.getCreationTime() + ", " +
          proc.getCPUBurst() + ", " +
          proc.getTotalCPUTime() + ", " +
          proc.getIOBurst() + ")");
      System.out.println("Finishing time: " + proc.getFinishingTime());
      System.out.println("Turnaround time: " + proc.getTurnAroundTime());
      System.out.println("I/O time: " + proc.getIOTime());
      System.out.println("Waiting time: " + proc.getWaitTime());
      System.out.println();
    }
  }

  protected void printRRResult() {
    printInputInfo();
    System.out.println("\nThe scheduling algorithm used was Round-Robin\n");
    printProcessInfo();
    System.out.println("Summary Data:");
    rr.printSummaryData();    
  }

  public void printFCFSResult() {
    printInputInfo();
    System.out.println("\nThe scheduling algorithm used was First Come First Served\n");
    printProcessInfo();
    System.out.println();
    System.out.println("Summary Data:");
    fcfs.printSummaryData();    
  }

  protected void printUnipResult() {
    printInputInfo();
    System.out.println("\nThe scheduling algorithm used was Uniprogramming\n");
    printProcessInfo();
    System.out.println();
    System.out.println("Summary Data:");
    uniprogramming.printSummaryData();    
  }

  protected void printHPRNResult() {
    printInputInfo();
    System.out.println("\nThe scheduling algorithm used was HPRN\n");
    printProcessInfo();
    System.out.println();
    System.out.println("Summary Data:");
    hprn.printSummaryData();    
  }

  protected void debug(int i, PrintWriter pw) {
    String str = "";
    str += "Before cycle " + i;
    for(int j = 0; j < processes.size(); j++) {
      if(processes.get(j).getStatus() == Process.INACTIVE) {
        str += " inactive " + 0 + " "; 
      } else if(processes.get(j).getStatus() == Process.BLOCKED) {
        str += " blocked " + processes.get(j).getCurrentIOBurst() + " ";
      } else if(processes.get(j).getStatus() == Process.READY) {
        str += " ready " + "0 ";
      } else if(processes.get(j).getStatus() == Process.RUNNING) {
        str += " running " + processes.get(j).getCurrentCPUBurst() + " ";
      } else if(processes.get(j).getStatus() == Process.TERMINATED) {
        str += " terminated " + "0 ";
      }
    }
    pw.println(str);
  }
}
