/*
 * EnclosingMethod.java
 *
 * @author Harald Roeder
 * @version 2008-02-18
 */


package classfileanalyzer.attributes;


import classfileanalyzer.*;
import java.util.*;
import static classfileanalyzer.Main.lt;





public class EnclosingMethod {
        
    int[] EnclosingMethod_attribute;
        int attribute_name_index;
        int attribute_length;
        int class_index;
        int method_index;
                        
    ConstantPool cp;
    StringBuffer sourceText;
    
    
    
    

    public EnclosingMethod(int[] EnclosingMethod_attribute, ConstantPool cp) {
        this.EnclosingMethod_attribute = EnclosingMethod_attribute;
        this.cp = cp;
        sourceText = new StringBuffer();
        parse();
    }
    
    
    
    
    
    public void parse() {
        attribute_name_index = (EnclosingMethod_attribute[0] << 8)  | 
                                EnclosingMethod_attribute[1];
        attribute_length =     (EnclosingMethod_attribute[2] << 24) | 
                               (EnclosingMethod_attribute[3] << 16) |
                               (EnclosingMethod_attribute[4] << 8)  | 
                                EnclosingMethod_attribute[5];
        class_index =          (EnclosingMethod_attribute[6] << 8)  | 
                                EnclosingMethod_attribute[7];
        method_index =         (EnclosingMethod_attribute[8] << 8)  | 
                                EnclosingMethod_attribute[9];
        
        Vector<Integer> cpConstant = new Vector<Integer>();
        
        if (method_index > 0) {
            cpConstant = cp.getConstant(class_index);
            String className = Builder.getClassNameCONSTANT_Class(cpConstant, cp);
            cpConstant = cp.getConstant(method_index);
            String methodName = Builder.getNameCONSTANT_NameAndType(cpConstant, cp);
            String methodType = Builder.getTypeCONSTANT_NameAndType(cpConstant, cp);

            sourceText.append(".enclosing method " + className + "/" +  methodName + methodType + lt);
            
        } else if (method_index == 0) {
            sourceText.append("; directive .enclosing method: method_index = 0" + lt);
        }
    }

    
    
    
    
    public StringBuffer getSourceText() {
        return sourceText;
    }
        
    
}

