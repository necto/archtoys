/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.yapm.interpretator;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author avlechen
 */
public class Interpretator {
    private FunctionalState state;
    private MemoryController meu;
    BufferedReader src = null;
    boolean retValue;
    boolean isShowDump;
    
    Interpretator(File aSrc, FunctionalState aState, MemoryController aMeu, boolean dump) {
        try {
            FileInputStream fin = new FileInputStream(aSrc);
            DataInputStream in = new DataInputStream(fin);
            src = new BufferedReader(new InputStreamReader(in));
        } catch (Exception ex) {
            Logger.getLogger(Interpretator.class.getName()).log(Level.SEVERE, null, ex);
        }
        state = aState;
        meu = aMeu;
        isShowDump = dump;
    }

    boolean Clock() {
        retValue = false;
        String macroCommand = Fetch();
        Operation[] ops = Decode(macroCommand);
        Execute(ops);
        return retValue;
    }

    private String Fetch() {
        String command = "";
        try {
            command = src.readLine();
        } catch (IOException ex) {
            Logger.getLogger(Interpretator.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (isShowDump) { System.out.println(command);}
        retValue = command != null;
        return command;
    }
    
    private Operation[] Decode(String macroCommand) {
        if (macroCommand == null)
            return null;

        retValue = true;
        String[] command = macroCommand.split(";\\s*");
        Operation[] ops = new Operation[command.length];
        for (int i = 0; i < command.length; ++i) {
            String[] split = command[i].replace(",", "").split("\\s+");
            ops[i] = new Operation(split);
        }
        return ops;
    }
    
    private void Execute(Operation[] ops) {
        if (ops == null)
            return;

        retValue = true;
        for (int i = 0; i < ops.length; ++i)
            ops[i].Execute(state, meu);
    }
}
