/*
 * Main.java
 *
 * @author Harald Roeder
 * @version 2008-02-17
 */


package classfileanalyzer;


import classfileanalyzer.attributes.Code;
import classfileanalyzer.util.*;
import java.io.*;
import java.util.*;





public class Main {
    
    public static final String lt = System.getProperty("line.separator");
           
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        String fileName = "";
        String jarName = "";
                
        boolean option = true;
        boolean version = false;
        boolean file = false;
        boolean info = false;
        boolean nopc = false;
        boolean help = false;
        
        Vector<String> options = new Vector<String>();
        Vector<String> sources = new Vector<String>();
                
        for (int i = 0; i < args.length; i++) {
            if ((args[i].startsWith("-")) && (option == true)) {
                options.add(args[i]);
              } else {
                option = false;
                sources.add(args[i]);
              }  
        }
        
        for (int i = 0; i < options.size(); i++) {
            String str = options.get(i);
            if (str.equals("-version")) { version = true; }
            if (str.equals("-file")) { file = true; }
            if (str.equals("-info")) { info = true; }
            if (str.equals("-nopc")) { nopc = true; }
            if (str.equals("-help") || str.equals("-?")) { help = true; }
        }
                
        if (sources.size() == 1) {
            fileName = sources.get(0);
        } 
        else if (sources.size() == 2) {
            jarName = sources.get(0);
            fileName = sources.get(1);
        }
             
        // print version number and exit
        if (version) {
            System.out.println(
                "ClassFileAnalyzer (Can), Version " +
                Version.majorVersion + "." +
                Version.minorVersion + "." +
                Version.updateVersion + " " +
                Version.addVersion
            );
            System.exit(0);
        }
        
        if (help || (sources.size() < 1) || (sources.size() > 2)) {
            String usage = "Usage:" + lt +
                lt +
                "java -jar classfileanalyzer.jar [options] [<jarfile>] <classfile>" + lt +
                lt +
                "java ClassFileAnalyzer [options] [<jarfile>] <classfile>" + lt +
                "java Can [options] [<jarfile>] <classfile>" + lt +
                lt +
                "classfile:" + lt +
                "  Java class file to be disassembled." + lt +
                lt +
                "jarfile:" + lt +
                "  Java Archive (JAR) file, which includes the Java class file to be" + lt +
                "  disassembled (optional)." + lt +
                lt +
                "options:" + lt +
                "  -version" + lt +
                "    Print version information." + lt +
                "  -file" + lt +
                "    Generate file in actual directory with assembler source text." + lt +
                "  -info" + lt +
                "    Print additional informations, e.g. hexdump of classfile." + lt +
                "  -nopc" + lt +
                "    Omit pc label before mnemonic." + lt +
                "  -help, -?" + lt +
                "    Print usage message." + lt;
                    
            System.out.println(usage);
            System.exit(0);
        }
                 
        ClassFile clazz;
        if (jarName.equals("")) {
            clazz = new ClassFile(fileName);
        } else {
            clazz = new ClassFile(jarName, fileName);
        }
        try {
            clazz.parse();
        } catch (ClassFormatError e) {
            System.out.println(e.toString());
            System.exit(1);
        }
        
        // dump additional informations
        if (info) {
            Info infos = new Info(clazz, fileName);
            StringBuffer infoBuffer = infos.getInfos();
            System.out.print(infoBuffer);
        }
                
        // omit pc label before mnemonic
        if (nopc) {
            Code.nopc = true;
        }
        
        
        // build Jasmin source text
        int[] minor_version = clazz.getMinorVersion();
        int[] major_version = clazz.getMajorVersion();
        ConstantPool cp = clazz.getConstantPool();
        int[] access_flags = clazz.getAccessFlags();
        int[] this_class = clazz.getThisClass();
        int[] super_class = clazz.getSuperClass();
        Interfaces ifs = clazz.getInterfaces();
        Fields fds = clazz.getFields();
        Methods mds = clazz.getMethods();
        Attributes aes = clazz.getAttributes();
        
        Builder builder = new Builder();
        try {
            builder.buildHeader(minor_version, 
                                major_version, 
                                cp,
                                access_flags, 
                                this_class, 
                                super_class,
                                ifs,
                                aes);
            builder.buildFields(fds, cp);
            builder.buildMethods(mds, cp);
        } catch (ClassFormatError e) {
            System.out.println(e.toString());
            System.exit(1);
        }
                
        StringBuffer assemblerSourceText = builder.getAssemblerSourceText();
        if (file) {
            String simpleNameClass = builder.getSimpleNameClass();
            String sourceTextFile = simpleNameClass + ".j";
            try {
                FileWriter writer = new FileWriter(sourceTextFile);
                writer.write(assemblerSourceText.toString());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Generated: " + sourceTextFile);
        } else {
            System.out.println(assemblerSourceText);
        }
    }
        
    
}

