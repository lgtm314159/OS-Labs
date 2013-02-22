package lab2;

import java.util.List;
import java.text.DecimalFormat;
import java.math.RoundingMode;

public class AlgorithmInfo {
  private int finishingTime = 0;
  private double cpuIdleTime = 0;
  private double cpuUsedTime = 0;
  private double ioIdleTime = 0;
  private double ioUsedTime = 0;
  private double avrgTurnaroundTime = 0;
  private double avrgWaitTime = 0;
  private int jobsDone = 0;
  private double throughPut = 0;
  private double cpuUtilization = 0;
  private double ioUtilization = 0;
  private DecimalFormat df = new DecimalFormat("#.000000");

  public AlgorithmInfo() {
    df.setRoundingMode(RoundingMode.HALF_UP);
  }

  protected void setFinishingTime(int finishingTime) {
    this.finishingTime = finishingTime;
  }

  protected int getFinishingTime() {
    return finishingTime;
  }

  protected void setCpuIdleTime(double cpuIdleTime) {
    this.cpuIdleTime = cpuIdleTime;
  }

  protected double getCpuIdleTime() {
    return cpuIdleTime;
  }

  protected void setCpuUsedTime(double cpuUsedTime) {
    this.cpuUsedTime = cpuUsedTime;
  }

  protected double getCpuUsedTime() {
    return cpuUsedTime;
  }

  protected void setIoIdleTime(double ioIdleTime) {
    this.ioIdleTime = ioIdleTime;
  }

  protected double getIoIdleTime() {
    return ioIdleTime;
  }

  protected void setIoUsedTime(double ioUsedTime) {
    this.ioUsedTime = ioUsedTime;
  }

  protected double getIoUsedTime() {
    return ioUsedTime;
  }

  protected void setThroughPut(double throughPut) {
    this.throughPut = throughPut;
  }

  protected double getThroughPut() {
    return throughPut;
  }

  protected void setAvrgTurnaroundTime(double avrgTurnaroundTime) {
    this.avrgTurnaroundTime = avrgTurnaroundTime;
  }

  protected double getAvrgTurnaroundTime() {
    return avrgTurnaroundTime;
  }

  protected void setAvrgWaitTime(double avrgWaitTime) {
    this.avrgWaitTime = avrgWaitTime;
  }

  protected double getAvrgWaitTime() {
    return avrgWaitTime;
  }

  protected void increaseJobsDone() {
    jobsDone += 1;
  }

  protected int getJobsDone() {
    return jobsDone;
  }

  protected void calcThroughput() {
    throughPut = (double)jobsDone / finishingTime * 100;
    this.throughPut = Double.parseDouble(df.format(this.throughPut));
  }

  protected void setCPUUtilization() {
    calcCPUUtilization();
  }

  protected void calcCPUUtilization() {
    this.cpuUtilization =
        this.cpuUsedTime / (this.cpuUsedTime + this.cpuIdleTime);
    this.cpuUtilization = Double.parseDouble(df.format(this.cpuUtilization));
  }

  protected void calcIOUtilization() {
    this.ioUtilization = this.ioUsedTime / (this.ioUsedTime + this.ioIdleTime);
    this.ioUtilization = Double.parseDouble(df.format(this.ioUtilization));
  }

  protected void calcAvrgTurnaroundTime(List<Process> plist) {
    double sum = 0;
    for(int i = 0; i < plist.size(); i++) {
      sum += plist.get(i).getTurnAroundTime();
    }
    this.avrgTurnaroundTime =
      Double.parseDouble(df.format(sum / plist.size()));
  }

  protected void calcAvrgWaitTime(List<Process> plist) {
    double sum = 0;
    for(int i = 0; i < plist.size(); i++) {
      sum += plist.get(i).getWaitTime();
    }
    this.avrgWaitTime =
      Double.parseDouble(df.format(sum / plist.size()));
  }

  protected void increaseCPUUsedTime() {
    this.cpuUsedTime++;
  }

  protected void increaseCPUIdleTime() {
    this.cpuIdleTime++;
  }

  protected void increaseIOUsedTime() {
    this.ioUsedTime++;
  }

  protected void increaseIOIdleTime() {
    this.ioIdleTime++;
  }

  protected void wrapupCalc() {
    
  }

  public void printSummaryData() {
    System.out.println("Finishing time: " + finishingTime);
    
    System.out.println("CPU Utilization: " + cpuUtilization);
    
    System.out.println("I/O Utilization: " + ioUtilization);
    
    System.out.println("Throughput: " + throughPut + " processes per hundred cycles");
    
    System.out.println("Average turnaround time: " + avrgTurnaroundTime);
    
    System.out.println("Average waiting time: " + avrgWaitTime);
  }
}
