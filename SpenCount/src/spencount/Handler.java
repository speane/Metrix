/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spencount;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Evgeny
 */
public class Handler {
    Handler(String fileName) {
        sourceCode = readSourceCodeFromFile(fileName);
    }
    
    private final String sourceCode;
    
    
    public static String readSourceCodeFromFile(String filename) {
        StringBuilder result = new StringBuilder();
        String tempStr = "";
        try {
            FileReader file = new FileReader(filename);
            BufferedReader textReader = new BufferedReader(file);
            while (textReader.ready()) {
                result.append(textReader.readLine());
                result.append('\n');
            }
        } catch(FileNotFoundException fnfe) {
            System.out.println("FILE not found");
        } catch(IOException ioe) {
            System.out.println("IO");
        }
        return new String(result);
    }
    
    public void start() {
        SyntaxAnalyzer sxAnalyzer = new SyntaxAnalyzer(sourceCode);
        sxAnalyzer.analyze();
    }
    
    
}
