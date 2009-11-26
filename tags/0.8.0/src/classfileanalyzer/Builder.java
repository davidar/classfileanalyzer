/*
 * Builder.java
 *
 * @author Harald Roeder
 * @version 2008-02-17
 */


package classfileanalyzer;


import classfileanalyzer.attributes.*;
import java.util.*;
import static classfileanalyzer.Main.lt;





public class Builder implements Constants {
    
    StringBuffer buffer;
    String simpleNameClass = "";
 
    
    
 
    
    public Builder() {
        buffer = new StringBuffer();
    }
    
    
    
    
    public void buildHeader(int[] minor_version, 
                            int[] major_version,
                            ConstantPool cp,
                            int[] access_flags, 
                            int[] this_class, 
                            int[] super_class,
                            Interfaces ifs,
                            Attributes attr) throws ClassFormatError {
        buildHeader(minor_version, major_version, cp, access_flags, this_class,
                super_class, ifs, attr, true);
    }
        
    
    
    
    
    public void buildHeader(int[] minor_version, 
                            int[] major_version,
                            ConstantPool cp,
                            int[] access_flags, 
                            int[] this_class, 
                            int[] super_class,
                            Interfaces ifs,
                            Attributes attr,
                            boolean can_header) throws ClassFormatError {
        
        StringBuffer headerBuffer = new StringBuffer();
        
        int minorVersion;
        int majorVersion;
               
        StringBuffer classInterfaceBuffer = new StringBuffer();
        StringBuffer implementsBuffer = new StringBuffer();
        StringBuffer notImplementedAttributeBuffer = new StringBuffer();
        String fullyQualifiedNameClass = "";
        String sourceString = "";
        String superString = "";
        String innerString = "";
        String enclosingMethodString = "";
        String sourceDebugExtensionString = "";
        String signatureString = "";
        String deprecatedString = "";
        String runtimeVisibleAnnotationsString = "";
        String runtimeInvisibleAnnotationsString = "";
                        
        int i;
        int cpIndex;
        Vector<Integer> cpConstant;
        
        

        
        
        // BEGIN: attributes ClassFile
        int attributes_count = attr.getAttributesCount();
        int attribute_name_index;
        String attributeName;
        
               
        for (int attributeNumber = 0; attributeNumber < attributes_count; attributeNumber++) {
            int[] attribute_info = attr.getAttribute(attributeNumber);
         
            attribute_name_index = (attribute_info[0] << 8)  | 
                                    attribute_info[1];
            cpConstant = cp.getConstant(attribute_name_index);
            attributeName = Builder.getStringCONSTANT_Utf8(cpConstant);
           
            if (attributeName.equals("SourceFile")) {
                SourceFile sourceFile = new SourceFile(attribute_info, cp);
                StringBuffer sourceFileSourceText = sourceFile.getSourceText();
                sourceString = sourceFileSourceText.toString();
            }
            else if (attributeName.equals("InnerClasses")) {
                InnerClasses innerClasses = new InnerClasses(attribute_info, cp);
                StringBuffer innerClassesSourceText = innerClasses.getSourceText();
                innerString = innerClassesSourceText.toString();
            }
            else if (attributeName.equals("EnclosingMethod")) {
                EnclosingMethod enclosingMethod = new EnclosingMethod(attribute_info, cp);
                StringBuffer enclosingMethodSourceText = enclosingMethod.getSourceText();
                enclosingMethodString = enclosingMethodSourceText.toString();
            }
            else if (attributeName.equals("SourceDebugExtension")) {
                SourceDebugExtension sourceDebugExtension = new SourceDebugExtension(attribute_info);
                StringBuffer sourceDebugExtensionSourceText = sourceDebugExtension.getSourceText();
                sourceDebugExtensionString = sourceDebugExtensionSourceText.toString();
            }
            else if (attributeName.equals("Signature")) {
                Signature signature = new Signature(attribute_info, cp);
                StringBuffer signatureSourceText = signature.getSourceText();
                signatureString = signatureSourceText.toString();
            }
            else if (attributeName.equals("Deprecated")) {
                classfileanalyzer.attributes.Deprecated deprecated = 
                        new classfileanalyzer.attributes.Deprecated(attribute_info);
                StringBuffer deprecatedSourceText = deprecated.getSourceText();
                deprecatedString = deprecatedSourceText.toString();
            }
            else if (attributeName.equals("RuntimeVisibleAnnotations")) {
                RuntimeVisibleAnnotations runtimeVisibleAnnotations = 
                        new RuntimeVisibleAnnotations(attribute_info, cp, 0);
                StringBuffer runtimeVisibleAnnotationsSourceText = runtimeVisibleAnnotations.getSourceText();
                runtimeVisibleAnnotationsString = runtimeVisibleAnnotationsSourceText.toString();
            }
            else if (attributeName.equals("RuntimeInvisibleAnnotations")) {
                RuntimeInvisibleAnnotations runtimeInvisibleAnnotations = 
                        new RuntimeInvisibleAnnotations(attribute_info, cp, 0);
                StringBuffer runtimeInvisibleAnnotationsSourceText = runtimeInvisibleAnnotations.getSourceText();
                runtimeInvisibleAnnotationsString = runtimeInvisibleAnnotationsSourceText.toString();
            }
            else {
                notImplementedAttributeBuffer.append("; not implemented attribute (ClassFile): " + 
                        attributeName + " = ");
                for (i = 0; i < attribute_info.length; i++) {
                    if (((i % 16) == 0) ) { notImplementedAttributeBuffer.append(lt + ";     "); }
                    notImplementedAttributeBuffer.append(attribute_info[i] + " ");
                }
                notImplementedAttributeBuffer.append(lt);
            }   
        }
        // END: attributes ClassFile
              
        
        
        
        
        // .bytecode
        minorVersion = (minor_version[0] << 8) | minor_version[1];
        majorVersion = (major_version[0] << 8) | major_version[1];
  
        
        
  
        
        // .class, .interface, .super
        int accessFlags = (access_flags[0] << 8) | access_flags[1];
        
        if ((accessFlags & ACC_INTERFACE) == ACC_INTERFACE) {
            classInterfaceBuffer.append(".interface ");
        } else {
            classInterfaceBuffer.append(".class ");
        }
        
        if ((accessFlags & ACC_PUBLIC) == ACC_PUBLIC) {
            classInterfaceBuffer.append("public ");
        }
        if ((accessFlags & ACC_FINAL) == ACC_FINAL) {
            classInterfaceBuffer.append("final ");
        }
        if ((accessFlags & ACC_ABSTRACT) == ACC_ABSTRACT) {
            classInterfaceBuffer.append("abstract ");
        }
        if ((accessFlags & ACC_SYNTHETIC) == ACC_SYNTHETIC) {
            classInterfaceBuffer.append("synthetic ");
        }
        if ((accessFlags & ACC_ANNOTATION) == ACC_ANNOTATION) {
            classInterfaceBuffer.append("annotation ");
        }
        if ((accessFlags & ACC_ENUM) == ACC_ENUM) {
            classInterfaceBuffer.append("enum ");
        }
        
        int thisClass = (this_class[0] << 8) | this_class[1];
        cpConstant = cp.getConstant(thisClass);
        if (cpConstant.get(0).intValue() == CONSTANT_Class) {
            cpIndex = (cpConstant.get(1).intValue() << 8) | cpConstant.get(2).intValue();
            cpConstant = cp.getConstant(cpIndex);
            fullyQualifiedNameClass = getStringCONSTANT_Utf8(cpConstant);
            classInterfaceBuffer.append(fullyQualifiedNameClass + lt);    
            
            // simple Name of the class or interface
            int lastIndexSlash = fullyQualifiedNameClass.lastIndexOf("/");
            if (lastIndexSlash != -1) {
                simpleNameClass = fullyQualifiedNameClass.substring(lastIndexSlash + 1);
            } else {
                simpleNameClass = fullyQualifiedNameClass;
            }
        }
        
        if ((accessFlags & ACC_SUPER) != ACC_SUPER) {
            classInterfaceBuffer.append("; Flag ACC_SUPER not set, see JVM spec" + lt);
        }
               
        int superClass = (super_class[0] << 8) | super_class[1];
        if (superClass == 0) { // class Object has no super class
            superString = "java/lang/Object" + lt +
                          "; If the value of the super_class item is zero, then this" + lt +
                          "; class file must represent the class Object, the" + lt +                          
                          "; only class or interface without a direct superclass." + lt +
                          "; See JVM spec.";
        } else {
            cpConstant = cp.getConstant(superClass);
            if (cpConstant.get(0).intValue() == CONSTANT_Class) {
                cpIndex = (cpConstant.get(1).intValue() << 8) | cpConstant.get(2).intValue();
                cpConstant = cp.getConstant(cpIndex);
                superString = getStringCONSTANT_Utf8(cpConstant);
            }
        }
        
           
        
        
                
        // .implements
        int interfacesCount = ifs.getInterfacesCount();
        int[] interfaces = ifs.getInterfaces();
        
        for (i = 0; i < interfacesCount; i++) {
            cpIndex = (interfaces[i * 2] << 8) | interfaces[(i * 2) + 1];
            cpConstant = cp.getConstant(cpIndex);
            if (cpConstant.get(0).intValue() == CONSTANT_Class) {
                cpIndex = (cpConstant.get(1).intValue() << 8) | cpConstant.get(2).intValue();
                cpConstant = cp.getConstant(cpIndex);
                String interfaceName = getStringCONSTANT_Utf8(cpConstant);
                implementsBuffer.append(".implements " + interfaceName + lt); 
            }
        }
        
        
        
        
        
        // build assembler source text (header)
        headerBuffer.append("; " + simpleNameClass + ".j" + lt);
        headerBuffer.append(lt);
        
        if(can_header) {
            headerBuffer.append("; Generated by ClassFileAnalyzer (Can)" + lt);
            headerBuffer.append("; Analyzer and Disassembler for Java class files" + lt);
            headerBuffer.append("; (Jasmin syntax 2, http://jasmin.sourceforge.net)" + lt);
            headerBuffer.append(";" + lt);
            headerBuffer.append("; ClassFileAnalyzer, version " + 
                                Version.majorVersion + "." +
                                Version.minorVersion + "." +
                                Version.updateVersion + " " +
                                Version.addVersion + lt);
            headerBuffer.append(lt);
            headerBuffer.append(lt);
        }
        
        // . bytecode
        headerBuffer.append(".bytecode " + majorVersion + "." + minorVersion + lt);
        
        // .source
        if (!sourceString.equals("")) { headerBuffer.append(sourceString); }
        
        // .class, .interface
        headerBuffer.append(classInterfaceBuffer);
        
        // .super
        if (!superString.equals("")) { headerBuffer.append(".super " + superString + lt); }
        
        // .implements
        headerBuffer.append(implementsBuffer);
        
        // .signature
        if (!signatureString.equals("")) { headerBuffer.append(signatureString); }
        
        // .enclosing method (before .inner, Jasmin v2.2)
        if (!enclosingMethodString.equals("")) { headerBuffer.append(enclosingMethodString); }
        
        // .deprecated
        if (!deprecatedString.equals("")) { headerBuffer.append(deprecatedString); }
        
        // .annotation visible
        if (!runtimeVisibleAnnotationsString.equals("")) { headerBuffer.append(runtimeVisibleAnnotationsString); }
        
        // .annotation invisible
        if (!runtimeInvisibleAnnotationsString.equals("")) { headerBuffer.append(runtimeInvisibleAnnotationsString); }
        
        // .attribute
        // ...
        
        // .debug
        if (!sourceDebugExtensionString.equals("")) { headerBuffer.append(sourceDebugExtensionString); }
        
        // .inner
        if (!innerString.equals("")) { headerBuffer.append(innerString); }
        
        
        // not implemented attributes (ClassFile)
        headerBuffer.append(notImplementedAttributeBuffer);
                
        buffer.append(headerBuffer);
        buffer.append(lt);
    } 
        
    
    
    
    
    public void buildFields(Fields fds, ConstantPool cp) throws ClassFormatError {
        
        int[] field_info;
            int access_flags;
            int name_index;
            int descriptor_index;
            int attributes_count;
            int[] attributes;
            
        StringBuffer fieldBuffer;
        StringBuffer fieldAttributesBuffer;
        StringBuffer notImplementedAttributeBuffer;
        String constantValueString = "";
        boolean endField = false;
                
        int i;
        Vector<Integer> cpConstant;        
        
        int fieldsCount = fds.getFieldsCount();
              
        
        
        for (int fieldNumber = 0; fieldNumber < fieldsCount; fieldNumber++) {
            
            fieldBuffer = new StringBuffer();
            fieldAttributesBuffer = new StringBuffer();
            notImplementedAttributeBuffer = new StringBuffer();
            constantValueString = "";
            
            field_info = fds.getField(fieldNumber);
            
            fieldBuffer.append(".field ");
            
            
            // access_flags
            access_flags = (field_info[0] << 8) | field_info[1];
            if (access_flags != 0) {
                if ((access_flags & ACC_PUBLIC) == ACC_PUBLIC) {
                    fieldBuffer.append("public ");
                }
                if ((access_flags & ACC_PRIVATE) == ACC_PRIVATE) {
                    fieldBuffer.append("private ");
                }
                if ((access_flags & ACC_PROTECTED) == ACC_PROTECTED) {
                    fieldBuffer.append("protected ");
                }
                if ((access_flags & ACC_STATIC) == ACC_STATIC) {
                    fieldBuffer.append("static ");
                }
                if ((access_flags & ACC_FINAL) == ACC_FINAL) {
                    fieldBuffer.append("final ");
                }
                if ((access_flags & ACC_VOLATILE) == ACC_VOLATILE) {
                    fieldBuffer.append("volatile ");
                }
                if ((access_flags & ACC_TRANSIENT) == ACC_TRANSIENT) {
                    fieldBuffer.append("transient ");
                }
                if ((access_flags & ACC_SYNTHETIC) == ACC_SYNTHETIC) {
                    fieldBuffer.append("synthetic ");
                }
                if ((access_flags & ACC_ENUM) == ACC_ENUM) {
                    fieldBuffer.append("enum ");
                }
            }
            
                               
            // name_index
            name_index = (field_info[2] << 8) | field_info[3];
            cpConstant = cp.getConstant(name_index);
            String fieldName = getStringCONSTANT_Utf8(cpConstant);
            fieldBuffer.append(fieldName + " "); 
            
            
            // descriptor_index
            descriptor_index = (field_info[4] << 8) | field_info[5];
            cpConstant = cp.getConstant(descriptor_index);
            String fieldDescriptor = getStringCONSTANT_Utf8(cpConstant);
            fieldBuffer.append(fieldDescriptor); 
            
            
            // attributes_count
            attributes_count = (field_info[6] << 8) | field_info[7];
            
            
            // attributes
            int attributesAreaLength = field_info.length - 8;
            attributes = new int[attributesAreaLength];
            for (i = 0; i < attributesAreaLength; i ++) {
                attributes[i] = field_info[8 + i];
            }
            
            
            // attributes field_info
            int[] attribute_info;
                int attribute_name_index;
                int attribute_length;
            
            String attributeName = "";
            int pointerAttributesArray = 0;
            
            
            
            fieldAttributesBuffer = new StringBuffer();
            
            // BEGIN: attributes field_info
            for (int attributeNumber = 0; attributeNumber < attributes_count; attributeNumber++) {
                
                attribute_name_index = (attributes[pointerAttributesArray] << 8) | 
                                        attributes[pointerAttributesArray + 1];
                attribute_length = (attributes[pointerAttributesArray + 2] << 24) |
                                   (attributes[pointerAttributesArray + 3] << 16) |
                                   (attributes[pointerAttributesArray + 4] << 8) |
                                    attributes[pointerAttributesArray + 5];
                cpConstant = cp.getConstant(attribute_name_index);
                attributeName = Builder.getStringCONSTANT_Utf8(cpConstant);

                attribute_info = new int[attribute_length + 6];
                for (i = 0; i < (attribute_length + 6); i++) {
                    attribute_info[i] = attributes[pointerAttributesArray];
                    pointerAttributesArray++;
                }
                if (attributeName.equals("ConstantValue")) {
                    ConstantValue constantValue = new ConstantValue(attribute_info, cp);
                    StringBuffer constantValueSourceText = constantValue.getSourceText();
                    constantValueString = constantValueSourceText.toString();
                }
                else if (attributeName.equals("Signature")) {
                    Signature signature = new Signature(attribute_info, cp);
                    StringBuffer signatureSourceText = signature.getSourceText();
                    fieldAttributesBuffer.append("  " + signatureSourceText);
                }
                else if (attributeName.equals("Deprecated")) {
                    classfileanalyzer.attributes.Deprecated deprecated = 
                            new classfileanalyzer.attributes.Deprecated(attribute_info);
                    StringBuffer deprecatedSourceText = deprecated.getSourceText();
                    fieldAttributesBuffer.append("  " + deprecatedSourceText);
                }
                else if (attributeName.equals("RuntimeVisibleAnnotations")) {
                    RuntimeVisibleAnnotations runtimeVisibleAnnotations = 
                            new RuntimeVisibleAnnotations(attribute_info, cp, 2);
                    StringBuffer runtimeVisibleAnnotationsSourceText = runtimeVisibleAnnotations.getSourceText();
                    fieldAttributesBuffer.append(runtimeVisibleAnnotationsSourceText);
                }
                else if (attributeName.equals("RuntimeInvisibleAnnotations")) {
                    RuntimeInvisibleAnnotations runtimeInvisibleAnnotations = 
                            new RuntimeInvisibleAnnotations(attribute_info, cp, 2);
                    StringBuffer runtimeInvisibleAnnotationsSourceText = runtimeInvisibleAnnotations.getSourceText();
                    fieldAttributesBuffer.append(runtimeInvisibleAnnotationsSourceText);
                }
                else {
                    notImplementedAttributeBuffer.append("; not implemented attribute (field_info): " + 
                            attributeName + " = ");
                    for (i = 0; i < attribute_info.length; i++) {
                        if (((i % 16) == 0) ) { notImplementedAttributeBuffer.append(lt + ";     "); }
                        notImplementedAttributeBuffer.append(attribute_info[i] + " ");
                    }
                    notImplementedAttributeBuffer.append(lt);
                }  
                
                                               
                if (attributeName.equals("Signature")                 ||
                    attributeName.equals("Deprecated")                ||
                    attributeName.equals("RuntimeVisibleAnnotations") ||
                    attributeName.equals("RuntimeInvisibleAnnotations")) {
                    endField = true;
                } else {
                    endField = false;
                }
            }
            // END: attributes field_info
                       
            
            
            if (constantValueString.equals("")) {
                fieldBuffer.append(lt);
            } else {
                fieldBuffer.append(constantValueString + lt);
            }
            
            fieldBuffer.append(fieldAttributesBuffer);
            fieldBuffer.append(notImplementedAttributeBuffer);
            
            if (endField) { 
                fieldBuffer.append("  .end field" + lt);
            } 
            buffer.append(fieldBuffer);
        }
        
        
        if (fieldsCount != 0) { buffer.append(lt); }
    }
      
    
    
    
    
    public void buildMethods(Methods mds, ConstantPool cp) throws ClassFormatError {
        
        int[] method_info;
            int access_flags;
            int name_index;
            int descriptor_index;
            int attributes_count;
            int[] attributes;
        
        StringBuffer methodBuffer;
        StringBuffer notImplementedAttributeBuffer;
        
        int i;
        Vector<Integer> cpConstant;
        Vector<Integer> methodLabels;
        
        int methodsCount = mds.getMethodsCount();
          
        
        
        for (int methodNumber = 0; methodNumber < methodsCount; methodNumber++) {
            
            methodBuffer = new StringBuffer();
            notImplementedAttributeBuffer = new StringBuffer();
            methodLabels = new Vector<Integer>();
            
            method_info = mds.getMethod(methodNumber);
            
            methodBuffer.append(".method ");
            
            access_flags = (method_info[0] << 8) | method_info[1];
            if (access_flags != 0) {
                if ((access_flags & ACC_PUBLIC) == ACC_PUBLIC) {
                    methodBuffer.append("public ");
                }
                if ((access_flags & ACC_PRIVATE) == ACC_PRIVATE) {
                    methodBuffer.append("private ");
                }
                if ((access_flags & ACC_PROTECTED) == ACC_PROTECTED) {
                    methodBuffer.append("protected ");
                }
                if ((access_flags & ACC_STATIC) == ACC_STATIC) {
                    methodBuffer.append("static ");
                }
                if ((access_flags & ACC_FINAL) == ACC_FINAL) {
                    methodBuffer.append("final ");
                }
                if ((access_flags & ACC_SYNCHRONIZED) == ACC_SYNCHRONIZED) {
                    methodBuffer.append("synchronized ");
                }
                if ((access_flags & ACC_NATIVE) == ACC_NATIVE) {
                    methodBuffer.append("native ");
                }
                if ((access_flags & ACC_ABSTRACT) == ACC_ABSTRACT) {
                    methodBuffer.append("abstract ");
                }
                if ((access_flags & ACC_STRICT) == ACC_STRICT) {
                    methodBuffer.append("strictfp ");
                }
                if ((access_flags & ACC_SYNTHETIC) == ACC_SYNTHETIC) {
                    methodBuffer.append("synthetic ");
                }
            }
            
            name_index = (method_info[2] << 8) | method_info[3]; 
            cpConstant = cp.getConstant(name_index);
            String methodName = getStringCONSTANT_Utf8(cpConstant);
            methodBuffer.append(methodName); 
            
            descriptor_index = (method_info[4] << 8) | method_info[5];
            cpConstant = cp.getConstant(descriptor_index);
            String methodDescriptor = getStringCONSTANT_Utf8(cpConstant);
            methodBuffer.append(methodDescriptor + lt); 
            
            if ((access_flags & ACC_BRIDGE) == ACC_BRIDGE) {
                methodBuffer.append("; Flag ACC_BRIDGE set, see JVM spec" + lt);
            }
            if ((access_flags & ACC_VARARGS) == ACC_VARARGS) {
                methodBuffer.append("; Flag ACC_VARARGS set, see JVM spec" + lt);
            }
                       
            attributes_count = (method_info[6] << 8) | method_info[7];
            
            int attributesAreaLength = method_info.length - 8;
            attributes = new int[attributesAreaLength];
            for (i = 0; i < attributesAreaLength; i ++) {
                attributes[i] = method_info[8 + i];
            }
                        
                      
            // attributes method_info
            int[] attribute_info;
                int attribute_name_index;
                int attribute_length;
            
            int pointerAttributesArray = 0;
            
                       
            
            // BEGIN: attributes method_info
            for (int attributeNumber = 0; attributeNumber < attributes_count; attributeNumber++) {
                
                attribute_name_index = (attributes[pointerAttributesArray] << 8) | 
                                        attributes[pointerAttributesArray + 1];
                attribute_length = (attributes[pointerAttributesArray + 2] << 24) |
                                   (attributes[pointerAttributesArray + 3] << 16) |
                                   (attributes[pointerAttributesArray + 4] << 8) |
                                    attributes[pointerAttributesArray + 5];
                cpConstant = cp.getConstant(attribute_name_index);
                String attributeName = Builder.getStringCONSTANT_Utf8(cpConstant);

                attribute_info = new int[attribute_length + 6];
                for (i = 0; i < (attribute_length + 6); i++) {
                    attribute_info[i] = attributes[pointerAttributesArray];
                    pointerAttributesArray++;
                }
                                      
            
                if (attributeName.equals("Code")) {
                    Code code = new Code(attribute_info, cp);
                    StringBuffer codeSourceText = code.getSourceText();
                    Vector<Integer> codeLabels = code.getLabels();
                    for (i = 0; i < codeLabels.size(); i++) {
                        methodLabels.add(codeLabels.get(i));
                    }
                    methodBuffer.append(codeSourceText);
                }
                else if (attributeName.equals("Exceptions")) {
                    Exceptions exceptions = new Exceptions(attribute_info, cp);
                    StringBuffer exceptionsSourceText = exceptions.getSourceText();
                    methodBuffer.append(exceptionsSourceText);
                }
                else if (attributeName.equals("Signature")) {
                    Signature signature = new Signature(attribute_info, cp);
                    StringBuffer signatureSourceText = signature.getSourceText();
                    methodBuffer.append("  " + signatureSourceText);
                }
                else if (attributeName.equals("Deprecated")) {
                    classfileanalyzer.attributes.Deprecated deprecated = 
                            new classfileanalyzer.attributes.Deprecated(attribute_info);
                    StringBuffer deprecatedSourceText = deprecated.getSourceText();
                    methodBuffer.append("  " + deprecatedSourceText);
                }
                else if (attributeName.equals("RuntimeVisibleAnnotations")) {
                    RuntimeVisibleAnnotations runtimeVisibleAnnotations = 
                            new RuntimeVisibleAnnotations(attribute_info, cp, 2);
                    StringBuffer runtimeVisibleAnnotationsSourceText = runtimeVisibleAnnotations.getSourceText();
                    methodBuffer.append(runtimeVisibleAnnotationsSourceText);
                }
                else if (attributeName.equals("RuntimeInvisibleAnnotations")) {
                    RuntimeInvisibleAnnotations runtimeInvisibleAnnotations = 
                            new RuntimeInvisibleAnnotations(attribute_info, cp, 2);
                    StringBuffer runtimeInvisibleAnnotationsSourceText = runtimeInvisibleAnnotations.getSourceText();
                    methodBuffer.append(runtimeInvisibleAnnotationsSourceText);
                }
                else if (attributeName.equals("RuntimeVisibleParameterAnnotations")) {
                    RuntimeVisibleParameterAnnotations runtimeVisibleParameterAnnotations = 
                            new RuntimeVisibleParameterAnnotations(attribute_info, cp, 2);
                    StringBuffer runtimeVisibleParameterAnnotationsSourceText = 
                            runtimeVisibleParameterAnnotations.getSourceText();
                    methodBuffer.append(runtimeVisibleParameterAnnotationsSourceText);
                }
                else if (attributeName.equals("RuntimeInvisibleParameterAnnotations")) {
                    RuntimeInvisibleParameterAnnotations runtimeInvisibleParameterAnnotations = 
                            new RuntimeInvisibleParameterAnnotations(attribute_info, cp, 2);
                    StringBuffer runtimeInvisibleParameterAnnotationsSourceText = 
                            runtimeInvisibleParameterAnnotations.getSourceText();
                    methodBuffer.append(runtimeInvisibleParameterAnnotationsSourceText);
                }
                else if (attributeName.equals("AnnotationDefault")) {
                    AnnotationDefault annotationDefault = 
                            new AnnotationDefault(attribute_info, cp, 2);
                    StringBuffer annotationDefaultSourceText = annotationDefault.getSourceText();
                    methodBuffer.append(annotationDefaultSourceText);
                }
                else {
                    notImplementedAttributeBuffer.append("; not implemented attribute (method_info): " + 
                            attributeName + " = ");
                    for (i = 0; i < attribute_info.length; i++) {
                        if (((i % 16) == 0) ) { notImplementedAttributeBuffer.append(lt + ";     "); }
                        notImplementedAttributeBuffer.append(attribute_info[i] + " ");
                    }
                    notImplementedAttributeBuffer.append(lt);
                }  

            }
            // END: attributes method_info
              
            
    
            methodBuffer.append(".end method" + lt);
            methodBuffer.append(notImplementedAttributeBuffer);
            methodBuffer.append(lt);
                        
            methodBuffer = removeUnusedLabels(methodBuffer, methodLabels);
            
            buffer.append(methodBuffer);
        } 
    }
    
    
    
    
    
    public StringBuffer removeUnusedLabels(StringBuffer methodBuffer, 
                                           Vector<Integer> methodLabels) {
        
        int labelIndex = 0;
        int eolIndex = 0;
        String labelString = "";
                     
        while (labelIndex <= methodBuffer.lastIndexOf("Label")) {
            labelIndex = methodBuffer.indexOf("Label", labelIndex);
            eolIndex = methodBuffer.indexOf(lt, labelIndex);
            labelString = methodBuffer.substring(labelIndex + 5, eolIndex);

            if (labelString.charAt(labelString.length() - 1) == ':') {
                labelString = labelString.substring(0, labelString.length() - 1);

                if (!methodLabels.contains(new Integer(labelString))) { 
                    methodBuffer.delete(labelIndex, eolIndex + lt.length());
                }
            }
            labelIndex = labelIndex + labelString.length();
        }
        return methodBuffer;
    }
    
    
    
    
        
    public static String getStringCONSTANT_Utf8(Vector<Integer> vi) {
        String s = "";
        if (vi.get(0).intValue() == CONSTANT_Utf8) {
            for (int i = 3; i < vi.size(); i++) {
                s = s + (char)vi.get(i).intValue();
            }
            
            s = s.replace("\\", "\\\\");
            s = s.replace("\b", "\\b");
            s = s.replace("\t", "\\t");
            s = s.replace("\n", "\\n");
            s = s.replace("\f", "\\f");
            s = s.replace("\r", "\\r");
            s = s.replace("\"", "\\\"");
            s = s.replace("\'", "\\\'");
        }
        return s;
    }
    
    
    
    
       
    public static int getIntegerCONSTANT_Integer(Vector<Integer> vi) {
        int integer = 0;
        if (vi.get(0).intValue() == CONSTANT_Integer) {
            integer = (vi.get(1).intValue() << 24) | 
                      (vi.get(2).intValue() << 16) | 
                      (vi.get(3).intValue() << 8) |    
                       vi.get(4).intValue();
        }
        return integer;
    }
    
    
    
    
    
    public static long getLongCONSTANT_Long(Vector<Integer> vi) {
        long l = 0;
        if (vi.get(0).intValue() == CONSTANT_Long) {
            l = ((long)vi.get(1).intValue() << 56) | 
                ((long)vi.get(2).intValue() << 48) | 
                ((long)vi.get(3).intValue() << 40) | 
                ((long)vi.get(4).intValue() << 32) |
                ((long)vi.get(5).intValue() << 24) | 
                ((long)vi.get(6).intValue() << 16) | 
                ((long)vi.get(7).intValue() << 8) | 
                 (long)vi.get(8).intValue();
        }
        return l;
    }
    
    
    
    
    
    public static float getFloatCONSTANT_Float(Vector<Integer> vi) {
        float f = 0.0f;
        if (vi.get(0).intValue() == CONSTANT_Float) {
            int bitsI = (vi.get(1).intValue() << 24) | 
                        (vi.get(2).intValue() << 16) |
                        (vi.get(3).intValue() << 8) | 
                         vi.get(4).intValue();
            f = Float.intBitsToFloat(bitsI);
        }
        return f;
    }
    
    
    
    
    
    public static double getDoubleCONSTANT_Double(Vector<Integer> vi) {
        double d = 0.0;
        if (vi.get(0).intValue() == CONSTANT_Double) {
            long bitsL = ((long)vi.get(1).intValue() << 56) | 
                         ((long)vi.get(2).intValue() << 48) | 
                         ((long)vi.get(3).intValue() << 40) | 
                         ((long)vi.get(4).intValue() << 32) |
                         ((long)vi.get(5).intValue() << 24) | 
                         ((long)vi.get(6).intValue() << 16) | 
                         ((long)vi.get(7).intValue() << 8) | 
                          (long)vi.get(8).intValue();
            d = Double.longBitsToDouble(bitsL);
        }
        return d;
    }
    
    
    
    

    public static String getClassNameCONSTANT_Class(Vector<Integer> vi, ConstantPool cp) {
        String className = "";
        if (vi.get(0).intValue() == CONSTANT_Class) {
            int index = (vi.get(1).intValue() << 8) | vi.get(2).intValue();
            Vector<Integer> cpConstant = cp.getConstant(index);
            className = getStringCONSTANT_Utf8(cpConstant);
        }
        return className;
    }
    
    
    
    
    
    public static String getNameCONSTANT_NameAndType(Vector<Integer> vi, ConstantPool cp) {
        String name = "";
        if (vi.get(0).intValue() == CONSTANT_NameAndType) {
            int index = (vi.get(1).intValue() << 8) | vi.get(2).intValue();
            Vector<Integer> cpConstant = cp.getConstant(index);
            name = getStringCONSTANT_Utf8(cpConstant);
        }
        return name;
    }
    
    
    
    
    
    public static String getTypeCONSTANT_NameAndType(Vector<Integer> vi, ConstantPool cp) {
        String type = "";
        if (vi.get(0).intValue() == CONSTANT_NameAndType) {
            int index = (vi.get(3).intValue() << 8) | vi.get(4).intValue();
            Vector<Integer> cpConstant = cp.getConstant(index);
            type = getStringCONSTANT_Utf8(cpConstant);
        }
        return type;
    }
    
        
    
    
    
    public static String[] getFieldInfoCONSTANT_Fieldref(Vector<Integer> vi, ConstantPool cp) {
        String[] sa = new String[3];
        if (vi.get(0).intValue() == CONSTANT_Fieldref) {
            int index = (vi.get(1).intValue() << 8) | vi.get(2).intValue();
            Vector<Integer> cpConstant = cp.getConstant(index); // CONSTANT_Class
            sa[0]  = getClassNameCONSTANT_Class(cpConstant, cp);
            index = (vi.get(3).intValue() << 8) | vi.get(4).intValue();
            cpConstant = cp.getConstant(index); // CONSTANT_NameAndType
            sa[1] = getNameCONSTANT_NameAndType(cpConstant, cp);
            sa[2] = getTypeCONSTANT_NameAndType(cpConstant, cp);
        }
        return sa;
    }
    
    
    
    
    
    public static String[] getMethodInfoCONSTANT_Methodref(Vector<Integer> vi, ConstantPool cp) {
        String[] sa = new String[3];
        if ((vi.get(0).intValue() == CONSTANT_Methodref) || 
                (vi.get(0).intValue() == CONSTANT_InterfaceMethodref)) {
            int index = (vi.get(1).intValue() << 8) | vi.get(2).intValue();
            Vector<Integer> cpConstant = cp.getConstant(index); // CONSTANT_Class
            sa[0]  = getClassNameCONSTANT_Class(cpConstant, cp);
            index = (vi.get(3).intValue() << 8) | vi.get(4).intValue();
            cpConstant = cp.getConstant(index); // CONSTANT_NameAndType
            sa[1] = getNameCONSTANT_NameAndType(cpConstant, cp);
            sa[2] = getTypeCONSTANT_NameAndType(cpConstant, cp);
        }
        return sa;
    }
    
        
    
    
    
    public String getSimpleNameClass() {
        return simpleNameClass;
    }
    
    
    
    
    
    public StringBuffer getAssemblerSourceText() {
        return buffer;
    }
        
    
}

