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
public class Field {
    Field(String name, String type, int depthLevel) {
        this.name = name;
        this.type = type;
        refCount = 1;
        active = true;
        this.depthLevel = depthLevel;
    }
    
    
    
    public boolean active;
    private final String name;
    private final String type;
    private int refCount;
    public int depthLevel;
    
    public void addRef() {
        this.refCount++;
    }
    
    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }
    
    public int getRefCount() {
        return refCount;
    }
}