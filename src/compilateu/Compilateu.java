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
       
        System.out.println("Entrez votre code");
        
        StringBuilder code = new StringBuilder();
        String ligne;
        
        while (scanner.hasNextLine()) {
            ligne = scanner.nextLine();
            if (ligne.trim().isEmpty()) break;
            code.append(ligne).append("\n");
        }
        
        String input = code.toString();
        
        if (input.trim().isEmpty()) {
            System.out.println("Aucun code fourni.");
            scanner.close();
            return;
        }
        
        // PHASE 1: ANALYSE LEXICALE
      
        System.out.println(" ANALYSE LEXICALE");
       
        
        AnalyseurLexical lexer = new AnalyseurLexical(input);
        List<Token> tokens = lexer.analyser();
        
        System.out.println("\n==== TOKENS ====");
        for (Token t : tokens) {
            if (t.type != TokenType.EOF) {
                System.out.println(t);
            }
        }
        
        System.out.println("\nTotal de tokens : " + (tokens.size() - 1));
        
        List<String> erreursLexicales = lexer.getErreurs();
        if (!erreursLexicales.isEmpty()) {
            System.out.println("\n==== ERREURS LEXICALES ====");
            for (String err : erreursLexicales) {
                System.out.println(err);
            }
        } else {
            System.out.println("\n✓ Aucune erreur lexicale");
        }
        
        // PHASE 2: ANALYSE SYNTAXIQUE
       
        System.out.println(" ANALYSE SYNTAXIQUE");
       
        
        AnalyseurSyntaxique analyseurSyntaxique = new AnalyseurSyntaxique(tokens);
        analyseurSyntaxique.analyser();
        
        List<String> erreursSyntaxiques = analyseurSyntaxique.getErreurs();
        if (!erreursSyntaxiques.isEmpty()) {
            System.out.println("\n==== ERREURS SYNTAXIQUES ====");
            for (String err : erreursSyntaxiques) {
                System.out.println(err);
            }
        }
        
       
    }
}

