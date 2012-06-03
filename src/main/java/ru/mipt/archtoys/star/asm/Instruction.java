/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.star.asm;

/**
 *
 * @author genius
 */
public class Instruction {

    public enum OperType {

        NONE(0), INT(1), FLOAT(2), ADDR(2);
        public int size;

        private OperType(int sz) {
            size = sz;
        }
    }

    public abstract class Oper {

        @Override
        public abstract String toString();

        @Override
        public abstract boolean equals(Object obj);

        @Override
        public abstract int hashCode();
    }

    public class OperInteger extends Oper {

        public int value;

        private OperInteger() {
            assert (defs.operType == OperType.INT);
        }

        public String toString() {
            return Integer.toString(value);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof OperInteger)) {
                return false;
            }
            if (obj == null) {
                return false;
            }
            return (this.value == ((OperInteger) obj).value);
        }

        @Override
        public int hashCode() {
            return value;
        }
    }

    public class OperFloat extends Oper {

        public float value;

        private OperFloat() {
            assert (defs.operType == OperType.FLOAT);
        }

        public String toString() {
            return Float.toString(value);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof OperFloat)) {
                return false;
            }
            if (obj == null) {
                return false;
            }
            return (this.value == ((OperFloat) obj).value);
        }

        @Override
        public int hashCode() {
            return Float.floatToIntBits(value);
        }
    }

    public class OperAddr extends Oper {

        public int value;

        private OperAddr() {
            assert (defs.operType == OperType.ADDR);
        }

        public String toString() {
            return Integer.toString(value);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof OperAddr)) {
                return false;
            }
            if (obj == null) {
                return false;
            }
            return (this.value == ((OperAddr) obj).value);
        }

        @Override
        public int hashCode() {
            return value;
        }
    }

    public enum Defs {

        LDCI(0, OperType.INT),
        LDCD(0, OperType.FLOAT),
        LDI(0, OperType.INT),
        LDD(0, OperType.FLOAT),
        LDSI(0, OperType.INT),
        LDSD(0, OperType.FLOAT),
        STI(0, OperType.NONE),
        STD(0, OperType.NONE),
        ALLOC(0, OperType.INT),
        MAI(0, OperType.NONE),
        MAD(0, OperType.NONE),
        SCR(0, OperType.NONE),
        LDA(0, OperType.ADDR),
        INDEX(0, OperType.NONE),
        ADDI(0, OperType.NONE),
        ADDD(0, OperType.NONE),
        SUBI(0, OperType.NONE),
        SUBD(0, OperType.NONE),
        MULI(0, OperType.NONE),
        MULD(0, OperType.NONE),
        DIVI(0, OperType.NONE),
        DIVD(0, OperType.NONE),
        REM(0, OperType.NONE),
        CHSI(0, OperType.NONE),
        CHSD(0, OperType.NONE),
        FI2D(0, OperType.NONE),
        FD2I(0, OperType.NONE),
        MS(0, OperType.NONE),
        CALL(0, OperType.NONE);
        private int opCode;
        private OperType operType;

        private Defs(int opc, OperType opt) {
            opCode = opc;
            operType = opt;
        }

        public int getOpCode() {
            return opCode;
        }

        public boolean hasOper() {
            return operType != OperType.NONE;
        }

        public OperType getOperType() {
            return operType;
        }
    }
    public Defs defs;
    public Oper oper;

    public Instruction(String name) {
        try {
            defs = Defs.valueOf(name.toUpperCase());
            switch (defs.getOperType()) {
                case NONE:
                    break;
                case INT:
                    oper = new OperInteger();
                    break;
                case FLOAT:
                    oper = new OperFloat();
                    break;
                case ADDR:
                    oper = new OperAddr();
                    break;
                default:
                    break;
            }
        } catch (IllegalArgumentException ex) {
            System.err.println("Caught runtime exception while creating instruction: "
                    + ex);
            System.exit(1);
        }
    }

    @Override
    public String toString() {
        String res = new String();

        res += defs;
        if (defs.hasOper()) {
            res += " " + oper;
        }
        
        res += ";";
        
        return res;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Instruction)) {
            return false;
        }
        boolean result = true;
        result = result && this.defs.equals(((Instruction) obj).defs);
        if (this.defs.hasOper()) {
            result = result && this.oper.equals(((Instruction) obj).oper);
        }
        return result;

    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.defs != null ? this.defs.hashCode() : 0);
        hash = 97 * hash + (this.oper != null ? this.oper.hashCode() : 0);
        return hash;
    }
}
