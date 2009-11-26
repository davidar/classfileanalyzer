/*
 * ClassFile.java
 *
 * @author Harald Roeder
 * @version 2008-02-17
 */


package classfileanalyzer;


import classfileanalyzer.util.*;
import java.io.*;
import java.util.*;
import java.util.jar.*;





/**  
 * The ClassFile Structure:<br>
 *  <br>
 *  <pre>ClassFile {
 *    	u4 magic;
 *  	u2 minor_version;
 *  	u2 major_version;
 *  	u2 constant_pool_count;
 *  	cp_info constant_pool[constant_pool_count-1];
 *  	u2 access_flags;
 *  	u2 this_class;
 *  	u2 super_class;
 *  	u2 interfaces_count;
 *  	u2 interfaces[interfaces_count];
 *  	u2 fields_count;
 *  	field_info fields[fields_count];
 *  	u2 methods_count;
 *  	method_info methods[methods_count];
 *  	u2 attributes_count;
 *  	attribute_info attributes[attributes_count];
 *  }</pre>
 */
    
public class ClassFile {

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
        
    protected static int accessFlagsBegin = -1;
    protected static int interfacesCountBegin = -1;
    protected static int fieldsCountBegin = -1;
    protected static int methodsCountBegin = -1;
    protected static int attributesCountBegin = -1;
    
    String fileName, jarName;
    int fileLength;
                
    
    
    
    
    public ClassFile() {
        this.magic = new int[4];
        this.minor_version = new int[2];
        this.major_version = new int[2];
        this.constant_pool_count = new int[2];
        this.access_flags = new int[2];
        this.this_class = new int[2];
        this.super_class = new int[2];
        this.interfaces_count = new int[2];
        this.fields_count = new int[2];
        this.methods_count = new int[2];
        this.attributes_count = new int[2];
    }
    
    
    
    
    
    public ClassFile(String fileName) {
        this(); // invoke constructor ClassFile()
        File f = new File(fileName);
        this.fileLength = (int)f.length();
        this.classBytes = new int[fileLength];
        this.fileName = fileName;
                
        int data;
        int i = 0;
        
        try {
            FileInputStream reader = new FileInputStream(fileName);
            while ((data = reader.read()) != -1) {
                classBytes[i] = data;
                i++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    
    
    public ClassFile(String jarName, String fileName) {
        this(); // invoke constructor ClassFile()
        this.fileName = fileName;
        this.jarName = jarName;
                
        int data;
        int i = 0;
                
        try {
            JarFile jarFile = new JarFile(jarName);
            JarEntry jarEntry = jarFile.getJarEntry(fileName);
            
            this.fileLength = (int)jarEntry.getSize();
            this.classBytes = new int[fileLength];
                        
            InputStream input = jarFile.getInputStream(jarEntry);
            BufferedInputStream reader = new BufferedInputStream(input);
            while ((data = reader.read()) != -1) {
                classBytes[i] = data;
                i++;
            }
            reader.close();
            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    
    
    public void parse() {
        magic[0] = classBytes[0];
        magic[1] = classBytes[1];
        magic[2] = classBytes[2];
        magic[3] = classBytes[3];
      
        if ( (magic[0] != 0xca) ||
             (magic[1] != 0xfe) ||
             (magic[2] != 0xba) ||
             (magic[3] != 0xbe) ) {
            System.out.println("ClassFileAnalyzer Error: " + 
                    "No Java class file, magic number invalid.");
            System.exit(1);
        }
        
        minor_version[0] = classBytes[4];
        minor_version[1] = classBytes[5];
        major_version[0] = classBytes[6];
        major_version[1] = classBytes[7];
        
        constant_pool_count[0] = classBytes[8];
        constant_pool_count[1] = classBytes[9];
        int constantPoolCount = ((constant_pool_count[0] << 8) | constant_pool_count[1]) - 1;
        cp = new ConstantPool(classBytes, constantPoolCount);
        
        access_flags[0] = classBytes[accessFlagsBegin];
        access_flags[1] = classBytes[accessFlagsBegin + 1];
        this_class[0] = classBytes[accessFlagsBegin + 2];
        this_class[1] = classBytes[accessFlagsBegin + 3];
        super_class[0] = classBytes[accessFlagsBegin + 4];
        super_class[1] = classBytes[accessFlagsBegin + 5];
        
        interfaces_count[0] = classBytes[interfacesCountBegin];
        interfaces_count[1] = classBytes[interfacesCountBegin + 1];
        int interfacesCount = (interfaces_count[0] << 8) | interfaces_count[1];
        ifs = new Interfaces(classBytes, interfacesCount);
        
        fields_count[0] = classBytes[fieldsCountBegin];
        fields_count[1] = classBytes[fieldsCountBegin + 1];
        int fieldsCount = (fields_count[0] << 8) | fields_count[1];
        fds = new Fields(classBytes, fieldsCount);
        
        methods_count[0] = classBytes[methodsCountBegin];
        methods_count[1] = classBytes[methodsCountBegin + 1];
        int methodsCount = (methods_count[0] << 8) | methods_count[1];
        mds = new Methods(classBytes, methodsCount);
        
        attributes_count[0] = classBytes[attributesCountBegin];
        attributes_count[1] = classBytes[attributesCountBegin + 1];
        int attributesCount = (attributes_count[0] << 8) | attributes_count[1];
        aes = new Attributes(classBytes, attributesCount);
    }

    
    
    
    
    public int[] getBytes() {
        return classBytes;
    }
    
    
    
    
    
    public int[] getMagic() {
        return magic;
    }
    
    
    
    
    
    public int[] getMinorVersion() {
        return minor_version;
    }
    
    
    
    
    
    public int[] getMajorVersion() {
        return major_version;
    }
    
    
    
    
    
    public int[] getConstantPoolCount() {
        return constant_pool_count;
    }
    
    
    
    
    
    public ConstantPool getConstantPool() {
        return cp;
    }
    
    
    
    
    
    public int[] getAccessFlags() {
        return access_flags;
    }
    
    
    
    
    
    public int[] getThisClass() {
        return this_class;
    }
    
    
    
    
    
    public int[] getSuperClass() {
        return super_class;
    }
    
    
    
    
    
    public int[] getInterfacesCount() {
        return interfaces_count;
    }
    
    
    
    
    
    public Interfaces getInterfaces() {
        return ifs;
    }
    
    
    
    
    
    public int[] getFieldsCount() {
        return fields_count;
    }

    
    
    
    
    public Fields getFields() {
        return fds;
    }
    
    
    
    
    
    public int[] getMethodsCount() {
        return methods_count;
    }
    
    
    
    
        
    public Methods getMethods() {
        return mds;
    }
    
    
    
    
    
    public int[] getAttributesCount() {
        return attributes_count;
    }
        
    
    
    
    
    public Attributes getAttributes() {
        return aes;
    }
        
    
}

