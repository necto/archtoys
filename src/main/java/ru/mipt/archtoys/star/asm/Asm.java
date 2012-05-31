package ru.mipt.archtoys.star.asm;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Hello world!
 *
 */
public class Asm 
{
    private static AsmReader reader;
    private static String fileName;
    
    public static void main( String[] args )
    {
        if ( args.length > 0)
        {
            fileName = args[0];
        } else {
            throw new Error("No input file specified");
        }
        
        File file = new File(fileName);
               
        reader = new AsmReader( file);
        LinkedList<Instruction> list = reader.readAll();
        Iterator<Instruction> iter = list.iterator();
        while ( iter.hasNext()){
            System.out.println( iter.next().toString());
        }
    }
}
