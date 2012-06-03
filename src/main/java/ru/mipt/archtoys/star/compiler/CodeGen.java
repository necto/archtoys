/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.star.compiler;

import gramm.analysis.DepthFirstAdapter;
import gramm.node.*;

/**
 *
 * @author necto
 */
public class CodeGen extends DepthFirstAdapter
{
	private String asm = "";
	
	private MemTable vars;
	private TypeDeriver types;
	private FunTable funcs;
	
	public CodeGen (MemTable v, TypeDeriver types, FunTable funcs)
	{
		this.vars = v;
		this.types = types;
		this.funcs = funcs;
	}
	
	private void pushCommand (String cmd)
	{
		asm += cmd + ";\n";
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
	
//	@Override
//	public void defaultIn(Node node) //for debug purposes
//    {
//        System.out.println (node.getClass().toString() + " " + node.toString());
//    }
	
	@Override
	public void defaultOut (Node node)
    {
        adoptType(node);
    }
	
	@Override
    public void inStart(Start node)
    {
        defaultIn(node);
		asm = "";
		pushCommand ("alloc", vars.getMemSize());
		for (MemTable.Array arr : vars.getArrays())
		{
			pushCommand("ldci", arr.length());
			pushCommand("lda", arr.adress);
			pushCommand("ma" + arr.type.sign());
		}
		
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
    public void caseASubExpr(ASubExpr node)
    {
        inASubExpr(node);
        if(node.getFactor() != null)
        {
            node.getFactor().apply(this);
        }
        if(node.getExpr() != null)
        {
            node.getExpr().apply(this);
        }
	pushCommand ("sub" + types.getType (node).sign());
        outASubExpr(node);
    }
	
    @Override
    public void caseASumExpr(ASumExpr node)
    {
        inASumExpr(node);
        if(node.getExpr() != null)
        {
            node.getExpr().apply(this);
        }
        if(node.getPlus() != null)
        {
            node.getPlus().apply(this);
        }
        if(node.getFactor() != null)
        {
            node.getFactor().apply(this);
        }
	pushCommand ("add" + types.getType (node).sign());
        outASumExpr(node);
    }
	
    @Override
    public void caseAMulFactor(AMulFactor node)
    {
        inAMulFactor(node);
        if(node.getUnit() != null)
        {
            node.getUnit().apply(this);
        }
        if(node.getFactor() != null)
        {
            node.getFactor().apply(this);
        }
	pushCommand ("mul" + types.getType (node).sign());
        outAMulFactor(node);
    }
	
	    @Override
    public void caseADivFactor(ADivFactor node)
    {
        inADivFactor(node);
        if(node.getUnit() != null)
        {
            node.getUnit().apply(this);
        }
        if(node.getFactor() != null)
        {
            node.getFactor().apply(this);
        }
	pushCommand ("div" + types.getType (node).sign());
        outADivFactor(node);
    }
	
		

    @Override
    public void caseAModFactor(AModFactor node)
    {
        inAModFactor(node);
        if(node.getUnit() != null)
        {
            node.getUnit().apply(this);
        }
        if(node.getFactor() != null)
        {
            node.getFactor().apply(this);
        }
	pushCommand ("rem");
        outAModFactor(node);
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
		assert (vars.isArray(name));
		
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
	
	public String getASM()
	{
		return asm;
	}

	public void reset()
	{
		asm="";
	}
}
