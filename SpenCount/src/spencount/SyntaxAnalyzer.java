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

public class SyntaxAnalyzer {
    SyntaxAnalyzer(String sourceCode) {
        lxAnalyzer = new LexemAnalyzer(sourceCode);
        classes = new ArrayList<>();
        depthLevel = 0;
        inRoundBraces = false;
    }
    
    private enum lexemType { OTHER, DATA_TYPE, IDENTIFIER, 
                             OPEN_CURLY_BRACE, CLOSE_CURLY_BRACE,
                             OPEN_ROUND_BRACE, CLOSE_ROUND_BRACE
    }
    
    private final String[] dataTypes = { "int", "void", "long", "short", 
                                        "double", "string", "unsigned", "byte", 
                                        "bool" , "var", "ushort", "ViewModel", 
                                        "Regex"};
    
    private final LexemAnalyzer lxAnalyzer;
    private ArrayList<ClassInfo> classes = new ArrayList<>();
    private ClassInfo tempClass;
    private Method tempMethod;  
    private String tempLexem;
    private int depthLevel;
    private boolean inRoundBraces;
    
    private final int IDENTIFIER = 1;
    private final int TYPE = 2;
    private final int CLASS = 8;
    private final int OPEN_CURLY = 4;
    private final int CLOSE_CURLY = 5;
    private final int OPEN_ROUND = 6;
    private final int CLOSE_ROUND = 7;
    private final int BREAKER = 3;
    private final int RUBBISH = 0;
    private final int COMMA = 9;
    
    
    public void analyze() {
        
        while (lxAnalyzer.isAvailable()) {
            tempLexem = lxAnalyzer.getNextLexem();
            switch (getLexemType(tempLexem)) {
                case TYPE :
                    typeLexem(tempLexem);
                    break;
                case IDENTIFIER :
                    identLexem(tempLexem);
                    break; 
                case CLASS :
                    classLexem();
                    break;
                case OPEN_CURLY :
                    openCurlyBraceLexem();
                    break;
                case CLOSE_CURLY :
                    closeCurlyBraceLexem();
                    break;
                case OPEN_ROUND :
                    openRoundBraceLexem();
                    break;
                case CLOSE_ROUND :
                    closeRoundBraceLexem();
                    break;
                default :
                    break;
            } 
        }
        
        printSpenCount();
    }
    
    private void typeLexem(String type) {
        while (lxAnalyzer.isAvailable()) {
            tempLexem = lxAnalyzer.getNextLexem();
            switch (getLexemType(tempLexem)) {
                case TYPE : 
                    typeLexem((type + tempLexem));
                    break;
                case IDENTIFIER :
                    newVarLexem(tempLexem, type);
                    return;
                case RUBBISH :
                    break;
                case CLOSE_ROUND :
                    closeRoundBraceLexem();
                default :
                    return;
            }
        }
    }
    
    private boolean isType(String word) {
        for (String tempStr : dataTypes) {
            if (tempStr.equals(word)) {
                return true;
            }
        }
        return false;
    }
    
    private int getLexemType(String lexem) {
        
        switch (lexem) {
            case "" :
                return RUBBISH;
            case "{" :
                return OPEN_CURLY;
            case "}" :
                return CLOSE_CURLY;
            case ";" :
                return BREAKER;
            case "(" :
                return OPEN_ROUND;
            case ")" :
                return CLOSE_ROUND;
            case "class" :
                return CLASS;
            case "," :
                return COMMA;
            default :
                if (isType(lexem)) {
                    return TYPE;
                }
                else {
                    return IDENTIFIER;
                }
        }
    }

    private void identLexem(String name) {
        if (tempMethod != null) {
            for (Field tempField : tempMethod.variables) {
                if (tempField.getName().equals(name) && tempField.active) {
                    tempField.addRef();
                    return;
                }
            }
        }
        if (tempClass != null) {
            for (Field tempField : tempClass.fields) {
                if (tempField.getName().equals(name)) {
                    tempField.addRef();
                }
            }
        }
    }
    
    private void openRoundBraceLexem() {
        inRoundBraces = true;
    }
    
    private void closeRoundBraceLexem() {
        inRoundBraces = false;
    }
    
    private void openCurlyBraceLexem() {
        depthLevel++;
    }
    
    private void closeCurlyBraceLexem() {
        depthLevel--;
        checkDepthLevels();
    }
    
    private void checkDepthLevels() {
        if (tempMethod != null) {
            if (tempMethod.depthLevel > depthLevel) {
                tempMethod = null;
            }
            else {
                for (Field tempField : tempMethod.variables) {
                    if (tempField.depthLevel > depthLevel) {
                        tempField.active = false;
                    }
                }
            }
        }
    }
    
    private void addNewMethod(String name, String type) {
        tempMethod = new Method(name, type, depthLevel + 1);
        tempClass.addMethod(tempMethod);
    }
  
    private void addNewVariable(String name, String type) {

        if (tempMethod != null) {
            if (inRoundBraces) {
                tempMethod.addVariable(name, type, (depthLevel + 1));
            }
            else {
                tempMethod.addVariable(name, type, (depthLevel));
            }
        }
        else {
            tempClass.addField(name, type, depthLevel);
        }
    }
    
    private void commaLexem(String type) {
        tempLexem = lxAnalyzer.getNextLexem();
        
        switch (getLexemType(tempLexem)) {
            case TYPE:
                typeLexem(tempLexem);
                break;
            case IDENTIFIER:
                newVarLexem(tempLexem, type);
                break;
        }
    }
    
    private void newVarLexem(String ident, String type) {
        tempLexem = lxAnalyzer.getNextLexem();
        
        if (getLexemType(tempLexem) != OPEN_ROUND) {
            addNewVariable(ident, type);
        }
        else {
            addNewMethod(ident, type);
            inRoundBraces = true;
        }
        
        switch (getLexemType(tempLexem)) {
            case COMMA :
                commaLexem(type);
                break;
            case TYPE :
                typeLexem(tempLexem);
                break;
            case IDENTIFIER :
                identLexem(tempLexem);
                break;
            case CLOSE_CURLY :
                closeCurlyBraceLexem();
                break;
            case CLOSE_ROUND :
                closeRoundBraceLexem();
                break;
            default :
                break;
        }
    }
    
    private void classLexem() {
        tempLexem = lxAnalyzer.getNextLexem();
        tempClass = new ClassInfo(tempLexem, (depthLevel + 1));
        classes.add(tempClass);
    }
    
    private void printSpenCount() {
        for (ClassInfo tempClass : classes) {
            
            System.out.println("class " + tempClass.getName());
            System.out.println("  Fields :");
            for (Field tempField : tempClass.fields) {
                System.out.println("    " + tempField.getType() + " " +
                                    tempField.getName() + " " + 
                                    (tempField.getRefCount() - 1));
            }
            System.out.println("  Methods :");
            for (Method tempMethod : tempClass.methods) {
                System.out.println("    " + tempMethod.getType() + 
                                    " " + tempMethod.getName());
                
                for (Field tempField : tempMethod.variables) {
                    System.out.println("        " + tempField.getType() + 
                                        " " + tempField.getName() + " " + 
                                        " " + (tempField.getRefCount() - 1));
                }
            }
        }
    }
}
