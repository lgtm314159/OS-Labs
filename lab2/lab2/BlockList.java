package lab2;

import java.util.ArrayList;
import java.util.List;

public class BlockList {
  private List<Process> blockList = new ArrayList<Process>();

  public void addProcess(Process process) {
    process.setStatus(Process.BLOCKED);
    blockList.add(process);
  }

  public void unblockProcesses(List<Process> intermediateList) {
    for(int i = 0; i < blockList.size(); i++) {
      Process process = blockList.get(i);
      if(process.getCurrentIOBurst() == 0) {
        intermediateList.add(process);
        process.setStatus(Process.READY);
        // Dynamically update the block list. 
        blockList.remove(i);
        // Decrease i by 1 after the removal of an entry to ensure the
        // index is correct.
        i--;
      }
    }
  }

  // Method to update the current IO burst and IO time for all blocked
  // processes.
  public void updateIOTime(int timeCollapsed) {
    // Update the block list.
    for(int i = 0; i < blockList.size(); i++) {
      Process process = blockList.get(i);
      if((process.getCurrentIOBurst() - timeCollapsed) < 0) {
        process.setCurrentIOBurst(0);
      } else {
        process.setCurrentIOBurst(process.getCurrentIOBurst() - timeCollapsed);
      }
      process.setIOTime(process.getIOTime() + timeCollapsed);
    }
  }

  public int getSize() {
    return blockList.size();
  }
}
