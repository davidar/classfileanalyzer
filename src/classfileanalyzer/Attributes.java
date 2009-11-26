/*
 * Attributes.java
 *
 * @author Harald Roeder
 * @version 2008-02-17
 */


package classfileanalyzer;


import java.util.*;





public class Attributes {
    
    int[] classBytes;
    int attributesCount;
    int[] attribute_info;
        
    Vector<Vector<Integer>> attributes = new Vector<Vector<Integer>>();
    int attributesCountBegin;
        
    
    
    
    
    public Attributes(int[] classBytes, int attributesCount) {
      this.classBytes = classBytes;
      this.attributesCount = attributesCount;
      this.attributesCountBegin = ClassFile.attributesCountBegin;
      this.getAttributes();
    }
    
    
    
    
    
    public Vector<Vector<Integer>> getAttributes() {
        int pointerAttributes = attributesCountBegin + 2;
               
        for (int i = 0; i < attributesCount; i++) {
            Vector<Integer> attribute = new Vector<Integer>();
            
            attribute.add(new Integer(classBytes[pointerAttributes]));
            attribute.add(new Integer(classBytes[pointerAttributes + 1]));
            attribute.add(new Integer(classBytes[pointerAttributes + 2]));
            attribute.add(new Integer(classBytes[pointerAttributes + 3]));
            attribute.add(new Integer(classBytes[pointerAttributes + 4]));
            attribute.add(new Integer(classBytes[pointerAttributes + 5]));
                                                
            int attributeLength = ((classBytes[pointerAttributes + 2] << 24) | 
                                   (classBytes[pointerAttributes + 3] << 16) |
                                   (classBytes[pointerAttributes + 4] << 8) | 
                                    classBytes[pointerAttributes + 5]);
            
            pointerAttributes = pointerAttributes + 6;
                                                                   
            for (int j = 0; j < attributeLength; j++) {
                attribute.add(new Integer(classBytes[pointerAttributes]));
                pointerAttributes++;
            }
            attributes.add(attribute);
        }
        return attributes;
    }
       
    
    
    
    
    public int[] getAttribute(int index) {
        Vector<Integer> attribute = attributes.get(index);
        int attributeInfoSize = attribute.size();
        attribute_info = new int[attributeInfoSize];
        for (int i = 0; i < attributeInfoSize; i++) {
            attribute_info[i] = attribute.get(i).intValue();
        }
        return attribute_info;
    }
  
    
    
    
   
    public int getAttributesCount() {
        return attributesCount;
    }
   
        
    
}

