/*
 * Fields.java
 *
 * @author Harald Roeder
 * @version 2008-02-17
 */


package classfileanalyzer;


import java.util.*;



 

public class Fields {
    
    int[] classBytes;
    int fieldsCount;
    int[] field_info;
        
    Vector<Vector<Integer>> fields = new Vector<Vector<Integer>>();
    int fieldsCountBegin;
        
    
    
    
    
    public Fields(int[] classBytes, int fieldsCount) {
      this.classBytes = classBytes;
      this.fieldsCount = fieldsCount;
      this.fieldsCountBegin = ClassFile.fieldsCountBegin;
      parse();
    }
    
    
    
    
 
    private void parse() {
        int pointerFields = fieldsCountBegin + 2;
               
        for (int fieldNumber = 0; fieldNumber < fieldsCount; fieldNumber++) {
            Vector<Integer> field = new Vector<Integer>();
            field.add(new Integer(classBytes[pointerFields]));
            field.add(new Integer(classBytes[pointerFields + 1]));
            field.add(new Integer(classBytes[pointerFields + 2]));
            field.add(new Integer(classBytes[pointerFields + 3]));
            field.add(new Integer(classBytes[pointerFields + 4]));
            field.add(new Integer(classBytes[pointerFields + 5]));
            field.add(new Integer(classBytes[pointerFields + 6]));
            field.add(new Integer(classBytes[pointerFields + 7]));
            
            int fieldAttributesCount = ((classBytes[pointerFields + 6] << 8) | 
                                         classBytes[pointerFields  + 7]);
            
            pointerFields = pointerFields + 8;
                                
            for (int i = 0; i < fieldAttributesCount; i++) {
                field.add(new Integer(classBytes[pointerFields]));
                field.add(new Integer(classBytes[pointerFields + 1]));
                field.add(new Integer(classBytes[pointerFields + 2]));
                field.add(new Integer(classBytes[pointerFields + 3]));
                field.add(new Integer(classBytes[pointerFields + 4]));
                field.add(new Integer(classBytes[pointerFields + 5]));
                                                
                int fieldAttributesLength = ((classBytes[pointerFields + 2] << 24) | 
                                             (classBytes[pointerFields + 3] << 16) |
                                             (classBytes[pointerFields + 4] << 8) | 
                                              classBytes[pointerFields + 5]);
                
                pointerFields = pointerFields + 6;
                                                
                for (int j = 0; j < fieldAttributesLength; j++) {
                    field.add(new Integer (classBytes[pointerFields]));
                    pointerFields++;
                }
            }
            fields.add(field);
        }
        ClassFile.methodsCountBegin = pointerFields;
    }
    
    
    
    
    
    public int getFieldsCount() {
        return fieldsCount;
    }
    
    
    
    
    
    public int[] getField(int index) {
        Vector<Integer> field = fields.get(index);
        int fieldInfoSize = field.size();
        field_info = new int[fieldInfoSize];
        for (int i = 0; i < fieldInfoSize; i++) {
            field_info[i] = field.get(i).intValue();
        }
        return field_info;
    }
    
    
    
    
    
    public Vector<Vector<Integer>> getFields() {
        return fields;
    }
            
    
}

