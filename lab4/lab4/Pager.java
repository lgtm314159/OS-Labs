package lab4;

import java.util.ArrayList;
import java.util.List;

// Class of a simulated pager with 3 algorithms.
public class Pager {
  private int[][] pageMatrix;
  private int numOfFrames = 0;
  private int pageSize = 0;
  private int randomNumber = 0;
  List<Frame> frameTable = new ArrayList<Frame>();

  // Constructor that initializes the frame table and the page matrix of
  // recent used values.
  public Pager(int numOfFrames, int pageSize) {
    this.numOfFrames = numOfFrames;
    this.pageSize = pageSize;
    createFrameTable();
    createPageMatrix();
  }

  // Method to pass in a random number into the pager.
  public void setRandomNumber(int rn) {
    randomNumber = rn;
  }

  // Method to create frame table.
  private void createFrameTable() {
    for(int i = 0; i < numOfFrames; i++) {
      Frame frame = new Frame(i);
      frameTable.add(frame);
    }
  }

  // Method to check if there is a page fault. Returns a non-negative frame id
  // if the required page is already loaded, or -1 if there is a page fault.
  public int checkPageFault(int pageId, int processId) {
    for(int i = 0; i < frameTable.size(); i++) {
      Frame frame = frameTable.get(i);
      if(frame.getPageId() == pageId && frame.getProcessId() == processId) {
        return frame.getId();
      }
    }
    return -1;
  }

  // Method to check if there is any free frame. If so, returns a non-negative
  // frame id of the free frame, or -1 if there is no free frame.
  public int anyFreeFrame() {
    for(int i = frameTable.size() - 1; i >= 0; i--) {
      Frame frame = frameTable.get(i);
      if(!frame.isInUse()) {
        return frame.getId();
      }
    }
    return -1;
  }

  // Method to decide which page of a process is needed to execute the process'
  // next reference.
  public int decidePageId(Process process) {
    return process.getNextRef() / pageSize;
  }

  // Method to create page matrix of recent used values.
  private void createPageMatrix() {
    pageMatrix = new int[numOfFrames][numOfFrames];
    for(int i = 0; i < numOfFrames; i++) {
      for(int j = 0; j < numOfFrames; j++) {
        pageMatrix[i][j] = 0;
      }
    }
  }

  // Method to update page matrix.
  public void updatePageMatrix(int frameId) {
    for(int i = 0; i < numOfFrames; i++) {
      pageMatrix[frameId][i] = 1;
    }
    for(int i = 0; i < numOfFrames; i++) {
      pageMatrix[i][frameId] = 0;
    }
    for(int i = 0; i < frameTable.size(); i++) {
      Frame frame = frameTable.get(i);
      calcRecntPageUsedValue(frame);
    }
  }

  // Method to calculate recent used value for each page in the frame.
  public void calcRecntPageUsedValue(Frame frame) {
    int value = 0;
    int frameId = frame.getId();
    for(int i = 0; i < numOfFrames; i++) {
      value += value * 2 + pageMatrix[frameId][i];
    }
    frame.setPageRecentUsedValue(value);
  }

  // Method to simulate FIFO paging algorithm.
  public int fifoAlgor() {
    int min = frameTable.get(0).getTimePageInserted();
    int minId = frameTable.get(0).getId();
    for(int i = 0; i < frameTable.size(); i++) {
      Frame frame = frameTable.get(i);
      if(frame.getTimePageInserted() < min) {
        min = frame.getTimePageInserted();
        minId = frame.getId();
      }
    }
    return minId;
  }

  // Method to simulate random paging algorithm.
  public int randomAlgor(int rn) {
    return rn % numOfFrames;
  }

  // Method to simulate LRU paging algorithm.
  public int lruAlgor() {
    int min = frameTable.get(0).getPageRecentUsedValue();
    int minId = frameTable.get(0).getId();
    for(int i = 0; i < frameTable.size(); i++) {
      Frame frame = frameTable.get(i);
      if(frame.getPageRecentUsedValue() < min) {
        min = frame.getPageRecentUsedValue();
        minId = frame.getId();
      }
    }
    return minId;
  }

  // Method to perform both eviction and insertion.
  public void evictAndInsert(String algorithm, int favoredPageId,
      int favoredProcessId, int currentTime, List<Process> plist, int mode) {
    int victimFrameId = 0;
    if(algorithm.equals("fifo")) {
      victimFrameId = fifoAlgor();
    } else if(algorithm.equals("random")) {
      victimFrameId = randomAlgor(randomNumber);
    } else {
      victimFrameId = lruAlgor();
    }

    // Eviction
    evictPage(victimFrameId, plist, currentTime, mode);
    // Insertion
    insertPage(victimFrameId, favoredPageId, favoredProcessId, currentTime,
        plist);
  }

  // Method to evict a page.
  public void evictPage(int frameId, List<Process> plist, int currentTime,
      int mode) {
    Frame frame = frameTable.get(frameId);
    int processId = frame.getProcessId();
    Process process = plist.get(processId - 1);
    process.increaseSum(currentTime - frame.getTimePageInserted());
    process.increaseNumOfEviction();
    if(mode != 0) {
      System.out.print("evicting page " + frame.getPageId() + " of " +
          processId + " from frame " + frameId);
    }
  }

  // Method to insert a page into the specified frame.
  public void insertPage(int frameId, int pageId, int processId,
      int currentTime, List<Process> plist) {
    Frame frame = frameTable.get(frameId);
    Process process = plist.get(processId - 1);
    process.increaseNumOfPageFault();
    frame.setTimePageInserted(currentTime);
    frame.setProcessId(processId);
    frame.setPageId(pageId);
    frame.setIsInUse(true);
    updatePageMatrix(frame.getId());
  }
}
