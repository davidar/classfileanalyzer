/*
 * SourceFile.java
 *
 * @author Harald Roeder
 * @version 2008-02-18
 */


package classfileanalyzer.attributes;


import classfileanalyzer.*;
import java.util.*;
import static classfileanalyzer.Main.lt;





public class SourceFile {
    
    int[] SourceFile_attribute;
        int attribute_name_index;
        int attribute_length;
        int sourcefile_index;
                
    ConstantPool cp;
    StringBuffer sourceText;
    
    
    
    
    
    public SourceFile(int[] SourceFile_attribute, ConstantPool cp) {
        this.SourceFile_attribute = SourceFile_attribute;
        this.cp = cp;
        sourceText = new StringBuffer();
        parse();
    }
    
    
    
    
    
    public void parse() {
        attribute_name_index = (SourceFile_attribute[0] << 8)  | 
                                SourceFile_attribute[1];
        attribute_length =     (SourceFile_attribute[2] << 24) | 
                               (SourceFile_attribute[3] << 16) |
                               (SourceFile_attribute[4] << 8)  | 
                                SourceFile_attribute[5];
        sourcefile_index =     (SourceFile_attribute[6] << 8)  | 
                                SourceFile_attribute[7];
                      
        Vector<Integer> cpConstant = cp.getConstant(sourcefile_index);
        String className = Builder.getStringCONSTANT_Utf8(cpConstant);
        sourceText.append(".source " + className + lt);    
    }
    
    
    
    
    
    public StringBuffer getSourceText() {
        return sourceText;
    }
    
    
}


