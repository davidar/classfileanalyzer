/*
 * Deprecated.java
 *
 * @author Harald Roeder
 * @version 2008-02-18
 */


package classfileanalyzer.attributes;


import static classfileanalyzer.Main.lt;





public class Deprecated {
    
    int[] Deprecated_attribute;
        int attribute_name_index;
        int attribute_length;
               
    StringBuffer sourceText;
    
    
    
    
    
    public Deprecated(int[] Deprecated_attribute) {
        this.Deprecated_attribute = Deprecated_attribute;
        sourceText = new StringBuffer();
        parse();
    }
    
    
    
    
    
    public void parse() {
        attribute_name_index = (Deprecated_attribute[0] << 8)  | 
                                Deprecated_attribute[1];
        // value of the attribute_length item is zero.
        attribute_length =     (Deprecated_attribute[2] << 24) | 
                               (Deprecated_attribute[3] << 16) |
                               (Deprecated_attribute[4] << 8)  | 
                                Deprecated_attribute[5];
        
        sourceText.append(".deprecated" + lt);
    }
    
    
    
    
    
    public StringBuffer getSourceText() {
        return sourceText;
    }
        
    
}

