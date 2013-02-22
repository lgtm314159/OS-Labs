package lab4;

// Class to hold information of a process.
public class Process {
  private int id = 0;
  private int size = 0;
  private double fracA = 0;
  private double fracB = 0;
  private double fracC = 0;
  private int refLeft = 0;
  private int nextRef = 0;
  private int numOfEviction = 0;
  private int numOfPageFault = 0;
  private int sum = 0;
  private double averResTime = -1;

  public Process(int id, int size) {
    this.id = id;
    this.size = size;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int getSize() {
    return size;
  }

  public void setFracA(double fracA) {
    this.fracA = fracA;
  }

  public double getFracA() {
    return fracA;
  }

  public void setFracB(double fracB) {
    this.fracB = fracB;
  }

  public double getFracB() {
    return fracB;
  }

  public void setFracC(double fracC) {
    this.fracC = fracC;
  }

  public double getFracC() {
    return fracC;
  }

  public void setRefLeft(int refLeft) {
    this.refLeft = refLeft;
  }

  public void decreaseRefLeft() {
    refLeft--;
  }

  public int getRefLeft() {
    return refLeft;
  }

  public void setNextRef(int nextRef) {
    this.nextRef = nextRef;
  }

  public int getNextRef() {
    return nextRef;
  }

  public void setNumOfEviction(int numOfEviction) {
    this.numOfEviction = numOfEviction;
  }

  public void increaseNumOfEviction() {
    this.numOfEviction++;
  }

  public int getNumOfEviction() {
    return numOfEviction;
  }

  public void setNumOfPageFault(int numOfPageFault) {
    this.numOfPageFault = numOfPageFault;
  }

  public void increaseNumOfPageFault() {
    this.numOfPageFault++;
  }

  public int getNumOfPagefault() {
    return numOfPageFault;
  }

  public void setSum(int sum) {
    this.sum = sum;
  }

  public void increaseSum(int value) {
    sum += value;
  }

  public int getSum() {
    return sum;
  }

  public void calcAverResTime() {
    if(numOfEviction > 0) {
      averResTime = (double) sum / numOfEviction;
    }
  }

  public double getAverResTime() {
    return averResTime;
  }
}
