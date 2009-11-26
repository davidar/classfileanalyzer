/*
 * LineNumberTable.java
 *
 * @author Harald Roeder
 * @version 2008-02-18
 */


package classfileanalyzer.attributes;


import java.util.*;





public class LineNumberTable {
    
    int[] LineNumberTable_attribute;
        int attribute_name_index;
        int attribute_length;
        int line_number_table_length;
        int[] line_number_table;
    
    HashMap<Integer,Integer> linesNumbers;
    
    
    
    
    
    public LineNumberTable(int[] LineNumberTable_attribute) {
        this.LineNumberTable_attribute = LineNumberTable_attribute;
        parse();
    }
    
    
    
    
    
    public void parse() {
        attribute_name_index = (LineNumberTable_attribute[0] << 8) | 
                                LineNumberTable_attribute[1];
        attribute_length = (LineNumberTable_attribute[2] << 24) | 
                           (LineNumberTable_attribute[3] << 16) |
                           (LineNumberTable_attribute[4] << 8) | 
                            LineNumberTable_attribute[5];
        line_number_table_length = (LineNumberTable_attribute[6] << 8) | 
                                    LineNumberTable_attribute[7];
        
        line_number_table = new int[line_number_table_length * 4];
        
        for (int i = 0; i < line_number_table.length; i++) {
            line_number_table[i] = LineNumberTable_attribute[8 + i];
        }
        
        linesNumbers = new HashMap<Integer,Integer>();
        for (int i = 0; i < line_number_table.length; i = i + 4) {
           int key = (line_number_table[i] << 8) | line_number_table[i + 1];
           int value = (line_number_table[i + 2] << 8) | line_number_table[i + 3];
           linesNumbers.put(key, value);
        }
    }
    
    
    
    
    
    public HashMap<Integer,Integer> getLinesNumbers() {
        return linesNumbers;
    }
    
    
}

