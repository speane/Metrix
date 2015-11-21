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



public class ClassInfo {
    ClassInfo(String name, int depthLevel) {
        this.name = name;
        this.depthLevel = depthLevel;
    }
    
    private final String name;
   
    public ArrayList<Field> fields = new ArrayList<>();
    public ArrayList<Method> methods = new ArrayList<>();
    public int depthLevel;
    
    public void addMethod(Method tempMethod) {
        methods.add(tempMethod);
    }
    
    public void addField(String name, String type, int depthLevel) {
        fields.add(new Field(name, type, depthLevel));
    }
    
    public String getName() {
        return name;
    }
}



