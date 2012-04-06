/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.star.compiler;

import gramm.analysis.DepthFirstAdapter;
import gramm.node.AName;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author necto
 */
public class VarsExtractor extends DepthFirstAdapter
{
	final public int INTEGER = 0;
	final public int FLOAT = 1;
	
	public Map<String, Integer> memoryTable = new HashMap<String, Integer>();
	private int lastAdress = 0;
	
	private Integer chooseNextAdress (int type)
	{
		switch (type)
		{
			case INTEGER:
				return lastAdress += 1;
			case FLOAT:
				return lastAdress += 2;
		}
		return 0;
	}
	
	public int getType (String name)
	{
		if ("ijklmn".indexOf( name.charAt(0)) != -1)
			return INTEGER;
		return FLOAT;
	}
	
	public Integer getAdress (String name)
	{
		return memoryTable.get(name);
	}
	
	private void handleVar (String name)
	{
		if (memoryTable.containsKey(name))
		{
		}
		else
			memoryTable.put (name, chooseNextAdress (getType(name)));
	}
	
	@Override
	public void outAName (AName node)
	{
		handleVar (node.getWord().getText());
	}
}
