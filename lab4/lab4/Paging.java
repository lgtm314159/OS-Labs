package lab4;

public class Paging {
  public static void main(String[] args) {
    Driver driver = new Driver(args, "lab4/random-numbers.txt");
    if(Integer.parseInt(args[6]) == 0) {
      driver.simulatPaging();
      driver.displayResult();
    } else if(Integer.parseInt(args[6]) == 1 ||
        Integer.parseInt(args[6]) == 11) {
      driver.simulatePagingDebug();
      driver.displayResult();
    } else {
      System.err.println("Invalid mode! Mode can only be 0, 1 or 11");
      System.exit(-1);
    }
  }
}
