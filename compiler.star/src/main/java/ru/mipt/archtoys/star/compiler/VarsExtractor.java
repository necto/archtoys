/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.star.compiler;

import gramm.analysis.DepthFirstAdapter;
import gramm.node.AArrName;
import gramm.node.AIndexVariable;
import gramm.node.AVarName;

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
	
	private void handleVar (String name, boolean arrayp)
	{
		if (!table.hasVar(name))
			table.registerVar(name, induceType(name), arrayp);
	}
	
	@Override
	public void outAVarName (AVarName node)
	{
		handleVar (node.getWord().getText(), false);
	}
	
	@Override
	public void outAIndexVariable(AIndexVariable node)
    {
		//TODO: support multidimensional arrays
		handleVar (((AArrName) (node.getArrName())).getWord().getText(), true);
    }
}
