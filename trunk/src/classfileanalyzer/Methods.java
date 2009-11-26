/*
 * Methods.java
 *
 * @author Harald Roeder
 * @version 2008-02-17
 */


package classfileanalyzer;


import java.util.*;





public class Methods {
    
    int[] classBytes;
    int methodsCount;
    int[] method_info;
        
    Vector<Vector<Integer>> methods = new Vector<Vector<Integer>>();
    int methodsCountBegin;
        
    
    
    
    
    public Methods(int[] classBytes, int methodsCount) {
      this.classBytes = classBytes;
      this.methodsCount = methodsCount;
      this.methodsCountBegin = ClassFile.methodsCountBegin;
      parse();
    }
    
    
       
    
    
    private void parse() {
        int pointerMethods = methodsCountBegin + 2;
                
        for (int methodNumber = 0; methodNumber < methodsCount; methodNumber++) {
            Vector<Integer> method = new Vector<Integer>();
            method.add(new Integer(classBytes[pointerMethods]));
            method.add(new Integer(classBytes[pointerMethods + 1]));
            method.add(new Integer(classBytes[pointerMethods + 2]));
            method.add(new Integer(classBytes[pointerMethods + 3]));
            method.add(new Integer(classBytes[pointerMethods + 4]));
            method.add(new Integer(classBytes[pointerMethods + 5]));
            method.add(new Integer(classBytes[pointerMethods + 6]));
            method.add(new Integer(classBytes[pointerMethods + 7]));
                        
            int methodAttributesCount = ((classBytes[pointerMethods + 6] << 8) | 
                                          classBytes[pointerMethods + 7]);
            
            pointerMethods = pointerMethods + 8;
                        
            for (int i = 0; i < methodAttributesCount; i++) {
                method.add(new Integer(classBytes[pointerMethods]));
                method.add(new Integer(classBytes[pointerMethods + 1]));
                method.add(new Integer(classBytes[pointerMethods + 2]));
                method.add(new Integer(classBytes[pointerMethods + 3]));
                method.add(new Integer(classBytes[pointerMethods + 4]));
                method.add(new Integer(classBytes[pointerMethods + 5]));
                                
                int methodAttributeLength = ((classBytes[pointerMethods + 2] << 24) | 
                                             (classBytes[pointerMethods + 3] << 16) |
                                             (classBytes[pointerMethods + 4] << 8) | 
                                              classBytes[pointerMethods + 5]);
                
                pointerMethods = pointerMethods + 6;
                                                
                for (int j = 0; j < methodAttributeLength; j++) {
                    method.add(new Integer(classBytes[pointerMethods]));
                    pointerMethods++;
                }
            }
            methods.add(method);
        }
        ClassFile.attributesCountBegin = pointerMethods;
    }
    
    
    
    
    
    public int getMethodsCount() {
        return methodsCount;
    }
    
        
    
    
    
    public int[] getMethod(int index) {
        Vector<Integer> method = methods.get(index);
        int methodInfoSize = method.size();
        method_info = new int[methodInfoSize];
        for (int i = 0; i < methodInfoSize; i++) {
            method_info[i] = method.get(i).intValue();
        }
        return method_info;
    }
       
    
    
    
      
    public Vector<Vector<Integer>> getMethods() {
        return methods;
    }
        
    
}

