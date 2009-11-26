/*
 * Code.java
 *
 * @author Harald Roeder
 * @version 2008-02-18
 */


package classfileanalyzer.attributes;


import classfileanalyzer.*;
import java.util.*;
import static classfileanalyzer.Main.lt;





public class Code implements Constants {
    
    int[] Code_attribute;
        int attribute_name_index; 
        int attribute_length;
        int max_stack;
        int max_locals;    
        int code_length;
        int[] code;
        int exception_table_length;
        int[] exception_table;
        int attributes_count;
        int[] attributes;    
        
    ConstantPool cp;
    Vector<Integer> labels = new Vector<Integer>();
           
    StringBuffer sourceText; 
    StringBuffer sourceTextAppend;
              
    public static boolean nopc = false;
    
        
    
    
    
    public Code(int[] Code_attribute, ConstantPool cp) {
        this.Code_attribute = Code_attribute;
        this.cp = cp;
        sourceText = new StringBuffer();
        sourceTextAppend = new StringBuffer();
        parse();
    }
        
    
    
    
    
    public void parse() {
             
        int i;
        int tag;
                
        byte b; 
        short st;
        int integer;
        float f; 
        long lg; 
        double d; 
        String s; 
              
        int operand;
        int startSwitch, fourByteBoundary, defaultSwitch;
        String indent;
        String[] methodInfo = new String[3];
        
        Vector<Integer> cpConstant;
        int cpIndex = 0;
        HashMap<Integer,Integer> linesNumbers = new HashMap<Integer,Integer>();
        
        
        
        attribute_name_index = (Code_attribute[0] << 8) | Code_attribute[1];
        attribute_length = (Code_attribute[2] << 24) | (Code_attribute[3] << 16) |
                           (Code_attribute[4] << 8) | Code_attribute[5];
                
        max_stack = (Code_attribute[6] << 8) | Code_attribute[7];
        sourceText.append("  .limit stack " + max_stack + lt);
        max_locals = (Code_attribute[8] << 8) | Code_attribute[9];
        sourceText.append("  .limit locals " + max_locals + lt);

        code_length = (Code_attribute[10] << 24) | (Code_attribute[11] << 16) |
                      (Code_attribute[12] << 8)  |  Code_attribute[13];

        code = new int[code_length];
        for (i = 0; i < code_length; i++) {
            code[i] = Code_attribute[14 + i];
        }
        
        exception_table_length = (Code_attribute[14 + code_length] << 8) |
                                  Code_attribute[14 + code_length + 1];
        
        int exceptionAreaLength = exception_table_length * 8;
        exception_table = new int[exceptionAreaLength];
        for (i = 0; i < exceptionAreaLength; i++) {
            exception_table[i] = Code_attribute[14 + code_length + 2 + i];
        }
        
        attributes_count = (Code_attribute[14 + code_length + 2 + exceptionAreaLength] << 8) |
                            Code_attribute[14 + code_length + 2 + exceptionAreaLength + 1];
        
        int attributesAreaLength = Code_attribute.length - (14 + code_length + 2 + exceptionAreaLength + 2);
        attributes = new int[attributesAreaLength];
        for (i = 0; i < attributesAreaLength; i ++) {
            attributes[i] = Code_attribute[14 + code_length + 2 + exceptionAreaLength + 2 + i];
        }
        
        
        
        // attributes (Code attribute)
        int[][] localVariableTableInt = null;
        int[][] localVariableTypeTableInt = null;
        boolean dvar = false;
        int pointerAttributesArray = 0;
        
        
        for (int attributeNumber = 0; attributeNumber < attributes_count; attributeNumber++) {
            int codeAttributeNameIndex = (attributes[pointerAttributesArray] << 8) | 
                                          attributes[pointerAttributesArray + 1];
            int codeAttributeLength = (attributes[pointerAttributesArray + 2] << 24) |
                                      (attributes[pointerAttributesArray + 3] << 16) |
                                      (attributes[pointerAttributesArray + 4] << 8) |
                                       attributes[pointerAttributesArray + 5];
            cpConstant = cp.getConstant(codeAttributeNameIndex);
            String attributeName = Builder.getStringCONSTANT_Utf8(cpConstant);

            int[] attribute_info = new int[codeAttributeLength + 6];
            for (i = 0; i < (codeAttributeLength + 6); i++) {
                attribute_info[i] = attributes[pointerAttributesArray];
                pointerAttributesArray++;
            }
            
            
            // .var 
            if (attributeName.equals("LocalVariableTable")) {
                //String lastMethodOpcode = jvmis[code[code.length - 1]];
                LocalVariableTables localVariableTable 
                        = new LocalVariableTables(false, attribute_info);
                localVariableTableInt = localVariableTable.getLocalVariableTables();
                dvar = true;
            } else if (attributeName.equals("LocalVariableTypeTable")) {
                //String lastMethodOpcode = jvmis[code[code.length - 1]];
                LocalVariableTables localVariableTypeTable 
                        = new LocalVariableTables(true, attribute_info);
                localVariableTypeTableInt = localVariableTypeTable.getLocalVariableTables();
                dvar = true;
            }
                
             // .line
            else if (attributeName.equals("LineNumberTable")) {
                LineNumberTable lnt = new LineNumberTable(attribute_info);
                linesNumbers = lnt.getLinesNumbers();
            }
                       
            // .stack
            else if (attributeName.equals("StackMapTable")) {
                StackMapTable stackMapTable = new StackMapTable(attribute_info, cp, 2);
                StringBuffer stackMapTableSourceText = stackMapTable.getSourceText();
                sourceTextAppend.append(stackMapTableSourceText);
            }
                               
            else {
                sourceTextAppend.append("; not implemented attribute (Code_attribute): " + 
                        attributeName + " = ");
                for (i = 0; i < attribute_info.length; i++) {
                    if (((i % 16) == 0) ) { sourceTextAppend.append(lt + ";     "); }
                    sourceTextAppend.append(attribute_info[i] + " ");
                }
                sourceTextAppend.append(lt);
            }   
        }
          
        
        // merge LocalVariableTable and LocalVariableTypeTable
        if (dvar) {
            String lastMethodOpcode = jvmis[code[code.length - 1]];
            LocalVariableTablesHelper lvtHelper = 
                    new LocalVariableTablesHelper(localVariableTableInt, 
                                                  localVariableTypeTableInt, 
                                                  cp, 
                                                  code.length, 
                                                  lastMethodOpcode);
            sourceText.append(lvtHelper.getSourceText());

            Vector<Integer> localVariableTableLabels = lvtHelper.getLabels();
            for (i = 0; i < localVariableTableLabels.size(); i++) {
                labels.add(localVariableTableLabels.get(i));
            }
        }
             
        
        
        // code array
        for (int k = 0; k < code_length; k++) { 
            sourceText.append("Label" + k + ":" + lt);
            if (linesNumbers.containsKey(k)) {
                sourceText.append("  .line " + linesNumbers.get(k) + lt);
            }
            
            // opcode
            int opcode = code[k];
            if (opcode != 196) { // not instruction wide
               if (Code.nopc) {
                   sourceText.append("  " + jvmis[opcode]);
               } else {
                   sourceText.append("  " + k + ": " + jvmis[opcode]);
               }
            }
            // operand bytes
            int operandCount = operandBytes[opcode]; 
                        
            
            // iload, lload, fload, dload, aload
            // istore, lstore, fstore, dstore, astore,
            // ret
            if ((opcode == 21) ||
                (opcode == 22) ||
                (opcode == 23) ||
                (opcode == 24) ||
                (opcode == 25) ||
                (opcode == 54) ||
                (opcode == 55) ||
                (opcode == 56) ||
                (opcode == 57) ||
                (opcode == 58) ||
                (opcode == 169)) {
                k++;
                operand = code[k];  
                sourceText.append(" " + operand);
            } 
                       
            // bipush
            else if (opcode == 16) { 
                k++;
                operand = code[k];
                b = (byte)operand;
                sourceText.append(" " + b);
            } 
                       
            // sipush
            else if (opcode == 17) { 
                operand = (code[k + 1] << 8) | code[k + 2];
                k = k + 2;
                st = (short)operand;
                sourceText.append(" " + st);
            }                       
                       
            // ldc, ldc_w
            else if ((opcode == 18) || (opcode == 19)) { 
                if (opcode == 18) {
                    k++;
                    cpIndex = code[k];
                }
                if (opcode == 19) {
                    cpIndex = (code[k + 1] << 8) | code[k + 2];
                    k = k + 2;
                }
                cpConstant = cp.getConstant(cpIndex);
                tag = cpConstant.get(0).intValue();
                if (tag == CONSTANT_Integer) {
                    integer = Builder.getIntegerCONSTANT_Integer(cpConstant);
                    sourceText.append(" " + integer);
                }
                else if (tag == CONSTANT_Float) {
                    f = Builder.getFloatCONSTANT_Float(cpConstant);
                    sourceText.append(" " + f);
                }   
                else if (tag == CONSTANT_String) {
                    cpIndex = (cpConstant.get(1).intValue() << 8) | cpConstant.get(2).intValue();
                    cpConstant = cp.getConstant(cpIndex);
                    s = Builder.getStringCONSTANT_Utf8(cpConstant);
                    sourceText.append(" \"" + s + "\"");
                }
                else if (tag == CONSTANT_Class) {
                    s = Builder.getClassNameCONSTANT_Class(cpConstant, cp);
                    sourceText.append(" \"" + s + "\"");
                }
                else {
                    sourceText.append("ClassFileAnalyzer: internal error ldc, ldc_w");
                }
            }     
                       
            // ldc2_w
            else if (opcode == 20) { 
                cpIndex = (code[k + 1] << 8) | code[k + 2];
                k = k + 2;
                cpConstant = cp.getConstant(cpIndex);
                tag = cpConstant.get(0).intValue();
                if (tag == CONSTANT_Long) {
                    lg = Builder.getLongCONSTANT_Long(cpConstant);
                    sourceText.append(" " + lg);
                }
                if (tag == CONSTANT_Double) {
                    d = Builder.getDoubleCONSTANT_Double(cpConstant);
                    sourceText.append(" " + d);
                }
            } 
                       
            // iinc
            else if (opcode == 132) { 
                k++;
                operand = code[k];
                sourceText.append(" " + operand);
                k++;
                operand = code[k];
                b = (byte)operand;
                sourceText.append(" " + b);
            } 
                       
            // ifeq, ifne, iflt, ifge, ifgt, ifle,
            // if_icmpeq, if_icmpne, if_icmplt, if_icmpge, if_icmpgt, if_icmple,
            //     if_acmpeq, if_acmpne
            // goto
            // jsr
            // ifnull, ifnonnull
            else if ((opcode == 153) ||
                     (opcode == 154) ||
                     (opcode == 155) ||
                     (opcode == 156) ||
                     (opcode == 157) ||
                     (opcode == 158) ||
                     (opcode == 159) ||
                     (opcode == 160) ||
                     (opcode == 161) ||
                     (opcode == 162) ||
                     (opcode == 163) ||
                     (opcode == 164) ||
                     (opcode == 165) ||
                     (opcode == 166) ||
                     (opcode == 167) ||
                     (opcode == 168) ||
                     (opcode == 198) ||
                     (opcode == 199)) {

                operand  = (code[k + 1] << 8) | code[k + 2];
                st = (short)operand;
                operand = k + st;
                labels.add(new Integer(operand));
                sourceText.append(" Label" + operand);
                k = k + 2;
            }   
                       
            // getstatic, putstatic, getfield, putfield
            else if ((opcode == 178) || 
                     (opcode == 179) ||
                     (opcode == 180) ||
                     (opcode == 181)) {
                cpIndex = (code[k + 1] << 8) | code[k + 2];
                k = k + 2;
                cpConstant = cp.getConstant(cpIndex);
                String[] fieldInfo = Builder.getFieldInfoCONSTANT_Fieldref(cpConstant, cp);
                sourceText.append(" " + fieldInfo[0] + "/" + fieldInfo[1] + " " + fieldInfo[2]);
            }
                               
            // invokevirtual, invokespecial, invokestatic
            else if ((opcode == 182) || 
                     (opcode == 183) ||
                     (opcode == 184)) {
                cpIndex = (code[k + 1] << 8) | code[k + 2];
                k = k + 2;
                cpConstant = cp.getConstant(cpIndex);
                methodInfo = Builder.getMethodInfoCONSTANT_Methodref(cpConstant, cp);
                sourceText.append(" " + methodInfo[0] + "/" + methodInfo[1] + "" + methodInfo[2]);
            } 
                       
            // invokeinterface
            else if (opcode == 185) { 
                cpIndex = (code[k + 1] << 8) | code[k + 2];
                cpConstant = cp.getConstant(cpIndex);
                methodInfo = Builder.getMethodInfoCONSTANT_Methodref(cpConstant, cp);
                sourceText.append(" " + methodInfo[0] + "/" + 
                                        methodInfo[1] + "" + 
                                        methodInfo[2] + " " +
                                        code[k + 3]);
                k = k + 4; // operand byte "0"!
            } 
                       
            // new, anewarray, checkcast, instanceof
            else if ((opcode == 187) ||
                     (opcode == 189) ||
                     (opcode == 192) ||
                     (opcode == 193)) {
                cpIndex = (code[k + 1] << 8) | code[k + 2];
                k = k + 2;
                cpConstant = cp.getConstant(cpIndex);
                sourceText.append(" " + Builder.getClassNameCONSTANT_Class(cpConstant, cp));
            } 
                       
            // newarray
            else if (opcode == 188) { 
                k++;
                tag = code[k];
                switch (tag) {
                    case 4: sourceText.append(" boolean");
                            break;
                    case 5: sourceText.append(" char");
                            break;
                    case 6: sourceText.append(" float");
                            break;
                    case 7: sourceText.append(" double");
                            break;
                    case 8: sourceText.append(" byte");
                            break;
                    case 9: sourceText.append(" short");
                            break;
                    case 10: sourceText.append(" int");
                             break;
                    case 11: sourceText.append(" long");
                             break;
                    default: sourceText.append(" ?");
                }
            }
                       
            // multianewarray
            else if (opcode == 197) { 
                cpIndex = (code[k + 1] << 8) | code[k + 2];
                cpConstant = cp.getConstant(cpIndex);
                sourceText.append(" " + 
                                  Builder.getClassNameCONSTANT_Class(cpConstant, cp) + 
                                  " " +
                                  code[k + 3]);
                k = k + 3;
            } 
                       
            // goto_w, jsr_w
            else if ((opcode == 200) ||
                     (opcode == 201)) { 
                operand = (code[k + 1] << 24) | (code[k + 2] << 16) |
                          (code[k + 3] << 8) | code[k + 4];
                operand = k + operand;
                labels.add(new Integer(operand));
                sourceText.append(" Label" + operand);
                k = k + 4;
            } 
                       
            // tableswitch
            else if (opcode == 170) { 
                startSwitch = k;
                fourByteBoundary = (k + 1) % 4;
                integer = Integer.toString(k).length() + 4;
                indent = "";
                for (i = 0; i < integer; i++) {
                    indent = indent + " ";
                }
                if (fourByteBoundary == 3) { k++; }
                if (fourByteBoundary == 2) { k = k + 2; }
                if (fourByteBoundary == 1) { k = k + 3; }

                defaultSwitch = (code[k + 1] << 24) |
                                (code[k + 2] << 16) |
                                (code[k + 3] << 8) |
                                 code[k + 4]; 
                int low = (code[k + 5] << 24) |
                          (code[k + 6] << 16) |
                          (code[k + 7] << 8) |
                           code[k + 8]; 
                int high = (code[k + 9] << 24) |
                           (code[k + 10] << 16) |
                           (code[k + 11] << 8) |
                            code[k + 12];
                k = k + 12;

                sourceText.append(" " + low + " " + high + lt);

                for (i = 0; i < (high - low + 1); i++) {
                    operand = (code[k + 1] << 24) |
                              (code[k + 2] << 16) |
                              (code[k + 3] << 8) |
                               code[k + 4]; 
                     k = k + 4;
                     operand = startSwitch + operand;
                     labels.add(new Integer(operand));
                     sourceText.append(indent + "    Label" + operand + lt);
                }

                defaultSwitch = startSwitch + defaultSwitch;
                labels.add(new Integer(defaultSwitch));
                sourceText.append(indent + "    default : " + "Label" + defaultSwitch);
            }
                        
            // lookupswitch
            else  if (opcode == 171) {
                startSwitch = k;
                fourByteBoundary = (k + 1) % 4;
                    
                sourceText.append(lt);

                integer = Integer.toString(k).length() + 4;
                indent = "";
                for (i = 0; i < integer; i++) {
                    indent = indent + " ";
                }
                if (fourByteBoundary == 3) { k++; }
                if (fourByteBoundary == 2) { k = k + 2; }
                if (fourByteBoundary == 1) { k = k + 3; }

                defaultSwitch = (code[k + 1] << 24) |
                                (code[k + 2] << 16) |
                                (code[k + 3] << 8) |
                                 code[k + 4]; 
                int npairs = (code[k + 5] << 24) |
                             (code[k + 6] << 16) |
                             (code[k + 7] << 8) |
                              code[k + 8]; 
                k = k + 8;

                for (i = 0; i < npairs; i++) {
                    operand = (code[k + 1] << 24) |
                              (code[k + 2] << 16) |
                              (code[k + 3] << 8) |
                               code[k + 4]; 
                    k = k + 4;
                    sourceText.append(indent + "    " + operand + " : ");
                    operand = (code[k + 1] << 24) |
                              (code[k + 2] << 16) |
                              (code[k + 3] << 8) |
                               code[k + 4]; 
                    k = k + 4;
                    operand = startSwitch + operand;
                    labels.add(new Integer(operand));
                    sourceText.append("Label" + operand + lt);
                }

                defaultSwitch = startSwitch + defaultSwitch;
                labels.add(new Integer(defaultSwitch));
                sourceText.append(indent + "    default : " + "Label" + defaultSwitch);
            }
                        
            // wide
            else if (opcode == 196) { 
                sourceText.append("  ; 0xc4 (wide)" + lt);
                
                k++; 
                tag = code[k];
                
                if (Code.nopc) {
                    sourceText.append("  ");
                } else {
                    sourceText.append("  " + k + ": ");
                }
                                
                if (tag == 25) { // wide aload
                    sourceText.append(jvmis[25]);
                    operand = (code[k + 1] << 8) | code[k + 2]; 
                    k = k + 2;
                    sourceText.append(" " + operand);
                } 
                else if (tag == 58) { // wide astore 
                    sourceText.append(jvmis[58]);
                    operand = (code[k + 1] << 8) | code[k + 2]; 
                    k = k + 2;
                    sourceText.append(" " + operand);
                }        
                else if (tag == 24) { // wide dload
                    sourceText.append(jvmis[24]);
                    operand = (code[k + 1] << 8) | code[k + 2]; 
                    k = k + 2;
                    sourceText.append(" " + operand);
                }     
                else if (tag == 57) { // wide dstore
                    sourceText.append(jvmis[57]);
                    operand = (code[k + 1] << 8) | code[k + 2]; 
                    k = k + 2;
                    sourceText.append(" " + operand);
                }       
                else if (tag == 23) { // wide float
                    sourceText.append(jvmis[23]);
                    operand = (code[k + 1] << 8) | code[k + 2]; 
                    k = k + 2;
                    sourceText.append(" " + operand);
                }        
                else if (tag == 56) { // wide fstore
                    sourceText.append(jvmis[56]);
                    operand = (code[k + 1] << 8) | code[k + 2]; 
                    k = k + 2;
                    sourceText.append(" " + operand);
                }        
                else if (tag == 21) { // wide iload
                    sourceText.append(jvmis[21]);
                    operand = (code[k + 1] << 8) | code[k + 2]; 
                    k = k + 2;
                    sourceText.append(" " + operand);
                }        
                else if (tag == 54) { // wide istore
                    sourceText.append(jvmis[54]);
                    operand = (code[k + 1] << 8) | code[k + 2]; 
                    k = k + 2;
                    sourceText.append(" " + operand);
                }        
                else if (tag == 22) { // wide lload
                    sourceText.append(jvmis[22]);
                    operand = (code[k + 1] << 8) | code[k + 2]; 
                    k = k + 2;
                    sourceText.append(" " + operand);
                }        
                else if (tag == 55) { // wide lstore
                    sourceText.append(jvmis[55]);
                    operand = (code[k + 1] << 8) | code[k + 2]; 
                    k = k + 2;
                    sourceText.append(" " + operand);
                }        
                else if (tag == 169) { // wide ret
                    sourceText.append(jvmis[169]);
                    operand = (code[k + 1] << 8) | code[k + 2]; 
                    k = k + 2;
                    sourceText.append(" " + operand);
                }        
                else if (tag == 132) { // wide iinc
                    sourceText.append(jvmis[132]);
                    operand = (code[k + 1] << 8) | code[k + 2]; 
                    sourceText.append(" " + operand);
                    k = k + 2;
                    operand = (code[k + 1] << 8) | code[k + 2]; 
                    st = (short)operand;
                    sourceText.append(" " + st);
                    k = k + 2;
                }
            }
                       
            // some opcode left??
            else if (operandCount > 0) {
                for (i = 0; i < operandCount; i++) {
                    k++;
                    operand = code[k];  
                    sourceText.append(" " + operand);
                }
            }
                       
            if (operandCount == -2) { // instructions: xxxunusedxxx, breakpoint
                System.out.println("Error: opcode, operand bytes.");
                System.exit(1);
            }
           
            sourceText.append(lt);
        } // code array
               
        
        
        // exception_table
        int start_pc;
        int end_pc;
        int handler_pc;
        String catch_type = "";
        
        for (i = 0; i < exception_table_length; i++ ) {
            integer = i * 8;
            start_pc = (exception_table[integer] << 8) | exception_table[integer + 1];
            end_pc = (exception_table[integer + 2] << 8) | exception_table[integer + 3];
            handler_pc = (exception_table[integer + 4] << 8) | exception_table[integer + 5];
            
            cpIndex = (exception_table[integer + 6] << 8) | exception_table[integer + 7];
            if (cpIndex == 0) {
                catch_type = "all";
            } else {
                cpConstant = cp.getConstant(cpIndex);
                catch_type = Builder.getClassNameCONSTANT_Class(cpConstant, cp);
            }
            
            sourceText.append("  .catch " + catch_type + 
                              " from Label" + start_pc + 
                              " to Label" + end_pc +
                              " using Label" + handler_pc +
                              lt);
            labels.add(new Integer(start_pc));
            labels.add(new Integer(end_pc));
            labels.add(new Integer(handler_pc));
        }
                       
      
        // append .stack, not implemented
        sourceText.append(sourceTextAppend);
    }
    
    
    
    
    
    public int[] getCodeArray() {
        return code;
    }
    
    
    
    
    
    public int[] getExceptionTable() {
        return exception_table;
    }
    
    
    
    
    
    public int getAttributesCount() {
        return attributes_count;
    }
    
    
    
    
    
    public int[] getAttributes() {
        return attributes;
    }
    
    
    
    
    
    public StringBuffer getSourceText() {
        return sourceText;
    }
    
    
    
    
    
    public Vector<Integer> getLabels() {
        return labels;
    }
        
    
}

