/*
 * AnnotationHelper.java
 *
 * @author Harald Roeder
 * @version 2008-02-18
 */


package classfileanalyzer.attributes;


import classfileanalyzer.*;
import java.util.*;
import static classfileanalyzer.Main.lt;





public class AnnotationHelper implements Constants {
 
    int[] annotations;
       
    ConstantPool cp;
    int annotationPolicy;
    int indent;
    String indentString;
    
    StringBuffer sourceText;
    
    boolean arrayValue = false;
    boolean nestedAnnotation = false;
    boolean nestedAnnotationArrayValue = false;
    
    int parameterNumber = 0;
    int pointerAnnotations = 0;
    
    
    

    
    public AnnotationHelper(int annotationPolicy, int[] annotations, ConstantPool cp, int indent) {
        this.annotationPolicy = annotationPolicy;
        this.annotations = annotations;
        this.cp = cp;
        this.indent = indent;
        if (indent == 0) { indentString = ""; }
        if (indent == 2) { indentString = "  "; }
        sourceText = new StringBuffer();
    }
      
    
    
    
    
    public void annotation() {
        
        // ANNOTATION_DEFAULT switch
        if ((annotationPolicy != ANNOTATION_DEFAULT) || (nestedAnnotation == true)) {
                  
            int type_index = (annotations[pointerAnnotations] << 8) | 
                              annotations[pointerAnnotations + 1]; 
        
            pointerAnnotations = pointerAnnotations + 2;
            int num_element_value_pairs = (annotations[pointerAnnotations] << 8) | 
                                           annotations[pointerAnnotations + 1];
            pointerAnnotations = pointerAnnotations + 2;

            Vector<Integer> cpConstant = cp.getConstant(type_index);
            String fieldDescriptorAnnotationType = Builder.getStringCONSTANT_Utf8(cpConstant);

            
            /*
            Example:
            import java.lang.annotation.*;
            @Documented
            @Retention(value = RetentionPolicy.RUNTIME)
            @Target(value = ElementType.METHOD)
            public @interface TestClaraRuntimeVisibleAnnotations {
              int id();
              String name();
              String vorname();
            }

            1. CONSTANT_Class[7](name_index = 20)
            2. CONSTANT_Class[7](name_index = 21)
            3. CONSTANT_Class[7](name_index = 22)
            4. CONSTANT_Utf8[1]("id")
            5. CONSTANT_Utf8[1]("()I")
            6. CONSTANT_Utf8[1]("name")
            7. CONSTANT_Utf8[1]("()Ljava/lang/String;")
            8. CONSTANT_Utf8[1]("vorname")
            9. CONSTANT_Utf8[1]("SourceFile")
            10. CONSTANT_Utf8[1]("TestClaraRuntimeVisibleAnnotations.java")
            11. CONSTANT_Utf8[1]("RuntimeVisibleAnnotations")
            12. CONSTANT_Utf8[1]("Ljava/lang/annotation/Documented;")
            13. CONSTANT_Utf8[1]("Ljava/lang/annotation/Retention;")
            14. CONSTANT_Utf8[1]("value")
            15. CONSTANT_Utf8[1]("Ljava/lang/annotation/RetentionPolicy;")
            16. CONSTANT_Utf8[1]("RUNTIME")
            17. CONSTANT_Utf8[1]("Ljava/lang/annotation/Target;")
            18. CONSTANT_Utf8[1]("Ljava/lang/annotation/ElementType;")
            19. CONSTANT_Utf8[1]("METHOD")
            20. CONSTANT_Utf8[1]("TestClaraRuntimeVisibleAnnotations")
            21. CONSTANT_Utf8[1]("java/lang/Object")
            22. CONSTANT_Utf8[1]("java/lang/annotation/Annotation")

            ; RuntimeVisibleAnnotations
            ;     0 11 0 0 0 31 
            ;                   0 3                         num_annotations
            ;                       0 12 0 0                type_index    num_element_value_pairs
            ;                                0 13 0 1       type_index    num_element_value_pairs
            ;     0 14 101 0 15 0 16                        element_name_index    tag(e)    type_name_index    const_name_index    
            ;                        0 17 0 1 0 14 91 0 1   type_index    num_element_value_pairs    element_name_index    tag([)    num_values 
            ;     101 0 18 0 19                             tag(e)    type_name_index    const_name_index
            */
            
            
            if (nestedAnnotation == false) {
                switch (annotationPolicy) {
                    case ANNOTATION_VISIBLE:
                        sourceText.append(indentString + ".annotation visible " + 
                                fieldDescriptorAnnotationType + lt);
                        break;
                    case ANNOTATION_INVISIBLE:
                        sourceText.append(indentString + ".annotation invisible " + 
                                fieldDescriptorAnnotationType + lt);
                        break;
                    case ANNOTATION_VISIBLEPARAM:
                        sourceText.append(indentString + ".annotation visibleparam " + 
                                parameterNumber + " " + fieldDescriptorAnnotationType + lt);
                        break;
                    case ANNOTATION_INVISIBLEPARAM:
                        sourceText.append(indentString + ".annotation invisibleparam " + 
                                parameterNumber + " " + fieldDescriptorAnnotationType + lt);
                        break;
                }
            } else {
                if (!arrayValue) {
                    sourceText.append(fieldDescriptorAnnotationType + " = .annotation" + lt);
                } else {
                    sourceText.append(indentString + "  .annotation" + lt);
                }
            }


            // element value pairs
            for (int i = 0; i < num_element_value_pairs; i++) {
                int element_name_index = (annotations[pointerAnnotations] << 8) | 
                                          annotations[pointerAnnotations + 1];
                pointerAnnotations = pointerAnnotations + 2;
                cpConstant = cp.getConstant(element_name_index);
                String elementName = Builder.getStringCONSTANT_Utf8(cpConstant);

                int tag = annotations[pointerAnnotations];
                pointerAnnotations++;

                sourceText.append(indentString + "  " + elementName + " ");
                element_value(tag);
                sourceText.append(lt);
            }
                        
            
            if (nestedAnnotation == false) {
                sourceText.append(indentString + "  .end annotation" + lt);
            } else {
                sourceText.append(indentString + "  .end annotation");
            }
           
        
        } else { // ANNOTATION_DEFAULT
            pointerAnnotations = 14;
            int tag = annotations[pointerAnnotations];
            pointerAnnotations++;
            sourceText.append(indentString + "  ");
            element_value(tag); // only one element value pair
            sourceText.append(lt);
        }
    }
       
    
    
    
    
    public void element_value(int tag) {
               
        int const_value_index;
        int type_name_index;
        int const_name_index;
        int num_values;
        int class_info_index;
        
        Vector<Integer> cpConstant;
        char tagChar = (char)tag;
                
        if (tagChar == '[') { sourceText.append("["); }
               
        
        // B
        // ascii: 66
        // const_value
        if (tagChar == 'B') {
            const_value_index = (annotations[pointerAnnotations] << 8) | 
                                 annotations[pointerAnnotations + 1];
            pointerAnnotations = pointerAnnotations + 2;
            cpConstant = cp.getConstant(const_value_index);
            int b = Builder.getIntegerCONSTANT_Integer(cpConstant);

            if (nestedAnnotation) {
                if (!nestedAnnotationArrayValue) {
                    sourceText.append("B = " + b);
                } else {
                    sourceText.append(" " + b);
                }
            } else {
                if (!arrayValue) {
                    sourceText.append("B = " + b);
                } else {
                    sourceText.append(" " + b);
                }
            }
        }
                
        
        // C
        // ascii: 67
        // const_value
        if (tagChar == 'C') {
            const_value_index = (annotations[pointerAnnotations] << 8) | 
                                 annotations[pointerAnnotations + 1];
            pointerAnnotations = pointerAnnotations + 2;
            cpConstant = cp.getConstant(const_value_index);
            int c = Builder.getIntegerCONSTANT_Integer(cpConstant);

            if (nestedAnnotation) {
                if (!nestedAnnotationArrayValue) {
                    sourceText.append("C = " + c);
                } else {
                    sourceText.append(" " + c);
                }
            } else {
                if (!arrayValue) {
                    sourceText.append("C = " + c);
                } else {
                    sourceText.append(" " + c);
                }
            }
        }
                
        
        // D
        // ascii: 68
        // const_value
        if (tagChar == 'D') {
            const_value_index = (annotations[pointerAnnotations] << 8) | 
                                 annotations[pointerAnnotations + 1];
            pointerAnnotations = pointerAnnotations + 2;
            cpConstant = cp.getConstant(const_value_index);
            double d = Builder.getDoubleCONSTANT_Double(cpConstant);

            if (nestedAnnotation) {
                if (!nestedAnnotationArrayValue) {
                    sourceText.append("D = " + d);
                } else {
                    sourceText.append(" " + d);
                }
            } else {
                if (!arrayValue) {
                    sourceText.append("D = " + d);
                } else {
                    sourceText.append(" " + d);
                }
            }
        }
                
        
        // F
        // ascii: 70
        // const_value
        if (tagChar == 'F') {
            const_value_index = (annotations[pointerAnnotations] << 8) | 
                                 annotations[pointerAnnotations + 1];
            pointerAnnotations = pointerAnnotations + 2;
            cpConstant = cp.getConstant(const_value_index);
            float f = Builder.getFloatCONSTANT_Float(cpConstant);

            if (nestedAnnotation) {
                if (!nestedAnnotationArrayValue) {
                    sourceText.append("F = " + f);
                } else {
                    sourceText.append(" " + f);
                }
            } else {
                if (!arrayValue) {
                    sourceText.append("F = " + f);
                } else {
                    sourceText.append(" " + f);
                }
            }
        }
                
        
        // I
        // ascii: 73
        // const_value
        if (tagChar == 'I') {
            const_value_index = (annotations[pointerAnnotations] << 8) | 
                                 annotations[pointerAnnotations + 1];
            pointerAnnotations = pointerAnnotations + 2;
            cpConstant = cp.getConstant(const_value_index);
            int integer = Builder.getIntegerCONSTANT_Integer(cpConstant);

            if (nestedAnnotation) {
                if (!nestedAnnotationArrayValue) {
                    sourceText.append("I = " + integer);
                } else {
                    sourceText.append(" " + integer);
                }
            } else {
                if (!arrayValue) {
                    sourceText.append("I = " + integer);
                } else {
                    sourceText.append(" " + integer);
                }
            }
        }
                
        
        // J
        // ascii: 74
        // const_value
        if (tagChar == 'J') {
            const_value_index = (annotations[pointerAnnotations] << 8) | 
                                 annotations[pointerAnnotations + 1];
            pointerAnnotations = pointerAnnotations + 2;
            cpConstant = cp.getConstant(const_value_index);
            long l = Builder.getLongCONSTANT_Long(cpConstant);

            if (nestedAnnotation) {
                if (!nestedAnnotationArrayValue) {
                    sourceText.append("J = " + l);
                } else {
                    sourceText.append(" " + l);
                }
            } else {
                if (!arrayValue) {
                    sourceText.append("J = " + l);
                } else {
                    sourceText.append(" " + l);
                }
            }
        }
                
        
        // S
        // ascii: 83
        // const_value
        if (tagChar == 'S') {
            const_value_index = (annotations[pointerAnnotations] << 8) | 
                                 annotations[pointerAnnotations + 1];
            pointerAnnotations = pointerAnnotations + 2;
            cpConstant = cp.getConstant(const_value_index);
            int st = Builder.getIntegerCONSTANT_Integer(cpConstant);

            if (nestedAnnotation) {
                if (!nestedAnnotationArrayValue) {
                    sourceText.append("S = " + st);
                } else {
                    sourceText.append(" " + st);
                }
            } else {
                if (!arrayValue) {
                    sourceText.append("S = " + st);
                } else {
                    sourceText.append(" " + st);
                }
            }
        }
                
        
        // Z
        // ascii: 90
        // const_value
        if (tagChar == 'Z') {
            const_value_index = (annotations[pointerAnnotations] << 8) | 
                                 annotations[pointerAnnotations + 1];
            pointerAnnotations = pointerAnnotations + 2;
            cpConstant = cp.getConstant(const_value_index);
            int b = Builder.getIntegerCONSTANT_Integer(cpConstant);

            if (nestedAnnotation) {
                if (!nestedAnnotationArrayValue) {
                    sourceText.append("Z = " + b);
                } else {
                    sourceText.append(" " + b);
                }
            } else {
                if (!arrayValue) {
                    sourceText.append("Z = " + b);
                } else {
                    sourceText.append(" " + b);
                }
            }
        }
                
        
        // s
        // ascii: 115
        // const_value
        if (tagChar == 's') {
            const_value_index = (annotations[pointerAnnotations] << 8) | 
                                 annotations[pointerAnnotations + 1];
            pointerAnnotations = pointerAnnotations + 2;
            cpConstant = cp.getConstant(const_value_index);
            String str = Builder.getStringCONSTANT_Utf8(cpConstant);

            if (nestedAnnotation) {
                if (!nestedAnnotationArrayValue) {
                    sourceText.append("s = \"" + str + "\"");
                } else {
                    sourceText.append(" \"" + str + "\"");
                }
            } else {
                if (!arrayValue) {
                     sourceText.append("s = \"" + str + "\"");
                } else {
                    sourceText.append(" \"" + str + "\"");
                }
            }
        }
                
        
        // e
        // ascii: 101
        // enum_const_value
        if (tagChar == 'e') {
            type_name_index = (annotations[pointerAnnotations] << 8) | 
                               annotations[pointerAnnotations + 1];
                pointerAnnotations = pointerAnnotations + 2;
                cpConstant = cp.getConstant(type_name_index);
                String typeName = Builder.getStringCONSTANT_Utf8(cpConstant);
            const_name_index = (annotations[pointerAnnotations] << 8) | 
                                annotations[pointerAnnotations + 1];
                pointerAnnotations = pointerAnnotations + 2;
                cpConstant = cp.getConstant(const_name_index);
                String constName = Builder.getStringCONSTANT_Utf8(cpConstant);
                
            if (nestedAnnotation) {
                if (!nestedAnnotationArrayValue) {
                    sourceText.append("e " + typeName + " = " + constName);
                } else {
                    sourceText.append(" " + constName);
                }
            } else {
                if (!arrayValue) {
                    sourceText.append("e " + typeName + " = " + constName);
                } else {
                    sourceText.append(" " + constName);
                }
            }
        }
                
        
        // c
        // ascii: 99
        // class_info
        if (tagChar == 'c') {
            class_info_index = (annotations[pointerAnnotations] << 8) | 
                                annotations[pointerAnnotations + 1];
            pointerAnnotations = pointerAnnotations + 2;
            cpConstant = cp.getConstant(class_info_index);
            String classInfoName = Builder.getStringCONSTANT_Utf8(cpConstant);

            if (nestedAnnotation) {
                if (!nestedAnnotationArrayValue) {
                    sourceText.append("c = " + classInfoName);
                } else {
                    sourceText.append(" " + classInfoName);
                }
            } else {
                if (!arrayValue) {
                    sourceText.append("c = " + classInfoName);
                } else {
                    sourceText.append(" " + classInfoName);
                }
            }
        }
                
           
        // [
        // ascii: 91
        // array_value
        if (tagChar == '[') {
            num_values = (annotations[pointerAnnotations] << 8) | 
                          annotations[pointerAnnotations + 1];
            pointerAnnotations = pointerAnnotations + 2;
            tag = annotations[pointerAnnotations];
            pointerAnnotations++;
                
            for (int i = 0; i < num_values; i++) {
                if (i > 0) { 
                    int tagArray = annotations[pointerAnnotations];
                    pointerAnnotations = pointerAnnotations + 1; 
                    if (tagArray != tag) {
                        System.out.println("Clara: internal error, annotation, array_value!");
                        System.exit(1);
                    }
                }
                element_value(tag);
                arrayValue = true;
                if (nestedAnnotation) { nestedAnnotationArrayValue = true; }
            }
                        
            arrayValue = false;
            nestedAnnotationArrayValue = false;
        }
                
  
        // @
        // ascii: 64
        // annotation (nested, complex)
        if (tagChar == '@') {
            if (!arrayValue) {
                sourceText.append("@ ");
            } else {
                sourceText.append(lt);
            }
            nestedAnnotation = true;
            annotation();
            nestedAnnotation = false;
        }
    }
  
        
       
     
    
    public StringBuffer getSourceText() {
        return sourceText;
    }
    
    
    
    
    
    public int getAnnotationsPointer() {
        return pointerAnnotations;
    }
    
    
    
    
    
    public void setParameterNumber(int parameterNumber) {
        this.parameterNumber = parameterNumber;
    }
        
    
}

