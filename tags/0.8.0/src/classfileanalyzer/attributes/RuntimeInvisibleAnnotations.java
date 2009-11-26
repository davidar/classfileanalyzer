/*
 * RuntimeInvisibleAnnotations.java
 *
 * @author Harald Roeder
 * @version 2008-02-18
 */


package classfileanalyzer.attributes;


import classfileanalyzer.*;





public class RuntimeInvisibleAnnotations implements Constants {
    
    int[] RuntimeInvisibleAnnotations_attribute;
        int attribute_name_index;
        int attribute_length;
        int num_annotations;
        int[] annotations;
            
    ConstantPool cp;
    int indent;
    StringBuffer sourceText;
    
    
    
    
    
    public RuntimeInvisibleAnnotations(int[] RuntimeInvisibleAnnotations_attribute, 
                                       ConstantPool cp, 
                                       int indent) throws ClassFormatError {
        this.RuntimeInvisibleAnnotations_attribute = RuntimeInvisibleAnnotations_attribute;
        this.cp = cp;
        this.indent = indent;
        sourceText = new StringBuffer();
        parse();
    }
        
    
    
    
    
    public void parse() throws ClassFormatError {
        attribute_name_index = (RuntimeInvisibleAnnotations_attribute[0] << 8)  | 
                                RuntimeInvisibleAnnotations_attribute[1];
        attribute_length =     (RuntimeInvisibleAnnotations_attribute[2] << 24) | 
                               (RuntimeInvisibleAnnotations_attribute[3] << 16) |
                               (RuntimeInvisibleAnnotations_attribute[4] << 8)  | 
                                RuntimeInvisibleAnnotations_attribute[5];
        num_annotations =      (RuntimeInvisibleAnnotations_attribute[6] << 8)  | 
                                RuntimeInvisibleAnnotations_attribute[7];
        
        annotations = new int[attribute_length - 2];
        
        for (int i = 0; i < annotations.length; i++) {
            annotations[i] = RuntimeInvisibleAnnotations_attribute[8 + i];
        }
        
        AnnotationHelper helper = new AnnotationHelper(ANNOTATION_INVISIBLE, annotations, cp, indent);
        
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

