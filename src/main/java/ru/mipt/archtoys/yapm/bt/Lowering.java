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
import ru.mipt.archtoys.yapm.bt.Op.OpConstFloat;
import ru.mipt.archtoys.yapm.bt.Op.OpConstInt;
import ru.mipt.archtoys.yapm.bt.Op.OpMem;
import ru.mipt.archtoys.yapm.bt.Op.OpReg;

/**
 *
 * @author danisimo
 */
public class Lowering {

    private Ir ir;
    private int tosAddr;
    private int msAddr;
    private int regNum;

    public Lowering(Ir i_r) {
        ir = i_r;
        tosAddr = 0;
        regNum = 0;
        msAddr = -1;
    }

    public LinkedList<Operation> runLowering() {
        Iterator<Instruction> iter;
        iter = ir.starAsm.iterator();
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
            case MAI:
            case MAD:
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
            case ADDI:
            case ADDD:
            case SUBI:
            case SUBD:
            case MULI:
            case MULD:
            case DIVI:
            case DIVD:
            case REM:
                lowirArith(instr);
                break;
            case CHSI:
            case CHSD:
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
        return ir.seq;
    }

    private void lowirLdConst(Instruction instr) {
        /*
         * Construct load of constant to register.
         */
        Operation oper = new Operation("ldc");
        switch (instr.defs.getOperType()) {
        case INT:
            int valueInt = ((OperInteger) instr.oper).value;
            oper.args.add(new OpConstInt(valueInt));
            break;
        case FLOAT:
            float valueFloat = ((OperFloat) instr.oper).value;
            oper.args.add(new OpConstFloat(valueFloat));
            break;
        default:
            assert false;
        }
        int reg = nextReg();
        oper.res.add(new OpReg(reg));
        ir.seq.add(oper);
        /*
         * Construct store from register to 'tos' address
         */
        oper = new Operation("st");
        oper.args.add(new OpReg(reg));
        oper.res.add(new OpMem(tosAddr));
        ir.seq.add(oper);
        /*
         * Correct tos address
         */
        tosAddr += 2;
    }

    private void lowirLdShift(Instruction instr) {
        /*
         * Construct load from address 'shift' to register
         */
        Operation oper = new Operation("ld");
        int shift = ((OperAddr) instr.oper).value;
        int reg = nextReg();
        oper.args.add(new OpMem(shift));
        oper.res.add(new OpReg(reg));
        ir.seq.add(oper);
        /*
         * Construct store from register to 'tos' address
         */
        oper = new Operation("st");
        oper.args.add(new OpReg(reg));
        oper.res.add(new OpMem(tosAddr));
        ir.seq.add(oper);
        /*
         * Correct tos address
         */
        tosAddr += 2;
    }

    private void lowirLdDyn(Instruction instr) {
        /*
         * Construct load from address 'tos' to register
         */
        Operation oper = new Operation("ld");
        int reg = nextReg();
        oper.args.add(new OpMem(tosAddr - 2)); // Address
        oper.res.add(new OpReg(reg));
        ir.seq.add(oper);
        /*
         * Construct load of 0 to base register
         */
        int reg1 = nextReg();
        oper = new Operation("ldc");
        oper.args.add(new OpConstInt(0));
        oper.args.add(new OpReg(reg1));
        ir.seq.add(oper);
        /*
         * Construct ld from address in index register to another register
         */
        oper = new Operation("lda");
        int reg2 = nextReg();
        oper.args.add(new OpReg(reg1));
        oper.args.add(new OpReg(reg));
        oper.res.add(new OpReg(reg2));
        ir.seq.add(oper);
        /*
         * Construct st from register to 'tos' address
         */
        oper = new Operation("st");
        oper.args.add(new OpReg(reg2));
        oper.res.add(new OpMem(tosAddr - 2));
        ir.seq.add(oper);
        /*
         * Correct tos address
         */
        tosAddr += 0; // Address is removed from tos, new value added
    }

    private void lowirStDyn(Instruction instr) {
        /*
         * Construct load address from address 'tos' to register
         */
        Operation oper = new Operation("ld");
        int reg = nextReg();
        oper.args.add(new OpMem(tosAddr - 2)); // Address
        oper.res.add(new OpReg(reg));
        ir.seq.add(oper);
        /*
         * Construct load value from address 'tos-1' to register
         */
        oper = new Operation("ld");
        int reg1 = nextReg();
        oper.args.add(new OpMem(tosAddr - 4)); // Value
        oper.res.add(new OpReg(reg1));
        ir.seq.add(oper);
        /*
         * Construct load of 0 to base register
         */
        int reg2 = nextReg();
        oper = new Operation("ldc");
        oper.args.add(new OpConstInt(0));
        oper.args.add(new OpReg(reg1));
        ir.seq.add(oper);
        /*
         * Construct st from register to address in index
         */
        oper = new Operation("sta");
        oper.args.add(new OpReg(reg2));
        oper.args.add(new OpReg(reg1));
        oper.args.add(new OpReg(reg));
        ir.seq.add(oper);
        /*
         * Correct tos address
         */
        tosAddr += -2;
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
        int addr = ((OperAddr) instr.oper).value;
        int reg = nextReg();
        oper.res.add(new OpConstInt(addr));
        oper.res.add(new OpReg(reg));
        ir.seq.add(oper);
        /*
         * Construct store from register to 'tos' address
         */
        oper = new Operation("st");
        oper.args.add(new OpReg(reg));
        oper.res.add(new OpMem(tosAddr));
        ir.seq.add(oper);
        /*
         * Correct tos address
         */
        tosAddr += 2;
    }

    private void lowirIndex(Instruction instr) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void lowirArith(Instruction instr) {
        String arithName = instr.defs.toString().substring(0, 3);
        /*
         * Construct load from 'tos-1' to register
         */
        Operation oper = new Operation("ld");
        int reg = nextReg();
        oper.args.add(new OpMem(tosAddr - 4)); // Arg 1
        oper.res.add(new OpReg(reg));
        ir.seq.add(oper);
        /*
         * Construct load from 'tos' to register
         */
        oper = new Operation("ld");
        int reg1 = nextReg();
        oper.args.add(new OpMem(tosAddr - 2)); // Arg 2
        oper.res.add(new OpReg(reg1));
        ir.seq.add(oper);
        /*
         * Construct arithm operation
         */
        oper = new Operation(arithName);
        int reg2 = nextReg();
        oper.args.add(new OpReg(reg));
        oper.args.add(new OpReg(reg1));
        oper.res.add(new OpReg(reg2));
        ir.seq.add(oper);
        /*
         * Store result to memory
         */
        oper = new Operation("st");
        oper.args.add(new OpReg(reg2));
        oper.args.add(new OpMem(tosAddr - 4));
        ir.seq.add(oper);
        /*
         * Correct tos address
         */
        tosAddr += -2;
    }

    private void lowirChs(Instruction instr) {
        String chsgnName = instr.defs.toString().substring(0, 3);
        /*
         * Construct load from 'tos' to register
         */
        Operation oper = new Operation("ld");
        int reg1 = nextReg();
        oper.args.add(new OpMem(tosAddr - 2));
        oper.res.add(new OpReg(reg1));
        ir.seq.add(oper);
        /*
         * Construct arithm operation
         */
        oper = new Operation(chsgnName);
        int reg2 = nextReg();
        oper.args.add(new OpReg(reg1));
        oper.res.add(new OpReg(reg2));
        ir.seq.add(oper);
        /*
         * Store result to memory
         */
        oper = new Operation("st");
        oper.args.add(new OpReg(reg2));
        oper.args.add(new OpMem(tosAddr - 2));
        ir.seq.add(oper);
        /*
         * Correct tos address
         */
        tosAddr += 0; // It remains the same
    }

    private void lowirConv(Instruction instr) {
        String convName = instr.defs.toString().substring(0, 3);
        Operation oper = new Operation("ld");
        /*
         * Construct load from 'tos' to register
         */
        oper = new Operation("ld");
        int reg1 = nextReg();
        oper.args.add(new OpMem(tosAddr - 2));
        oper.res.add(new OpReg(reg1));
        ir.seq.add(oper);
        /*
         * Construct arithm operation
         */
        oper = new Operation(convName);
        int reg2 = nextReg();
        oper.args.add(new OpReg(reg1));
        oper.res.add(new OpReg(reg2));
        ir.seq.add(oper);
        /*
         * Store result to memory
         */
        oper = new Operation("st");
        oper.args.add(new OpReg(reg2));
        oper.args.add(new OpMem(tosAddr - 2));
        ir.seq.add(oper);
        /*
         * Correct tos address
         */
        tosAddr += 0;
    }

    private void lowirMs(Instruction instr) {
        msAddr = tosAddr;
        tosAddr += 2;
    }

    private void lowirCall(Instruction instr) {
        /*
         * Construct call
         */
        Operation oper = new Operation("call");
        assert(msAddr != -1);
        oper.args.add(new OpMem(msAddr + 2));
        oper.args.add(new OpMem(tosAddr - 2));
        ir.seq.add(oper);
        /*
         * Correct tos address
         */
        tosAddr = msAddr;
        msAddr = -1;
    }

    private int nextReg() {
        regNum  = (regNum + 1) % 256;
        return regNum;
    }

    private int getStackOperSize(Instruction instr) {
        switch (instr.defs) {
        case LDCI:
        case LDI:            
        case LDSI:
        case STI:
        case MAI:
        case ADDI:
        case SUBI:
        case MULI:
        case DIVI:
        case CHSI:
            return 1;
        case LDCD:
        case LDD:            
        case LDSD:
        case STD:
        case MAD:
        case ADDD:
        case SUBD:
        case MULD:
        case DIVD:
        case CHSD:
            return 2;
        default:
            assert false;
        }
        assert false;
        return 0;
    }
}
