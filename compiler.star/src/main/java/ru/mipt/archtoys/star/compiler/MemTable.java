/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.star.compiler;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author necto
 */
public class MemTable
{
	public final int ARRAY_SIZE = 100;
	
	public class Variable
	{
		public String name;
		public int adress;
		public Type type;
		public int arrayd; //TODO: support multidimensional arrays
		
		Variable (String name, int adress, Type type, int arrayd)
		{
			this.name = name;
			this.adress = adress;
			this.type = type;
			this.arrayd = arrayd;
		}
		
		@Override
		public String toString()
		{
			return "[" + name + "(" + adress + "," +
					type.sign() + ((arrayd > 0) ? " array"+arrayd : " scalar") + ")]";
		}
	}
	
	private int lastAdress = 0;
	public Map<String, Variable> table = new HashMap<String, Variable>();
	
	private Integer chooseNextAdress (Type type, int arrayd)
	{
		int adress = lastAdress;
		int size = type.size();
		while (arrayd -->0)
			size *= ARRAY_SIZE;
		lastAdress += size;
		return adress;
	}
	
	public boolean hasVar (String name)
	{
		return table.containsKey(name);
	}
	
	public void registerVar (String name, Type type, int arrayd)
	{
		table.put (name,
				   new Variable (name, chooseNextAdress(type, arrayd),
								 type, arrayd));
	}
	
	public Type getType (String name)
	{
		return table.get(name).type;
	}
	
	public Integer getAdress (String name)
	{
		return table.get(name).adress;
	}
	
	public int getArrayd (String name)
	{
		return table.get(name).arrayd;
	}
	
	public int getMemSize()
	{
		return lastAdress;
	}
}
