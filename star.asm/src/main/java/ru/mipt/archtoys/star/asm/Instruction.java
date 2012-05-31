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
    
    public abstract class Oper{
        @Override
        public abstract String toString();
    }
    
    public class OperInteger extends Oper{
        public int value;
        
        private OperInteger(){
            assert( defs.operType == OperType.INT);
        }
        
        public String toString(){
            return Integer.toString(value);
        }
    }
    
    public class OperFloat extends Oper{
        public float value;
        
        private OperFloat(){
            assert( defs.operType == OperType.FLOAT);
        }
        
        public String toString(){
            return Float.toString(value);
        }
    }
    
    public class OperAddr extends Oper{
        public int value;
        
        private OperAddr(){
            assert( defs.operType == OperType.ADDR);
        }
        
        public String toString(){
            return Integer.toString(value);
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
            switch (defs.getOperType()){
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
    
    public void print(){
        System.out.print(defs);
        if (defs.hasOper()){
            System.out.print(" " + oper);
        }
        System.out.println();
    }
}
