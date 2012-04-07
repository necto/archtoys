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
	
	public enum Type
	{
		INTEGER (1, 'i'),
		FLOAT (2, 'd');
		private int size;
		private char sign;
		public int size() {return size;}
		public char sign() {return sign;}
		Type (int size, char sign)
		{
			this.size = size;
			this.sign = sign;
		}
	};
	
	public class Variable
	{
		public String name;
		public int adress;
		public Type type;
		public boolean arrayp; //TODO: support multidimensional arrays
		
		Variable (String name, int adress, Type type, boolean arrayp)
		{
			this.name = name;
			this.adress = adress;
			this.type = type;
			this.arrayp = arrayp;
		}
		
		@Override
		public String toString()
		{
			return "[" + name + "(" + adress + "," +
					type.sign() + (arrayp? " array" : " scalar") + ")]";
		}
	}
	
	private int lastAdress = 0;
	public Map<String, Variable> table = new HashMap<String, Variable>();
	
	private Integer chooseNextAdress (Type type, boolean arrayp)
	{
		int adress = lastAdress;
		lastAdress += type.size() * (arrayp? ARRAY_SIZE : 1);
		return adress;
	}
	
	public boolean hasVar (String name)
	{
		return table.containsKey(name);
	}
	
	public void registerVar (String name, Type type, boolean arrayp)
	{
		table.put (name,
				   new Variable (name, chooseNextAdress(type, arrayp),
								 type, arrayp));
	}
	
	public Type getType (String name)
	{
		return table.get(name).type;
	}
	
	public Integer getAdress (String name)
	{
		return table.get(name).adress;
	}
	
	public boolean isArray (String name)
	{
		return table.get(name).arrayp;
	}
	
	public int getMemSize()
	{
		return lastAdress;
	}
}
