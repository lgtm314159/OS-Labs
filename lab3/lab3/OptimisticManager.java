package lab3;

public class OptimisticManager extends ResourceManager{
  private boolean currentCycleRelease = false;
  private boolean lastCycleRelease = false;

  // Constructor which performs the input data processing.
  public OptimisticManager(String file) {
    readInput(file);
  }

  // Method to manage resources in the "optimistic" way.
  public void manageResource() {
    int tasksLeft = tasks;
    zeroOutResourceReleasedArray();
    currentTime = 0;
    while(tasksLeft > 0) {
      lastCycleRelease = currentCycleRelease;
      currentCycleRelease = false;
      // Update the resource available array with released resources from last
      // cycle.
      updateResourceAvailableArray();
      // Clear the temporary resource released array.
      zeroOutResourceReleasedArray();
      // Clear the tempory unblock list.
      unblockList.clear();

      // If there was resource released in last cycle, check the wait list.
      if(lastCycleRelease) {
          for(int i = 0; i < waitList.size(); i++) {
            Task task = waitList.get(i);
            String[] temp = task.getNextInstruction().split(" ");
            int resourceType = Integer.parseInt(temp[1]);
            int resourceUnits = Integer.parseInt(temp[2]);
            if(resourceUnits <= resourceAvailable[resourceType]) {
              resourceAvailable[resourceType] -= resourceUnits;
              resourceAssigned[task.getId()][resourceType] += resourceUnits;
              // Move the instruction pointer to the next instruction.
              task.increaseInstructionPointerByOne();
              task.setStatus(Task.RUNNING);
              // Remove this task from the waiting list and reduce i by 1 to
              // ensure the correct index.
              waitList.remove(task);
              i--;
              unblockList.add(task);
            }
          }
      }

      for(int i = 0; i < taskList.size(); i++) {
        Task task = taskList.get(i);
        // If task hasn't started, start it.
        if(task.getStatus() == Task.INACTIVE) {
          task.setStatus(Task.RUNNING);
        }
        // Execute the next instruction for running task, except for the ones
        // which were just unblocked and put into running mode.
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
            if(resourceUnits <= resourceAvailable[resourceType]) {
              resourceAvailable[resourceType] -= resourceUnits;
              resourceAssigned[task.getId()][resourceType] += resourceUnits;
              // Move the instruction pointer to the next instruction.
              task.increaseInstructionPointerByOne();
              task.setStatus(Task.RUNNING);
            } else {
              task.setStatus(Task.WAITING);
              waitList.add(task);
            }
          // If the instruction is release, update the resource available
          // array and resource assigned array.
          } else if(instruction.equals("release")) {
            // Store the released resource in the temporary resource released,
            // array, and update the available resource available array at
            // the beginning of next cycle.
            resourceReleased[resourceType] += resourceUnits;
            // Update the resource assigned array.
            resourceAssigned[task.getId()][resourceType] -= resourceUnits;
            // Move the instruction pointer to the next instruction.
            task.increaseInstructionPointerByOne();
            // Mark the isReleased flag to be true.
            currentCycleRelease = true;
          } else if(instruction.equals("terminate")) {
            task.setStatus(Task.TERMINATED);
            task.setFinishTime(currentTime);
            task.calculateWaitTimePercent();
            tasksLeft--;
          } else {
            // Ignore the instructions of initiate and compute for optimistic
            // manager, and just move the instruction pointer forward.
            task.increaseInstructionPointerByOne();
          }
        }
      }

      // Check for deadlock, and if there exists one, abort necessary task(s).
      if(detectDeadlock()) {
        System.out.println("During cycle " + currentTime + "-" + 
            (currentTime + 1) +
            " of optimistic management, deadlock is detected");
        tasksLeft = abortTask(tasksLeft);
        currentCycleRelease = true;
      }

      // Update the waiting time for tasks that are in the wait list.
      this.updateWaitTimeForWaitList();

      currentTime++;
    }
  }

  // Method to detect deadlock.
  private boolean detectDeadlock() {
    boolean anyWaitingTask = false;
    for(int i = 0; i < taskList.size(); i++) {
      Task task = taskList.get(i);
      // Check if there is any of the waiting tasks' requests tha can be
      // fulfilled. If there is, no deadlock; otherwise there might be a
      // deadlock.
      if(task.getStatus() == Task.WAITING) {
        anyWaitingTask = true;
        String[] temp = task.getNextInstruction().split(" ");
        int resourceType = Integer.parseInt(temp[1]);
        int resourceUnits = Integer.parseInt(temp[2]);
        if(resourceUnits <= resourceAvailable[resourceType] + resourceReleased[resourceType]) {
          return false;
        }
      }

      // If there is any running task, there is no deadlock at the current
      // cycle.
      if(task.getStatus() == Task.RUNNING) {
        return false;
      }
    }

    // If the code reaches this far and there are tasks waiting, a deadlock
    // exisits.
    if(anyWaitingTask) {
      return true;
    // Otherwise it is not a deadlock. Situations fall into such condition
    // include the whole list of tasks have been aborted.
    } else {
      return false;
    }
  }

  // Method to abort task(s) when there exists a deadlock.
  private int abortTask(int tasksLeft) {
    while(detectDeadlock()) {
      for(int i = 0; i < taskList.size(); i++) {
        Task task = taskList.get(i);
        if(task.getStatus() == Task.WAITING) {
          // Abort the task.
          task.setStatus(Task.ABORTED);
          // Remove the task from the wait list.
          waitList.remove(task);
          System.out.print("    Task " + (task.getId() + 1) + " aborted; ");
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
          break;
        }
      }
    }
    return tasksLeft;
  }
}
