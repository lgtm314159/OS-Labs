/* Author: Junyang Xin
 * Email: jx372@nyu.edu
 * Date: 01/27/2012
 */

package lab1;

/* Class to store information of a single use. */
public class UseInfo {
  private String symbol;
  private String errMsg;

  public UseInfo(String symbol) {
    this.symbol = symbol;
    this.errMsg = "";
  }
  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public String getSymbol() {
    return symbol;
  }

  public void addErrMsg(String errMsg) {
    if(this.errMsg.length() > 0) {
      this.errMsg += " " + errMsg;
    } else {
      this.errMsg = errMsg;
    }
  }

  public String getErrMsg() {
    return errMsg;
  }

  public boolean hasErrMsg() {
    if(errMsg.isEmpty()) {
      return false;
    } else {
      return true;
    }
  }
}
