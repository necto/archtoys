/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.star.compiler;

import gramm.lexer.Lexer;
import gramm.lexer.LexerException;
import gramm.node.Start;
import gramm.parser.Parser;
import gramm.parser.ParserException;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;

/**
 *
 * @author necto
 */
public class Compiler
{
	MemTable mem;
	FunTable funcs;
	TypeDeriver td;
	VarsExtractor vv;
	CodeGen cg;

	public Compiler()
	{
		mem = new MemTable();
		funcs = new FunTable();
		td = new TypeDeriver(mem, funcs);
		vv = new VarsExtractor(mem);
		cg = new CodeGen(mem, td, funcs);
	}
	
	public void reset()
	{
		mem.reset();
		td.reset();
		cg.reset();
	}
	
	private static Parser initParser (Reader input)
	{
		return  new Parser (
				new Lexer( 
				new PushbackReader (input)));
	}
	
	public static Start parse (Reader input) throws ParserException,
											  LexerException,
											  IOException
	{
		return initParser(input).parse();
	}
	
	public String compileAST (Start tree)
	{
		reset();
		tree.apply(vv);
		tree.apply(td);
		tree.apply(cg);
		
		return cg.getASM();
	}
	
	public String compile (Reader input) throws ParserException,
												LexerException,
												IOException
	{
		return compileAST (parse (input));
	}
	
	public String compile (String input) throws ParserException,
												LexerException,
												IOException
	{
		return compileAST (parse (new StringReader(input)));
	}
}
