package lab4;

// Class to hold information of a frame.
public class Frame {
  private int id = 0;
  private boolean isInUse = false;
  private int processId = 0;
  private int pageId = 0;
  private int timePageInserted = 0;
  private int pageRecentUsedValue = 0;

  public Frame(int id) {
    this.id = id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setTimePageInserted(int currentTime) {
    timePageInserted = currentTime;
  }

  public int getTimePageInserted() {
    return timePageInserted;
  }
  
  public void setProcessId(int processId) {
    this.processId = processId;
  }

  public int getProcessId() {
    return processId;
  }

  public void setPageId(int pageId) {
    this.pageId = pageId;
  }

  public int getPageId() {
    return pageId;
  }

  public void setPageRecentUsedValue(int value) {
    pageRecentUsedValue = value;
  }

  public int getPageRecentUsedValue() {
    return pageRecentUsedValue;
  }

  public void setIsInUse(boolean isInUse) {
    this.isInUse = isInUse;
  }

  public boolean isInUse() {
    return isInUse;
  }
}
