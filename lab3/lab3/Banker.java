package lab3;

public class Banker extends ResourceManager{
  
  // Constructor to read in the input data.
  public Banker(String file) {
    readInput(file);
  }

  // Method to manage resources with Banker's Algorithm.
  public void manageResource() {
    int tasksLeft = tasks;
    zeroOutResourceReleasedArray();
    currentTime = 0;
    while(tasksLeft > 0) {
      // Update the resource available array with released resources from last
      // cycle.
      updateResourceAvailableArray();
      // Clear the temporary resource released array.
      zeroOutResourceReleasedArray();
      // Clear the temporary unblock list.
      unblockList.clear();

      for(int i = 0; i < waitList.size(); i++) {
        Task task = waitList.get(i);
        String[] temp = task.getNextInstruction().split(" ");
        int resourceType = Integer.parseInt(temp[1]);
        int resourceUnits = Integer.parseInt(temp[2]);
        if(resourceUnits <= resourceAvailable[resourceType]) {
          // Check safety before granting resource.
          if(checkSafety(task.getId(), resourceType, resourceUnits)) {
            // If safe, grant resource and update the arrays correspondingly.
            resourceAvailable[resourceType] -= resourceUnits;
            resourceAssigned[task.getId()][resourceType] += resourceUnits;
            resourceStillNeeded[task.getId()][resourceType] -= resourceUnits;
            // Move the instruction pointer to the next instruction.
            task.increaseInstructionPointerByOne();
            task.setStatus(Task.RUNNING);
            // Remove this task from the waiting list and reduce i by 1 to
            // ensure the correct index.
            waitList.remove(task);
            i--;
            // Add the task to the temporary unblock list to avoid
            // double-processing in one cycle.
            unblockList.add(task);
          }
          clearBankerMark();
        }
      }
      
      for(int i = 0; i < taskList.size(); i++) {
        Task task = taskList.get(i);
        // If task hasn't started, start it.
        if(task.getStatus() == Task.INACTIVE) {
          task.setStatus(Task.RUNNING);
        }
        if(task.getStatus() == Task.RUNNING && !isJustUnblocked(task.getId())) {
          // Execute the next instruction of the task.
          String[] temp = task.getNextInstruction().split(" ");
          String instruction = temp[0];
          int resourceType = 0;
          int resourceUnits = 0;
          // If the instruction is request or release.
          if(temp.length > 1) {
            resourceType = Integer.parseInt(temp[1]);
            resourceUnits = Integer.parseInt(temp[2]);
          }
          // If the instruction is request, check to see if the request can
          // be fulfilled.
          if(instruction.equals("request")) {
            if((resourceUnits + resourceAssigned[task.getId()][resourceType]) >
                resourceClaim[task.getId()][resourceType]) {
              task.setStatus(Task.ABORTED);
              System.out.println("During cycle " + currentTime + "-" +
              		(currentTime + 1) + " of Banker's algorithms");
              System.out.print("    Task " + task.getId() +
                  "'s request exceeds its claim; aborted; ");
              
              // Release all its resources.
              for(int j = 0; j < resourceAvailable.length; j++) {
                // Store the released resource in the temporary resource released,
                // array, and update the available resource available array at
                // the beginning of next cycle.
                resourceReleased[j] += resourceAssigned[task.getId()][j];
                System.out.print(resourceAssigned[task.getId()][j] +
                    " units of resource type " + (j + 1) + " ");
                // Update the resource assigned array.
                resourceAssigned[task.getId()][j] = 0;
              }
              System.out.println("available next cycle");
              tasksLeft--; 
            } else {
              if(resourceUnits <= resourceAvailable[resourceType]) {
                // Check safety before granting resource.
                if(checkSafety(task.getId(), resourceType, resourceUnits)) {
                  // If safe, grant resource and update the arrays correspondingly.
                  resourceAvailable[resourceType] -= resourceUnits;
                  resourceAssigned[task.getId()][resourceType] += resourceUnits;
                  resourceStillNeeded[task.getId()][resourceType] -= resourceUnits;
                  // Move the instruction pointer to the next instruction.
                  task.increaseInstructionPointerByOne();
                  task.setStatus(Task.RUNNING);
                } else {
                  // If not safe, block the task.
                  task.setStatus(Task.WAITING);
                  waitList.add(task);
                }
                // Check safety method may set the banker mark flag of a task,
                // therefore it needs to be cleared for subsequent processing.
                clearBankerMark();
              } else {
                task.setStatus(Task.WAITING);
                waitList.add(task);
              }
            }
          } else if(instruction.equals("release")) {
            // Store the released resource in the temporary resource released,
            // array, and update the available resource available array at
            // the beginning of next cycle.
            resourceReleased[resourceType] += resourceUnits;
            // Update the resource assigned array.
            resourceAssigned[task.getId()][resourceType] -= resourceUnits;
            // Update the resource still needed array.
            resourceStillNeeded[task.getId()][resourceType] += resourceUnits;
            // Move the instruction pointer to the next instruction.
            task.increaseInstructionPointerByOne();
          } else if(instruction.equals("terminate")) {
            task.setStatus(Task.TERMINATED);
            task.setFinishTime(currentTime);
            task.calculateWaitTimePercent();
            tasksLeft--;
          } else if(instruction.equals("initiate")) {
            if(resourceUnits > resourcePresent[resourceType]) {
              task.setStatus(Task.ABORTED);
              System.out.println("Banker aborts task " + (task.getId() + 1) +
                  " before run begins:");
              System.out.println("    claim for resourse " + 
			      (resourceType + 1) +
                  " (" +resourceUnits + ") exceeds number of units present (" +
                  resourcePresent[resourceType] + ")");
              tasksLeft--;
            } else {
              resourceClaim[task.getId()][resourceType] = resourceUnits;
              resourceStillNeeded[task.getId()][resourceType] = resourceUnits;
              task.increaseInstructionPointerByOne();
            }
          } else {
            // Ignore the compute instruction and simply move the instruction
            // pointer to the next instruction.
            task.increaseInstructionPointerByOne();
          }
        }
      }
      updateWaitTimeForWaitList();
      
      currentTime++;
    }
  }

  // Method to check safety with Banker's Algorithm.
  public boolean checkSafety(int taskId, int resourceType, int resourceUnits) {
    // Create temporary resource arrays for resource grant simulation.
    int[] tempResAvail = resourceAvailable.clone();
    int[][] tempResAssigned = new int[tasks][resourceTypes];
    int[][] tempResStillNeeded = new int[tasks][resourceTypes];
    copy2DArray(resourceAssigned, tempResAssigned);
    copy2DArray(resourceStillNeeded, tempResStillNeeded);
    // Simulate that the requested resource has been granted, temporary
    // resources arrays are updated correspondingly.
    tempResAvail[resourceType] -= resourceUnits;
    tempResAssigned[taskId][resourceType] += resourceUnits;
    tempResStillNeeded[taskId][resourceType] -= resourceUnits;
    int unmarkedTasks = taskList.size();

    // Loop until all tasks are marked, or there is no more task which can be
    // marked.
    while(unmarkedTasks > 0) {

      boolean anyTaskMarked = false;
      for(int i = 0; i < taskList.size(); i++) {
        Task task = taskList.get(i);
        if(task.getBankerMark() == 0) {
          if(task.getStatus() == Task.ABORTED || task.getStatus() == Task.TERMINATED) {
            task.setBankerMark(1);
            unmarkedTasks--;
            anyTaskMarked = true;
          } else {
            boolean canComplete = true;
            // Check to see if the task can complete with current available
            // resources.
            for(int j = 0; j < resourceTypes; j++) {
              if(tempResStillNeeded[task.getId()][j] > tempResAvail[j]) {
                canComplete = false;
                break;
              }
            }
            // If the task can complete, mark it and release all its
            // resources.
            if(canComplete) {
              task.setBankerMark(1);
              for(int j = 0; j < resourceTypes; j++) {
                tempResAvail[j] += tempResAssigned[task.getId()][j];
              }
              unmarkedTasks--;
              anyTaskMarked = true;
            }
          }
        }
      }
      // If there is no task left whose request can be satisfied, break out the loop.
      if(!anyTaskMarked) {
        break;
      }
    }

    if(unmarkedTasks > 0) {
      // Unsafe to grant resource.
      return false;
    } else {
      // Safe to grant resource.
      return true;
    }
  }

  // Method to copy a 2-dimensional array.
  public void copy2DArray(int[][] arrayFrom, int[][] arrayTo) {
    for(int i = 0; i < arrayFrom.length; i++) {
      for(int j = 0; j < arrayFrom[0].length; j++) {
        arrayTo[i][j] = arrayFrom[i][j];
      }
    }
  }

  // Method to clear banker mark flag for subsequent processing.
  public void clearBankerMark() {
    for(int i = 0; i < taskList.size(); i++) {
      Task task = taskList.get(i);
        task.setBankerMark(0);
    }
  }
}
