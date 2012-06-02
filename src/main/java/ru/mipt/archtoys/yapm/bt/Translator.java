/*
 * Translator class.
 * 
 * Implementation of binary translation system from StAr to yapm.
 */
package ru.mipt.archtoys.yapm.bt;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import ru.mipt.archtoys.star.asm.AsmReader;
import ru.mipt.archtoys.star.asm.Instruction;
import ru.mipt.archtoys.yapm.bt.Operation.MacroOperation;

/**
 *
 * @author danisimo
 */
public class Translator {

    private static String fileName;
    private static Ir ir = new Ir();

    /**
     * Main translation process
     */
    private static void runTranslation() {
        /*
         * Process input asm file
         */
        ir.starAsm = new LinkedList<Instruction>();
        File file = new File(fileName);
        AsmReader asmReader = new AsmReader(file);
        ir.starAsm = asmReader.readAll();

        /**
         * Simple translation from star to yasm. Construct eqivalents of all
         * star operation with realization of stack on memory
         */
        ir.seq = new LinkedList<Operation>();
        Lowering lowir = new Lowering(ir);
        lowir.runLowering();
        
        /**
         * Form macrooperations
         */
        Scheduler sched = new Scheduler(ir);
        sched.simpleScheduling();
    }

    public static void main(String[] args) {
        /*
         * Get filename
         */
        if (args.length > 0) {
            fileName = args[0];
        } else {
            throw new Error("No input file specified");
        }

        /*
         * Start translation
         */
        runTranslation();

        /*
         * Output
         */
        outResult();
    }

    private static void outResult() {
        /*
         * Ouput wide IR to stdout
         */
        Iterator<MacroOperation> iter = ir.seqWide.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next().toString());
        }
    }
}
