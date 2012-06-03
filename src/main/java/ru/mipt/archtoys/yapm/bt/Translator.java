/*
 * Translator class.
 * 
 * Implementation of binary translation system from StAr to yapm.
 */
package ru.mipt.archtoys.yapm.bt;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.*;
import ru.mipt.archtoys.star.asm.AsmReader;
import ru.mipt.archtoys.star.asm.Instruction;
import ru.mipt.archtoys.yapm.bt.Operation.MacroOperation;

/**
 *
 * @author danisimo
 */
public class Translator {

    private static Ir ir = new Ir();
    private static Reader input;
    private static Writer output;

    /**
     * Main translation process
     */
    private static void runTranslation() {
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
        try {
            Options opts = new Options();

            opts.addOption("i", "input", true, "Specify input file.");
            opts.addOption("o", "output", true, "Specify output file.");
            opts.addOption("h", "help", false, "Print this usage message");
            CommandLineParser clp = new PosixParser();
            CommandLine cl = clp.parse(opts, args);

            if (cl.hasOption("h")) {
                HelpFormatter hlp = new HelpFormatter();
                System.out.println("Binary translator from StAr asm code "
                        + "to yapm asm code.");
                hlp.printHelp("translator [-i input_file] [-o output_file] \n"
                        + "Where  options are:", opts);
                System.out.println("If no input file given, stdin will be used.");
                System.out.println("If no output file given, stdout will be used.");
                return;
            }

            input = null;
            if (cl.hasOption("i")) {
                input = new FileReader(new File(cl.getOptionValue("i")));
            } else {
                input = new InputStreamReader(System.in);
            }

            OutputStream out = null;
            if (cl.hasOption("o")) {
                out = new FileOutputStream(new File(cl.getOptionValue("o")));
            } else {
                out = System.out;
            }

            output = new OutputStreamWriter(out);

            /*
             * Input
             */
            inpAsm();

            /*
             * Run translation
             */
            runTranslation();

            /*
             * Output
             */
            outResult();

        } catch (ParseException ex) {
            System.err.println(ex.getMessage());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private static void outResult() {
        /*
         * Ouput wide IR to stdout
         */
        Iterator<MacroOperation> iter = ir.seqWide.iterator();
        try {
            while (iter.hasNext()) {
                output.write(iter.next().toString() + "\n");
            }
            output.close();
        } catch (IOException ex) {
            System.err.println("Exception while output: " + ex.getMessage());
        }
    }

    private static void inpAsm() {
        /*
         * Process input asm file
         */
        ir.starAsm = new LinkedList<Instruction>();
        AsmReader asmReader = new AsmReader(input);
        ir.starAsm = asmReader.readAll();
    }
}
