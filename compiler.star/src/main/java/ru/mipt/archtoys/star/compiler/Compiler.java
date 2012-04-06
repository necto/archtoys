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
	public void outAName (AName node)
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
		
        if(node.getName() != null)
        {
			String name = ((AName) node.getName()).getWord().getText();
			char type = vars.getType(name) == vars.INTEGER ? 'i' : 'd';
			
			pushCommand("lda", vars.getAdress(name));
			pushCommand("st" + type);
        }
        outAAssignment(node);
    }
}
