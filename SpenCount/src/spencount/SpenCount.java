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
public class SpenCount {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Handler codeAnalyzer = new Handler("source.cs");
        codeAnalyzer.start();
    }
    
}
