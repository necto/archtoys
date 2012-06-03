/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.yapm.interpretator;

/**
 *
 * @author avlechen
 */
public class FunctionalState {
    public static final int REGISTERS_NUM = 256;
    Register[] register = new Register[REGISTERS_NUM];
    
    FunctionalState() {
        for (int i = 0; i < REGISTERS_NUM; ++i) {
            register[i] = new Register();
        }
    }
    
    void Dump(long cycle) {
        System.out.println("Functional state @cycle: " + String.valueOf(cycle));
        for (int i = 0; i < REGISTERS_NUM; ++i)
            register[i].Dump("r"+String.valueOf(i));
    }
    
    int GetRegister(int i) { return register[i].value(); }
    void SetRegister(int i, int aValue) { register[i].set_value(aValue); }
}
