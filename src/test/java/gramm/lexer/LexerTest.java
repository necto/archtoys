
package gramm.lexer;

import gramm.node.TBlank;
import gramm.node.TFnumber;
import gramm.node.TNumber;
import gramm.node.TWord;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import junit.framework.TestCase;

/**
 *
 * @author necto
 */
public class LexerTest extends TestCase
{
	
	public LexerTest(String testName) {
		super(testName);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public Lexer initLexer (String input)
	{
		Lexer lex = new Lexer (new PushbackReader (new StringReader (input)));
		return lex;
	}
	
	public void testNum() throws LexerException, IOException
	{
		Lexer lex = initLexer("1234");
		assertEquals ("Integer", TNumber.class, lex.next().getClass());
		lex = initLexer("65.2");
		assertEquals ("Floating point", TFnumber.class, lex.next().getClass());
	}
	
	public void testWord() throws LexerException, IOException
	{
		Lexer lex = initLexer("brut i brut");
		assertEquals ("Name", TWord.class, lex.next().getClass());
		assertEquals ("delim", TBlank.class, lex.next().getClass());
		assertEquals ("i", TWord.class, lex.next().getClass());
		assertEquals ("delim", TBlank.class, lex.next().getClass());
		assertEquals ("brut", TWord.class, lex.next().getClass());
	}
	
	public void testTri() throws LexerException, IOException
	{
		Lexer lex = initLexer("");
		System.out.println (lex.next());
	}
}
