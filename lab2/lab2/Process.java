package lab2;

public class Process {
  public final static int INACTIVE = 0;
  public final static int READY = 1;
  public final static int RUNNING = 2;
  public final static int BLOCKED = 3;
  public final static int TERMINATED = 4;
  private int cpuBurst = 0;
  private int ioBurst = 0;
  private int currentCPUBurst= 0;
  private int currentIOBurst = 0;
  private int cpuTimeRemaining = 0;
  private int creationTime = 0;
  private int finishingTime = 0;
  private int totalCPUTime = 0;
  private int waitTime = 0;
  private int ioTime = 0;
  private int status = INACTIVE;
  private int priority = 0;
  private int turnAroundTime = 0;
  private double penaltyRatio = 0;
  
  protected void setCPUBurst(int cpuBurst) {
    this.cpuBurst = cpuBurst;
  }

  protected int getCPUBurst() {
    return cpuBurst;
  }

  protected void setIOBurst(int ioBurst) {
    this.ioBurst = ioBurst;
  }

  protected int getIOBurst() {
    return ioBurst;
  }

  protected void setCurrentCPUBurst(int currentCPUBurst) {
    this.currentCPUBurst = currentCPUBurst;
  }

  protected void decreaseCurrentCPUBurst(int timeCollapsed) {
    this.currentCPUBurst -= timeCollapsed;
  }

  protected int getCurrentCPUBurst() {
    return currentCPUBurst;
  }

  protected void setCurrentIOBurst(int currentIOBurst) {
    this.currentIOBurst = currentIOBurst;
  }

  protected void decreaseCurrentIOBurst(int timeCollapsed) {
    this.currentIOBurst -= timeCollapsed;
  }

  protected int getCurrentIOBurst() {
    return currentIOBurst;
  }

  protected void setCreationTime(int creationTime) {
    this.creationTime = creationTime;
  }

  protected int getCreationTime() {
    return creationTime;
  }

  protected void setFinishingTime(int finishingTime) {
    this.finishingTime = finishingTime;
  }

  protected int getFinishingTime() {
    return finishingTime;
  }

  protected void setWaitTime(int waitTime) {
    this.waitTime = waitTime;
  }

  protected void increaseWaitTime(int timeCollapsed) {
    this.waitTime += timeCollapsed;
  }

  protected int getWaitTime() {
    return waitTime;
  }

  protected void setIOTime(int ioTime) {
    this.ioTime = ioTime;
  }

  protected void increaseIOTime(int timeCollapsed) {
    this.ioTime += timeCollapsed;
  }

  protected int getIOTime() {
    return ioTime;
  }

  protected void setTotalCPUTime(int totalCPUTime) {
    this.totalCPUTime = totalCPUTime;
  }

  protected int getTotalCPUTime() {
    return totalCPUTime;
  }

  protected void setStatus(int status) {
    this.status = status;
  }
  
  protected int getStatus() {
    return status;
  }

  protected void setPriority(int priority) {
    this.priority = priority;
  }

  protected int getPriority() {
    return priority;
  }

  protected void setCPUTimeRemaining(int cpuTimeRemaining) {
    this.cpuTimeRemaining = cpuTimeRemaining;
  }

  protected void decreaseCPURemaining(int timeCollapsed) {
    this.cpuTimeRemaining -= timeCollapsed;
  }

  protected int getCPUTimeRemaining() {
    return cpuTimeRemaining;
  }

  protected void setTurnAroundTime(int turnAroundTime) {
    this.turnAroundTime = turnAroundTime;
  }

  protected int getTurnAroundTime() {
    return turnAroundTime;
  }

  protected void calcPenaltyRatio() {
    if(totalCPUTime == cpuTimeRemaining) {
      penaltyRatio = (double)waitTime;
    } else {
      penaltyRatio = ((double)totalCPUTime - cpuTimeRemaining + ioTime + waitTime) /
        (totalCPUTime - cpuTimeRemaining) ;
    }
  }

  protected double getPenaltyRatio() {
    return penaltyRatio;
  }

  protected void resetInfo() {
    currentCPUBurst= 0;
    currentIOBurst = 0;
    cpuTimeRemaining = totalCPUTime;
    finishingTime = 0;
    waitTime = 0;
    ioTime = 0;
    status = INACTIVE;
    turnAroundTime = 0;
  }

  public String toString() {
    String str = "";
    str = "(" + creationTime + " " + cpuBurst + " " + totalCPUTime + " " + ioBurst + ")";
    return str;
  }
}
