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
	private static Parser initParser (String input)
	{
		return  new Parser (
				new Lexer( 
				new PushbackReader (
				new StringReader (input))));
	}
	
	private static Start parse (String input) throws ParserException,
											  LexerException,
											  IOException
	{
		return initParser(input).parse();
	}
	
	private static String compile (Start tree)
	{
		MemTable mem = new MemTable();
		FunTable funcs = new FunTable();
		TypeDeriver td = new TypeDeriver(mem, funcs);
		VarsExtractor vv = new VarsExtractor(mem);
		ru.mipt.archtoys.star.compiler.Compiler c = new ru.mipt.archtoys.star.compiler.Compiler(mem, td, funcs);
		
		tree.apply(vv);
		tree.apply(td);
		tree.apply(c);
		
		return c.asm;
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
//				new StringReader ("var = -sin(-5 + -3)"))));
//				new StringReader ("ivar[r] = a[6-4.4]"))));
//				new StringReader ("ivan = -sin(iabs)\n print ivan, abs(iabs)"))));
//				new StringReader ("a[5,3,8.3/2] = n[15-3]"))));
				new StringReader ("a = 30*i - 4 \n" +
											 "print 54, 18/a[3] mod 3"))));
		
		try {
		Start tree = p.parse();
		MemTable mem = new MemTable();
		FunTable funcs = new FunTable();
		TypeDeriver td = new TypeDeriver(mem, funcs);
		VarsExtractor vv = new VarsExtractor(mem);
		Compiler c = new Compiler(mem, td, funcs);
		
		tree.apply (vv);
		tree.apply (td);
		tree.apply (c);
		System.out.println (mem.table);
		
		System.out.println (compile (parse ( "a = 30*i - 4 \n" +
											 "print 54, 18/b[3] mod 3")));
		
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
