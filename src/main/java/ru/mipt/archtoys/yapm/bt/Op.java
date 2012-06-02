/*
 * Operand class.
 * Describes different types of operands in operations
 * used in binary translation
 */
package ru.mipt.archtoys.yapm.bt;

/**
 *
 * @author danisimo
 */
/**
 * Operand class
 */
abstract class Op {

    protected enum OpType {

        CONST, MEM, REG, NONE
    }
    protected OpType type = OpType.NONE;

    @Override
    public abstract String toString();

    /*
     * Constant operand class
     */
    public static abstract class OpConst extends Op {

        protected enum OpConstType {

            INT,
            FLOAT,
            NONE
        }
        protected OpConstType constType = OpConstType.NONE;

        public OpConst() {
            type = OpType.CONST;
        }

        @Override
        public abstract String toString();
    }

    /*
     * Integer constant operand class
     */
    public static class OpConstInt extends OpConst {

        public int value;

        public OpConstInt() {
            this(0);
        }

        public OpConstInt(int val) {
            constType = OpConstType.INT;
            value = val;
        }

        @Override
        public String toString() {
            return ((Integer) value).toString();
        }
    }

    /*
     * Float constant operand class
     */
    public static class OpConstFloat extends OpConst {

        public float value;

        public OpConstFloat() {
            this(0);
        }

        public OpConstFloat(float val) {
            constType = OpConstType.FLOAT;
            value = val;
        }

        @Override
        public String toString() {
            return ((Float) value).toString();
        }
    }

    /*
     * Memory operand class
     */
    public static class OpMem extends Op {

        public int addr;

        public OpMem() {
            this(-1);
        }

        public OpMem(int val) {
            type = OpType.MEM;
            addr = val;
        }

        @Override
        public String toString() {
            return ((Integer) addr).toString();
        }
    }

    /*
     * Register operand class
     */
    public static class OpReg extends Op {

        public int num;

        public OpReg() {
            this(-1);
        }

        public OpReg(int val) {
            type = OpType.REG;
            num = val;
        }

        @Override
        public String toString() {
            return "r" + ((Integer) num).toString();
        }
    }
}
