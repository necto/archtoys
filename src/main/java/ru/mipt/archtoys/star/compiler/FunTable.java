/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.star.compiler;

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.ranges.RangeException;

/**
 *
 * @author necto
 */
public class FunTable
{
	public class Function
	{
		public String name;
		public Type arg;
		public Type val;
		public int code;

		public Function(String name, Type arg, Type val, int code) {
			this.name = name;
			this.arg = arg;
			this.val = val;
			this.code = code;
		}
	}
	
	private Map <String, Function> table =
			new HashMap<String, FunTable.Function>();
	
	public Function get (String name)
	{
		Function rez = table.get(name);
		if (rez == null)
			throw new RuntimeException( "Function " + name + " is not defined");
		return rez;
	}
	
	public void put (String name, Type arg, Type val, int code)
	{
		table.put(name, new Function(name, arg, val, code));
	}

	public FunTable()
	{
		put ("sin", Type.FLOAT, Type.FLOAT, 0);
		put ("cos", Type.FLOAT, Type.FLOAT, 1);
		put ("print", null, null, 2);
	}
}
