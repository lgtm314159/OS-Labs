package lab3;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Parent class to hold common fields and methods.
public class ResourceManager {
  List<Task> taskList = new ArrayList<Task>();
  List<Task> waitList = new ArrayList<Task>();
  List<Task> unblockList = new ArrayList<Task>();
  int[] resourcePresent;
  int[] resourceAvailable;
  int[] resourceReleased;
  int[][] resourceAssigned;
  int[][] resourceClaim;
  int[][] resourceStillNeeded;
  int currentTime = 0;
  int tasks = 0;
  int resourceTypes = 0;

  // Method to process the input data.
  protected void readInput(String file) {
    FileInputStream input = null;
    try {
      input = new FileInputStream(file);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    if(input != null) {
      Scanner sc = new Scanner(input);
      tasks = sc.nextInt();
      resourceTypes = sc.nextInt();
      resourcePresent = new int[resourceTypes];
      resourceAvailable = new int[resourceTypes];
      resourceReleased = new int[resourceTypes];
      resourceAssigned = new int[tasks][resourceTypes];
      resourceClaim = new int[tasks][resourceTypes];
      resourceStillNeeded = new int[tasks][resourceTypes];
      // In my program the task id starts from 0.
      for(int i = 0; i < tasks; i++) {
        // Task is a class defined by me to hold information of a specific
        // task. It has fields such as id, status, finish time, etc.
        Task task = new Task(i);
        taskList.add(task);
      }
      // Resource type also starts from 0.
      for(int i = 0; i < resourceTypes; i++) {
        resourcePresent[i] = sc.nextInt();
        resourceAvailable[i] = resourcePresent[i];
      }
      
      String instruction = "";
      while(sc.hasNext()) {
        instruction = sc.next();
        // Instruction of initiate, request or release will appear as a String
        // in the format "instruction resourceType resourceUnit" in each task's
        // instruction sequence.
        if(instruction.equals("initiate") || instruction.equals("request") ||
            instruction.equals("release")) {
          // Task id equals task number in the input file minus 1.
          int taskId = sc.nextInt() - 1;
          // Resource type also equals resource type in the input file minus 1.
          int resourceType = sc.nextInt() - 1;
          int numOfResource = sc.nextInt();
          taskList.get(taskId).addInstruction(instruction + " " + resourceType + " " + numOfResource);
        } else if(instruction.equals("compute")) {
          // Task id equals task number in the input file minus 1.
          int taskId = sc.nextInt() - 1;
          int cycles = sc.nextInt();
          for(int i = 0; i < cycles; i++) {
            taskList.get(taskId).addInstruction("compute");
          }
        } else {
          // Task id equals task number in the input file minus 1.
          int taskId = sc.nextInt() - 1;
          taskList.get(taskId).addInstruction("terminate");
        }
      }
    }
  }

  // Method to zero out the resource released array.
  protected void zeroOutResourceReleasedArray() {
    for(int i = 0; i < resourceReleased.length; i++) {
      resourceReleased[i] = 0;
    }
  }

  // Method to update resource available array with data in resource released
  // array.
  protected void updateResourceAvailableArray() {
    for(int i = 0; i < resourceAvailable.length; i++) {
      resourceAvailable[i] += resourceReleased[i];
    }
  }

  // Method to print the result of a single task.
  public void printSingleTaskResult(int i) {
    Task task = taskList.get(i);
    System.out.format("%-10s", "Task " + (task.getId() + 1));
    if(task.getStatus() == Task.TERMINATED) {
      System.out.format("%-6s", "" + task.getFinishTime());
      System.out.format("%-6s", task.getWaitTime());
      System.out.format("%-6s", task.getWaitTimePercent());
    } else if(task.getStatus() == Task.ABORTED) {
      System.out.format("%-18s", "aborted");
    }
  }

  // Method to print the overall result.
  public void printTotalResult() {
    int totalFinishTime = 0;
    int totalWaitTime = 0;
    for(int i = 0; i < taskList.size(); i++) {
      Task task = taskList.get(i);
      if(task.getStatus() == Task.TERMINATED) {
        totalFinishTime += task.getFinishTime();
        totalWaitTime += task.getWaitTime();
      }
    }
    int totalWaitPercent = (int)Math.round((double)totalWaitTime / totalFinishTime * 100);
    System.out.format("%-10s", "Total");
    System.out.format("%-6s", "" + totalFinishTime);
    System.out.format("%-6s", totalWaitTime);
    System.out.format("%-6s", totalWaitPercent + "%");
  }

  // Method to increase the waiting time by one for each task in the wai list.
  protected void updateWaitTimeForWaitList() {
    for(int i = 0; i < waitList.size(); i++) {
      waitList.get(i).increaseWaitTimeByOne();
    }
  }

  // Method to check if a task was just unblocked.
  protected boolean isJustUnblocked(int taskId) {
    for(int i = 0; i < unblockList.size(); i++) {
      Task task = unblockList.get(i);
      if(task.getId() == taskId) {
        return true;
      }
    }
    return false;
  }
}

