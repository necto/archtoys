/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mipt.archtoys.yapm.interpretator;

/**
 *
 * @author avlechen
 */
public class MemoryController {
    byte[] byteArray = new byte[0x10000];
    MemoryController() {
    }

    int ReadMemory(int addr) {
        return byteArray[addr];
    }

    void WriteMemory(int src, int addr) {
        System.out.println("Write " + String.valueOf(src) + " to addr " + String.valueOf(addr));
        byteArray[addr] = (byte)src;
    }
}

