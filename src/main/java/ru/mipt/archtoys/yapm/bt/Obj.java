/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.yapm.bt;

/**
 *
 * @author danisimo
 */
/**
 * Object class
 */
abstract class Obj {

    protected enum ObjType {

        CONST, MEM, REG, NONE
    }
    protected ObjType type = ObjType.NONE;

    @Override
    public abstract String toString();

    /*
     * Constant object class
     */
    public static abstract class ObjConst extends Obj {

        protected enum ObjConstType {

            INT,
            FLOAT,
            NONE
        }
        protected ObjConstType constType = ObjConstType.NONE;

        public ObjConst() {
            type = ObjType.CONST;
        }

        @Override
        public abstract String toString();
    }

    /*
     * Integer constant object class
     */
    public static class ObjConstInt extends ObjConst {

        public int value;

        public ObjConstInt() {
            this(0);
        }

        public ObjConstInt(int val) {
            constType = ObjConstType.INT;
            value = val;
        }

        @Override
        public String toString() {
            return ((Integer) value).toString();
        }
    }

    /*
     * Float constant object class
     */
    public static class ObjConstFloat extends ObjConst {

        public float value;

        public ObjConstFloat() {
            this(0);
        }

        public ObjConstFloat(float val) {
            constType = ObjConstType.FLOAT;
            value = val;
        }

        @Override
        public String toString() {
            return ((Float) value).toString();
        }
    }

    /*
     * Memory object class
     */
    public static class ObjMem extends Obj {

        public int addr;

        public ObjMem() {
            this(-1);
        }

        public ObjMem(int val) {
            type = ObjType.MEM;
            addr = val;
        }

        @Override
        public String toString() {
            return ((Integer) addr).toString();
        }
    }

    /*
     * Register object class
     */
    public static class ObjReg extends Obj {

        public int num;

        public ObjReg() {
            this(-1);
        }

        public ObjReg(int val) {
            type = ObjType.REG;
            num = val;
        }

        @Override
        public String toString() {
            return "r" + ((Integer) num).toString();
        }
    }
}
