package org.mocktail.aspectpoc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author : Pokidov.Dmitry
 * @date: 27.05.2008
 * <p>* Class that help to test equals for two object.
 * For test use {@code weakEquals()} method.
 */
public class WeakEqualsHelper {
 private static ArrayList<String> errorLog = new ArrayList<String>();
 private static Map<String, List<String>> fields;

 private enum CheckNullsResult {
  BOTH_NULLS, NOT_NULLS, ONE_NULL;
 }

 /**
  * Recursively compare object by its class and all superclasses.
  * To standalone test collections, arrays and maps use methods: 
  * {@code equalsCollection(), equalsArray(), equalsMap()}
  *
  * @param o1 First object for compare
  * @param o2 Second object for compare
  * @return {@code true}, if objects are equals, else {@code false}
  */
 public static boolean weakEquals(Object o1, Object o2) {
  return weakEquals(o1, o2, null);
 }

 /**
  * Recursively compare object by its class and all superclasses.
  * To standalone test collections, arrays and maps use methods: 
  * {@code equalsCollection(), equalsArray(), equalsMap()}
  *
  * @param o1 First object for compare
  * @param o2 Second object for compare
  * @param notEqualsFields fields that will be ignore. Key is name of class,   Value - list of field's name.
  * @return {@code true}, if objects are equals, else {@code false}
  */
 public static boolean weakEquals(Object o1, Object o2, Map<String, List<String>> notEqualsFields) {
  fields = notEqualsFields;
  boolean ret = doEquals(o1, o2);
  if (!ret) {
   for (String error : errorLog) {
    System.out.println(error);
   }
  }
  return ret;
 }

 public boolean weakEquals(Object o) {
  if (o == null ||
    !(o instanceof WeakEqualsHelper)) {
   return false;
  }
  return o == this || doEquals(this, o);
 }

 private static boolean needEqual(Field f, Map<String, List<String>> fields) {
  if (fields != null) {
   List<String> classFields = fields.get(f.getDeclaringClass().getName());
   return !(classFields != null && classFields.contains(f.getName()));
  }

  return true;
 }

 public static boolean doEquals(Object obj1, Object obj2) {
  if (obj1 == obj2) {
   return true;
  }
  Class c1 = obj1.getClass();
  Class c2 = obj2.getClass();
  if (c1.getName().compareTo(c2.getName()) == 0) {

   while (!c1.getName().equals(Object.class.getName())) {
    for (Field f1 : c1.getDeclaredFields()) {
     if (needEqual(f1, fields)) {
      String errorMessage = "ERROR: Class " + obj1.getClass().getName() + " are not equal in field -->" + f1.getName() + "<--";
      if (!f1.getName().contains("this$")) {
       try {
        Field f2 = c2.getDeclaredField(f1.getName());

        f1.setAccessible(true);
        f2.setAccessible(true);

        errorLog.add("Current field-->" + f1.getName() + "<--");

        if (f1.isEnumConstant()) {
         if (!f1.get(obj1).equals(f2.get(obj2))) {
          return false;
         }
        } else if (!doObjectEquals(f1.get(obj1), f2.get(obj2))) {
         errorLog.add(errorMessage + "[" + (f1.get(obj1) != null && f1.get(obj1).equals("null") ? "NULL" : f1.get(obj1)) + ":" + (f2.get(obj2) != null && f2.get(obj2).equals("null") ? "NULL" : f2.get(obj2)) + "]");
         return false;
        }
       } catch (NoSuchFieldException nsme) {
        errorLog.add("ERROR: No such field " + f1.getName());
        return false;
       } catch (IllegalAccessException e) {
        e.printStackTrace();
        return false;
       }
      } else {
       errorLog.add("INFO: ignore field -->" + f1.getName() + "<--");
      }
     } else {
      System.out.println("INFO: ignore field -->" + f1.getName() + "<--");
     }
    }

    c1 = c1.getSuperclass();
    c2 = c2.getSuperclass();
    obj1 = c1.cast(obj1);
    obj2 = c2.cast(obj2);
   }
  } else {
   errorLog.add("Class names are not equals in field[" + c1.getName() + "," + c2.getName() + "]");
   return false;
  }
  return true;
 }

 private static boolean doObjectEquals(Object o1, Object o2) {
  if (checkNulls(o1, o2) == CheckNullsResult.NOT_NULLS) {
   if (o1 instanceof Map.Entry) {
    Map.Entry e1 = (Map.Entry) o1;
    Map.Entry e2 = (Map.Entry) o2;
    return doObjectEquals(e1.getKey(), e2.getKey()) &&
      doObjectEquals(e1.getValue(), e2.getValue());
   } else if (o1 instanceof Map) {
    return equalsMap((Map) o1, (Map) o2);
   } else if (o1 instanceof Collection) {
    return equalsCollection((Collection) o1, (Collection) o2);
   } else if (o1.getClass().isArray()) {
    return equalsArray((Object[]) o1, (Object[]) o2);
   } else if (!o1.getClass().getName().equals(Object.class.getName())) {
    if (hasEquals(o1.getClass())) {
     if (!o1.equals(o2)) {
      if (o1 instanceof String &&
        o2 instanceof String) {
       String s1 = ((String) o1).replaceAll("[\n\t\r ]", "");
       String s2 = ((String) o2).replaceAll("[\n\t\r ]", "");
       if (!s1.equals(s2)) {
        return false;
       } else {
        System.out.println("WARNING: Strings are not equals with escape symbols and whitespaces");
        return true;
       }
      }
      System.out.println("Equals return false");
      return false;
     }

     return true;
    } else {
     return doEquals(o1, o2);
    }
   }
   System.out.println("UNKNOWN TYPE: " + o1.getClass().getName());
  }

  return equalsForNull(o1, o2);
 }

 private static CheckNullsResult checkNulls(Object object, Object otherObject) {
  if (object == null && otherObject == null) {
   return CheckNullsResult.BOTH_NULLS;
  }
  if (object != null && otherObject != null) {
   return CheckNullsResult.NOT_NULLS;
  }

  return CheckNullsResult.ONE_NULL;
 }

 private static boolean equalsForNull(Object object, Object otherObject) {
  return object == null && otherObject == null;
 }

 public static boolean equalsMap(Map map, Map otherMap) throws NullPointerException {
  return equalsCollection(map.entrySet(), otherMap.entrySet());
 }

 public static boolean equalsCollection(Collection collection, Collection otherCollection)
   throws NullPointerException {
  return equalsArray(collection.toArray(), otherCollection.toArray());
 }

 public static boolean equalsArray(Object[] array, Object[] otherArray)
   throws NullPointerException {
  if (array.length != otherArray.length) {
   errorLog.add("Array or Collection length are not equals[" + array.length + "," + otherArray.length + "]");
   return false;
  }

  ArrayList<Integer> usedIndexes = new ArrayList<Integer>();
  for (int i = 0; i < array.length; i++) {
   Object currObject = array[i];
   boolean found = false;
   for (int k = 0; k < otherArray.length; k++) {
    if (!usedIndexes.contains(k)) {
     found = doObjectEquals(currObject, otherArray[k]);
     if (found) {
      usedIndexes.add(k);
      break;
     }
    }
   }
   if (!found) {
    return false;
   }
  }

  return true;
 }

 private static boolean hasEquals(Class c) {
  Method[] methods = c.getDeclaredMethods();
  for (Method m : methods) {
   if (m.getName().equals("equals") &&
     m.getParameterTypes().length == 1) {
    return true;
   }
  }
  return false;
 }

        /*------------TEST------------*/
 public class TestClass {
  public class TestNestClass extends TestClass {
   public TestNestClass(int a, int b, ArrayList list) {
    super(a, b, list);
   }

   public void setD(int d) {
    this.d = d;
   }

   public void putMap(String s, Integer i) {
    map.put(s, i);
   }

   private int d;
   private Map<String, Integer> map = new HashMap<String, Integer>();
  }

  private int a;
  private int b;
  private ArrayList list = new ArrayList();
  private String c = "Hello!!!";

  public TestClass(int a, int b, ArrayList list) {
   this.a = a;
   this.b = b;
   this.list = list;
  }

  public int getA() {
   return a;
  }

  public int getB() {
   return b;
  }

  public String getC() {
   return c;
  }

  public void setC(String c) {
   this.c = c;
  }

  public ArrayList getList() {
   return list;
  }

  public TestNestClass createNest() {
   return new TestNestClass(a, b, list);
  }
 }

 public TestClass createTestClass(int a, int b, ArrayList list) {
  return new TestClass(a, b, list);
 }

 public static void main(String[] args) {
  ArrayList ar1 = new ArrayList();
  ar1.add(1);
  ar1.add(5);
  ar1.add(7);
  ArrayList ar2 = new ArrayList();
  ar2.add(1);
  ar2.add(5);
  ar2.add(7);
  WeakEqualsHelper eh = new WeakEqualsHelper();
  TestClass tc1 = eh.createTestClass(1, 15, ar1);
  TestClass tc2 = eh.createTestClass(1, 15, ar2);
  TestClass.TestNestClass tnc = tc1.createNest();
  TestClass.TestNestClass tnc2 = tc2.createNest();
  tnc.putMap("Hello", 1);
  tnc2.putMap("Hello", 1);
  tnc.putMap("goodbye", 1);
  tnc2.putMap("goodbye", 1);
  System.out.println("tc1.equals(tc2) == " + WeakEqualsHelper.doEquals(tnc, tnc2));

 }
}