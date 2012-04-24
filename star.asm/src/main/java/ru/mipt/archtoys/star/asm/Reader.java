/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.star.asm;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;

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
    
    private String readMnemo() throws IOException, EOFException{
        int token = tokenizer.nextToken();
        if (token == StreamTokenizer.TT_EOF){
            throw new EOFException();
        }
        if (token != StreamTokenizer.TT_WORD){
            throw new IOException("Expected word: " + (char)token);
        }
        return tokenizer.sval;
    }
    
    private int readInt() throws IOException{
        int token = tokenizer.nextToken();
        if (token != StreamTokenizer.TT_NUMBER){
            throw new IOException("Expected number: " + (char)token);
        }
        return (int)tokenizer.nval;
    }

    private float readFloat() throws IOException{
        int token = tokenizer.nextToken();
        if (token != StreamTokenizer.TT_NUMBER){
            throw new IOException("Expected number: " + (char)token);
        }
        return (float)tokenizer.nval;
    }
    
    private int readAddr() throws IOException {
        int token = tokenizer.nextToken();
        if (token != StreamTokenizer.TT_NUMBER){
            throw new IOException("Expected number: " + (char)token);
        }
        return (int)tokenizer.nval;
    }
    
    private void readEnd() throws IOException{
        int token = tokenizer.nextToken();
        if (token != ';'){
            throw new IOException((char)token + "Expected semicolon");
        }
    }

    
    public Instruction readNext() throws EOFException{
        Instruction instr = null;
                
        try {
            String mnemo = readMnemo();
            instr = new Instruction(mnemo);

            switch (instr.defs.getOperType()) {
            case INT:
                int operInt = readInt();
                ((Instruction.OperInteger)instr.oper).value = operInt;
                break;
            case FLOAT:
                float operFloat = readFloat();
                ((Instruction.OperFloat)instr.oper).value = operFloat;
                break;
            case ADDR:
                int operAddr = readAddr();
                ((Instruction.OperAddr)instr.oper).value = operAddr;
                break;
            default:
                break;
            }
            
            readEnd();
        } catch (EOFException ex){
            throw ex; 
        } catch (IOException ex) {
            System.err.println("Caught IOException while tokenizing: "
                    + ex.getMessage());
            System.exit(1);
        }
        return instr;
    }
}
