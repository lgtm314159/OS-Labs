/* Author: Junyang Xin
 * Email: jx372@nyu.edu
 * Date: 01/27/2012
 */

package lab1;

public class LinkerRunner {
  public static void main(String[] args) {
    Linker linker = new Linker(args[0]);
    linker.printMemoryMap();
  }
}
