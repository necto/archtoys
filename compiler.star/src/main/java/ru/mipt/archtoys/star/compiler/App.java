package ru.mipt.archtoys.star.compiler;

import gramm.parser.ParserException;
import java.io.*;
import org.apache.commons.cli.*;

public class App 
{
	private static boolean isNotAnOption (String opt)
	{
		return !opt.matches("-[-a-z]+");
	}
	
	private static void doCompile (Reader input, Writer output)
	{
		try
		{
			Compiler c = new Compiler();
			output.write (c.compile (input));
		} catch (ParserException e)
		{
			System.err.print(e.getToken());
			System.err.println(e.getMessage());
		} catch (Exception e)
		{
			System.err.println(e.getMessage());
		}
	}
	
    public static void main( String[] args )
    {
		try
		{
			Options opts = new Options();
			
			opts.addOption ("i", "input", true, "Specify input file. Facultative.");
			opts.addOption ("o", "output", true, "Specify output file. If it isn't " +
									   "given, i will use stdout.");
			opts.addOption ("h", "help", false, "Print this usage message");
			CommandLineParser clp = new PosixParser();
			CommandLine cl = clp.parse (opts, args);
			
			if (cl.hasOption("h"))
			{
				HelpFormatter hlp = new HelpFormatter();
				hlp.printHelp("starcalc [file] [options] \n"
							+ "Where file is a file to compile, options are:", opts);
				System.out.println ("If no input file given, stdin will be used.");
				return;
			}
			
			Reader input = null;
			if (cl.hasOption("i"))
				input = new FileReader (new File (cl.getOptionValue ("i")));
			else if (args.length > 0 && isNotAnOption (args[0]))
				input = new FileReader (new File (args[0]));
			else
				input = new InputStreamReader (System.in);
			
			OutputStream out = null;
			if (cl.hasOption ("o"))
				out = new FileOutputStream (new File (cl.getOptionValue ("o")));
			else
				out = System.out;
			
			Writer output = new OutputStreamWriter(out);
			doCompile(input, output);
			output.close();
			
		} catch (ParseException ex)
		{
			System.err.println(ex.getMessage());
		} catch (IOException ex)
		{
			System.err.println(ex.getMessage());
		}
    }
}
