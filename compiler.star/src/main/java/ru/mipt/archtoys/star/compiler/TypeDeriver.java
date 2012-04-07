/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.star.compiler;

import gramm.analysis.DepthFirstAdapter;
import gramm.node.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author necto
 */
public class TypeDeriver extends DepthFirstAdapter
{
	private Map<Node, MemTable.Type> types = new HashMap<Node, MemTable.Type>();
	private MemTable vars;

	public TypeDeriver(MemTable vars)
	{
		this.vars = vars;
	}
	
	public MemTable.Type getType (Node n)
	{
		return types.get(n);
	}
	
	private MemTable.Type derive(Node ... children)
	{
		MemTable.Type t = MemTable.Type.INTEGER;
		for (Node n : children)
			if (n != null)
			{
				MemTable.Type childType = types.get(n);
				if (childType == MemTable.Type.FLOAT)
				{
					t = MemTable.Type.FLOAT;// float dominates integer always
					break;
				}
			}
		return t;
	}

	@Override
    public void outStart(Start node)
    {
    }
	
	@Override
    public void outAProgram(AProgram node)
    {
		types.put(node, derive(node.getStatement()));
    }

	@Override
    public void outAPrognProgram(APrognProgram node)
    {
		types.put(node, derive(node.getProgram())); //Type of the whole programm
    }												// is the type of the last statement

	@Override
    public void outAAssignmentStatement(AAssignmentStatement node)
    {
		types.put(node, derive(node.getAssignment()));
    }

	@Override
    public void outAPrinterStatement(APrinterStatement node)
    {
		types.put(node, derive(node.getPrinter()));
    }

	@Override
    public void outAAssignment(AAssignment node)
    {
		types.put(node, derive(node.getVariable()));
    }

	@Override
    public void outAPrinter(APrinter node)
    {
		types.put(node, derive(node.getExprList()));
    }

	@Override
    public void outAExprList(AExprList node)
    {
		types.put(node, derive(node.getExpr()));
    }

	@Override
    public void outASeveralExprList(ASeveralExprList node)
    {
		types.put(node, derive(node.getExprList())); //type of the last expr
    }

	@Override
    public void outAExpr(AExpr node)
    {
		types.put(node, derive(node.getFactor()));
    }

	@Override
    public void outASumExpr(ASumExpr node)
    {
		types.put(node, derive(node.getExpr(), node.getFactor()));
    }

	@Override
    public void outASubExpr(ASubExpr node)
    {
		types.put(node, derive(node.getExpr(), node.getFactor()));
    }

	@Override
    public void outAFactor(AFactor node)
    {
		types.put(node, derive(node.getUnit()));
    }

	@Override
    public void outAMulFactor(AMulFactor node)
    {
		types.put(node, derive(node.getFactor(), node.getUnit()));
    }

	@Override
    public void outADivFactor(ADivFactor node)
    {
		types.put(node, derive(node.getFactor(), node.getUnit()));
    }

	@Override
    public void outAModFactor(AModFactor node)
    {
		types.put(node, MemTable.Type.INTEGER);
    }

	@Override
    public void outAUnit(AUnit node)
    {
		types.put(node, derive(node.getOperand()));
    }

	@Override
    public void outAInvertedUnit(AInvertedUnit node)
    {
		types.put(node, derive(node.getUnit()));
    }

	@Override
    public void outACallUnit(ACallUnit node)
    {
		types.put(node, derive(node.getFunName()));
    }

	@Override
    public void outAOperand(AOperand node)
    {
		types.put(node, derive(node.getVal()));
    }

	@Override
    public void outANestedOperand(ANestedOperand node)
    {
		types.put(node, derive(node.getExpr()));
    }

	@Override
    public void outAVal(AVal node)
    {
        types.put(node, derive(node.getVariable()));
    }

	@Override
    public void outAConstVal(AConstVal node)
    {
        types.put(node, MemTable.Type.INTEGER);
    }

	@Override
    public void outAFconstVal(AFconstVal node)
    {
        types.put(node, MemTable.Type.FLOAT);
    }

	@Override
    public void outAVariable(AVariable node)
    {
		types.put(node, derive(node.getVarName()));
    }

	@Override
    public void outAIndexVariable(AIndexVariable node)
    {
		types.put(node, derive(node.getArrName()));
    }

	@Override
    public void outAVarName(AVarName node)
    {
        types.put(node, vars.getType(node.getWord().getText()));
    }

	@Override
    public void outAFunName(AFunName node)
    {
        types.put(node, MemTable.Type.FLOAT); //TODO: take a function table into account
											  // get a function return type here!!!
    }

	@Override
    public void outAArrName(AArrName node)
    {
        types.put(node, vars.getType(node.getWord().getText()));
    }
}
