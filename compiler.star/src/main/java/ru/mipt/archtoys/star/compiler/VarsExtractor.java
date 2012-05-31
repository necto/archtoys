/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.star.compiler;

import gramm.analysis.DepthFirstAdapter;
import gramm.node.*;
import ru.mipt.archtoys.star.compiler.MemTable.IncompatibleUsage;

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
	
	private void handleVar (String name, int arrayd) throws IncompatibleUsage
	{
		table.registerVar(name, induceType(name), arrayd);
	}
	
	@Override
	public void outAVarName (AVarName node)
	{
		try {
			handleVar (node.getWord().getText(), 0);
		}
		catch (IncompatibleUsage ex) {
			throw new RuntimeException ("[ " + node.getWord().getLine() +
										", " + node.getWord().getPos() + 
										"] " + node.toString() + ex.getMessage());
		}
	}
	
	@Override
	public void outAIndexVariable(AIndexVariable node)
    {
		try {
			handleVar (((AArrName) (node.getArrName())).getWord().getText(),
						countExprs(node.getExprList()));
		}
		catch (IncompatibleUsage ex) {
			throw new RuntimeException ("[ " + node.getLsqBr().getLine() +
										", " + node.getLsqBr().getPos() + 
										"] " + node.toString() + ex.getMessage());
		}
    }
}
