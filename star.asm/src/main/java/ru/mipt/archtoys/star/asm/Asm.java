package ru.mipt.archtoys.star.asm;

import java.io.EOFException;

/**
 * Hello world!
 *
 */
public class Asm 
{
    private static Reader reader;
    
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        reader = new Reader("input.star");
        Instruction instr;
       
        try {
            while(true)
            {
                instr = reader.readNext();
                instr.print();
            }
        } catch (EOFException ex) {
        }
    }
}
