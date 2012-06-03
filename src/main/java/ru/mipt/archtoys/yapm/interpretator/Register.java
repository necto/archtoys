/*
 * Register class implementation
 */
package ru.mipt.archtoys.yapm.interpretator;

/**
 *
 * @author avlechen
 */
public class Register {
    private int _value;
    private boolean _isChanged;
    
    int value() { return _value; }
    void set_value(int aValue) { _isChanged = true; _value = aValue; }
    
    void Dump(String name) {
        if (_isChanged) {
            _isChanged = false;
            System.out.println(name + ": " + String.valueOf(_value));
        }
    }
}
