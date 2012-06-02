/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.yapm.bt;

import java.util.LinkedList;
import ru.mipt.archtoys.star.asm.Instruction;
import ru.mipt.archtoys.yapm.bt.Operation.MacroOperation;

/**
 *
 * @author danisimo
 */
public class Ir {
    public LinkedList<Instruction> starAsm;
    public LinkedList<Operation> seq;
    public LinkedList<MacroOperation> seqWide;
    
    public Ir(){
        starAsm = new LinkedList<Instruction>();
        seq = new LinkedList<Operation>();
        seqWide = new LinkedList<MacroOperation>();
    }
}
