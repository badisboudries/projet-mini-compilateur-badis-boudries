/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilateu;

public enum TokenType {
    // Mots-clés Java
    PUBLIC, PRIVATE, PROTECTED, CLASS, STATIC, VOID, INT, DOUBLE, FLOAT, STRING, BOOLEAN,
    IF, ELSE, WHILE, DO, FOR, SWITCH, CASE, DEFAULT, RETURN, BREAK, CONTINUE,
    
    // Mots-clés personnalisés (REMPLACEZ PAR VOTRE NOM ET PRÉNOM)
    VOTRE_NOM, VOTRE_PRENOM,

    
    // Identifiants et littéraux
    IDENT, NUMBER, STRING_LITERAL, BOOLEAN_LITERAL,
    
    // Opérateurs arithmétiques
    PLUS, MINUS, MUL, DIV, MOD,
    INCREMENT, DECREMENT,
    
    // Opérateurs de comparaison
    EQ, NEQ, LT, GT, LE, GE,
    
    // Opérateurs logiques
    AND, OR, NOT,
    
    // Opérateurs d'affectation
    ASSIGN, PLUS_ASSIGN, MINUS_ASSIGN, MUL_ASSIGN, DIV_ASSIGN,
    
    // Séparateurs
    LPAR, RPAR, LBRACE, RBRACE, LBRACKET, RBRACKET,
    SEMI, COMMA, DOT, COLON,
    
    // Spéciaux
    EOF, ERROR
    

}

