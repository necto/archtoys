/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.star.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author necto
 */
public class MemTable
{
	
	public class IncompatibleUsage extends Exception
	{
		public IncompatibleUsage(String message) {
			super(message);
		}
	}
	
	abstract public class Variable
	{
		private String name;
		public int adress;
		public Type type;
		
		Variable (String name, int adress, Type type)
		{
			this.name = name;
			this.adress = adress;
			this.type = type;
		}
		
		abstract public boolean arrayp();
		abstract public int size();
		abstract protected String spec();
		
		@Override
		public String toString()
		{
			return "[" + name + "(" + adress + ","
					+ type.sign() + ":" + spec() +")]";
		}
		
	}
	
	public class Array extends Variable
	{
		private final int ARRAY_SIZE = 100;
		private int dim;
		
		Array (String name, int adress, Type type, int dimensions)
		{
			super (name, adress, type);
			assert (dimensions>0);
			this.dim = dimensions;
		}
		
		public boolean arrayp()
		{
			return true;
		}
		
		public int length()
		{
			int len = 1;
			int d = dim;
			while (d -->0)
				len *= ARRAY_SIZE;
			return len;
		}
		
		public int size()
		{
			return length()*type.size() + Type.INTEGER.size();//extra integer for the head.
		}
		
		protected String spec() { return "array " + dim;}
	}
	
	public class Scalar extends Variable
	{	
		Scalar (String name, int adress, Type type)
		{
			super (name, adress, type);
		}
		
		public boolean arrayp()
		{
			return false;
		}
		
		public int size()
		{
			return type.size();
		}
		
		protected String spec() { return "scalar";}
	}
	
	private int lastAdress = 0;
	public Map<String, Variable> table = new HashMap<String, Variable>();
	
	public void reset()
	{
		table.clear();
		lastAdress = 0;
	}
	
	public boolean hasVar (String name)
	{
		return table.containsKey(name);
	}
	
	public void registerVar (String name, Type type, int arrayd)
			throws IncompatibleUsage
	{
		Variable prev = table.get(name);
		
		Variable candidate = null;
		if (arrayd > 0)
			candidate = new Array (name, lastAdress, type, arrayd);
		else
			candidate = new Scalar(name, lastAdress, type);
		if (prev != null)
		{
			if ( !prev.type.equals(type)
			   || (prev.arrayp() && ( !candidate.arrayp()
							|| (((Array)candidate).length() != ((Array)prev).length())))
			   || (!prev.arrayp() && candidate.arrayp()))
				throw new IncompatibleUsage("Variable " + name + " was differently " + 
										"defined: " + prev + " then" + candidate);
		}
		else
		{
			lastAdress += candidate.size();
			table.put(name, candidate);
		}
	}
	
	public Variable getVarSafe (String name)
	{
		Variable var = table.get(name);
			if (var == null)  throw new RuntimeException("Internal error! accessing not registered variable");
		return var;
	}
	
	public Type getType (String name)
	{
		return getVarSafe(name).type;
	}
	
	public Integer getAdress (String name)
	{
		return getVarSafe(name).adress;
	}
	
	public boolean isArray (String name)
	{
		return getVarSafe(name).arrayp();
	}
	
	public int getMemSize()
	{
		return lastAdress;
	}
	
	public List<Array> getArrays()
	{
		List<Array> ret = new ArrayList<MemTable.Array>();
		for (Variable v : table.values())
			if (v.arrayp()) ret.add((Array)v);
		return ret;
	}
}
