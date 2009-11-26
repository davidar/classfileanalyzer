/*
 * LocalVariableTables.java
 *
 * @author Harald Roeder
 * @version 2008-02-18
 */


package classfileanalyzer.attributes;





public class LocalVariableTables {

    int[] LocalVariableTables_attribute;
        int attribute_name_index;
        int attribute_length;
        int local_variable_tables_length;
        int[] local_variable_tables;
    
    int[][] localVariableTables;
    boolean typeTable;
    
    
    
    
    
    public LocalVariableTables(boolean typeTable, int[] LocalVariableTables_attribute) {
        this.typeTable = typeTable;
        this.LocalVariableTables_attribute = LocalVariableTables_attribute;
        parse();
    }
    
    
    
    
    
    public void parse() {
        attribute_name_index =         (LocalVariableTables_attribute[0] << 8)  | 
                                        LocalVariableTables_attribute[1];
        attribute_length =             (LocalVariableTables_attribute[2] << 24) | 
                                       (LocalVariableTables_attribute[3] << 16) |
                                       (LocalVariableTables_attribute[4] << 8)  | 
                                        LocalVariableTables_attribute[5];
        local_variable_tables_length = (LocalVariableTables_attribute[6] << 8)  | 
                                        LocalVariableTables_attribute[7];
                       
        local_variable_tables = new int[local_variable_tables_length * 10];
        
        for (int i = 0; i < local_variable_tables.length; i++) {
            local_variable_tables[i] = LocalVariableTables_attribute[8 + i];
        }
        
        
        // 0: index 
        // 1: name_index 
        // 2: descriptor_index 
        // 3: signature_index 
        // 4: start_pc 
        // 5: start_pc + length
        localVariableTables = new int[local_variable_tables_length][6];
        
        int entry = 0;
        
        for (int i = 0; i < local_variable_tables.length; i = i + 10) {
            int start_pc =                  (local_variable_tables[i] << 8) | 
                                             local_variable_tables[i + 1];
            int length =                    (local_variable_tables[i + 2] << 8) | 
                                             local_variable_tables[i + 3];
            int name_index =                (local_variable_tables[i + 4] << 8) | 
                                             local_variable_tables[i + 5];
            int descriptorSignature_index = (local_variable_tables[i + 6] << 8) | 
                                             local_variable_tables[i + 7];
            int index =                     (local_variable_tables[i + 8] << 8) | 
                                             local_variable_tables[i + 9];
            
            localVariableTables[entry][0] = index;
            localVariableTables[entry][1] = name_index;
            if (typeTable) { 
                localVariableTables[entry][2] = 0;
                localVariableTables[entry][3] = descriptorSignature_index;
            } else {
                localVariableTables[entry][2] = descriptorSignature_index;
                localVariableTables[entry][3] = 0;
            }
            localVariableTables[entry][4] = start_pc;
            localVariableTables[entry][5] = start_pc + length;
            entry++;
        }  
    }
       
    
    
    
    
    public int[][] getLocalVariableTables() {
        return localVariableTables;
    }
    
    
}

