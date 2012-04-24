/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import junit.framework.TestCase;
import ru.mipt.archtoys.star.compiler.Compiler;

/**
 *
 * @author necto
 */
public class CompilerTest extends TestCase {
	
	public CompilerTest(String testName) {
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
	
	private static String canonize (String str)
	{
		return str.replaceAll("[ \n\t]", "").toLowerCase();
	}
	
    public void testSimple() throws Exception
    {
		Compiler c = new Compiler();
		assertEquals ("Basic print",
					  canonize(c.compile("print 2")),
					  canonize("alloc 0; ms; ldci 2; ldci 1; call;"));
		assertEquals ("Basic assignment",
					  canonize(c.compile("i = 2")),
					  canonize("alloc 1; ldci 2; lda 0; sti;"));
    }
	
    public void testArithmetic() throws Exception
    {
		Compiler c = new Compiler();
		assertEquals ("Summation ",
					  canonize(c.compile("print 2 + 2 + 3 - 5")),
					  canonize("alloc 0; ms; ldci 2; ldci 2; addi; ldci 3;" + 
							   "addi; ldci 5; subi; ldci 1; call;"));
		assertEquals ("Multiplication",
					  canonize(c.compile("i = 15*64/3")),
					  canonize("alloc 1; ldci 15; ldci 64; muli; ldci 3;" +
							   "divi; lda 0; sti;"));
		assertEquals ("Precedency",
					  canonize(c.compile("i = 3*(5 + 8)/2 + 3 * 4")),
					  canonize("alloc 1; ldci 3; ldci 5; ldci 8; addi; muli; ldci 2;" +
							   "divi; ldci 3; ldci 4; muli; addi; lda 0; sti;"));
    }
	
    public void testTypes() throws Exception
    {
		Compiler c = new Compiler();
		assertEquals ("Variables convention",
					  canonize(c.compile("print i, j, k, a, b, c")),
					  canonize("alloc 9; ms; lda 0; ldsi; lda 1; ldsi; lda 2;" +
							   "ldsi; lda 3; ldsd; lda 5; ldsd; lda 7; ldsd;" +
							   "ldci 1; call;"));
		assertEquals ("Auto convertion",
					  canonize(c.compile("i = 2.3 + (5.5 mod 18)")),
					  canonize("alloc 1; ldcd 2.3; ldcd 5.5; fd2i; ldci 18; rem;" +
							   "fi2d; addd; fd2i; lda 0; sti;"));
    }
	
    public void testArrays() throws Exception
    {
		Compiler c = new Compiler();
		assertEquals ("Rvalue reference",
					  canonize(c.compile("print f[34] + i[3]")),
					  canonize("alloc 300; ms; ldci 34; lda 0; index; ldsd;" +
							   "ldci 3; lda 200; index; ldsi; fi2d; addd; ldci 1;" +
							   "call;"));
		assertEquals ("Lvalue reference",
					  canonize(c.compile("i[2] = 15/4")),
					  canonize("alloc 100; ldci 15; ldci 4; divi; ldci 2; lda 0;" +
							   "index; sti;"));
		assertEquals ("Multidimensional",
					  canonize(c.compile("print al[32, 5-2] *" + 
										 "igro[43/2, 11/3, 54+6]")),
					  canonize("alloc 1020000; ms; ldci 32; ldci 5; ldci 2; subi;" +
							   "ldci 100; muli; addi; lda 0; index; ldsd; ldci 43;" + 
							   "ldci 2; divi; ldci 11; ldci 3; divi; ldci 54; ldci 6;" +
							   "addi; ldci 100; muli; addi; ldci 100; muli; addi;" +
							   "lda 20000; index; ldsi; fi2d; muld; ldci 1; call;"));
    }
//	@Test(expected=MemTable.IncompatibleUsage.class)
//	public void testDifDefDetector() throws Exception
//	{
//		compile(parse("a[2] = a"));
//	}
	
    public void testFunctions() throws Exception
	{
		
	}
	
	public void testComplex() throws Exception
	{
		Compiler c = new Compiler();
		assertEquals ("2 operations",
					  canonize(c.compile("a = 30*i - 4 \n" +
										 "print 54, 18/b[3] mod 3")),
					  canonize("alloc 203; ldci 30; lda 2; ldsi; muli; ldci 4;" + 
							   "subi; fi2d; lda 0; std; ms; ldci 54; ldci 18; " +
							   "fi2d; ldci 3; lda 3; index; ldsd; divd; fd2i; " +
							   "ldci 3; rem; ldci 1; call;"));
	}
}
