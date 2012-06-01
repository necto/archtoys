/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.star.asm;

import junit.framework.TestCase;

/**
 *
 * @author danisimo
 */
public class InstructionTest extends TestCase {
    
    public InstructionTest(String testName) {
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
     * Test of print method, of class Instruction.
     */
    public void testPrint() {
        System.out.println("Testing instruction print");
        Instruction instance = new Instruction("ldci");
        ((Instruction.OperInteger)instance.oper).value = 10;
        assertEquals("Integer instruction", "LDCI 10;", instance.toString());
        instance = new Instruction("lda");
        ((Instruction.OperAddr)instance.oper).value = 16000;
        assertEquals("Address instruction", "LDA 16000;", instance.toString());
        instance = new Instruction("std");
        assertEquals("Float instruction", "STD;", instance.toString());
        instance = new Instruction("index");
        assertEquals("Simple instruction", "INDEX;", instance.toString());    
    }
}
