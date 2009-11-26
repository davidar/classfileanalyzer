/*
 * RuntimeVisibleAnnotations.java
 *
 * @author Harald Roeder
 * @version 2008-02-18
 */


package classfileanalyzer.attributes;


import classfileanalyzer.*;





public class RuntimeVisibleAnnotations implements Constants {
    
    int[] RuntimeVisibleAnnotations_attribute;
            int attribute_name_index;
            int attribute_length;
            int num_annotations;
            int[] annotations;
            
    ConstantPool cp;
    int indent;
    StringBuffer sourceText;
    
    
    
    
    
    public RuntimeVisibleAnnotations(int[] RuntimeVisibleAnnotations_attribute, 
                                     ConstantPool cp, 
                                     int indent) {
        this.RuntimeVisibleAnnotations_attribute = RuntimeVisibleAnnotations_attribute;
        this.cp = cp;
        this.indent = indent;
        sourceText = new StringBuffer();
        parse();
    }
      
    
    
    
    
    public void parse() {
        attribute_name_index = (RuntimeVisibleAnnotations_attribute[0] << 8)  | 
                                RuntimeVisibleAnnotations_attribute[1];
        attribute_length =     (RuntimeVisibleAnnotations_attribute[2] << 24) | 
                               (RuntimeVisibleAnnotations_attribute[3] << 16) |
                               (RuntimeVisibleAnnotations_attribute[4] << 8)  | 
                                RuntimeVisibleAnnotations_attribute[5];
        num_annotations =      (RuntimeVisibleAnnotations_attribute[6] << 8)  | 
                                RuntimeVisibleAnnotations_attribute[7];
        
        annotations = new int[attribute_length - 2];
        
        for (int i = 0; i < annotations.length; i++) {
            annotations[i] = RuntimeVisibleAnnotations_attribute[8 + i];
        }
        
        AnnotationHelper helper = new AnnotationHelper(ANNOTATION_VISIBLE, annotations, cp, indent);
        
        for (int i = 0; i < num_annotations; i++) {
            helper.annotation();
        }
        
        StringBuffer annotationSourceText = helper.getSourceText();
        sourceText.append(annotationSourceText);
    }
        
    
     
    
    
    public StringBuffer getSourceText() {
        return sourceText;
    }
        
    
}

