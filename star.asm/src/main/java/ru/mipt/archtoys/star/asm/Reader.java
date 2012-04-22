/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.star.asm;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;
import java.io.StreamTokenizer;
import ru.mipt.archtoys.star.asm.Instruction;

/**
 *
 * @author genius
 */
public class Reader {
    
    private static FileReader inputStream = null;
    private static StreamTokenizer tokenizer = null;
    
    public Reader( String fileName){
        try {
            inputStream = new FileReader(fileName);
            tokenizer = new StreamTokenizer(inputStream);
        } catch (IOException ex) {
            System.err.println("Caught IOException while init: " + ex.getMessage());
        }        
    }
    
    private String readMnemo() throws IOException{
        int token = tokenizer.nextToken();
        assert (token == StreamTokenizer.TT_WORD) : "Mnemonic must be word";
        return tokenizer.sval;
    }
    
    private int readInt() throws IOException{
        int token = tokenizer.nextToken();
        assert (token == StreamTokenizer.TT_NUMBER) : "Expected number";
        return (int)tokenizer.nval;
    }

    private float readFloat() throws IOException{
        int token = tokenizer.nextToken();
        assert (token == StreamTokenizer.TT_NUMBER) : "Expected number";
        return (float)tokenizer.nval;
    }
    
    private int readAddr() throws IOException {
        int token = tokenizer.nextToken();
        assert (token == StreamTokenizer.TT_NUMBER) : "Expected number";
        return (int)tokenizer.nval;
    }
    
    private void readEnd() throws IOException {
        int token = tokenizer.nextToken();
        assert (token == ';') : "Expected semicolon";
    }

    
    public Instruction readNext() {
        Instruction instr = null;
                
        try {
            String mnemo = readMnemo();
            instr = new Instruction(mnemo);

            switch (instr.defs.getOperType()) {
            case INT:
                int operInt = readInt();
                instr.oper.setInt(operInt);
                break;
            case FLOAT:
                float operFloat = readFloat();
                instr.oper.setFloat(operFloat);
                break;
            case ADDR:
                int operAddr = readAddr();
                instr.oper.setAddr(operAddr);
                break;
            default:
                break;
            }
            
            readEnd();
        } catch (IOException ex) {
            System.err.println("Caught IOException while tokenizing: "
                    + ex.getMessage());
            System.exit(1);
        }
        return instr;
    }
}
