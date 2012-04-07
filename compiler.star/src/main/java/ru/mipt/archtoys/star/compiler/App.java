package ru.mipt.archtoys.star.compiler;

import gramm.lexer.Lexer;
import gramm.node.Start;
import gramm.node.Token;
import gramm.node.TNumber;
import gramm.parser.Parser;
import gramm.parser.ParserException;
import java.io.PushbackReader;
import java.io.StringReader;

public class App 
{
	public static Lexer initLexer (String input)
	{
		Lexer lex = new Lexer (new PushbackReader (new StringReader (input)));
		return lex;
	}
	
    public static void main( String[] args )
    {
		Parser p = new Parser (
				new Lexer( 
				new PushbackReader (
//				new StringReader("var = etty - 56* (6 / 5 - 3.3) + a4a + gi \n" +
//								 "print var - 1\n" +
//								 "itar = i - k + nana"))));
//				new StringReader ("var = 3 + frac - 8*(k mod 3 - 5/6)\n print var"))));
				new StringReader ("var = -sin(-5)"))));
		
		
		try {
		Start tree = p.parse();
		VarsExtractor vv = new VarsExtractor();
		Compiler c = new Compiler(vv);
		
		tree.apply(vv);
		tree.apply(c);
		System.out.println (vv.memoryTable);
		System.out.println (c.asm);
		
//		Lexer lex = initLexer("1234");
//		Token tok = lex.next();
//		System.out.println (tok);
//		System.out.println (tok.getClass());
//		System.out.println (tok.getClass().equals(TNumber.class));
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
