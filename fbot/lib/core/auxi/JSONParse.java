package fbot.lib.core.auxi;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONParse
{
  public static JSONObject peel(JSONObject jo, String... keys)
  {
    JSONObject x = jo;
    String[] arrayOfString;
    int j = (arrayOfString = keys).length;
    for (int i = 0; i < j; i++)
    {
      String s = arrayOfString[i];
      x = x.getJSONObject(s);
    }
    return x;
  }
  
  public static String getIthString(JSONObject jo, int index)
  {
    Object result = getIth(jo, index);
    return (result instanceof String) ? (String)result : null;
  }
  
  public static JSONObject getIthJSONObject(JSONObject jo, int index)
  {
    Object result = getIth(jo, index);
    return (result instanceof JSONObject) ? (JSONObject)result : null;
  }
  
  public static int getIthInt(JSONObject jo, int index)
  {
    Object result = getIth(jo, index);
    return (result instanceof Integer) ? ((Integer)result).intValue() : -1;
  }
  
  public static JSONArray getIthJSONArray(JSONObject jo, int index)
  {
    Object result = getIth(jo, index);
    return (result instanceof JSONArray) ? (JSONArray)result : null;
  }
  
  public static int getIntR(JSONObject jo, String key)
  {
    Object result = getR(jo, key);
    return (result instanceof Integer) ? ((Integer)result).intValue() : -1;
  }
  
  public static String getStringR(JSONObject jo, String key)
  {
    Object result = getR(jo, key);
    return (result instanceof String) ? (String)result : null;
  }
  
  public static JSONObject getJSONObjectR(JSONObject jo, String key)
  {
    Object result = getR(jo, key);
    return (result instanceof JSONObject) ? (JSONObject)result : null;
  }
  
  public static JSONArray getJSONArrayR(JSONObject jo, String key)
  {
    Object result = getR(jo, key);
    return (result instanceof JSONArray) ? (JSONArray)result : null;
  }
  
  private static Object getIth(JSONObject jo, int index)
  {
    String[] x = JSONObject.getNames(jo);
    if ((x == null) || (x.length > index)) {
      return null;
    }
    return jo.get(x[index]);
  }
  
  private static Object getR(JSONObject jo, String key)
  {
    if (jo.has(key)) {
      return jo.get(key);
    }
    String[] x = JSONObject.getNames(jo);
    if (x == null) {
      return null;
    }
    String[] arrayOfString1;
    int j = (arrayOfString1 = x).length;
    for (int i = 0; i < j; i++)
    {
      String s = arrayOfString1[i];
      try
      {
        Object result = getR(jo.getJSONObject(s), key);
        if (result != null) {
          return result;
        }
      }
      catch (Throwable localThrowable) {}
    }
    return null;
  }
}
