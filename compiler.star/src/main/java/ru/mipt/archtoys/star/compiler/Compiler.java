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
	
	private VarsExtractor vars;
	
	public Compiler (VarsExtractor v)
	{
		vars = v;
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
	
	@Override
	public void outAVarName (AVarName node)
	{
		String name = node.getWord().getText();
		char type = vars.getType(name) == vars.INTEGER ? 'i' : 'd';
		
		pushCommand("lda", vars.getAdress(name));
		pushCommand("ld" + type);
	}

    @Override
    public void caseAAssignment(AAssignment node)
    {
        inAAssignment(node);
		
        if(node.getExpr() != null)
        {
            node.getExpr().apply(this);
        }
		
        if(node.getVarName() != null)
        {
			String name = ((AVarName) node.getVarName()).getWord().getText();
			char type = vars.getType(name) == vars.INTEGER ? 'i' : 'd';
			
			pushCommand("lda", vars.getAdress(name));
			pushCommand("st" + type);
        }
		else throw new RuntimeException("The value must be assigned to" +
										"some variable [" +
										node.getAssign().getLine() + ", " +
										node.getAssign().getPos() + "]\n");
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
		
        if(node.getUnit() != null)
        {
            node.getUnit().apply(this);
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
}
