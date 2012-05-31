/*
 * Translator class.
 * 
 * Implementation of binary translation system from StAr to yapm.
 */
package ru.mipt.archtoys.yapm.bt;

import java.util.Iterator;
import java.util.LinkedList;
import ru.mipt.archtoys.star.asm.Instruction;
import ru.mipt.archtoys.star.asm.AsmReader;

/**
 *
 * @author danisimo
 */
public class Translator {

    private static String fileName;
    private static LinkedList<Instruction> starAsm;
    private static LinkedList<Operation> yapmIr;

    /**
     * Simple translation from star to yasm.
     * Construct eqivalents of all star operation with realization
     * of stack on memory
     */
    private static void lowering() {
        Iterator<Instruction> iter = starAsm.iterator();
        while (iter.hasNext()) {
            Instruction instr = iter.next();
            Operation oper = new Operation( instr);
            yapmIr.add(oper);
        }
    }

    /**
     * Main translation process 
     */
    private static void runTranslation() {
        /* Process input asm file */
        starAsm = new LinkedList<Instruction>();
        AsmReader asmReader = new AsmReader(fileName);
        starAsm = asmReader.readAll();

        /* Lowering from star asm to yapm operations */
        lowering();
    }

    public static void main(String[] args) {
        /*
         * Get filename
         */
        if (args.length > 0) {
            fileName = args[0];
        } else {
            throw new Error("No input file specified");
        }

        /*
         * Start translation
         */
        runTranslation();
    }

    /**
     * Object class
     */
    private static abstract class Obj {

        protected enum ObjType {

            CONST,
            MEM,
            REG,
            NONE
        }
        protected ObjType type = ObjType.NONE;
    }

    /*
     * Constant object class
     */
    private static abstract class ObjConst extends Obj {

        protected enum ObjConstType {

            INT,
            FLOAT,
            NONE
        }
        protected ObjConstType constType = ObjConstType.NONE;

        public ObjConst() {
            type = ObjType.CONST;
        }
    }

    /*
     * Integer constant object class
     */
    private class ObjConstInt extends ObjConst {

        public ObjConstInt() {
            constType = ObjConstType.INT;
        }
    }

    /*
     * Float constant object class
     */
    private class ObjConstFloat extends ObjConst {

        public ObjConstFloat() {
            constType = ObjConstType.FLOAT;
        }
    }

    /*
     * Memory object class
     */
    private class ObjMem extends Obj {

        public ObjMem() {
            type = ObjType.MEM;
        }
    }

    /*
     * Register object class
     */
    private class ObjReg extends Obj {

        public ObjReg() {
            type = ObjType.REG;
        }
    }

    /**
     * Operation class - internal representation of operation
     */
    private static class Operation {

        public enum defs {
            LDC,
            LD,
            ST,
            LDA,
            STA,
            CALL,
            ADD,
            SUB,
            MUL,
            DIV,
            REM,
            NEG,
            FD2I,
            FI2D
        }
        
        public LinkedList<Obj> args;
        public LinkedList<Obj> res;

        public Operation() {
        }
        
        public Operation( Instruction instr) {
            
        }
    }
}
