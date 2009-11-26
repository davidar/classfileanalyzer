/*
 * LocalVariableTablesHelper.java
 *
 * @author Harald Roeder
 * @version 2008-02-18
 */


package classfileanalyzer.attributes;


import classfileanalyzer.*;
import java.util.*;
import static classfileanalyzer.Main.lt;





public class LocalVariableTablesHelper {
     
    int[][] localVariableTable;
    int[][] localVariableTypeTable;

    ConstantPool cp;
    StringBuffer sourceText;
    int codeLength;
    String lastMethodOpcode;
    
    Vector<Integer> labels;
    
    
    
    
    
    public LocalVariableTablesHelper(int[][] localVariableTable, 
                                     int[][] localVariableTypeTable,
                                     ConstantPool cp,
                                     int codeLength, 
                                     String lastMethodOpcode) {
        
        this.localVariableTable = localVariableTable;
        this.localVariableTypeTable = localVariableTypeTable;
        this.cp = cp;
        this.codeLength = codeLength;
        this. lastMethodOpcode = lastMethodOpcode;
        
        labels = new Vector<Integer>();
        sourceText = new StringBuffer();
        mergeLocalVariableTables();
        generateSourceText();
    }
    
    
    
    
    
    public void mergeLocalVariableTables() {
        // 0: index 
        // 1: name_index 
        // 2: descriptor_index 
        // 3: signature_index 
        // 4: start_pc 
        // 5: start_pc + length
                
        if (localVariableTypeTable != null) {
            
            for (int i = 0; i < localVariableTypeTable.length; i++) {
                int typeTable0 = localVariableTypeTable[i][0];
                int typeTable1 = localVariableTypeTable[i][1];
                int typeTable3 = localVariableTypeTable[i][3];
                int typeTable4 = localVariableTypeTable[i][4];
                int typeTable5 = localVariableTypeTable[i][5];
                    
                for (int j = 0; j < localVariableTable.length; j++) {
                    int table0 = localVariableTable[j][0];
                    int table1 = localVariableTable[j][1];
                    int table4 = localVariableTable[j][4];
                    int table5 = localVariableTable[j][5];
                     
                    if ( (table0 == typeTable0) && (table1 == typeTable1) &&
                         (table4 == typeTable4) && (table5 == typeTable5) ) {
                        
                        localVariableTable[j][3] = typeTable3;
                    }
                }
            }
        }
    }
     
    
    
    
    
    public void generateSourceText() {
         // 0: index 
         // 1: name_index 
         // 2: descriptor_index 
         // 3: signature_index 
         // 4: start_pc 
         // 5: start_pc + length
        
         TreeSet<String> sortedVarDirectives = new TreeSet<String>();
                 
         for (int i = 0; i < localVariableTable.length; i++) {
            Vector<Integer> cpConstant = cp.getConstant(localVariableTable[i][1]);
            String localVariableName = Builder.getStringCONSTANT_Utf8(cpConstant);

            cpConstant = cp.getConstant(localVariableTable[i][2]);
            String localVariableDescriptor = Builder.getStringCONSTANT_Utf8(cpConstant);

            String localVariableSignature = "";
            if (localVariableTable[i][3] != 0) {
                cpConstant = cp.getConstant(localVariableTable[i][3]);
                localVariableSignature = Builder.getStringCONSTANT_Utf8(cpConstant);
            }

            String dvar = "";

            if (localVariableTable[i][5] == codeLength) { // label before last opcode within method
                //String lastMethodOpcode = jvmis[code[endPc - 1]];
                if (lastMethodOpcode.equals("areturn") ||
                    lastMethodOpcode.equals("dreturn") ||
                    lastMethodOpcode.equals("freturn") ||
                    lastMethodOpcode.equals("ireturn") ||
                    lastMethodOpcode.equals("lreturn") ||
                    lastMethodOpcode.equals("return")) {

                    localVariableTable[i][5] = localVariableTable[i][5] - 1;

                    dvar = "  .var " + localVariableTable[i][0] + " is " + 
                           localVariableName + " " + localVariableDescriptor;

                    if (!localVariableSignature.equals("")) {
                        dvar = dvar + " signature \"" + localVariableSignature + "\"";
                    }

                    dvar = dvar + " from " + "Label" + localVariableTable[i][4] + 
                                  " to "   + "Label" + localVariableTable[i][5] + lt;

                    sortedVarDirectives.add(dvar);

                    labels.add(new Integer(localVariableTable[i][4]));
                    labels.add(new Integer(localVariableTable[i][5]));

                } else {
                    dvar = "  ; directive .var not used for: " +
                           localVariableTable[i][0] + " " + localVariableTable[i][1] + " " +
                           localVariableTable[i][2] + " ";
                    if (!localVariableSignature.equals("")) {
                        dvar = dvar + localVariableTable[i][3] + " ";
                    }
                    dvar = dvar +  localVariableTable[i][4] + " " +  localVariableTable[i][5] + lt;
                    sortedVarDirectives.add(dvar);
                }

            } else {
                dvar = "  .var " + localVariableTable[i][0] + " is " + 
                           localVariableName + " " + localVariableDescriptor;
                if (!localVariableSignature.equals("")) {
                    dvar = dvar + " signature \"" + localVariableSignature + "\"";
                }
                dvar = dvar + " from " + "Label" + localVariableTable[i][4] + 
                              " to "   + "Label" + localVariableTable[i][5] + lt;
                sortedVarDirectives.add(dvar);
                labels.add(new Integer(localVariableTable[i][4]));
                labels.add(new Integer(localVariableTable[i][5]));
            }   
        }

        Iterator<String> iter = sortedVarDirectives.iterator();
        while (iter.hasNext()) {
            sourceText.append(iter.next());
        }
    }
     
    
    
    
    
    public Vector<Integer> getLabels() {
        return labels;
    }
    
    
    
    
    
    public StringBuffer getSourceText() {
        return sourceText;
    }  
        
    
}

