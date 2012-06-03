/*
 * Interpretator class.
 * 
 * Implementation of yapm interpretator.
 */

package ru.mipt.archtoys.yapm.interpretator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author avlechen
 */
public class App {
    /**
     * Main translation process
     */
    private static void Run(File obj, boolean isStep, boolean isShowDump) {
        String userCommand = "";
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);
        
        long cycle = 0;
        FunctionalState state = new FunctionalState();
        MemoryController meu = new MemoryController(isShowDump);
        Interpretator interpretator = new Interpretator(obj, state, meu, isShowDump);
        while (interpretator.Clock()) {
            if (isShowDump) {
                state.Dump(cycle++);
            }
            if (isStep) {
                try {
                    userCommand = in.readLine();
                    isStep = !userCommand.equalsIgnoreCase("run");
                } catch (IOException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void main(String[] args) {
        String fileName;
        boolean isStep = false;
        boolean isShowDump = false;
        /*
         * Get filename
         */
        if (args.length > 0) {
            fileName = args[0];
        } else {
            throw new Error("No input file specified");
        }
        
        /*
         * Parse other options
         */
        for (int i = 1; i < args.length; ++i) {
            if (args[i].contains("step")) {
                isStep = true;
            }
            else if (args[i].contains("dump")) {
                isShowDump = true;
            }
        }

        /*
         * Start translation
         */        
        File file = new File(fileName);
        Run(file, isStep, isShowDump);
    }
}
