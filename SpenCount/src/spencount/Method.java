/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spencount;

/**
 *
 * @author Evgeny
 */
import java.util.*;

public class Method {
    Method(String name, String type, int depthLevel) {
        this.name = name;
        this.type = type;
        this.depthLevel = depthLevel;
    }
    
    private final String name;
    private final String type;
    public ArrayList<Field> variables = new ArrayList<>();
    public int depthLevel;
    
    public void addVariable(String name, String type, int depthLevel) {
        for (Field tempField : this.variables) {
            if (tempField.getName().equals(name) 
                && tempField.getType().equals(type)) {
                tempField.active = false;
                break;
            }
        }
        Field tempField = new Field(name, type, depthLevel);
        variables.add(tempField);
    }
    
    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }
}
