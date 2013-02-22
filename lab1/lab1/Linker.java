/* Author: Junyang Xin
 * Email: jx372@nyu.edu
 * Date: 01/27/2012
 */

package lab1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/* Class which similates the operations of a linker. */
public class Linker {
  // Use "UTF-8" as the file encoding.
  private final String ENCODING = "UTF-8";
  // Limit of symbol length set to 32.
  private final int SYM_MAX_LEN = 32;
  private String file;
  // A map is chosen here to be the dictionary to store (symbol, address) pairs.
  // LinkedHashMap is chosen to preserve the order in which symbols are defined
  // in the data file as their order in the symbol table.
  private Map<String, SymbolDefInfo> symbolTable = new LinkedHashMap<String, SymbolDefInfo>();
  private Map<String, SymbolDefInfo> nonDefSymbolTable = new LinkedHashMap<String, SymbolDefInfo>();
  private List<String> ignoreErr = new ArrayList<String>();
  private Scanner sc;

  public Linker(String file) {
    this.file = file;
  }

  public void pass1() {
    try {
      FileInputStream input = new FileInputStream(file);
      sc = new Scanner(input, ENCODING);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(-1);
    }

    int counter = 0;
    int modCounter = 1;
    int base = 0;
    while(sc.hasNext()) {
      counter = sc.nextInt();
      // Parse the definition list in pass1.
      if(counter > 0) {
        for(int i = 0; i < counter; i++) {
          String symbol = sc.next();
          
          // Rule 1: if a symbol is multiply defined, set an error message and
          // use the value in the first definition. Here rule 1, 2, 3... refer
          // to the other requirements rules of lab 1, for clarity.
          if(!symbolTable.containsKey(symbol)) {
            int addr = base + Integer.parseInt(sc.next());
            SymbolDefInfo symDefInfo = new SymbolDefInfo(addr);
            symDefInfo.setModule(modCounter);
            symbolTable.put(symbol, symDefInfo);
          } else {
            // Skip the duplicated symbol and set a descriptive error message.
            sc.next();
            String errMsg = "Error: This variable is multiply defined; first value used.";
            symbolTable.get(symbol).addErrMsg(errMsg);
          }

          // Check to see if the symbol length exceeds the limit of 32. If it
          // does, an error message will be printed in the final output but no
          // more operation will be performed against this error.
          if(symbol.length() > SYM_MAX_LEN) {
            String errLength = "Error: The symbol length exceeds limit! Should contain no more than 32 characters.";
            symbolTable.get(symbol).addErrMsg(errLength);
          }
        }
      }
      // Skip the use list in pass1.
      counter = sc.nextInt();
      if(counter > 0) {
        for(int i = 0; i < counter; i++) {
          while(!sc.next().equals("-1")) {
            // Do nothing. Just skipping.
          }
        }
      }
      
      counter = sc.nextInt();
      // Rule 5. Check if the the address of a definition exceeds the size of module.
      checkDefAddr(counter, modCounter, base);

      base += counter;
      // Skip the text list in pass1.
      if(counter > 0) {
        for(int i = 0; i < counter * 2; i++) {
          // Do nothing. Just skipping.
          sc.next();
        }
      }

      modCounter++;
    }

  }

  public void pass2() {
    try {
      FileInputStream input = new FileInputStream(file);
      sc = new Scanner(input, ENCODING);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(-1);
    }

    int counter = 0;
    int modCounter = 1;
    int base = 0;
    while(sc.hasNext()) {
      counter = sc.nextInt();
      // Skip the definition list in pass 2.
      if(counter > 0) {
        for(int i = 0; i < counter * 2; i++) {
          // Do nothing. Just skipping.
          sc.next();
        }
      }

      // Parse the use list.
      counter = sc.nextInt();
      // Builds a hash map to store the (address of instruction, symbol) pairs
      // which are used later by the parsing of text list. The size of the hash
      // map is proportional to the size of a module's use list.
      Map<Integer, UseInfo> useList = new HashMap<Integer, UseInfo>();
      if(counter > 0) {
        for(int i = 0; i < counter; i++) {
          String symbol = sc.next();
          if(symbolTable.containsKey(symbol)) {
            symbolTable.get(symbol).setIsUsed(true);
          } else {
            // Rule 2. If a symbol is used but not defined, use the value zero
            // as its address.
            SymbolDefInfo symDefInfo = new SymbolDefInfo(0);
            symDefInfo.addErrMsg("Error: " + symbol + " is not defined; zero used.");
            nonDefSymbolTable.put(symbol, symDefInfo);
          }

          int addr = 0;
          while((addr = sc.nextInt()) != -1) {
            // Rule 4. If multiple variables used in one instruction, set an
            // corresponding error message.
            if(!useList.containsKey(new Integer(addr))) {
              UseInfo useInfo = new UseInfo(symbol);
              useList.put(new Integer(addr), useInfo);
            } else {
              useList.get(new Integer(addr)).addErrMsg(
                  "Error: Multiple variables used in instruction; " +
                  "all but first ignored.");
            }
          }

        }
      }

      counter = sc.nextInt();
      // Rule 6.
      checkUseAddr(counter, modCounter, useList);

      // Parse the text list.
      if(counter > 0) {
        for(int i = 0; i < counter; i++) {
          String type = sc.next();
          String addr = sc.next();
          int num = i + base;
          if(type.equals("A")) {
            // Rule 7. Absolute address exceeds the size of machine.
            if(Integer.parseInt(addr.substring(1, 4)) <= 200) {
              //System.out.println(num + ": " + addr);
              
              System.out.format("%-5s", num + ":");
              System.out.println(addr);
            } else {
              //System.out.println(num + ": " + addr.substring(0, 1) + "000 Error" +
              //		": Absolute address exceeds machine size; zero used.");
              System.out.format("%-5s", num + ":");
              System.out.println(addr.substring(0, 1) + "000 Error" + ": " +
              		"Absolute address exceeds machine size; " + "zero used.");
            }
          } else if(type.equals("I")) {
            //System.out.println(num + ": " + addr);
            System.out.format("%-5s", num + ":");
            System.out.println(addr);
          } else if(type.equals("R")) {
            // Rule 8. Relative address exceeds the size of module.
            if(Integer.parseInt(addr.substring(1, 4)) >= counter) {
              //System.out.println(num + ": " + addr.substring(0, 1) + "000 Error: Relative address exceeds module size; zero used.");
              System.out.format("%-5s", num + ":");
              System.out.println(addr.substring(0, 1) +  "000 Error: " +
              		"Relative address exceeds module size; " + "zero used.");
            } else {
              //System.out.println(num + ": " + (Integer.parseInt(addr) + base));
              System.out.format("%-5s", num + ":");
              System.out.println((Integer.parseInt(addr) + base));
            }
          } else if(type.equals("E")) {
            UseInfo useInfo = useList.get(new Integer(i));
            String symbol = useInfo.getSymbol();
            String str = "";
            // If the symbol in use is defined.
            if(symbolTable.containsKey(symbol)) {
              str = addr.substring(0, 1);
              int symbolAddr = symbolTable.get(symbol).getAddr();
              if(symbolAddr < 10) {
                str += "00" + symbolAddr;
              } else if(symbolAddr < 100) {
                str += "0" + symbolAddr;
              } else {
                str += symbolAddr;
              }
            } else {
              // If the symbol in use is not defined, append the
              // corresponding error message and use zero as the address.
              str += addr.substring(0, 1) + "000 ";
              str += nonDefSymbolTable.get(symbol).getErrMsg();
            }
            if(useInfo.hasErrMsg()) {
              str += " " + useInfo.getErrMsg();
            }
            //System.out.println(str);
            System.out.format("%-5s", num + ":");
            System.out.println(str);
          }
        }
      }
      base += counter;
      modCounter++;
    }
  }

  // Rule 5. If a definition address exceeds the size of module, set a
  // descriptive error message and treat the address as 0 (relative).
  public void checkDefAddr(int moduleSize, int module, int base) {
    Iterator<Map.Entry<String, SymbolDefInfo>> it = symbolTable.entrySet().iterator();
    while(it.hasNext()) {
      Map.Entry<String, SymbolDefInfo> entry = it.next();
      if(entry.getValue().getModule() == module) {
        if((entry.getValue().getAddr() - base) >= moduleSize) {
          entry.getValue().setAddr(base);
          entry.getValue().addErrMsg("Error: Definition exceeds module size; " +
          		"zero used.");
        }
      }
    }
  }

  // Rule 6.
  public void checkUseAddr(int moduleSize, int module, Map<Integer,
      UseInfo> useList) {
    Iterator<Integer> it = useList.keySet().iterator();
    while(it.hasNext()) {
      Integer addr = it.next();
      if(addr >= moduleSize) {
        ignoreErr.add("Error: Use of " + useList.get(addr).getSymbol() +
            " in module " + module + " exceeds module size; use ignored.");
      }
    }
  }

  // Rule 3. Warning message for defined but not used symbol.
  public void printWarning() {
    Iterator<Map.Entry<String, SymbolDefInfo>> it =
        symbolTable.entrySet().iterator();
    while(it.hasNext()) {
      Map.Entry<String, SymbolDefInfo> entry = it.next();
      if(!entry.getValue().isUsed()) {
        System.out.println("Warning: " + entry.getKey() +
            " was defined in module " + entry.getValue().getModule() +
            " but never used.");
      }
    }
  }

  // Method to print the symbol table.
  public void printSymbolTable() {
    System.out.println("Symbol Table");
    Iterator<Map.Entry<String, SymbolDefInfo>> it =
        symbolTable.entrySet().iterator();
    while(it.hasNext()) {
      Map.Entry<String, SymbolDefInfo> entry = it.next();
      String str = entry.getKey() + "=" + entry.getValue().getAddr();
      if(entry.getValue().hasErrMsg()) {
        str += " " + entry.getValue().getErrMsg();
      }
      System.out.println(str);
    }
  }

  public void printIgnoreErr() {
    for(String str: ignoreErr) {
      System.out.println(str);
    }
  }

  // Method to be called to produce the whole memory map.
  public void printMemoryMap() {
    pass1();
    printSymbolTable();
    System.out.println();

    System.out.println("Memory Map");
    pass2();
    System.out.println();

    printIgnoreErr();
    System.out.println();

    printWarning();
  }
}
