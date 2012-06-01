/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.yapm.bt;

import java.util.Iterator;
import java.util.LinkedList;
import ru.mipt.archtoys.star.asm.Instruction;
import ru.mipt.archtoys.star.asm.Instruction.OperAddr;
import ru.mipt.archtoys.star.asm.Instruction.OperFloat;
import ru.mipt.archtoys.star.asm.Instruction.OperInteger;
import ru.mipt.archtoys.yapm.bt.Obj.ObjConstFloat;
import ru.mipt.archtoys.yapm.bt.Obj.ObjConstInt;
import ru.mipt.archtoys.yapm.bt.Obj.ObjMem;
import ru.mipt.archtoys.yapm.bt.Obj.ObjReg;

/**
 *
 * @author danisimo
 */
public class Lowering {

    private LinkedList<Instruction> starAsm;
    private LinkedList<Operation> yapmIr;
    private int tosAddr;
    private int regNum;

    public Lowering(LinkedList<Instruction> asm) {
        starAsm = asm;
        yapmIr = new LinkedList<Operation>();
        tosAddr = 0;
        regNum = 0;
    }

    public LinkedList<Operation> runLowering() {
        Iterator<Instruction> iter;
        iter = starAsm.iterator();
        while (iter.hasNext()) {
            Instruction instr = iter.next();
            switch (instr.defs) {
            case LDCI:
            case LDCD:
                lowirLdConst(instr);
                break;
            case LDI:
            case LDD:
                lowirLdShift(instr);
                break;
            case LDSI:
            case LDSD:
                lowirLdDyn(instr);
                break;
            case STI:
            case STD:
                lowirStDyn(instr);
                break;
            case ALLOC:
                lowirAlloc(instr);
                break;
            case MRI:
            case MRF:
                lowirMa(instr);
                break;
            case SCR:
                lowirScr(instr);
                break;
            case LDA:
                lowirLdAddr(instr);
                break;
            case INDEX:
                lowirIndex(instr);
                break;
            case ADD:
            case SUB:
            case MUL:
            case DIV:
            case REM:
                lowirArith(instr);
            case CHS:
                lowirChs(instr);
            case FI2D:
            case FD2I:
                lowirConv(instr);
                break;
            case MS:
                lowirMs(instr);
                break;
            case CALL:
                lowirCall(instr);
                break;
            }
        }
        return yapmIr;
    }

    private void lowirLdConst(Instruction instr) {
        /*
         * Construct load of constant to register.
         */
        Operation oper = new Operation("ldc");
        int tosIncr = 0;
        switch (instr.defs.getOperType()) {
        case INT:
            int valueInt = ((OperInteger) instr.oper).value;
            oper.args.add(new ObjConstInt(valueInt));
            tosIncr = 1;
            break;
        case FLOAT:
            float valueFloat = ((OperFloat) instr.oper).value;
            oper.args.add(new ObjConstFloat(valueFloat));
            tosIncr = 2;
            break;
        default:
            assert false;
        }
        int reg = nextReg();
        oper.res.add(new ObjReg(reg));
        yapmIr.add(oper);
        /*
         * Construct store from register to 'tos' address
         */
        oper = new Operation("st");
        oper.args.add(new ObjReg(reg));
        oper.res.add(new ObjMem(tosAddr));
        yapmIr.add(oper);
        /*
         * Correct tos address
         */
        tosAddr += tosIncr;
    }

    private void lowirLdShift(Instruction instr) {
        /*
         * Construct load from address 'shift' to register
         */
        Operation oper = new Operation("ld");
        int tosIncr = 0;
        switch (instr.defs.getOperType()) {
        case INT:
            tosIncr = 1;
            break;
        case FLOAT:
            tosIncr = 2;
            break;
        default:
            assert false;
        }
        int shift = ((OperAddr) instr.oper).value;
        int reg = nextReg();
        oper.args.add(new ObjMem(shift));
        oper.res.add(new ObjReg(reg));
        yapmIr.add(oper);
        /*
         * Construct store from register to 'tos' address
         */
        oper = new Operation("st");
        oper.args.add(new ObjReg(reg));
        oper.res.add(new ObjMem(tosAddr));
        yapmIr.add(oper);
        /*
         * Correct tos address
         */
        tosAddr += tosIncr;
    }

    private void lowirLdDyn(Instruction instr) {
        /*
         * Construct load from address 'tos' to register
         */
        Operation oper = new Operation("ld");
        int tosIncr = 0;
        switch (instr.defs.getOperType()) {
        case INT:
            tosIncr = 1;
            break;
        case FLOAT:
            tosIncr = 2;
            break;
        default:
            assert false;
        }
        int reg = nextReg();
        oper.args.add(new ObjMem(tosAddr - 2)); // Address is 2byte wide
        oper.res.add(new ObjReg(reg));
        yapmIr.add(oper);
        /*
         * Construct load of 0 to base register
         */
        int reg1 = nextReg();
        oper = new Operation("ldc");
        oper.args.add(new ObjConstInt(0));
        oper.args.add(new ObjReg(reg1));
        yapmIr.add(oper);
        /*
         * Construct ld from address in index register to another register
         */
        oper = new Operation("lda");
        int reg2 = nextReg();
        oper.args.add(new ObjReg(reg1));
        oper.args.add(new ObjReg(reg));
        oper.res.add(new ObjReg(reg2));
        yapmIr.add(oper);
        /*
         * Construct st from register to 'tos' address
         */
        oper = new Operation("st");
        oper.args.add(new ObjReg(reg2));
        oper.res.add(new ObjMem(tosAddr));
        yapmIr.add(oper);
        /*
         * Correct tos address
         */
        tosIncr -= 2; // Address is removed from tos
        tosAddr += tosIncr;
    }

    private void lowirStDyn(Instruction instr) {
        /*
         * Construct load address from address 'tos' to register
         */
        Operation oper = new Operation("ld");
        int reg = nextReg();
        oper.args.add(new ObjMem(tosAddr - 2)); // Address is 2byte wide
        oper.res.add(new ObjReg(reg));
        yapmIr.add(oper);
        /*
         * Construct load value from address 'tos-1' to register
         */
        int tosIncr = 0;
        switch (instr.defs.getOperType()) {
        case INT:
            tosIncr = 1;
            break;
        case FLOAT:
            tosIncr = 2;
            break;
        default:
            assert false;
        }
        oper = new Operation("ld");
        int reg1 = nextReg();
        oper.args.add(new ObjMem(tosAddr - 2 - tosIncr)); // Address is 2byte wide
        // Value is 1-2byte
        oper.res.add(new ObjReg(reg1));
        yapmIr.add(oper);
        /*
         * Construct load of 0 to base register
         */
        int reg2 = nextReg();
        oper = new Operation("ldc");
        oper.args.add(new ObjConstInt(0));
        oper.args.add(new ObjReg(reg1));
        yapmIr.add(oper);
        /*
         * Construct st from register to address in index
         */
        oper = new Operation("sta");
        oper.args.add(new ObjReg(reg2));
        oper.args.add(new ObjReg(reg1));
        oper.args.add(new ObjReg(reg));
        yapmIr.add(oper);
        /*
         * Correct tos address
         */
        tosIncr = -tosIncr - 2; // Address is removed from tos
        tosAddr += tosIncr;
    }

    private void lowirAlloc(Instruction instr) {
        int tosIncr = ((OperInteger) instr.oper).value;
        tosAddr += tosIncr;
    }

    private void lowirMa(Instruction instr) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void lowirScr(Instruction instr) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void lowirLdAddr(Instruction instr) {
        /*
         * Construct load of address constant to register.
         */
        Operation oper = new Operation("ldc");
        int tosIncr = 2;
        int addr = ((OperAddr) instr.oper).value;
        int reg = nextReg();
        oper.res.add(new ObjConstInt(addr));
        oper.res.add(new ObjReg(reg));
        yapmIr.add(oper);
        /*
         * Construct store from register to 'tos' address
         */
        oper = new Operation("st");
        oper.args.add(new ObjReg(reg));
        oper.res.add(new ObjMem(tosAddr));
        yapmIr.add(oper);
        /*
         * Correct tos address
         */
        tosAddr += tosIncr;
    }

    private void lowirIndex(Instruction instr) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void lowirArith(Instruction instr) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void lowirChs(Instruction instr) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void lowirConv(Instruction instr) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void lowirMs(Instruction instr) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void lowirCall(Instruction instr) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private int nextReg() {
        return regNum++;
    }
}
