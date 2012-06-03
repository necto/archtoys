package ru.mipt.archtoys.star.asm;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.commons.cli.*;

/**
 * Hello world!
 *
 */
public class Asm {

    private static AsmReader reader;
    private static String fileName;

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
                System.out.println("Assember for StAr asm code.\n"
                        + "Used to test Asm functionality.");
                hlp.printHelp("asm [-i input_file] [-o output_file] \n"
                        + "Where  options are:", opts);
                System.out.println("If no input file given, stdin will be used.");
                System.out.println("If no output file given, stdout will be used.");
                return;
            }

            Reader input = null;
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

            Writer output = new OutputStreamWriter(out);
            
            /* Process input asm */
            reader = new AsmReader(new FileReader(fileName));
            LinkedList<Instruction> list = reader.readAll();
            
            /* Output(Debug) */
            Iterator<Instruction> iter = list.iterator();
            while (iter.hasNext()) {
                output.write(iter.next().toString() + "\n");
            }

        } catch (ParseException ex) {
            System.err.println(ex.getMessage());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
