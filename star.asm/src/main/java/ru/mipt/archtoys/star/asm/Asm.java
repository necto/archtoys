package ru.mipt.archtoys.star.asm;
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
        System.out.print(reader.readNext().defs);
    }
}
