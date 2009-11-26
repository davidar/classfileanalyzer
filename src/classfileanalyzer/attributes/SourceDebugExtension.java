/*
 * SourceDebugExtension.java
 *
 * @author Harald Roeder
 * @version 2008-02-18
 */


package classfileanalyzer.attributes;


import static classfileanalyzer.Main.lt;





public class SourceDebugExtension {
    
    int[] SourceDebugExtension_attribute;
        int attribute_name_index;
        int attribute_length;
        int[] debug_extension;
    
    StringBuffer sourceText;
    
    
    
    
    
    public SourceDebugExtension(int[] SourceDebugExtension_attribute) {
        this.SourceDebugExtension_attribute = SourceDebugExtension_attribute;
        sourceText = new StringBuffer();
        parse();
    }
    
    
    
    
    
    public void parse() {
        attribute_name_index = (SourceDebugExtension_attribute[0] << 8)  | 
                                SourceDebugExtension_attribute[1];
        attribute_length =     (SourceDebugExtension_attribute[2] << 24) | 
                               (SourceDebugExtension_attribute[3] << 16) |
                               (SourceDebugExtension_attribute[4] << 8)  | 
                                SourceDebugExtension_attribute[5];
        
        debug_extension = new int[attribute_length];
        
        for (int i = 0; i < attribute_length; i++) {
            debug_extension[i] = SourceDebugExtension_attribute[6 + i];
        }
        sourceText.append(".debug \"");
        for (int i = 0; i < debug_extension.length; i++) {
            sourceText.append((char)debug_extension[i]);
        }
        sourceText.append("\"" + lt);
    }
    
    
    
    
    
    public StringBuffer getSourceText() {
        return sourceText;
    }
        
    
}

