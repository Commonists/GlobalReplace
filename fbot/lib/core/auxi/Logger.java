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
    HashMap<String, String> m = new HashMap();
    
    String[] tl = { "BLACK", "RED", "GREEN", "YELLOW", "BLUE", "PURPLE", "CYAN", "WHITE" };
    for (int i = 0; i < tl.length; i++) {
      m.put(tl[i], String.format("\033[3%dm%%s\033[0m", new Object[] { Integer.valueOf(i) }));
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
      ps.println(String.format((String)colors.get(code.toUpperCase()), new Object[] { s }));
    } else {
      ps.println(s);
    }
  }
  
  public static void info(String s)
  {
    log(s, "GREEN");
  }
  
  public static void warn(String s)
  {
    log(s, "YELLOW");
  }
  
  public static void error(String s)
  {
    log(s, "RED");
  }
  
  public static void fyi(String s)
  {
    log(s, "CYAN");
  }
}
