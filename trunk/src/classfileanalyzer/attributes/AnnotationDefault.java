/*
 * AnnotationDefault.java
 *
 * @author Harald Roeder
 * @version 2008-02-18
 */


package classfileanalyzer.attributes;


import classfileanalyzer.*;
import static classfileanalyzer.Main.lt;





public class AnnotationDefault implements Constants {
    
    int[] AnnotationDefault_attribute;
            int attribute_name_index;
            int attribute_length;
            int[] default_value;
                  
    ConstantPool cp;
    int indent;
    StringBuffer sourceText;
    
    
    
    
    
    public AnnotationDefault(int[] AnnotationDefault_attribute, ConstantPool cp, int indent) {
        this.AnnotationDefault_attribute = AnnotationDefault_attribute;
        this.cp = cp;
        this.indent = indent;
        sourceText = new StringBuffer();
        parse();
    }
        
    
    
    
    
    public void parse() {
        int i;
        
        attribute_name_index = (AnnotationDefault_attribute[0] << 8)  | 
                                AnnotationDefault_attribute[1];
        attribute_length =     (AnnotationDefault_attribute[2] << 24) | 
                               (AnnotationDefault_attribute[3] << 16) |
                               (AnnotationDefault_attribute[4] << 8)  | 
                                AnnotationDefault_attribute[5];
                
        default_value = new int[attribute_length];
        
        for (i = 0; i < default_value.length; i++) {
            default_value[i] = AnnotationDefault_attribute[6 + i];
        }
        
        int[] annotations = new int[default_value.length + 14];
        
        // annotations = 00 0000 00 00 00 00 default_value
        for (i = 0; i < 14; i++) {
            annotations[i] = 0;
        }
        for (i = 0; i < default_value.length; i++) {
            annotations[i + 14] = default_value[i];
        }
                
        AnnotationHelper helper = new AnnotationHelper(ANNOTATION_DEFAULT, annotations, cp, indent);
        helper.annotation();
                
        StringBuffer annotationSourceText = helper.getSourceText();
        
        sourceText.append("  .annotation default" + lt);
        sourceText.append(annotationSourceText);
        sourceText.append("    .end annotation" + lt);
    }  
    
      
    
    
    
    public StringBuffer getSourceText() {
        return sourceText;
    }
        
    
}

