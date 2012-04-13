package ru.mipt.archtoys.star.compiler;

import gramm.lexer.Lexer;
import gramm.lexer.LexerException;
import gramm.node.Node;
import gramm.node.Start;
import gramm.node.Token;
import gramm.node.TNumber;
import gramm.parser.Parser;
import gramm.parser.ParserException;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.Map;

public class App 
{	
    public static void main( String[] args )
    {
		
		try {
		
		Compiler c = new Compiler();
		
		System.out.println (c.compile ( "a = 30*i - 4 \n" +
											 "print 54, 18/b[3] mod 3"));
		
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
    }
}
