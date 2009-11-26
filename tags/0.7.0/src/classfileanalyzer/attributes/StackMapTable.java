/*
 * StackMapTable.java
 *
 * @author Harald Roeder
 * @version 2008-02-18
 */


package classfileanalyzer.attributes;


import classfileanalyzer.*;
import java.util.*;
import static classfileanalyzer.Main.lt;





public class StackMapTable {
    
    int[] StackMapTable_attribute;
        int attribute_name_index;
        int attribute_length;
        int number_of_entries;
        int[] entries;
              
    ConstantPool cp;
    int indent;
    String indentString;
    
    int pointerEntries = 0;
            
    Vector<String> localsFrame;
    Vector<String> stackFrame;
    Vector<Integer> frameBytes;
    Vector<Vector<String>> localsVector = new Vector<Vector<String>>();
    Vector<Vector<String>> stackVector = new Vector<Vector<String>>();
            
    StringBuffer sourceText;
    StringBuffer frameSourceText;
    StringBuffer frameComment;
    
    private static final int STACK_LOCALS = 1;
    private static final int STACK_STACK = 2;
    
    
    
    
    
    public StackMapTable(int[] StackMapTable_attribute, ConstantPool cp, int indent) {
        this.StackMapTable_attribute = StackMapTable_attribute;
        this.cp = cp;
        
        this.indent = indent;
        if (indent == 0) { indentString = ""; }
        if (indent == 2) { indentString = "  "; }
        
        sourceText = new StringBuffer();
        parse();
    }
      
    
    
    
    
    public void verification_type_info(int tag, int localsStack) {
        
        switch (tag) {
                    
            // Top_variable_info
            case 0:
                if (localsStack == STACK_LOCALS) {
                    localsFrame.add("Top");
                    frameSourceText.append(indentString + "  locals Top" + lt);
                } else if (localsStack == STACK_STACK) {
                    stackFrame.add("Top");
                    frameSourceText.append(indentString + "  stack Top" + lt);
                }
                break;
                
            // Integer_variable_info
            case 1:
                if (localsStack == STACK_LOCALS) {
                    localsFrame.add("Integer");
                    frameSourceText.append(indentString + "  locals Integer" + lt);
                } else if (localsStack == STACK_STACK) {
                    stackFrame.add("Integer");
                    frameSourceText.append(indentString + "  stack Integer" + lt);
                }
                break;
                
            // Float_variable_info
            case 2:
                if (localsStack == STACK_LOCALS) {
                    localsFrame.add("Float");
                    frameSourceText.append(indentString + "  locals Float" + lt);
                } else if (localsStack == STACK_STACK) {
                    stackFrame.add("Float");
                    frameSourceText.append(indentString + "  stack Float" + lt);
                }
                break;
                
            // Double_variable_info
            case 3:
                if (localsStack == STACK_LOCALS) {
                    localsFrame.add("Double");
                    frameSourceText.append(indentString + "  locals Double" + lt);
                } else if (localsStack == STACK_STACK) {
                    stackFrame.add("Double");
                    frameSourceText.append(indentString + "  stack Double" + lt);
                }
                break;
                
            // Long_variable_info
            case 4:
                if (localsStack == STACK_LOCALS) {
                    localsFrame.add("Long");
                    frameSourceText.append(indentString + "  locals Long" + lt);
                } else if (localsStack == STACK_STACK) {
                    stackFrame.add("Long");
                    frameSourceText.append(indentString + "  stack Long" + lt);
                }
                break;
                
            // Null_variable_info
            case 5:
                if (localsStack == STACK_LOCALS) {
                    localsFrame.add("Null");
                    frameSourceText.append(indentString + "  locals Null" + lt);
                } else if (localsStack == STACK_STACK) {
                    stackFrame.add("Null");
                    frameSourceText.append(indentString + "  stack Null" + lt);
                }
                break;
                
            // UninitializedThis_variable_info
            case 6:
                if (localsStack == STACK_LOCALS) {
                    localsFrame.add("UninitializedThis");
                    frameSourceText.append(indentString + "  locals UninitializedThis" + lt);
                } else if (localsStack == STACK_STACK) {
                    stackFrame.add("UninitializedThis");
                    frameSourceText.append(indentString + "  stack UninitializedThis" + lt);
                }
                break;
                
            // Object_variable_info
            case 7:
                int cpool_index = (entries[pointerEntries] << 8)  | 
                                   entries[pointerEntries + 1];
                
                frameBytes.add(entries[pointerEntries]);
                frameBytes.add(entries[pointerEntries + 1]);
                
                pointerEntries = pointerEntries + 2;
                
                Vector<Integer> cpConstant = cp.getConstant(cpool_index);
                String classname = Builder.getClassNameCONSTANT_Class(cpConstant, cp);
                
                if (localsStack == STACK_LOCALS) {
                    localsFrame.add("Object " + classname);
                    frameSourceText.append(indentString + "  locals Object " + classname + lt);
                } else if (localsStack == STACK_STACK) {
                    stackFrame.add("Object " + classname);
                    frameSourceText.append(indentString + "  stack Object " + classname + lt);
                }
                break;
                
            // Uninitialized_variable_info
            case 8:
                int offsetValue = (entries[pointerEntries] << 8)  | 
                              entries[pointerEntries + 1];
                
                frameBytes.add(entries[pointerEntries]);
                frameBytes.add(entries[pointerEntries + 1]);
                
                pointerEntries = pointerEntries + 2;
                                
                if (localsStack == STACK_LOCALS) {
                    localsFrame.add("Uninitialized " + offsetValue);
                    frameSourceText.append(indentString + "  locals Uninitialized " + offsetValue + lt);
                } else if (localsStack == STACK_STACK) {
                    stackFrame.add("Uninitialized " + offsetValue);
                    frameSourceText.append(indentString + "  stack Uninitialized " + offsetValue + lt);
                }
                break;
        }       
    }
        
    
    
    
    
    public void parse() {
        attribute_name_index = (StackMapTable_attribute[0] << 8)  | 
                                StackMapTable_attribute[1];
        attribute_length =     (StackMapTable_attribute[2] << 24) | 
                               (StackMapTable_attribute[3] << 16) |
                               (StackMapTable_attribute[4] << 8)  | 
                                StackMapTable_attribute[5];
        number_of_entries =    (StackMapTable_attribute[6] << 8)  | 
                                StackMapTable_attribute[7];
                
        entries = new int[attribute_length - 2];
        
        for (int i = 0; i < entries.length; i++) {
            entries[i] = StackMapTable_attribute[8 + i];
        }
            
        
        
        int frame_type;
        int offset_delta;
        int number_of_locals;
        int number_of_stack_items;
        int tag;
        int[] bytecodeOffset = new int[number_of_entries];
        
        
        
        for (int frameNumber = 0; frameNumber < number_of_entries; frameNumber++) {
            localsFrame = new Vector<String>();
            stackFrame = new Vector<String>();
            frameBytes = new Vector<Integer>();
            frameSourceText = new StringBuffer();
            frameComment = new StringBuffer();
            
            Vector<String> previousLocalsFrame = new Vector<String>();
                        
            frame_type= entries[pointerEntries]; 
            pointerEntries++;
                        
            frameBytes.add(frame_type);
                        
            
            // same_frame
            if ((frame_type >= 0) && (frame_type <= 63)) {
                frameSourceText.append(lt);
                offset_delta = frame_type;
                if (frameNumber > 0) {
                    bytecodeOffset[frameNumber] = bytecodeOffset[frameNumber - 1] + offset_delta + 1;
                } else {
                    bytecodeOffset[frameNumber] = offset_delta;
                }
                
                frameComment.append(indentString + "; same_frame (frameNumber = " + 
                        frameNumber + ")" + lt + indentString + "; frame_type = " + 
                        frame_type + ", offset_delta = " +  offset_delta + lt);
                frameSourceText.append(indentString + "  offset " + bytecodeOffset[frameNumber] + lt);
                
                // frame (same_frame) has exactly the same locals as the previous stack map frame
                // and the number of stack items is zero.
                if (frameNumber > 0) {
                    previousLocalsFrame = localsVector.get(frameNumber - 1);
                    for (int i = 0; i < previousLocalsFrame.size(); i++) {
                        frameSourceText.append(indentString + "  locals " + 
                                previousLocalsFrame.get(i) + lt);
                        localsFrame.add(previousLocalsFrame.get(i));
                    } 
                }
            }
            
            
            // same_locals_1_stack_item_frame
            if ( (frame_type >= 64) && (frame_type <= 127) ) {
                frameSourceText.append(lt);
                offset_delta = frame_type - 64;
                if (frameNumber > 0) {
                    bytecodeOffset[frameNumber] = bytecodeOffset[frameNumber - 1] + offset_delta + 1;
                } else {
                    bytecodeOffset[frameNumber] = offset_delta;
                }
                
                frameComment.append(indentString + "; same_locals_1_stack_item_frame (frameNumber = " + 
                        frameNumber + ")" + lt + indentString + "; frame_type = " + 
                        frame_type + ", offset_delta = " + offset_delta + lt);
                frameSourceText.append(indentString + "  offset " + bytecodeOffset[frameNumber] + lt);
                
                // frame (same_locals_1_stack_item_frame) has exactly the same locals as the 
                // previous stack map frame and the number of stack items is 1.
                // verification_type_info for the one stack item.
                if (frameNumber > 0) {
                    previousLocalsFrame = localsVector.get(frameNumber - 1);
                    for (int i = 0; i < previousLocalsFrame.size(); i++) {
                        frameSourceText.append(indentString + "  locals " + 
                                previousLocalsFrame.get(i) + lt);
                        localsFrame.add(previousLocalsFrame.get(i));
                    } 
                }

                tag = entries[pointerEntries];
                frameBytes.add(tag);
                pointerEntries++;
                verification_type_info(tag, STACK_STACK);
            }
            
                        
            // same_locals_1_stack_item_frame_extended
            if (frame_type == 247) {
                frameSourceText.append(lt);
                offset_delta = (entries[pointerEntries] << 8)  | 
                                entries[pointerEntries + 1];
                frameBytes.add(entries[pointerEntries]);
                frameBytes.add(entries[pointerEntries + 1]);
                pointerEntries = pointerEntries + 2;
                              
                if (frameNumber > 0) {
                    bytecodeOffset[frameNumber] = bytecodeOffset[frameNumber - 1] + offset_delta + 1;
                } else {
                    bytecodeOffset[frameNumber] = offset_delta;
                }
                
                frameComment.append(indentString + "; same_locals_1_stack_item_frame_extended (frameNumber = " + 
                        frameNumber + ")" + lt + indentString + "; frame_type = " + 
                        frame_type + ", offset_delta = " + offset_delta + lt);
                frameSourceText.append(indentString + "  offset " + bytecodeOffset[frameNumber] + lt);
                
                // frame (same_locals_1_stack_item_frame_extended) has exactly the same locals as the 
                // previous stack map frame and the number of stack items is 1.
                // verification_type_info for the one stack item.
                // The offset_delta value for the frame is given explicitly
                if (frameNumber > 0) {
                    previousLocalsFrame = localsVector.get(frameNumber - 1);
                    for (int i = 0; i < previousLocalsFrame.size(); i++) {
                        frameSourceText.append(indentString + "  locals " + 
                                previousLocalsFrame.get(i) + lt);
                        localsFrame.add(previousLocalsFrame.get(i));
                    } 
                }
                
                tag = entries[pointerEntries];
                frameBytes.add(tag);
                pointerEntries++;
                verification_type_info(tag, STACK_STACK);
            }
            
            
            // chop_frame
            if ( (frame_type >= 248) && (frame_type <=250) ) {
                frameSourceText.append(lt);
                offset_delta = (entries[pointerEntries] << 8)  | 
                                entries[pointerEntries + 1];
                frameBytes.add(entries[pointerEntries]);
                frameBytes.add(entries[pointerEntries + 1]);
                pointerEntries = pointerEntries + 2;
                
                if (frameNumber > 0) {
                    bytecodeOffset[frameNumber] = bytecodeOffset[frameNumber - 1] + offset_delta + 1;
                } else {
                    bytecodeOffset[frameNumber] = offset_delta;
                }
                
                frameComment.append(indentString + "; chop_frame (frameNumber = " + 
                        frameNumber + ")" + lt + indentString + "; frame_type = " + 
                        frame_type + ", offset_delta = " + offset_delta + lt);
                frameSourceText.append(indentString + "  offset " + bytecodeOffset[frameNumber] + lt);
                
                // frame (chop_frame) 
                // operand stack is empty
                // current locals are the same as the locals in the previous frame
                // except that the k last locals are absent, k = 251 - frame_type
                if (frameNumber > 0) {
                    previousLocalsFrame = localsVector.get(frameNumber - 1);
                    for (int i = 0; i < (previousLocalsFrame.size() - (251 - frame_type)); i++) {
                        frameSourceText.append(indentString + "  locals " + 
                                previousLocalsFrame.get(i) + lt);
                        localsFrame.add(previousLocalsFrame.get(i));
                    } 
                }
            }
                        
            
            // same_frame_extended
            if (frame_type == 251) {
                frameSourceText.append(lt);
                offset_delta = (entries[pointerEntries] << 8)  | 
                                entries[pointerEntries + 1];
                frameBytes.add(entries[pointerEntries]);
                frameBytes.add(entries[pointerEntries + 1]);
                pointerEntries = pointerEntries + 2;
                
                if (frameNumber > 0) {
                    bytecodeOffset[frameNumber] = bytecodeOffset[frameNumber - 1] + offset_delta + 1;
                } else {
                    bytecodeOffset[frameNumber] = offset_delta;
                }
                
                frameComment.append(indentString + "; same_frame_extended (frameNumber = " + 
                        frameNumber + ")" + lt + indentString + "; frame_type = " + 
                        frame_type + ", offset_delta = " + offset_delta + lt);
                frameSourceText.append(indentString + "  offset " + bytecodeOffset[frameNumber] + lt);
                
                // frame (same_frame_extended) has exactly the same locals as the previous stack map frame
                // and the number of stack items is zero.
                if (frameNumber > 0) {
                    previousLocalsFrame = localsVector.get(frameNumber - 1);
                    for (int i = 0; i < previousLocalsFrame.size(); i++) {
                        frameSourceText.append(indentString + "  locals " + 
                                previousLocalsFrame.get(i) + lt);
                        localsFrame.add(previousLocalsFrame.get(i));
                    } 
                }
            }
            
            
            // append_frame
            if ( (frame_type >= 252) && (frame_type <= 254) ) {
                frameSourceText.append(lt);
                offset_delta = (entries[pointerEntries] << 8)  | 
                                entries[pointerEntries + 1];
                frameBytes.add(entries[pointerEntries]);
                frameBytes.add(entries[pointerEntries + 1]);
                pointerEntries = pointerEntries + 2;
                
                if (frameNumber > 0) {
                    bytecodeOffset[frameNumber] = bytecodeOffset[frameNumber - 1] + offset_delta + 1;
                } else {
                    bytecodeOffset[frameNumber] = offset_delta;
                }
                
                frameComment.append(indentString + "; append_frame (frameNumber = " + 
                        frameNumber + ")" + lt + indentString + "; frame_type = " + 
                        frame_type + ", offset_delta = " + offset_delta + lt);
                frameSourceText.append(indentString + "  offset " + bytecodeOffset[frameNumber] + lt);
                
                // frame (append_frame) 
                // operand stack is empty
                // current locals are the same as the locals in the previous frame
                // except that k additional locals are defined, k = frame_type - 251
                if (frameNumber > 0) {
                    previousLocalsFrame = localsVector.get(frameNumber - 1);
                    for (int i = 0; i < previousLocalsFrame.size(); i++) {
                        frameSourceText.append(indentString + "  locals " + 
                                previousLocalsFrame.get(i) + lt);
                        localsFrame.add(previousLocalsFrame.get(i));
                    } 
                }
                for (int i = 0; i < (frame_type - 251); i++) {
                    tag = entries[pointerEntries];
                    frameBytes.add(tag);
                    pointerEntries++;
                    verification_type_info(tag, STACK_LOCALS);
                }
            }
                        
            
            // full_frame
            if (frame_type == 255) {
                frameSourceText.append(lt);
                offset_delta = (entries[pointerEntries] << 8)  | 
                                entries[pointerEntries + 1];
                frameBytes.add(entries[pointerEntries]);
                frameBytes.add(entries[pointerEntries + 1]);
                pointerEntries = pointerEntries + 2;
                
                if (frameNumber > 0) {
                    bytecodeOffset[frameNumber] = bytecodeOffset[frameNumber - 1] + offset_delta + 1;
                } else {
                    bytecodeOffset[frameNumber] = offset_delta;
                }
                
                frameComment.append(indentString + "; full_frame (frameNumber = " + 
                        frameNumber + ")" + lt + indentString + "; frame_type = " + 
                        frame_type + ", offset_delta = " + offset_delta + lt);
                frameSourceText.append(indentString + "  offset " + bytecodeOffset[frameNumber] + lt);
                
                number_of_locals = (entries[pointerEntries] << 8)  | 
                                    entries[pointerEntries +1 ];
                
                frameBytes.add(entries[pointerEntries]);
                frameBytes.add(entries[pointerEntries + 1]);
                
                pointerEntries = pointerEntries + 2;
                
                for (int i = 0; i < number_of_locals; i++) {
                    tag = entries[pointerEntries];
                    frameBytes.add(tag);
                    pointerEntries++;
                    verification_type_info(tag, STACK_LOCALS);
                }
                
                number_of_stack_items = (entries[pointerEntries] << 8)  | 
                                         entries[pointerEntries + 1];
                
                frameBytes.add(entries[pointerEntries]);
                frameBytes.add(entries[pointerEntries + 1]);
                
                pointerEntries = pointerEntries + 2;
                                
                for (int i = 0; i < number_of_stack_items; i++) {
                    tag = entries[pointerEntries];
                    frameBytes.add(tag);
                    pointerEntries++;
                    verification_type_info(tag, STACK_STACK);
                }
            }
                       
            
            localsVector.add(localsFrame);
            stackVector.add(stackFrame);
            sourceText.append(frameComment);
            sourceText.append(indentString + "; frame bytes: ");
            
            for (int i = 0; i < frameBytes.size(); i++) {
                sourceText.append(frameBytes.get(i) + " ");
            }
            
            sourceText.append(lt);
            sourceText.append(indentString + ".stack ");
            sourceText.append(frameSourceText);
            sourceText.append(indentString + "  .end stack" + lt);
        }
    }
     
    
    
    
    
    public StringBuffer getSourceText() {
        return sourceText;
    }
        
    
}

