/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.star.compiler;

/**
 *
 * @author necto
 */
public enum Type {
	INTEGER(1, 'i'), FLOAT(2, 'd');
	private int size;
	private char sign;

	public int size() {
		return size;
	}

	public char sign() {
		return sign;
	}

	Type(int size, char sign) {
		this.size = size;
		this.sign = sign;
	}
	
}
