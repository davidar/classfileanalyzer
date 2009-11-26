/*
 * Info.java
 *
 * @author Harald Roeder
 * @version 2008-02-18
 */


package classfileanalyzer.util;


import classfileanalyzer.*;
import java.util.*;
import static classfileanalyzer.Main.lt;





public class Info {
    
    int[] classBytes;
    
    int[] magic;
    int[] minor_version;
    int[] major_version;
    int[] constant_pool_count;
    ConstantPool cp;
    int[] access_flags;
    int[] this_class;
    int[] super_class;
    int[] interfaces_count;
    Interfaces ifs;
    int[] fields_count;
    Fields fds;
    int[] methods_count;
    Methods mds;
    int[] attributes_count;
    Attributes aes;
   
    String fileName;
    StringBuffer infoBuffer;
   
    
    
   
    
    public Info(ClassFile clazz, String fileName) {
        this.classBytes = clazz.getBytes();
        this.magic = clazz.getMagic();
        this.minor_version = clazz.getMinorVersion();
        this.major_version = clazz.getMajorVersion();
        this.constant_pool_count = clazz.getConstantPoolCount();
        this.cp = clazz.getConstantPool();
        this.access_flags = clazz.getAccessFlags();
        this.this_class = clazz.getThisClass();
        this.super_class = clazz.getSuperClass();
        this.interfaces_count = clazz.getInterfacesCount();
        this.ifs = clazz.getInterfaces();
        this.fields_count = clazz.getFieldsCount();
        this.fds = clazz.getFields();
        this.methods_count = clazz.getMethodsCount();
        this.mds = clazz.getMethods();
        this.attributes_count = clazz.getAttributesCount();
        this.aes = clazz.getAttributes();
        
        this.fileName = fileName;
        infoBuffer = new StringBuffer();
               
        make();
    }
      
    
    
    
    
    public void make() { 
            
        // integers
        infoBuffer.append(fileName + " consists of the following integers " + 
                "(" + classBytes.length + " Bytes):" + lt + lt);
        infoBuffer.append("0: ");
        for (int i = 0; i < classBytes.length; i++) {
            infoBuffer.append(classBytes[i] + " ");
            if (((i + 1) % 16 == 0)) {
                infoBuffer.append(lt);
                infoBuffer.append((i + 1) + ": ");
            }  
        }
        infoBuffer.append(lt + lt + lt);
         

        // hexdump
        infoBuffer.append("Hexdump " + fileName + " (" + classBytes.length + " Bytes):" 
                + lt + lt);
        Hexdump hexdump = new Hexdump(classBytes);
        StringBuffer hexdumpBuffer = hexdump.getHexdump();
        infoBuffer.append(hexdumpBuffer);
        infoBuffer.append(lt + lt);
       

        // magic
        infoBuffer.append("magic: " + 
                           magic[0] + " " +
                           magic[1] + " " +
                           magic[2] + " " +
                           magic[3] + lt);
        infoBuffer.append(lt);


        // minor_version, major_version
        infoBuffer.append("minor_version: " +
                           minor_version[0] + " " +
                           minor_version[1] + lt);
        infoBuffer.append("major_version: " + 
                           major_version[0] + " " +
                           major_version[1] + lt);
        infoBuffer.append(lt);
 

        // constant_pool_count
        infoBuffer.append("constant_pool_count: " + 
                           constant_pool_count[0] + " " +
                           constant_pool_count[1] + lt);
        
        
        // constant_pool
        infoBuffer.append("constant_pool (indexed from 1 to constant_pool_count-1):" + lt);
        
        int constantPoolCount = ((constant_pool_count[0] << 8) | constant_pool_count[1]) - 1;
        
        for (int i = 1; i < (constantPoolCount + 1); i++) {
            Vector<Integer> cpConstant = cp.getConstant(i);

            String constantInfoStructure = "";
            switch (cpConstant.get(0)) {
                case 7:  constantInfoStructure = "CONSTANT_Class_info";
                         break;
                case 9:  constantInfoStructure = "CONSTANT_Fieldref_info";
                         break;
                case 10: constantInfoStructure = "CONSTANT_Methodref_info";
                         break;
                case 11: constantInfoStructure = "CONSTANT_InterfaceMethodref_info";
                         break;
                case 8:  constantInfoStructure = "CONSTANT_String_info";
                         break;
                case 3:  constantInfoStructure = "CONSTANT_Integer_info";
                         break;
                case 4:  constantInfoStructure = "CONSTANT_Float_info";
                         break;
                case 5:  constantInfoStructure = "CONSTANT_Long_info";
                         break;
                case 6:  constantInfoStructure = "CONSTANT_Double_info";
                         break;
                case 12: constantInfoStructure = "CONSTANT_NameAndType_info";
                         break;
                case 1:  constantInfoStructure = "CONSTANT_Utf8_info";
                         break;
                case -1: constantInfoStructure = "All 8-byte constants take up two " + 
                                 "entries in the constant_pool table of the class file.";
                         break;
                default: constantInfoStructure = "";
            }

            infoBuffer.append("  " + i + " (" + constantInfoStructure + "): ");
            for (int j = 0; j < cpConstant.size(); j++) {
                infoBuffer.append(cpConstant.get(j).toString() + " ");
            }
            infoBuffer.append(lt);
        }
        StringBuffer constantPoolBuffer = cp.getConstantPoolBuffer();
        infoBuffer.append(lt + constantPoolBuffer + lt + lt);
       
        
        // access_flags, this_class, super_class
        infoBuffer.append("access_flags: " + 
                          access_flags[0] + " " +
                          access_flags[1] + lt);
        infoBuffer.append("this_class: " +
                          this_class[0] + " " +
                          this_class[1] + lt);
        infoBuffer.append("super_class: " +
                          super_class[0] + " " +
                          super_class[1] + lt);
        infoBuffer.append(lt);

        
        // interfaces_count, interfaces
        infoBuffer.append("interfaces_count: " + 
                          interfaces_count[0] + " " +
                          interfaces_count[1] + lt);
        infoBuffer.append("interfaces:" + lt);
        int[] interfaces = ifs.getInterfaces();
        int k = 0;
        for (int i = 0; i < interfaces.length; i = i + 2) {
            k++;
            infoBuffer.append("  " + k + ": " + interfaces[i] + " " + interfaces[i + 1] + lt);
        }
        infoBuffer.append(lt);
        

        // fields_count, fields
        infoBuffer.append("fields_count: " + 
                          fields_count[0] + " " +
                          fields_count[1] + lt);
        
        int fieldsCount = (fields_count[0] << 8) | fields_count[1];
        
        infoBuffer.append("fields:" + lt);
        for (int i = 0; i < fieldsCount; i++) {
            int[] field_info = fds.getField(i);
            infoBuffer.append("  " + (i+1) + " (field_info): ");
            for (int j = 0; j < field_info.length; j++) {
                infoBuffer.append(field_info[j] + " ");
            }
            infoBuffer.append(lt);
        }
        infoBuffer.append(lt);

        
        // methods_count, methods
        infoBuffer.append("methods_count: " + 
                          methods_count[0] + " " +
                          methods_count[1] + lt);
        
        int methodsCount = (methods_count[0] << 8) | methods_count[1];
        
        infoBuffer.append("methods:" + lt);
        for (int i = 0; i < methodsCount; i++) {
            int[] method_info = mds.getMethod(i);
            infoBuffer.append("  " + (i+1) + " (method_info): ");
            for (int j = 0; j < method_info.length; j ++) {
                infoBuffer.append(method_info[j] + " ");
            }
            infoBuffer.append(lt);
        }
        infoBuffer.append(lt);
       

        // attributes_count, attributes
        infoBuffer.append("attributes_count: " + 
                          attributes_count[0] + " " +
                          attributes_count[1] + lt);
        
        int attributesCount = (attributes_count[0] << 8) | attributes_count[1];
        
        infoBuffer.append("attributes:" + lt);
        for (int i = 0; i < attributesCount; i++) {
            int[] attribute_info = aes.getAttribute(i);
            infoBuffer.append("  " + (i+1) + " (attribute_info): ");
            for (int j = 0; j < attribute_info.length; j ++) {
                infoBuffer.append(attribute_info[j] + " ");
            }
            infoBuffer.append(lt);
        }
        infoBuffer.append(lt);


        infoBuffer.append(lt + lt + lt);
    }
     
      
    
    
    
    public StringBuffer getInfos() {
        return infoBuffer;
    }
        
    
}

