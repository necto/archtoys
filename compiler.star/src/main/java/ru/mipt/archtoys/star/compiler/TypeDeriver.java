/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.star.compiler;

import gramm.analysis.DepthFirstAdapter;
import gramm.node.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author necto
 */
public class TypeDeriver extends DepthFirstAdapter
{
	private Map<Node, Type> types = new HashMap<Node, Type>();
	private Map<Node, Type> expected = new HashMap<Node, Type>();
	private MemTable vars;
	private FunTable funcs;

	public TypeDeriver(MemTable vars, FunTable funcs)
	{
		this.vars = vars;
		this.funcs = funcs;
	}
		
	public Type getType (Node n)
	{
		return types.get(n);
	}
	
	public Type getExpectedType (Node n)
	{
		return expected.get(n);
	}
	
	private Type derive(Node ... children)
	{
		Type t = Type.INTEGER;
		for (Node n : children)
			if (n != null)
			{
				Type childType = types.get(n);
				if (childType == Type.FLOAT)
				{
					t = Type.FLOAT;// float dominates integer always
					break;
				}
			}
		return t;
	}
	
	private void inheritType (Node node, Node ... children)
	{
		Type t = derive(children);
		types.put(node, t);
		for (Node child : children)
			expected.put(child, t);
	}

	@Override
    public void outStart(Start node)
    {
    }
	
	@Override
    public void outAProgram(AProgram node)
    {
		inheritType(node, node.getStatement());
    }

	@Override
    public void outAPrognProgram(APrognProgram node)
    {
		types.put(node, derive(node.getProgram())); //Type of the whole programm
    }	// is the type of the last statement, it doesnt limit other types of expressions.

	@Override
    public void outAAssignmentStatement(AAssignmentStatement node)
    {
		inheritType(node, node.getAssignment());
    }

	@Override
    public void outAPrinterStatement(APrinterStatement node)
    {
		inheritType(node, node.getPrinter());
    }

	@Override
    public void outAAssignment(AAssignment node)
    {
		inheritType(node, node.getVariable());
		expected.put(node.getExpr(), types.get(node));
    }

	@Override
    public void outAPrinter(APrinter node)
    {
		types.put(node, derive(node.getExprList()));
		//Printer mustn't limit it's arguments in types.
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
		// expectations of types will come from up (eg. from function call)
    }

	@Override
    public void outAExpr(AExpr node)
    {
		inheritType(node, node.getFactor());
    }

	@Override
    public void outASumExpr(ASumExpr node)
    {
		inheritType(node, node.getFactor(), node.getExpr());
    }

	@Override
    public void outASubExpr(ASubExpr node)
    {
		inheritType(node, node.getFactor(), node.getExpr());
    }

	@Override
    public void outAFactor(AFactor node)
    {
		inheritType(node, node.getUnit());
    }

	@Override
    public void outAMulFactor(AMulFactor node)
    {
		inheritType(node, node.getFactor(), node.getUnit());
    }

	@Override
    public void outADivFactor(ADivFactor node)
    {
		inheritType(node, node.getFactor(), node.getUnit());
    }

	@Override
    public void outAModFactor (AModFactor node)
    {
		types.put (node, Type.INTEGER);
		expected.put (node.getFactor(), Type.INTEGER);
		expected.put (node.getUnit(), Type.INTEGER);
    }

	@Override
    public void outAUnit (AUnit node)
    {
		inheritType (node, node.getOperand());
    }

	@Override
    public void outAInvertedUnit (AInvertedUnit node)
    {
		inheritType (node, node.getUnit());
    }

	@Override
    public void outACallUnit (ACallUnit node)
    {
		inheritType (node, node.getFunName());
		String fname = ((AFunName) (node.getFunName())).getWord().getText();
		if (funcs.get(fname).val == null)
			types.put(node, types.get(node.getExprList()));
		else
			types.put(node, funcs.get(fname).val);
		if (funcs.get(fname).arg != null)
			expected.put (node.getExprList(), funcs.get(fname).arg);
    }

	@Override
    public void outAOperand (AOperand node)
    {
		inheritType (node, node.getVal());
    }

	@Override
    public void outANestedOperand (ANestedOperand node)
    {
		inheritType (node, node.getExpr());
    }

	@Override
    public void outAVal (AVal node)
    {
		inheritType (node, node.getVariable());
    }

	@Override
    public void outAConstVal (AConstVal node) //leaf
    {
        types.put (node, Type.INTEGER);
    }

	@Override
    public void outAFconstVal (AFconstVal node) //leaf
    {
        types.put (node, Type.FLOAT);
    }

	@Override
    public void outAVariable (AVariable node)
    {
		inheritType (node, node.getVarName());
    }
	
	private List<PExpr> getExprList (PExprList list)
	{
		if (list instanceof ASeveralExprList)
		{
			ASeveralExprList several = (ASeveralExprList)list;
			List<PExpr> ret =  getExprList(several.getExprList());
			ret.add(several.getExpr());
			return ret;
		}
		List<PExpr> ret = new ArrayList<PExpr>();
		assert (list instanceof AExprList);
		ret.add (((AExprList)list).getExpr());
		return ret;
	}

	@Override
    public void outAIndexVariable (AIndexVariable node)
    {
		inheritType (node, node.getArrName());
		for(PExpr e : getExprList(node.getExprList()))
			expected.put (e, Type.INTEGER);
    }

	@Override
    public void outAVarName (AVarName node) //leaf
    {
        types.put (node, vars.getType(node.getWord().getText()));
    }

	@Override
    public void outAFunName (AFunName node) //leaf
    {
        types.put (node, funcs.get(node.getWord().getText()).val);
    }

	@Override
    public void outAArrName (AArrName node) //leaf
    {
        types.put (node, vars.getType(node.getWord().getText()));
    }

	public void reset()
	{
		types.clear();
		expected.clear();
	}
}
