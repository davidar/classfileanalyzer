/*
 * Signature.java
 *
 * @author Harald Roeder
 * @version 2008-02-18
 */


package classfileanalyzer.attributes;


import classfileanalyzer.*;
import java.util.*;
import static classfileanalyzer.Main.lt;





public class Signature {
    
    int[] Signature_attribute;
        int attribute_name_index;
        int attribute_length;
        int signature_index;
                
    ConstantPool cp;
    StringBuffer sourceText;
    
    
    
    
    
    public Signature(int[] Signature_attribute, ConstantPool cp) {
        this.Signature_attribute = Signature_attribute;
        this.cp = cp;
        sourceText = new StringBuffer();
        parse();
    }
    
    
    
    
    
    public void parse() {
        attribute_name_index = (Signature_attribute[0] << 8)  | 
                                Signature_attribute[1];
        attribute_length =     (Signature_attribute[2] << 24) | 
                               (Signature_attribute[3] << 16) |
                               (Signature_attribute[4] << 8)  | 
                                Signature_attribute[5];
        signature_index =      (Signature_attribute[6] << 8)  | 
                                Signature_attribute[7];
                      
        Vector<Integer> cpConstant = cp.getConstant(signature_index);
        String signature = Builder.getStringCONSTANT_Utf8(cpConstant);
        sourceText.append(".signature \"" + signature + "\"" + lt);
    }
    
    
    
    
    
    public StringBuffer getSourceText() {
        return sourceText;
    }    
        
    
}

