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

    public enum OperType{
        NONE(0), INT(1), FLOAT(2), ADDR(2);
        
        public int size;
        
        private OperType(int sz){
            size = sz;
        }
    }
    
    public class Oper{
        private int valInt;
        private float valFloat;
        private int valAddr;
        
        private OperType type;
        
        public Oper(OperType tp){
            type = tp;
        }
        
        public void setInt(int val){
            assert (type == OperType.INT);
            valInt = val;
        }
        
        public void setFloat(float val) {
            assert (type == OperType.FLOAT);
            valFloat = val;
        }

        public void setAddr(int val) {
            assert (type == OperType.ADDR);
            valAddr = val;
        }
        
        public int getInt(){
            assert (type == OperType.INT);
            return valInt;
        }

        public float getFloat() {
            assert (type == OperType.FLOAT);
            return valFloat;
        }

        public int getAddr() {
            assert (type == OperType.ADDR);
            return valAddr;
        }
    }
      
    public enum Defs{
        LDCI  (0,OperType.INT), 
        LDCD  (0,OperType.FLOAT),
        LDI   (0,OperType.INT), 
        LDD   (0,OperType.FLOAT),
        LDSI  (0,OperType.INT), 
        LDSD  (0,OperType.FLOAT),
        STI   (0,OperType.INT), 
        STD   (0,OperType.FLOAT),
        ALLOC (0,OperType.INT),
        SCR   (0,OperType.NONE),
        LDA   (0,OperType.ADDR),
        INDEX (0,OperType.NONE),
        ADD   (0,OperType.NONE),
        SUB   (0,OperType.NONE),
        MUL   (0,OperType.NONE),
        DIV   (0,OperType.NONE),
        REM   (0,OperType.NONE),
        CHS   (0,OperType.NONE),
        FI2D  (0,OperType.NONE),
        FD2I  (0,OperType.NONE),
        MS    (0,OperType.NONE),
        CALL  (0,OperType.NONE);
        
        private int opCode;
        
        private OperType operType;
        
        private Defs(int opc, OperType opt){
            opCode = opc;
            operType = opt;
        }
        
        public int getOpCode(){
            return opCode;
        }
        
        public boolean hasOper(){
            return operType != OperType.NONE;
        }
        
        public OperType getOperType(){
            return operType;
        }
    }
    
    public Defs defs;
    public Oper oper;
    
    public Instruction( String name) {
        try{
            defs = Defs.valueOf(name.toUpperCase());
            oper = new Oper(defs.getOperType());
        } catch (IllegalArgumentException ex) {
            System.err.println("Caught runtime exception while creating instruction: "
                    + ex);
            System.exit(1);
        }
    }
}
