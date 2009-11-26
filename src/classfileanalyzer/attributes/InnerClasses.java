/*
 * InnerClasses.java
 *
 * @author Harald Roeder
 * @version 2008-02-18
 */


package classfileanalyzer.attributes;


import classfileanalyzer.*;
import java.util.*;
import static classfileanalyzer.Main.lt;





public class InnerClasses implements Constants {
       
    int[] InnerClasses_attribute;
        int attribute_name_index;
        int attribute_length;
        int number_of_classes;
        int[] classes;
                
    ConstantPool cp;
    StringBuffer sourceText;
    
    
    
    
    
    public InnerClasses(int[] InnerClasses_attribute, ConstantPool cp) {
        this.InnerClasses_attribute = InnerClasses_attribute;
        this.cp = cp;
        sourceText = new StringBuffer();
        parse();
    }
        
    
    
    
    
    public void parse() {
        Vector<Integer> cpConstant;
        
        attribute_name_index = (InnerClasses_attribute[0] << 8)  | 
                                InnerClasses_attribute[1];
        attribute_length =     (InnerClasses_attribute[2] << 24) | 
                               (InnerClasses_attribute[3] << 16) |
                               (InnerClasses_attribute[4] << 8)  | 
                                InnerClasses_attribute[5];
        number_of_classes =    (InnerClasses_attribute[6] << 8)  | 
                                InnerClasses_attribute[7];
        
        classes = new int[number_of_classes * 8];
        
        for (int i = 0; i < classes.length; i++) {
            classes[i] = InnerClasses_attribute[8 + i];
        }
               
        for (int i = 0; i < classes.length; i = i + 8) {
            int inner_class_info_index =   (classes[i] << 8)     |
                                            classes[i + 1];
            int outer_class_info_index =   (classes[i + 2] << 8) |
                                            classes[i + 3];
            int inner_name_index       =   (classes[i + 4] << 8) |
                                            classes[i + 5];
            int inner_class_access_flags = (classes[i + 6] << 8) |
                                            classes[i + 7];
            
            if ((inner_class_access_flags & ACC_INTERFACE) == ACC_INTERFACE) {
                sourceText.append(".inner interface ");
            } else {
                sourceText.append(".inner class ");
            }

            if ((inner_class_access_flags & ACC_PUBLIC) == ACC_PUBLIC) {
                sourceText.append("public ");
            }
            if ((inner_class_access_flags & ACC_PRIVATE) == ACC_PRIVATE) {
                sourceText.append("private ");
            }
            if ((inner_class_access_flags & ACC_PROTECTED) == ACC_PROTECTED) {
                sourceText.append("protected ");
            }
            if ((inner_class_access_flags & ACC_STATIC) == ACC_STATIC) {
                sourceText.append("static ");
            }
            if ((inner_class_access_flags & ACC_FINAL) == ACC_FINAL) {
                sourceText.append("final ");
            }
            if ((inner_class_access_flags & ACC_ABSTRACT) == ACC_ABSTRACT) {
                sourceText.append("abstract ");
            }
            if ((inner_class_access_flags & ACC_SYNTHETIC) == ACC_SYNTHETIC) {
                sourceText.append("synthetic ");
            }
            if ((inner_class_access_flags & ACC_ANNOTATION) == ACC_ANNOTATION) {
                sourceText.append("annotation ");
            }
            if ((inner_class_access_flags & ACC_ENUM) == ACC_ENUM) {
                sourceText.append("enum ");
            }
            
            // inner_name_index == 0: anonymous class
            if (inner_name_index != 0) { 
              cpConstant = cp.getConstant(inner_name_index);
              String innerClassName = Builder.getStringCONSTANT_Utf8(cpConstant);
              sourceText.append(innerClassName + " ");
            }
            if (inner_class_info_index != 0) {
                cpConstant = cp.getConstant(inner_class_info_index);
                String innerClassInfoName = Builder.getClassNameCONSTANT_Class(cpConstant, cp);
                sourceText.append("inner " + innerClassInfoName);
            }
            // outer_class_info_index == 0: not a member
            if (outer_class_info_index != 0) { 
                cpConstant = cp.getConstant(outer_class_info_index);
                String outerClassInfoName = Builder.getClassNameCONSTANT_Class(cpConstant, cp);
                sourceText.append(" outer " + outerClassInfoName);
            }
            if ((inner_name_index == 0) || (outer_class_info_index == 0)) {
                sourceText.append(" ;");
            }
            if (inner_name_index == 0) { sourceText.append(" <anonymous>"); }
            if (outer_class_info_index == 0) { sourceText.append(" <not a member>"); }
                        
            sourceText.append(lt);
         }
    }
        
    
    
    
    
    public StringBuffer getSourceText() {
        return sourceText;
    }
        
    
}

