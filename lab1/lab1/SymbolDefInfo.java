/* Author: Junyang Xin
 * Email: jx372@nyu.edu
 * Date: 01/27/2012
 */

package lab1;

/* Class to store information of a single symbol. */
public class SymbolDefInfo {
  private int addr;
  private int module;
  private boolean isUsed;
  private String errMsg;

  public SymbolDefInfo(int addr) {
    this.addr = addr;
    this.isUsed = false;
    this.errMsg = "";
    this.module = 0;
  }

  public void setAddr(int addr) {
    this.addr = addr;
  }
  
  public int getAddr() {
    return addr;
  }
  
  public void setIsUsed(boolean isUsed) {
    this.isUsed = isUsed;
  }
  
  public boolean isUsed() {
    return isUsed;
  }

  public String getErrMsg() {
    return errMsg;
  }

  public void addErrMsg(String errMsg) {
    if(this.errMsg.length() > 0) {
      this.errMsg += " " + errMsg;
    } else {
      this.errMsg = errMsg;
    }
  }

  public void setModule(int module) {
    this.module = module;
  }

  public int getModule() {
    return module;
  }

  public boolean hasErrMsg() {
    if(errMsg.isEmpty()) {
      return false;
    } else {
      return true;
    }
  }

}
