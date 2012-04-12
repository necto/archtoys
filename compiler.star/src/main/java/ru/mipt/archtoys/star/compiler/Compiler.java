/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.star.compiler;

import gramm.analysis.DepthFirstAdapter;
import gramm.node.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author necto
 */
public class Compiler extends DepthFirstAdapter
{
	public String asm = "";
	
	private MemTable vars;
	private TypeDeriver types;
	private FunTable funcs;
	
	public Compiler (MemTable v, TypeDeriver types, FunTable funcs)
	{
		this.vars = v;
		this.types = types;
		this.funcs = funcs;
	}
	
	private void pushCommand (String cmd)
	{
		asm += cmd + "\n";
	}
	
	private void pushCommand (String cmd, Object argument)
	{
		pushCommand (cmd + " " + argument);
	}
	
	private void adoptType (Node node)
	{
		Type type = types.getType (node);
		Type expected = types.getExpectedType (node);
		
		if (type == null || expected == null)
			return;
		
		switch (type)
		{
			case INTEGER:
				switch (expected)
				{
					case INTEGER: break;
					case FLOAT:
						pushCommand("fi2d");
						break;
				}
				break;
			case FLOAT:
				switch (expected)
				{
					case INTEGER:
						pushCommand("fd2i");
						break;
					case FLOAT: break;
				}
				break;
		}
	}
	
	private char getCharType (Node n)
	{
		return types.getType(n).sign();
	}
	
	@Override
	public void defaultIn(Node node)
    {
//        System.out.println (node.getClass().toString() + " " + node.toString());
    }
	
	@Override
	public void defaultOut (Node node)
    {
        adoptType(node);
    }
	
	@Override
    public void inStart(Start node)
    {
        defaultIn(node);
		pushCommand ("alloc", vars.getMemSize());
        defaultOut(node);
    }

	@Override
    public void outAConstVal(AConstVal node)
    {
		pushCommand ("ldci", node.getNumber().getText());
        defaultOut(node);
    }
	
	@Override
    public void outAFconstVal(AFconstVal node)
    {
		pushCommand ("ldcd", node.getFnumber().getText());
        defaultOut(node);
    }
	
	@Override
    public void outASubExpr (ASubExpr node)
    {
		pushCommand ("sub" + types.getType (node).sign());
        defaultOut(node);
    }
	
	@Override
    public void outASumExpr (ASumExpr node)
    {
		pushCommand ("add" + types.getType (node).sign());
        defaultOut(node);
    }
	
	@Override
    public void outAMulFactor (AMulFactor node)
    {
		pushCommand ("mul" + types.getType (node).sign());
        defaultOut(node);
    }
	
	@Override
    public void outADivFactor (ADivFactor node)
    {
		pushCommand ("div" + types.getType (node).sign());
        defaultOut(node);
    }
	
	@Override
    public void outAModFactor (AModFactor node)
    {
		pushCommand ("rem");
        defaultOut(node);
    }
	
	@Override
    public void outAInvertedUnit(AInvertedUnit node)
    {
        pushCommand ("chs" + types.getType (node).sign());
        defaultOut(node);
    }
	
	@Override
	public void outAVarName (AVarName node)
	{
		String name = node.getWord().getText();
		pushCommand ("lda", vars.getAdress(name));
        defaultOut(node);
	}
	
	@Override
	public void outAVal (AVal node)
	{
		pushCommand ("lds" + getCharType(node));
        defaultOut(node);
	}

    @Override
    public void caseAAssignment(AAssignment node)
    {
        inAAssignment(node);
		
        if (node.getExpr() != null)
        {
            node.getExpr().apply(this);
        }
		
        if (node.getVariable() != null)
        {
            node.getVariable().apply(this);
        }
		
		pushCommand("st" + getCharType(node));
        outAAssignment(node);
    }
	
    @Override
    public void caseAPrinter(APrinter node)
    {
        inAPrinter(node);
		pushCommand("ms");

        if(node.getExprList() != null)
        {
            node.getExprList().apply(this);
        }
		
		pushCommand("ldci", funcs.get("print").code);
		pushCommand("call");
        outAPrinter(node);
    }
	
    @Override
    public void caseACallUnit(ACallUnit node)
    {
        inACallUnit(node);
		pushCommand("ms");

        if(node.getExprList() != null)
        {
            node.getExprList().apply(this);
        }
		
        if(node.getFunName() != null)
        {
			String name = ((AFunName) node.getFunName()).getWord().getText();
			pushCommand("ldci", funcs.get(name).code);
        }
		else throw new RuntimeException("Here expected some function name [" +
										node.getLBr().getLine() + ", " +
										node.getLBr().getPos() + "]\n");
		pushCommand("call");
        outACallUnit(node);
    }
	
	private int countExprs (PExprList list)
	{
		if (list instanceof ASeveralExprList)
			return countExprs(((ASeveralExprList)list).getExprList()) + 1;
		assert (list instanceof AExprList);
		return 1;
	}
	
    @Override
    public void caseAIndexVariable(AIndexVariable node)
    {
        inAIndexVariable(node);
        if (node.getArrName() != null)
        {
            node.getArrName().apply(this);
        }
        if (node.getExprList() != null)
        {
            node.getExprList().apply(this);
        }
		String name = ((AArrName) (node.getArrName())).getWord().getText();
		assert (vars.getArrayd(name) > 0);
		
		int nIndexes = countExprs (node.getExprList());
		while (0<--nIndexes)
		{
			pushCommand ("ldci", 100);
			pushCommand ("muli");
			pushCommand ("addi");
		}
		
		pushCommand ("lda", vars.getAdress (name));
		pushCommand ("index");
        outAIndexVariable (node);
    }
}
