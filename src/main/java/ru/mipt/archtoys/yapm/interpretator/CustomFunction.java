/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.yapm.interpretator;

/**
 *
 * @author avlechen
 */
public class CustomFunction {
    static void Call(byte[] args, int descriptor) {
        switch(descriptor) {
        case 2: 
            String out = "";
            for (int i = 0; i < args.length; ++i)
                out += String.valueOf((int)args[i]) + " ";
            
            System.out.println(out);
        default: break;
        }
    }
}
