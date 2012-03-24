package ru.mipt.archtoys.star.compiler;

import java.io.PushbackReader;
import java.io.StringReader;
import postfix.lexer.Lexer;
import postfix.node.Start;
import postfix.parser.Parser;
import postfix.parser.ParserException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
		Parser p = new Parser (
				new Lexer ( 
				new PushbackReader (
				new StringReader(//"var = etty - 56* (6 / 5 - 3.3) + a4a + gi \n"))));// +
								 "print 1"))));
		try {//Why doesn't this ^^^^ letter is not "first_letter" token
		Start tree = p.parse();
		} catch (ParserException e)
		{
			System.out.print(e.getToken());
			System.out.println(e.getMessage());
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
        System.out.println( "Hello World!" );
    }
}
