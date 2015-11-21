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
public class LexemAnalyzer {
    
    LexemAnalyzer(String text) {
        this.sourceCode = text;
        this.tempSymbolNumber = 0;
    }
    
    private final String wordStartSymbols = "abcdefghijklmnopqrstuvwxyz" +
                                            "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    private final String wordSymbols = wordStartSymbols + 
                                       "0123456789";
    
    private final String sourceCode;
    private int tempSymbolNumber;
    
    private boolean isWordStartSymbol(String symbol) {
        return wordStartSymbols.contains(symbol);
    }
    
    private boolean isWordSymbol(String symbol) {
        return wordSymbols.contains(symbol);
    }
    
    private String word() {
        StringBuilder result;
        result = new StringBuilder("");
        
        boolean end = false;
        String nextSymbol;
        
        while (!end && tempSymbolNumber < sourceCode.length()) {
            nextSymbol = showNextSymbol();
            
            if (isWordSymbol(nextSymbol)) {
                result.append(getNextSymbol());
            }
            else {
                end = true;
            }
        }
        
        return new String(result);
    }
    
    private boolean isLeftCurlyBrace(String symbol) {
        return symbol.equals("{");
    }
    
    private boolean isRightCurlyBrace(String symbol) {
        return symbol.equals("}");
    }
    
     private boolean isLeftBrace(String symbol) {
        return symbol.equals("(");
    }
    
    private boolean isRightBrace(String symbol) {
        return symbol.equals(")");
    }
    
    private boolean isBreaker(String symbol) {
        return symbol.equals(";");
    }
    
    private boolean isComma(String symbol) {
        return symbol.equals(",");
    }
    
    private boolean isQuotes(String symbol) {
        return symbol.equals("\"");
    }
    
    private boolean isNewLine(String symbol) {
        return symbol.equals("\n");
    }
    
    private boolean isSlash(String symbol) {
        return symbol.equals("/");
    }
    
    private boolean isAsterisk(String symbol) {
        return symbol.equals("*");
    }
    
    private boolean isApostrophe(String symbol) {
        return symbol.equals("\'");
    }
    
    private void charLiteral() {
        String tempSymbol;
        
        while (isAvailable()) {
            tempSymbol = getNextSymbol();
            
            if (isApostrophe(tempSymbol)) {
                return;
            }
        }
    }
    
    private void stringLiteral() {
        String tempSymbol;
        
        while (isAvailable()) {
            tempSymbol = getNextSymbol();
            
            if (isQuotes(tempSymbol)) {
                return;
            }
        }
    }
    
    private void singleCommentary() {
        String tempSymbol;
        
        while (isAvailable()) {
            tempSymbol = getNextSymbol();
            
            if (isNewLine(tempSymbol)) {
                return;
            }
        }
    }
    
    private void commentaryStart() {
        String tempSymbol;
        
        if (isAvailable()) {
            tempSymbol = showNextSymbol();
            if (isSlash(tempSymbol)) {
                getNextSymbol();
                singleCommentary();
            }
            else if (isAsterisk(tempSymbol)) {
                getNextSymbol();
                multipleCommentary();
            }
        }
    }
    
    private void multipleCommentary() {
        String tempSymbol;
        
        while (isAvailable()) {
            tempSymbol = getNextSymbol();
            
            if (isAsterisk(tempSymbol)) {
                if (isAvailable()) {
                    tempSymbol = getNextSymbol();
                    if (isSlash(tempSymbol)) {
                        return;
                    }
                }
            }
        }
    }
    
    private String blank() {
        String tempSymbol;
        
        while (isAvailable()) {
            tempSymbol = showNextSymbol();
            
            if (isWordStartSymbol(tempSymbol)) {
                return word();
            }
            else if (isLeftCurlyBrace(tempSymbol) || isRightCurlyBrace(tempSymbol) ||
                    isLeftBrace(tempSymbol) || isRightBrace(tempSymbol) ||
                    isBreaker(tempSymbol) || isComma(tempSymbol)) {
                return getNextSymbol();
            }
            else if (isQuotes(tempSymbol)) {
                getNextSymbol();
                stringLiteral();
            }
            else if (isSlash(tempSymbol)) {
                getNextSymbol();
                commentaryStart();
            }
            else if (isApostrophe(tempSymbol)) {
                getNextSymbol();
                charLiteral();
            }
            else {
                getNextSymbol();
            }
        }
        return "";
    }
    
    public boolean isAvailable() {
        return tempSymbolNumber < sourceCode.length();
    }
    
    public String getNextLexem() {
        return blank();
    }
    
    private String getNextSymbol() {
        tempSymbolNumber++;
        return sourceCode.substring(tempSymbolNumber - 1, tempSymbolNumber);
    }
    
    private String showNextSymbol() {
        return sourceCode.substring(tempSymbolNumber, tempSymbolNumber + 1);
    }
}
