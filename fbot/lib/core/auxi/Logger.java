package fbot.lib.core.auxi;

import java.io.PrintStream;
import java.util.HashMap;

public class Logger
{
  private static boolean noColor = (System.getProperty("os.name").contains("Windows")) || 
    (System.getProperty("os.version").startsWith("10.6"));
  private static final HashMap<String, String> colors = init();
  private static PrintStream ps = System.out;
  
  private static HashMap<String, String> init()
  {
    HashMap<String, String> m = new HashMap<String, String>();
    
    String[] tl = { "BLACK", "RED", "GREEN", "YELLOW", "BLUE", "PURPLE", "CYAN", "WHITE" };
    for (int i = 0; i < tl.length; i++) {
      m.put(tl[i], String.format("\033[3%dm%%s\033[0m", i));
    }
    return m;
  }
  
  public static synchronized void setPrintStream(PrintStream out)
  {
    ps = out;
  }
  
  public static void log(String s, String code)
  {
    if (noColor) {
      ps.println(s);
    } else if (colors.containsKey(code.toUpperCase())) {
      ps.println(String.format( colors.get(code.toUpperCase()), s));
    } else {
      ps.println(s);
    }
  }
  /**
   * Log the string in green color
   * @param s the string to log
   */
  public static void info(String s)
  {
    log(s, "GREEN");
  }
  /**
   * Log the string in yellow color
   * @param s the string to log
   */
  public static void warn(String s)
  {
    log(s, "YELLOW");
  }
  /**
   * Log the string in red color
   * @param s the string to log
   */
  public static void error(String s)
  {
    log(s, "RED");
  }
  
  /**
   * Log the string in cyan color
   * @param s the string to log
   */
  public static void fyi(String s)
  {
    log(s, "CYAN");
  }
}
