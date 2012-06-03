/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.yapm.interpretator;

/**
 *
 * @author avlechen
 */
public class Operation {
    static public final int NOP = 0;
    static public final int LDC = 1;
    static public final int LD = 2;
    static public final int ST = 3;
    static public final int LDA = 4;
    static public final int STA = 5;
    static public final int CALL = 6;
    static public final int ADD = 7;
    static public final int SUB = 8;
    static public final int MUL = 9;
    static public final int DIV = 10;
    static public final int REM = 11;
    static public final int CHSGN = 12;
    static public final int NOT = 13;
    public int type = 0;
    public int operand1 = -1;
    public int operand2 = -1;
    public int operand3 = -1;
    public int result = -1;
    public int dst = -1;

    Operation(String[] split) {
        if (split[0].equals("NOP")) {
            type = NOP;
        }
        else if (split[0].equals("LDC")) {
            type = LDC;
        }
        else if (split[0].equals("LD")) {
            type = LD;
        }
        else if (split[0].equals("ST")) {
            type = ST;
        }
        else if (split[0].equals("LDA")) {
            type = LDA;
        }
        else if (split[0].equals("STA")) {
            type = STA;
        }
        else if (split[0].equals("CALL")) {
            type = CALL;
        }
        else if (split[0].equals("ADD")) {
            type = ADD;
        }
        else if (split[0].equals("SUB")) {
            type = SUB;
        }
        else if (split[0].equals("MUL")) {
            type = MUL;
        }
        else if (split[0].equals("DIV")) {
            type = DIV;
        }
        else if (split[0].equals("REM")) {
            type = REM;
        }
        else if (split[0].equals("CHSGN")) {
            type = CHSGN;
        }
        else if (split[0].equals("NOT")) {
            type = NOT;
        }
        switch (type) {
        case NOP: break;
        case LDC: 
            operand1 = GetConstant(split[1]);
            operand2 = GetRegNum(split[2]);
            break;
        case LD:
            operand1 = GetAddr(split[1]);
            operand2 = GetRegNum(split[2]);
            break;
        case ST:
            operand1 = GetRegNum(split[1]);
            operand2 = GetAddr(split[2]);
            break;
        case LDA:
        case STA:
            operand1 = GetRegNum(split[1]);
            operand2 = GetRegNum(split[2]);
            operand3 = GetRegNum(split[3]);
            break;
        case CALL:
            operand1 = GetRegNum(split[1]);
            operand2 = GetRegNum(split[2]);
            break;
        case ADD:
        case SUB:
        case MUL:
        case DIV:
        case REM:
            operand1 = GetRegNum(split[1]);
            operand2 = GetRegNum(split[2]);
            operand3 = GetRegNum(split[3]);
            break;
        case CHSGN:
        case NOT:
            operand1 = GetRegNum(split[1]);
            operand1 = GetRegNum(split[2]);
            break;
        default: break;
        }
    }
    
    int GetAddr(String addrString) {
        return Integer.parseInt(addrString);
    }
    
    int GetRegNum(String regString) {
        return Integer.parseInt(regString.replace("r", ""));
    }
    
    int GetConstant(String constString) {
        return Integer.parseInt(constString);
    }

    void Execute(FunctionalState state, MemoryController meu) {
        switch (type) {
        case NOP: break;
        case LDC:
            state.SetRegister(operand2, operand1);
            break;
        case LD:
            state.SetRegister(operand2, meu.ReadMemory(operand1));
            break;
        case ST:
            meu.WriteMemory(state.GetRegister(operand1), operand2);
            break;
        case LDA:
            state.SetRegister(operand3, meu.ReadMemory(operand1 + operand2));
            break;
        case STA:
            meu.WriteMemory(state.GetRegister(operand3), operand1 + operand2);
            break;
        case CALL:
            int argsNum = (operand2 - operand1) / 2;
            byte[] args = new byte[argsNum];
            for (int i = 0; i < args.length; ++i)
                args[i] = (byte) meu.ReadMemory(operand1 + i * 2);
            int index = meu.ReadMemory(operand2);
            CustomFunction.Call(args, index);
            break;
        case ADD:
            state.SetRegister(operand3, state.GetRegister(operand1) + state.GetRegister(operand2));
            break;
        case SUB:
            state.SetRegister(operand3, state.GetRegister(operand1) - state.GetRegister(operand2));
            break;
        case MUL:
            state.SetRegister(operand3, state.GetRegister(operand1) * state.GetRegister(operand2));
            break;
        case DIV:
            state.SetRegister(operand3, state.GetRegister(operand1) / state.GetRegister(operand2));
            break;
        case REM:
            state.SetRegister(operand3, state.GetRegister(operand1) % state.GetRegister(operand2));
            break;
        case CHSGN:
            state.SetRegister(operand2, (-1)*state.GetRegister(operand1));
            break;
        case NOT:
            state.SetRegister(operand2, ~state.GetRegister(operand1));
            break;
        default: break;
        }
    }
}
