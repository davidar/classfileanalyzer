/*
 * Exceptions.java
 *
 * @author Harald Roeder
 * @version 2008-02-18
 */


package classfileanalyzer.attributes;


import classfileanalyzer.*;
import java.util.*;
import static classfileanalyzer.Main.lt;





public class Exceptions {
    
    int[] Exceptions_attribute;
        int attribute_name_index;
        int attribute_length;
        int number_of_exceptions;
        int[] exception_index_table;
        
    ConstantPool cp;
    StringBuffer sourceText;
    
    
    
    
    
    public Exceptions(int[] Exceptions_attribute, ConstantPool cp) {
        this.Exceptions_attribute = Exceptions_attribute;
        this.cp = cp;
        sourceText = new StringBuffer();
        parse();
    }
    
    
    
    
    
    public void parse() {
        int i;
        
        attribute_name_index = (Exceptions_attribute[0] << 8)  | 
                                Exceptions_attribute[1];
        attribute_length =     (Exceptions_attribute[2] << 24) | 
                               (Exceptions_attribute[3] << 16) |
                               (Exceptions_attribute[4] << 8)  | 
                                Exceptions_attribute[5];
        number_of_exceptions = (Exceptions_attribute[6] << 8)  | 
                                Exceptions_attribute[7];
        
        exception_index_table = new int[number_of_exceptions * 2];
        
        for (i = 0; i < exception_index_table.length; i++) {
            exception_index_table[i] = Exceptions_attribute[8 + i];
        }
               
        for (i = 0; i < exception_index_table.length; i = i + 2) {
            int cpIndex = (exception_index_table[i] << 8) |
                           exception_index_table[i + 1];
            Vector<Integer> cpConstant = cp.getConstant(cpIndex);
            String className = Builder.getClassNameCONSTANT_Class(cpConstant, cp);
            sourceText.append("  .throws " + className + lt);    
        }
   }
    
    
    
    
    
    public StringBuffer getSourceText() {
        return sourceText;
    }
        
    
}

