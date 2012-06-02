/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.yapm.bt;

import java.util.Iterator;
import java.util.LinkedList;
import ru.mipt.archtoys.yapm.bt.Operation.MacroOperation;

/**
 *
 * @author danisimo
 */
class Scheduler {
    private Ir ir;

    public Scheduler(Ir i_r) {
        ir = i_r;
    }

    public void simpleScheduling() {
        Iterator<Operation> iter = ir.seq.iterator();

        while (iter.hasNext()) {
            MacroOperation macro = new MacroOperation();
            Operation oper = iter.next();
            int unit = oper.getUnit();
            if (unit == 1) {
                unit = 0;
            }
            macro.opers[unit] = oper;
            ir.seqWide.add(macro);
        }
    }
}
