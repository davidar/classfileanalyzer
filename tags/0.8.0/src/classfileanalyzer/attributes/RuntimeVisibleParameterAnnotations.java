/*
 * RuntimeVisibleParameterAnnotations.java
 *
 * @author Harald Roeder
 * @version 2008-02-18
 */


package classfileanalyzer.attributes;


import classfileanalyzer.*;





public class RuntimeVisibleParameterAnnotations implements Constants {
    
    int[] RuntimeVisibleParameterAnnotations_attribute;
            int attribute_name_index;
            int attribute_length;
            int num_parameters;
            int[] parameter_annotations;
            
    ConstantPool cp;
    int indent;
    StringBuffer sourceText;
    
    int pointerParameterAnnotations = 0;
    
    
    

    
    public RuntimeVisibleParameterAnnotations(int[] RuntimeVisibleParameterAnnotations_attribute, 
                                              ConstantPool cp, 
                                              int indent) throws ClassFormatError {
        this.RuntimeVisibleParameterAnnotations_attribute = RuntimeVisibleParameterAnnotations_attribute;
        this.cp = cp;
        this.indent = indent;
        sourceText = new StringBuffer();
        parse();
    }
      
    
    
    
    
    public void parse() throws ClassFormatError {
        attribute_name_index = (RuntimeVisibleParameterAnnotations_attribute[0] << 8)  | 
                                RuntimeVisibleParameterAnnotations_attribute[1];
        attribute_length =     (RuntimeVisibleParameterAnnotations_attribute[2] << 24) | 
                               (RuntimeVisibleParameterAnnotations_attribute[3] << 16) |
                               (RuntimeVisibleParameterAnnotations_attribute[4] << 8)  | 
                                RuntimeVisibleParameterAnnotations_attribute[5];
        num_parameters =        RuntimeVisibleParameterAnnotations_attribute[6];
        
        parameter_annotations = new int[attribute_length - 1];
        
        for (int i = 0; i < parameter_annotations.length; i++) {
            parameter_annotations[i] = RuntimeVisibleParameterAnnotations_attribute[7 + i];
        }
        
   
        for (int i = 0; i < num_parameters; i++) {
            int num_annotations = (parameter_annotations[pointerParameterAnnotations] << 8) | 
                                   parameter_annotations[pointerParameterAnnotations + 1]; 
            pointerParameterAnnotations = pointerParameterAnnotations + 2;
            int[] parameter_annotations_part = new int[attribute_length - 1 - pointerParameterAnnotations];
            
            for (int j = 0; j < parameter_annotations_part.length; j++) {
                 parameter_annotations_part[j] = parameter_annotations[pointerParameterAnnotations + j];
            }
                        
            AnnotationHelper helper = new AnnotationHelper(ANNOTATION_VISIBLEPARAM, 
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

