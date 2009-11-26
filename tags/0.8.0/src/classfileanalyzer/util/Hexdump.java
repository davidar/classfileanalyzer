/*
 * Hexdump.java
 *
 * @author Harald Roeder
 * @version 2008-02-18
 */


package classfileanalyzer.util;


import static classfileanalyzer.Main.lt;





public class Hexdump {
     
    int[] classBytes;
    StringBuffer dump;

    
    
    

    public Hexdump(int[] classBytes) {
        this.classBytes = classBytes;
        dump = new StringBuffer();
        parse();
    }
    
    
    
  
  
    public void parse() {
        int i = 1; 
        int j = 0;
        StringBuffer ascii = new StringBuffer();
        
        for (int k = 0; k < classBytes.length; k++) {
            String hex = Integer.toHexString(classBytes[k]);
            if (hex.length() == 1) { hex = "0" + hex; } 
            dump.append(hex + " ");
            // ascii
            if ((classBytes[k] >= 32) && (classBytes[k] < 127)) {
                ascii.append((char)classBytes[k]);
            } else {
                ascii.append('.');
            }
            if (i % 16 == 0) {
                j = i - 16; 
                dump.append("    " + ascii + "     " + j + lt);
                ascii.setLength(0);
            }  
            i++; 
        }
        // last line
        int lastBytes = (i % 16) - 1;
        if (lastBytes > 0) {
            for (int k = 0; k < (16 - lastBytes); k++) { dump.append("   "); }
            dump.append("    " + ascii + "     ");
            for (int k = 0; k < (16 - lastBytes); k++) { dump.append(" "); }
            dump.append(j + 16);
        }
    }
    
    
    
    
    
    public StringBuffer getHexdump() {
        return dump;
    }
    
    
}

