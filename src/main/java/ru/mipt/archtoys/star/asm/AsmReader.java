/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.star.asm;

import java.io.*;
import java.util.LinkedList;

/**
 *
 * @author genius
 */
public class AsmReader {

    private static StreamTokenizer tokenizer = null;
    private static int token = 0;

    public AsmReader(Reader reader) {
        tokenizer = new StreamTokenizer(reader);
        tokenizer.eolIsSignificant(true);
    }

    private String readMnemo() throws IOException {
        if (token != StreamTokenizer.TT_WORD) {
            throw new IOException("Expected word: " + (char) token);
        }
        return tokenizer.sval;
    }

    private int readInt() throws IOException {
        if (token != StreamTokenizer.TT_NUMBER) {
            throw new IOException("Expected number: " + (char) token);
        }
        return (int) tokenizer.nval;
    }

    private float readFloat() throws IOException {
        if (token != StreamTokenizer.TT_NUMBER) {
            throw new IOException("Expected number: " + (char) token);
        }
        return (float) tokenizer.nval;
    }

    private int readAddr() throws IOException {
        if (token != StreamTokenizer.TT_NUMBER) {
            throw new IOException("Expected number: " + (char) token);
        }
        return (int) tokenizer.nval;
    }

    private void readEnd() throws IOException {
        if (token != ';') {
            throw new IOException((char) token + "Expected semicolon");
        }
    }

    private Instruction readNext() throws IOException {
        Instruction instr = null;
        int pos = 0;


        while ((token != StreamTokenizer.TT_EOL)
                && (token != StreamTokenizer.TT_EOF)) {
            switch (pos) {
            case 0:
                String mnemo = readMnemo();
                instr = new Instruction(mnemo);
                break;
            case 1:
                switch (instr.defs.getOperType()) {
                case INT:
                    int operInt = readInt();
                    ((Instruction.OperInteger) instr.oper).value = operInt;
                    break;
                case FLOAT:
                    float operFloat = readFloat();
                    ((Instruction.OperFloat) instr.oper).value = operFloat;
                    break;
                case ADDR:
                    int operAddr = readAddr();
                    ((Instruction.OperAddr) instr.oper).value = operAddr;
                    break;
                default:
                    break;
                }
                break;
            case 2:
                readEnd();
                break;
            default:
                System.err.println("Unexpected end of line: " + (char) token);
                break;
            }
            token = tokenizer.nextToken();
            pos++;
        }

        return instr;
    }

    public LinkedList<Instruction> readAll() {
        LinkedList<Instruction> list = new LinkedList<Instruction>();
        try {
            Instruction instr;
            while (token != StreamTokenizer.TT_EOF) {
                token = tokenizer.nextToken();
                instr = readNext();
                if (instr != null) {
                    list.add(instr);
                }
            }
        } catch (IOException ex) {
            System.err.println("Caught IOException while tokenizing: "
                    + ex.getMessage());
            System.exit(1);
        }
        return list;
    }
}
