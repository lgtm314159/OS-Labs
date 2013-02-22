package lab2;

public class SchedulerRunner {
  public static void main(String[] args) {
    if(args.length > 0) {
      
      Scheduler scheduler = new Scheduler(args[0], args[1]);
      args[2] = args[2].toLowerCase();
      if(args[2].equals("fcfs")) {
        scheduler.fcfsScheduling();
        scheduler.printFCFSResult();
      } else if(args[2].equals("rr")) {
        scheduler.rrScheduling();
        scheduler.printRRResult();
      } else if(args[2].equals("unip")) {
        scheduler.uniprogramming();
        scheduler.printUnipResult();
      } else if(args[2].equals("hprn")) {
        scheduler.hprnScheduling();
        scheduler.printHPRNResult();
      }
      
    }
  }
}
