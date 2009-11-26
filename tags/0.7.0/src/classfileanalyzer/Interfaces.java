/*
 * Interfaces.java
 *
 * @author Harald Roeder
 * @version 2008-02-17
 */


package classfileanalyzer;


import java.util.*;





public class Interfaces {
    
    int[] classBytes;
    int interfacesCount;
    int[] interfaces;
    
    int interfacesCountBegin;
    
    
    
    
    
    public Interfaces(int[] classBytes, int interfacesCount) {
      this.classBytes = classBytes;
      this.interfacesCount = interfacesCount;
      this.interfacesCountBegin = ClassFile.interfacesCountBegin;
      ClassFile.fieldsCountBegin = ClassFile.interfacesCountBegin + 2 + (2 * interfacesCount);
      parse();
    }
    
    
    
    
    
    private void parse() {
        interfaces = new int[interfacesCount * 2];
        for (int i = 0; i < interfacesCount; i++) {
            interfaces[i * 2] = classBytes[interfacesCountBegin + 2 + (i * 2)];
            interfaces[(i * 2) + 1] = classBytes[interfacesCountBegin + 2 + (i * 2) + 1];
        }
    }
    
    
    
    
    
    public int getInterfacesCount() {
        return interfacesCount;
    }
    
    
    
    
    
    public int[] getInterfaces() {
        return interfaces;
    }
        
    
}

