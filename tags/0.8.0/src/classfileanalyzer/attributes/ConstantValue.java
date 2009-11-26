/*
 * ConstantValue.java
 *
 * @author Harald Roeder
 * @version 2008-02-18
 */


package classfileanalyzer.attributes;


import classfileanalyzer.*;
import java.util.*;





public class ConstantValue implements Constants {
    
    int[] ConstantValue_attribute;
        int attribute_name_index;
        int attribute_length;
        int constantvalue_index;
        
    ConstantPool cp;
    StringBuffer sourceText;
    
    
    
    
    
    public ConstantValue(int[] ConstantValue_attribute, ConstantPool cp) {
        this.ConstantValue_attribute = ConstantValue_attribute;
        this.cp = cp;
        sourceText = new StringBuffer();
        parse();
    }
    
    
    
    
    
    public void parse() {
        attribute_name_index = (ConstantValue_attribute[0] << 8)  | 
                                ConstantValue_attribute[1];
        attribute_length =     (ConstantValue_attribute[2] << 24) | 
                               (ConstantValue_attribute[3] << 16) |
                               (ConstantValue_attribute[4] << 8)  | 
                                ConstantValue_attribute[5];
        constantvalue_index =  (ConstantValue_attribute[6] << 8)  | 
                                ConstantValue_attribute[7];
               
        Vector<Integer> cpConstant = cp.getConstant(constantvalue_index);
        
        if (cpConstant.get(0).intValue() == CONSTANT_Integer) {
            int iconst =  (cpConstant.get(1).intValue() << 24) |
                          (cpConstant.get(2).intValue() << 16) |
                          (cpConstant.get(3).intValue() << 8) |
                           cpConstant.get(4).intValue();
            sourceText.append(" = " + iconst);
        } 
        
        else if (cpConstant.get(0).intValue() == CONSTANT_Long) {
            long lconst = ((long)cpConstant.get(1).intValue() << 56) |
                          ((long)cpConstant.get(2).intValue() << 48) |
                          ((long)cpConstant.get(3).intValue() << 40) |
                          ((long)cpConstant.get(4).intValue() << 32) |
                          ((long)cpConstant.get(5).intValue() << 24) |
                          ((long)cpConstant.get(6).intValue() << 16) |
                          ((long)cpConstant.get(7).intValue() << 8) |
                           (long)cpConstant.get(8).intValue();
            sourceText.append(" = " + lconst);
        }
        
        else if (cpConstant.get(0).intValue() == CONSTANT_Float) {
            int bitsI = (cpConstant.get(1).intValue() << 24) |
                          (cpConstant.get(2).intValue() << 16) |
                          (cpConstant.get(3).intValue() << 8) |
                          cpConstant.get(4).intValue();
            float fconst = Float.intBitsToFloat(bitsI);
            sourceText.append(" = " + fconst);
        }
        
        else if (cpConstant.get(0).intValue() == CONSTANT_Double) {
            long bitsL = ((long)cpConstant.get(1).intValue() << 56) |
                            ((long)cpConstant.get(2).intValue() << 48) |
                            ((long)cpConstant.get(3).intValue() << 40) |
                            ((long)cpConstant.get(4).intValue() << 32) |
                            ((long)cpConstant.get(5).intValue() << 24) |
                            ((long)cpConstant.get(6).intValue() << 16) |
                            ((long)cpConstant.get(7).intValue() << 8) |
                             (long)cpConstant.get(8).intValue();
            double dconst = Double.longBitsToDouble(bitsL);
            sourceText.append(" = " + dconst);
        }
        
        else if (cpConstant.get(0).intValue() == CONSTANT_String) {
            int cpIndex = (cpConstant.get(1).intValue() << 8) | 
                           cpConstant.get(2).intValue();
            cpConstant = cp.getConstant(cpIndex);
            String sconst = Builder.getStringCONSTANT_Utf8(cpConstant);
            sourceText.append(" = \"" + sconst + "\""); 
        }
    }
    
    
    
    
    
    public StringBuffer getSourceText() {
        return sourceText;
    }
        
    
}

