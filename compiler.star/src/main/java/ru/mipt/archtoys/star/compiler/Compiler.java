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
public class Compiler extends DepthFirstAdapter
{
	public String asm = "";
	
	private MemTable vars;
	private TypeDeriver types;
	
	public Compiler (MemTable v, TypeDeriver types)
	{
		this.vars = v;
		this.types = types;
	}
	
	private void pushCommand (String cmd)
	{
		asm += cmd + "\n";
	}
	
	private void pushCommand (String cmd, Object argument)
	{
		pushCommand(cmd + " " + argument);
	}
	
	private void pushCommand (String cmd, Object arg1, Object arg2)
	{
		pushCommand(cmd + " " + arg1 + " " + arg2);
	}
	
	@Override
	public void defaultIn(Node node)
    {
        System.out.println (node.getClass().toString() + " " + node.toString());
    }
	
	@Override
    public void inStart(Start node)
    {
        defaultIn(node);
		pushCommand("alloc", vars.getMemSize());
    }

	@Override
    public void outAConstVal(AConstVal node)
    {
		pushCommand("ldci", node.getNumber().getText());
    }
	
	@Override
    public void outAFconstVal(AFconstVal node)
    {
		pushCommand("ldcd", node.getFnumber().getText());
    }
	
	@Override
    public void outASubExpr (ASubExpr node)
    {
		pushCommand("sub");
    }
	
	@Override
    public void outASumExpr (ASumExpr node)
    {
		pushCommand("add");
    }
	
	@Override
    public void outAMulFactor (AMulFactor node)
    {
		pushCommand("mul");
    }
	
	@Override
    public void outADivFactor (ADivFactor node)
    {
		pushCommand("div");
    }
	
	@Override
    public void outAModFactor (AModFactor node)
    {
		pushCommand("rem");
    }
	
	@Override
    public void outAInvertedUnit(AInvertedUnit node)
    {
        pushCommand("chs");
    }
	
	private char getCharType (Node n)
	{
		return types.getType(n).sign();
	}
	
	@Override
	public void outAVarName (AVarName node)
	{
		String name = node.getWord().getText();
		pushCommand("lda", vars.getAdress(name));
	}
	
	@Override
	public void outAVal (AVal node)
	{
		pushCommand("lds" + getCharType(node));
	}

    @Override
    public void caseAAssignment(AAssignment node)
    {
        inAAssignment(node);
		
        if(node.getExpr() != null)
        {
            node.getExpr().apply(this);
        }
		
        if(node.getVariable() != null)
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
		
		pushCommand("ldci", "\'print procedure adress\'");
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
			pushCommand("ldci", "\'adress of " + name + "\'");
        }
		else throw new RuntimeException("Here expected some function name [" +
										node.getLBr().getLine() + ", " +
										node.getLBr().getPos() + "]\n");
		pushCommand("call");
        outACallUnit(node);
    }
	
    @Override
    public void caseAIndexVariable(AIndexVariable node)
    {
        inAIndexVariable(node);
        if(node.getArrName() != null)
        {
            node.getArrName().apply(this);
        }
        if(node.getLsqBr() != null)
        {
            node.getLsqBr().apply(this);
        }
        if(node.getExpr() != null)
        {
            node.getExpr().apply(this);
        }
        if(node.getRsqBr() != null)
        {
            node.getRsqBr().apply(this);
        }
		String name = ((AArrName) (node.getArrName())).getWord().getText();
		assert (vars.isArray(name));
		pushCommand("lda", vars.getAdress(name));
		pushCommand("index");
        outAIndexVariable(node);
    }
	
//	
//	@Override
//	public void outAIndexVariable(AIndexVariable node)
//    {
//        inAIndexVariable(node);
//        if(node.getArrName() != null)
//        {
//            node.getArrName().apply(this);
//        }
//        if(node.getLsqBr() != null)
//        {
//            node.getLsqBr().apply(this);
//        }
//        if(node.getExpr() != null)
//        {
//            node.getExpr().apply(this);
//        }
//        if(node.getRsqBr() != null)
//        {
//            node.getRsqBr().apply(this);
//        }
//		
////		String name = ((AArrName) (node.getArrName())).getWord().getText();
////		assert (vars.isArray(name));
////		pushCommand("lda", vars.getAdress(name));
////		pushCommand("index");
////		pushCommand("lds" + getCharType(name));
//		
//        outAIndexVariable(node);
//    }
}
