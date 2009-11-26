/*
 * ConstantPool.java
 *
 * @author Harald Roeder
 * @version 2008-02-17
 */


package classfileanalyzer;


import java.util.*;
import static classfileanalyzer.Main.lt;





public class ConstantPool {
    
    int[] classBytes;
    int constantPoolCount;
    
    Vector<Vector<Integer>> constants = new Vector<Vector<Integer>>();
    StringBuffer buffer = new StringBuffer();
    
    
    
    
    
    public ConstantPool(int[] classBytes, int constantPoolCount) {
      this.classBytes = classBytes;
      this.constantPoolCount = constantPoolCount;
      this.getConstants();
    }
     
    
    
    
    
    public Vector<Vector<Integer>> getConstants() {
        Vector<Integer> constant = new Vector<Integer>();
        constant.add(new Integer(-1)); // not needed
        constants.add(constant); 
        
        int i;
        int pointerCp = 10;  // first constant: byte 10
                
               
        for (int constantNumber = 1; constantNumber <= constantPoolCount; constantNumber++) {
            constant = new Vector<Integer>();
                    
            switch (classBytes[pointerCp]) {
                // CONSTANT_Class, 3 bytes
                case 0x07: constant.add(new Integer(7));
                           constant.add(new Integer(classBytes[pointerCp + 1]));
                           constant.add(new Integer(classBytes[pointerCp + 2]));
                           constants.add(constant);
                           buffer.append(constantNumber + ". CONSTANT_Class: class #" + 
                               ((classBytes[pointerCp + 1] << 8) | classBytes[pointerCp + 2]) + 
                               lt);
                           pointerCp = pointerCp + 3;
                           break;
                // CONSTANT_Fieldref, 5 bytes
                case 0x09: constant.add(new Integer(9));
                           constant.add(new Integer(classBytes[pointerCp + 1]));
                           constant.add(new Integer(classBytes[pointerCp + 2]));
                           constant.add(new Integer(classBytes[pointerCp + 3]));
                           constant.add(new Integer(classBytes[pointerCp + 4]));
                           constants.add(constant);
                           buffer.append(constantNumber + ". CONSTANT_Fieldref: " +
                               "field of class #" + 
                               ((classBytes[pointerCp + 1] << 8) | classBytes[pointerCp + 2]) + 
                               ", name/type #" + 
                               ((classBytes[pointerCp + 3] << 8) | classBytes[pointerCp + 4]) + 
                               lt);
                           pointerCp = pointerCp + 5;
                           break; 
                // CONSTANT_Methodref, 5 bytes
                case 0x0a: constant.add(new Integer(10));
                           constant.add(new Integer(classBytes[pointerCp + 1]));
                           constant.add(new Integer(classBytes[pointerCp + 2]));
                           constant.add(new Integer(classBytes[pointerCp + 3]));
                           constant.add(new Integer(classBytes[pointerCp + 4]));
                           constants.add(constant);
                           buffer.append(constantNumber + ". CONSTANT_Methodref: " +
                               "method of class #" + 
                               ((classBytes[pointerCp + 1] << 8) | classBytes[pointerCp + 2]) + 
                               ", name/type #" + 
                               ((classBytes[pointerCp + 3] << 8) | classBytes[pointerCp + 4]) + 
                               lt);
                           pointerCp = pointerCp + 5;
                           break;
                // CONSTANT_InterfaceMethodref, 5 bytes
                case 0x0b: constant.add(new Integer(11));
                           constant.add(new Integer(classBytes[pointerCp + 1]));
                           constant.add(new Integer(classBytes[pointerCp + 2]));
                           constant.add(new Integer(classBytes[pointerCp + 3]));
                           constant.add(new Integer(classBytes[pointerCp + 4]));
                           constants.add(constant);
                           buffer.append(constantNumber + ". CONSTANT_InterfaceMethodref: " +
                               "interface method of class #" + 
                               ((classBytes[pointerCp + 1] << 8) | classBytes[pointerCp + 2]) + 
                               ", name/type #" + 
                               ((classBytes[pointerCp + 3] << 8) | classBytes[pointerCp + 4])  + 
                               lt);
                           pointerCp = pointerCp + 5;
                           break;
                // CONSTANT_String, 3 bytes
                case 0x08: constant.add(new Integer(8));
                           constant.add(new Integer(classBytes[pointerCp + 1]));
                           constant.add(new Integer(classBytes[pointerCp + 2]));
                           constants.add(constant);
                           buffer.append(constantNumber + ". CONSTANT_String: " + 
                               "String #" + 
                               ((classBytes[pointerCp + 1] << 8) | classBytes[pointerCp + 2]) + 
                               lt);
                           pointerCp = pointerCp + 3;
                           break;
                // CONSTANT_Integer, 5 bytes
                case 0x03: constant.add(new Integer(3));
                           constant.add(new Integer(classBytes[pointerCp + 1]));
                           constant.add(new Integer(classBytes[pointerCp + 2]));
                           constant.add(new Integer(classBytes[pointerCp + 3]));
                           constant.add(new Integer(classBytes[pointerCp + 4]));
                           constants.add(constant);
                           int integer = ((classBytes[pointerCp + 1] << 24) | 
                                          (classBytes[pointerCp + 2] << 16) |
                                          (classBytes[pointerCp + 3] << 8) | 
                                           classBytes[pointerCp + 4]);
                           buffer.append(constantNumber + ". CONSTANT_Integer: " + integer + lt);
                           pointerCp = pointerCp + 5;
                           break;
                // CONSTANT_Float, 5 bytes
                case 0x04: constant.add(new Integer(4));
                           constant.add(new Integer(classBytes[pointerCp + 1]));
                           constant.add(new Integer(classBytes[pointerCp + 2]));
                           constant.add(new Integer(classBytes[pointerCp + 3]));
                           constant.add(new Integer(classBytes[pointerCp + 4]));
                           constants.add(constant);
                           int bitsI = (classBytes[pointerCp + 1] << 24) | 
                                       (classBytes[pointerCp + 2] << 16) |
                                       (classBytes[pointerCp + 3] << 8) | 
                                        classBytes[pointerCp + 4];
                           float f = Float.intBitsToFloat(bitsI);
                           buffer.append(constantNumber + ". CONSTANT_Float: " + f  + lt);
                           pointerCp = pointerCp + 5;
                           break;
                // CONSTANT_Long, 9 bytes           
                case 0x05: constant.add(new Integer(5));
                           constant.add(new Integer(classBytes[pointerCp + 1]));
                           constant.add(new Integer(classBytes[pointerCp + 2]));
                           constant.add(new Integer(classBytes[pointerCp + 3]));
                           constant.add(new Integer(classBytes[pointerCp + 4]));
                           constant.add(new Integer(classBytes[pointerCp + 5]));
                           constant.add(new Integer(classBytes[pointerCp + 6]));
                           constant.add(new Integer(classBytes[pointerCp + 7]));
                           constant.add(new Integer(classBytes[pointerCp + 8]));
                           constants.add(constant);
                           long l = ((long)classBytes[pointerCp + 1] << 56) | 
                                    ((long)classBytes[pointerCp + 2] << 48) | 
                                    ((long)classBytes[pointerCp + 3] << 40) | 
                                    ((long)classBytes[pointerCp + 4] << 32) |
                                    ((long)classBytes[pointerCp + 5] << 24) | 
                                    ((long)classBytes[pointerCp + 6] << 16) | 
                                    ((long)classBytes[pointerCp + 7] << 8) | 
                                     (long)classBytes[pointerCp + 8];
                           buffer.append(constantNumber + ". CONSTANT_Long: " + l  + lt);
                           
                           constant = new Vector<Integer>();
                           constant.add(new Integer(-1)); // 2 constants for CONSTANT_Long
                           constants.add(constant); 
                           
                           constantNumber = constantNumber + 1;
                           
                           buffer.append(constantNumber + ". constant: constants of type " +
                                   "CONSTANT_Long take up two entries in the constant_pool table." +lt);
                           pointerCp = pointerCp + 9;
                           break;
                // CONSTANT_Double, 9 bytes
                case 0x06: constant.add(new Integer(6));
                           constant.add(new Integer(classBytes[pointerCp + 1]));
                           constant.add(new Integer(classBytes[pointerCp + 2]));
                           constant.add(new Integer(classBytes[pointerCp + 3]));
                           constant.add(new Integer(classBytes[pointerCp + 4]));
                           constant.add(new Integer(classBytes[pointerCp + 5]));
                           constant.add(new Integer(classBytes[pointerCp + 6]));
                           constant.add(new Integer(classBytes[pointerCp + 7]));
                           constant.add(new Integer(classBytes[pointerCp + 8]));
                           constants.add(constant);
                           long bitsL = ((long)classBytes[pointerCp + 1] << 56) | 
                                        ((long)classBytes[pointerCp + 2] << 48) | 
                                        ((long)classBytes[pointerCp + 3] << 40) | 
                                        ((long)classBytes[pointerCp + 4] << 32) |
                                        ((long)classBytes[pointerCp + 5] << 24) | 
                                        ((long)classBytes[pointerCp + 6] << 16) | 
                                        ((long)classBytes[pointerCp + 7] << 8) | 
                                         (long)classBytes[pointerCp + 8];
                           double d = Double.longBitsToDouble(bitsL);
                           buffer.append(constantNumber + ". CONSTANT_Double: " + d + lt);
                           
                           constant = new Vector<Integer>();
                           constant.add(new Integer(-1)); // 2 constants for CONSTANT_Double
                           constants.add(constant); 
                           
                           constantNumber = constantNumber + 1;
                           
                           buffer.append(constantNumber + ". constant: constants of type " +
                                   "CONSTANT_Double take up two entries in the constant_pool table." + lt);
                           pointerCp = pointerCp + 9;
                           break;
                // CONSTANT_NameAndType, 5 bytes
                case 0x0c: constant.add(new Integer(12));
                           constant.add(new Integer(classBytes[pointerCp + 1]));
                           constant.add(new Integer(classBytes[pointerCp + 2]));
                           constant.add(new Integer(classBytes[pointerCp + 3]));
                           constant.add(new Integer(classBytes[pointerCp + 4]));
                           constants.add(constant);
                           buffer.append(constantNumber + ". CONSTANT_NameAndType: " +
                               "name #" + 
                               ((classBytes[pointerCp + 1] << 8) | classBytes[pointerCp + 2]) + 
                               ", type #" + 
                               ((classBytes[pointerCp + 3] << 8) | classBytes[pointerCp + 4]) + 
                               lt);
                           pointerCp = pointerCp + 5;
                           break;
                // CONSTANT_Utf8, 2 bytes + utf8Length bytes
                case 0x01: int utf8Length = (classBytes[pointerCp + 1] << 8) | 
                                             classBytes[pointerCp + 2];
                           constant.add(new Integer(1));
                           constant.add(new Integer(classBytes[pointerCp + 1]));
                           constant.add(new Integer(classBytes[pointerCp + 2]));
                           for (i = 0; i < utf8Length; i++) {
                               constant.add(new Integer(classBytes[pointerCp + 3 + i]));
                           }
                           constants.add(constant);
                                                      
                           buffer.append(constantNumber + ". CONSTANT_Utf8: ");
                           String s = "";
                           for (i = 1; i <= utf8Length; i++) {
                               s = s + (char)classBytes[pointerCp + 2 + i]; 
                           }
                           s = s.replace("\\", "\\\\");
                           s = s.replace("\b", "\\b");
                           s = s.replace("\t", "\\t");
                           s = s.replace("\n", "\\n");
                           s = s.replace("\f", "\\f");
                           s = s.replace("\r", "\\r");
                           s = s.replace("\"", "\\\"");
                           s = s.replace("\'", "\\\'");
                           buffer.append(s);
                           buffer.append(lt);
                           
                           pointerCp = pointerCp + 3 + utf8Length;
                           break;
            }             
        }
               
        ClassFile.accessFlagsBegin = pointerCp;
        ClassFile.interfacesCountBegin = pointerCp + 6;
        return constants;
    }  
        
    
    
    
    
    public Vector<Integer> getConstant(int index) {
        return constants.get(index);
    }
    
    
    
    
    
    public StringBuffer getConstantPoolBuffer() {
        return buffer;
    }
            
    
}

