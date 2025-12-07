/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilateu;

import java.util.List;
import java.util.Scanner;

/**
 *
 * @author utili
 */
public class Compilateu {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
       

    Scanner scanner = new Scanner(System.in);
    System.out.println("Entrez votre code (ligne vide pour terminer):");

    StringBuilder code = new StringBuilder();
    String ligne;

    while (scanner.hasNextLine()) {
        ligne = scanner.nextLine();
        if (ligne.trim().isEmpty()) break;
        code.append(ligne).append("\n");
    }

    String input = code.toString();

    AnalyseurLexical lexer = new AnalyseurLexical(input);

    List<Token> tokens = lexer.analyser();

    System.out.println("\n==== TOKENS ====");
    for (Token t : tokens) {
        System.out.println(t);
    }

    System.out.println("\n==== ERREURS ====");
    for (String err : lexer.getErreurs()) {
        System.out.println(err);
    }


        
    
}}
