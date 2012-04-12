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
public class VarsExtractor extends DepthFirstAdapter
{
	private MemTable table;

	public VarsExtractor (MemTable table)
	{
		this.table = table;
	}
	
	private Type induceType (String name)
	{
		if ("ijklmn".indexOf( name.charAt(0)) != -1)
			return Type.INTEGER;
		return Type.FLOAT;
	}
	
	private int countExprs (PExprList list)
	{
		if (list instanceof ASeveralExprList)
			return countExprs(((ASeveralExprList)list).getExprList()) + 1;
		assert (list instanceof AExprList);
		return 1;
	}
	
	private void handleVar (String name, int arrayd)
	{
		if (!table.hasVar(name))
			table.registerVar(name, induceType(name), arrayd);
	}
	
	@Override
	public void outAVarName (AVarName node)
	{
		handleVar (node.getWord().getText(), 0);
	}
	
	@Override
	public void outAIndexVariable(AIndexVariable node)
    {
		//TODO: support multidimensional arrays
		handleVar (((AArrName) (node.getArrName())).getWord().getText(),
					countExprs(node.getExprList()));
    }
}
