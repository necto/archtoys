/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.star.asm;

import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import junit.framework.TestCase;

/**
 *
 * @author danisimo
 */
public class AsmReaderTest extends TestCase {

    public AsmReaderTest(String testName) {
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

    /**
     * Test of readAll method, of class AsmReader.
     */
    public void testReadAll() {
        System.out.println("Testing reading assembler");
        String testString = "ldci 2;\n sti;\n index;\n ldcd 1.0;\n";
        Reader reader = new StringReader(testString);
        AsmReader instance = new AsmReader(reader);
        LinkedList<Instruction> expResult = new LinkedList<Instruction>();
        LinkedList<Instruction> result = instance.readAll();
        /*
         * Create reference list of instructions
         */
        Instruction instr = new Instruction("ldci");
        ((Instruction.OperInteger) instr.oper).value = 2;
        expResult.add(instr);
        instr = new Instruction("sti");
        expResult.add(instr);
        instr = new Instruction("index");
        expResult.add(instr);
        instr = new Instruction("ldcd");
        ((Instruction.OperFloat) instr.oper).value = (float) 1.0;
        expResult.add(instr);
        /*
         * Compare
         */
        assertEquals(expResult, result);
    }
}
