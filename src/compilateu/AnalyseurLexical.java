/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilateu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class AnalyseurLexical {
    
    private String input;
    private int position;
    private int ligne;
    private int colonne;
    private char currentChar;
    private List<String> erreurs;
    private Map<String, TokenType> motsReserves;
    
    public AnalyseurLexical(String input) {
        this.input = input + "\0";
        this.position = 0;
        this.ligne = 1;
        this.colonne = 1;
        this.currentChar = this.input.charAt(0);
        this.erreurs = new ArrayList<>();
        initMotsReserves();
    }
    
    private void initMotsReserves() {
        motsReserves = new HashMap<>();
        // Mots-clés Java
        motsReserves.put("public", TokenType.PUBLIC);
        motsReserves.put("private", TokenType.PRIVATE);
        motsReserves.put("protected", TokenType.PROTECTED);
        motsReserves.put("class", TokenType.CLASS);
        motsReserves.put("static", TokenType.STATIC);
        motsReserves.put("void", TokenType.VOID);
        motsReserves.put("int", TokenType.INT);
        motsReserves.put("double", TokenType.DOUBLE);
        motsReserves.put("float", TokenType.FLOAT);
        motsReserves.put("String", TokenType.STRING);
        motsReserves.put("boolean", TokenType.BOOLEAN);
        motsReserves.put("if", TokenType.IF);
        motsReserves.put("else", TokenType.ELSE);
        motsReserves.put("while", TokenType.WHILE);
        motsReserves.put("do", TokenType.DO);
        motsReserves.put("for", TokenType.FOR);
        motsReserves.put("switch", TokenType.SWITCH);
        motsReserves.put("case", TokenType.CASE);
        motsReserves.put("default", TokenType.DEFAULT);
        motsReserves.put("return", TokenType.RETURN);
        motsReserves.put("break", TokenType.BREAK);
        motsReserves.put("continue", TokenType.CONTINUE);
        motsReserves.put("true", TokenType.BOOLEAN_LITERAL);
        motsReserves.put("false", TokenType.BOOLEAN_LITERAL);
        
        // REMPLACEZ PAR VOTRE NOM ET PRÉNOM
        motsReserves.put("boudries", TokenType.VOTRE_NOM);
        motsReserves.put("badis", TokenType.VOTRE_PRENOM);
    }
    
    private void avancer() {
        if (currentChar == '\n') {
            ligne++;
            colonne = 0;
        }
        position++;
        colonne++;
        if (position >= input.length()) {
            currentChar = '\0';
        } else {
            currentChar = input.charAt(position);
        }
    }
    
    private char regarderSuivant() {
        int nextPos = position + 1;
        if (nextPos >= input.length()) return '\0';
        return input.charAt(nextPos);
    }
    
    private void ignorerEspaces() {
        while (currentChar != '\0' && Character.isWhitespace(currentChar)) {
            avancer();
        }
    }
    
    private void ignorerCommentaireLigne() {
        while (currentChar != '\0' && currentChar != '\n') {
            avancer();
        }
        if (currentChar == '\n') avancer();
    }
    
    private void ignorerCommentaireBloc() {
        avancer(); // skip *
        while (currentChar != '\0') {
            if (currentChar == '*' && regarderSuivant() == '/') {
                avancer(); // skip *
                avancer(); // skip /
                return;
            }
            avancer();
        }
        erreurs.add("ERREUR LEXICALE ligne " + ligne + ": Commentaire bloc non fermé");
    }
    
    private Token identifiant() {
        int startLigne = ligne;
        int startCol = colonne;
        StringBuilder sb = new StringBuilder();
        
        while (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
            sb.append(currentChar);
            avancer();
        }
        
        String value = sb.toString();
        TokenType type = motsReserves.getOrDefault(value, TokenType.IDENT);
        return new Token(type, value, startLigne, startCol);
    }
    
    private Token nombre() {
        int startLigne = ligne;
        int startCol = colonne;
        StringBuilder sb = new StringBuilder();
        
        while (Character.isDigit(currentChar) || currentChar == '.') {
            sb.append(currentChar);
            avancer();
        }
        
        return new Token(TokenType.NUMBER, sb.toString(), startLigne, startCol);
    }
    
    private Token chaine() {
        int startLigne = ligne;
        int startCol = colonne;
        StringBuilder sb = new StringBuilder();
        
        avancer(); // skip "
        
        while (currentChar != '\0' && currentChar != '"') {
            if (currentChar == '\\') {
                avancer();
                if (currentChar != '\0') {
                    sb.append('\\').append(currentChar);
                    avancer();
                }
            } else {
                sb.append(currentChar);
                avancer();
            }
        }
        
        if (currentChar == '"') {
            avancer();
            return new Token(TokenType.STRING_LITERAL, sb.toString(), startLigne, startCol);
        } else {
            erreurs.add("ERREUR LEXICALE ligne " + startLigne + ": Chaîne non fermée");
            return new Token(TokenType.ERROR, sb.toString(), startLigne, startCol);
        }
    }
    
    public List<Token> analyser() {
      
    List<Token> tokens = new ArrayList<>();

    while (currentChar != '\0') {

        // 1. Ignorer espaces
        if (Character.isWhitespace(currentChar)) {
            ignorerEspaces();
            continue;
        }

        int startLigne = ligne;
        int startCol = colonne;

        // 2. Commentaires
        if (currentChar == '/' && regarderSuivant() == '/') {
            ignorerCommentaireLigne();
            continue;
        }

        if (currentChar == '/' && regarderSuivant() == '*') {
            avancer();
            ignorerCommentaireBloc();
            continue;
        }

        // 3. Identifiants
        if (Character.isLetter(currentChar) || currentChar == '_') {
            tokens.add(identifiant());
            continue;
        }

        // 4. Nombres
        if (Character.isDigit(currentChar)) {
            tokens.add(nombre());
            continue;
        }

        // 5. Chaînes
        if (currentChar == '"') {
            tokens.add(chaine());
            continue;
        }

        // 6. Symboles
        switch (currentChar) {
            case '+': avancer(); tokens.add(new Token(TokenType.PLUS, "+", startLigne, startCol)); break;
            case '-': avancer(); tokens.add(new Token(TokenType.MINUS, "-", startLigne, startCol)); break;
            case '*': avancer(); tokens.add(new Token(TokenType.MUL, "*", startLigne, startCol)); break;
            case '/': avancer(); tokens.add(new Token(TokenType.DIV, "/", startLigne, startCol)); break;
            case '=': avancer(); tokens.add(new Token(TokenType.ASSIGN, "=", startLigne, startCol)); break;
            case ';': avancer(); tokens.add(new Token(TokenType.SEMI, ";", startLigne, startCol)); break;
            case '(': avancer(); tokens.add(new Token(TokenType.LPAR, "(", startLigne, startCol)); break;
            case ')': avancer(); tokens.add(new Token(TokenType.RPAR, ")", startLigne, startCol)); break;
            case '{': avancer(); tokens.add(new Token(TokenType.LBRACE, "{", startLigne, startCol)); break;
            case '}': avancer(); tokens.add(new Token(TokenType.RBRACE, "}", startLigne, startCol)); break;
            case '[':avancer();tokens.add(new Token(TokenType.LBRACKET, "[", startLigne, startCol));break;
            case ']':avancer();tokens.add(new Token(TokenType.RBRACKET, "]", startLigne, startCol));break;

            default:
                // Caractère inconnu (ex: @)
                erreurs.add("ERREUR LEXICALE ligne " + startLigne + ", colonne " + startCol +
                        " : Caractère inconnu '" + currentChar + "'");
                avancer();
        }
    }

    tokens.add(new Token(TokenType.EOF, "", ligne, colonne));
    return tokens;


    }
    
    public List<String> getErreurs() {
        return erreurs;
    }

}
