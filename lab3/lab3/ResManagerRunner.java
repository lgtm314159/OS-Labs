package lab3;

public class ResManagerRunner {

  public static void main(String[] args) {
    String input = args[0];
    OptimisticManager opti = new OptimisticManager(input);
    opti.manageResource();
    Banker banker = new Banker(input);
    banker.manageResource();

    System.out.format("%-10s", "");
    System.out.format("%-28s", "FIFO");
    System.out.println("Banker");
    for(int i = 0; i < opti.taskList.size(); i++) {
      opti.printSingleTaskResult(i);
      banker.printSingleTaskResult(i);
      System.out.println();
    }
    opti.printTotalResult();
    banker.printTotalResult();
    System.out.println();
  }
}
