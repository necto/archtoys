/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.yapm.bt;

import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author danisimo
 */
/**
 * Operation class - internal representation of operation
 */
class Operation {

    public enum Defs {

        LDC(1), LD(1), ST(1), LDA(1), STA(1), CALL(1), ADD(2), SUB(2), MUL(2),
        DIV(2), REM(2), NEG(2), FD2I(2), FI2D(2), NOP(0);
        int exeUnit;

        private Defs(int unit) {
            exeUnit = unit;
        }
    }
    public LinkedList<Op> args;
    public LinkedList<Op> res;
    private Defs defs;

    public Operation(String name) {
        args = new LinkedList<Op>();
        res = new LinkedList<Op>();
        defs = Defs.valueOf(name.toUpperCase());
    }
    
    public int getUnit(){
        return defs.exeUnit;
    }

    @Override
    public String toString() {
        String str = new String();

        str += defs;

        LinkedList<Op> allArgs = new LinkedList<Op>();
        allArgs.addAll(args);
        allArgs.addAll(res);

        Iterator<Op> iter = allArgs.iterator();
        if (iter.hasNext()) {
            str += " ";
        }
        while (iter.hasNext()) {
            str += iter.next().toString();
            if (iter.hasNext()) {
                str += ", ";
            }
        }

        str += "; ";

        return str;
    }

    public static class MacroOperation {

        public final int MACRO_WIDTH = 4;
        public Operation[] opers;

        public MacroOperation() {
            opers = new Operation[MACRO_WIDTH];
            int i;
            for (i = 0; i < MACRO_WIDTH; i++) {
                opers[i] = new Operation("nop");
            }
        }

        @Override
        public String toString() {
            String str = new String();
            int i;
            for (i = 0; i < MACRO_WIDTH; i++) {
                str += opers[i].toString();
            }

            return str;
        }
    }
}
