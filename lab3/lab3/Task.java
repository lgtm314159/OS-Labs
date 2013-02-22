package lab3;

import java.util.ArrayList;
import java.util.List;

// The class to hold information of a task.
public class Task {
  public final static int INACTIVE = 0;
  public final static int RUNNING = 1;
  public final static int WAITING = 2;
  public final static int TERMINATED = 3;
  public final static int ABORTED = 4;
  private int id = 0;
  private int status = 0;
  private int waitTime = 0;
  private int waitTimePercent = 0;
  private int finishTime = 0;
  // This is a flag used by the banker's algorithm, 0 means it is not
  // terminated, 1 means it is terminated.
  private int bankerMark = 0;
  // This is the pointer which points to the next instruction of the task.
  private int instructionPointer = 0;
  private List<String> instructionSequence = new ArrayList<String>();

  public Task(int id) {
    this.id = id;
    status = INACTIVE;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getStatus() {
    return status;
  }

  public void setWaitTime(int waitTime) {
    this.waitTime = waitTime;
  }

  public void increaseWaitTimeByOne() {
    waitTime++;
  }

  public int getWaitTime() {
    return waitTime;
  }

  public void setFinishTime(int finishTime) {
    this.finishTime = finishTime;
  }

  public void increaseFinishTimeByOne() {
    finishTime++;
  }

  public int getFinishTime() {
    return finishTime;
  }

  
  public void setBankerMark(int bankerMark) {
    this.bankerMark = bankerMark;
  }

  public int getBankerMark() {
    return bankerMark;
  }
  

  public void setInstructionPointer(int instructionPointer) {
    this.instructionPointer = instructionPointer;
  }

  public void increaseInstructionPointerByOne() {
    instructionPointer++;
  }

  public int getInstructionPointer() {
    return instructionPointer;
  }

  public String getNextInstruction() {
    return instructionSequence.get(instructionPointer);
  }

  public void addInstruction(String instruction) {
    instructionSequence.add(instruction);
  }

  public void calculateWaitTimePercent() {
    waitTimePercent = (int)Math.round((((double)waitTime / finishTime) * 100));
  }

  public String getWaitTimePercent() {
    return waitTimePercent + "%";
  }

  public String toString() {
    return id + " : " + instructionSequence;
  }
}
