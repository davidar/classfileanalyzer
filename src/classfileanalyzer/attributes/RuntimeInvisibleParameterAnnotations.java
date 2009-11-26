/*
 * RuntimeInvisibleParameterAnnotations.java
 *
 * @author Harald Roeder
 * @version 2008-02-18
 */


package classfileanalyzer.attributes;


import classfileanalyzer.*;





public class RuntimeInvisibleParameterAnnotations implements Constants {
    
    int[] RuntimeInvisibleParameterAnnotations_attribute;
        int attribute_name_index;
        int attribute_length;
        int num_parameters;
        int[] parameter_annotations;
            
    ConstantPool cp;
    int indent;
    StringBuffer sourceText;
    
    int pointerParameterAnnotations = 0;
    
    
    
    
   
    public RuntimeInvisibleParameterAnnotations(int[] RuntimeInvisibleParameterAnnotations_attribute, 
                                                ConstantPool cp, 
                                                int indent) throws ClassFormatError {
        this.RuntimeInvisibleParameterAnnotations_attribute = RuntimeInvisibleParameterAnnotations_attribute;
        this.cp = cp;
        this.indent = indent;
        sourceText = new StringBuffer();
        parse();
    }
     
    
    
    
    
    public void parse() throws ClassFormatError {
        attribute_name_index = (RuntimeInvisibleParameterAnnotations_attribute[0] << 8)  | 
                                RuntimeInvisibleParameterAnnotations_attribute[1];
        attribute_length =     (RuntimeInvisibleParameterAnnotations_attribute[2] << 24) | 
                               (RuntimeInvisibleParameterAnnotations_attribute[3] << 16) |
                               (RuntimeInvisibleParameterAnnotations_attribute[4] << 8)  | 
                                RuntimeInvisibleParameterAnnotations_attribute[5];
        num_parameters =        RuntimeInvisibleParameterAnnotations_attribute[6];
        
        parameter_annotations = new int[attribute_length - 1];
        
        for (int i = 0; i < parameter_annotations.length; i++) {
            parameter_annotations[i] = RuntimeInvisibleParameterAnnotations_attribute[7 + i];
        }
        
   
        for (int i = 0; i < num_parameters; i++) {
            int num_annotations = (parameter_annotations[pointerParameterAnnotations] << 8) | 
                                   parameter_annotations[pointerParameterAnnotations + 1]; 
            pointerParameterAnnotations = pointerParameterAnnotations + 2;
            int[] parameter_annotations_part = new int[attribute_length - 1 - pointerParameterAnnotations];
            
            for (int j = 0; j < parameter_annotations_part.length; j++) {
                 parameter_annotations_part[j] = parameter_annotations[pointerParameterAnnotations + j];
            }
            
            AnnotationHelper helper = new AnnotationHelper(ANNOTATION_INVISIBLEPARAM, 
                                                           parameter_annotations_part, 
                                                           cp, 
                                                           indent);
            helper.setParameterNumber(i + 1);
            
            for (int j = 0; j < num_annotations; j++) {
                helper.annotation();
            }
            
            int annotationsPointer = helper.getAnnotationsPointer();
            pointerParameterAnnotations = pointerParameterAnnotations + annotationsPointer;
           
            StringBuffer annotationSourceText = helper.getSourceText();
            sourceText.append(annotationSourceText);
        }
    }
       
    
    
    
    
    public StringBuffer getSourceText() {
        return sourceText;
    }
        
    
}

