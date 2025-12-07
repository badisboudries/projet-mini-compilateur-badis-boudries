/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilateu;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author utili
 */
public class AnalyseurSyntaxique {
    
    
    
    
    private List<Token> tokens;
    private int position;
    private Token currentToken;
    private List<String> erreurs;
    
    public AnalyseurSyntaxique(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
        this.currentToken = tokens.get(0);
        this.erreurs = new ArrayList<>();
    }
    
    private void avancer() {
        if (position < tokens.size() - 1) {
            position++;
            currentToken = tokens.get(position);
        }
    }
    
    private void consommer(TokenType type) {
        if (currentToken.type == type) {
            avancer();
        } else {
            erreurs.add("ERREUR SYNTAXIQUE ligne " + currentToken.ligne + 
                       ": Attendu " + type + " mais trouvé " + currentToken.type + 
                       " (\"" + currentToken.value + "\")");
            // Ne pas arrêter, continuer l'analyse
            avancer();
        }
    }
    
    private boolean verifier(TokenType type) {
        return currentToken.type == type;
    }
    
    private boolean verifierPlusieurs(TokenType... types) {
        for (TokenType type : types) {
            if (currentToken.type == type) return true;
        }
        return false;
    }
    
    public void analyser() {
        try {
            programme();
            if (!erreurs.isEmpty()) {
                System.out.println("\n⚠ Analyse terminée avec " + erreurs.size() + " erreur(s)");
            } else {
                System.out.println("\n✓ Analyse syntaxique réussie : Aucune erreur détectée !");
            }
        } catch (Exception e) {
            erreurs.add("ERREUR CRITIQUE: " + e.getMessage());
        }
    }
    
    // Programme → { Declaration | Instruction }
    private void programme() {
        while (currentToken.type != TokenType.EOF) {
            if (verifierPlusieurs(TokenType.PUBLIC, TokenType.PRIVATE, TokenType.PROTECTED, TokenType.CLASS)) {
                declaration();
            } else if (verifierPlusieurs(TokenType.INT, TokenType.DOUBLE, TokenType.FLOAT, TokenType.STRING, TokenType.BOOLEAN)) {
                declarationVariable();
            } else if (currentToken.type == TokenType.IDENT) {
                affectation();
                consommer(TokenType.SEMI);
            } else if (currentToken.type == TokenType.DO) {
                boucleDoWhile(); // INSTRUCTION PRINCIPALE
            } else if (verifierPlusieurs(TokenType.IF, TokenType.WHILE, TokenType.FOR, TokenType.SWITCH)) {
                // Ignorer les autres structures de contrôle
                ignorerStructure();
            } else if (currentToken.type == TokenType.RETURN) {
                instructionReturn();
            } else {
                erreurs.add("ERREUR SYNTAXIQUE ligne " + currentToken.ligne + 
                           ": Instruction ou déclaration invalide");
                avancer(); // Récupération d'erreur
            }
        }
    }
    
    // Declaration → Modificateurs class IDENT { Corps }
    private void declaration() {
        // Modificateurs
        while (verifierPlusieurs(TokenType.PUBLIC, TokenType.PRIVATE, TokenType.PROTECTED, TokenType.STATIC)) {
            avancer();
        }
        
        if (verifier(TokenType.CLASS)) {
            consommer(TokenType.CLASS);
            consommer(TokenType.IDENT);
            consommer(TokenType.LBRACE);
            
            while (!verifier(TokenType.RBRACE) && currentToken.type != TokenType.EOF) {
                if (verifierPlusieurs(TokenType.PUBLIC, TokenType.PRIVATE, TokenType.PROTECTED, TokenType.STATIC)) {
                    declarationMethode();
                } else if (verifierPlusieurs(TokenType.INT, TokenType.DOUBLE, TokenType.FLOAT, TokenType.STRING, TokenType.BOOLEAN)) {
                    declarationVariable();
                } else {
                    avancer();
                }
            }
            
            consommer(TokenType.RBRACE);
        }
    }
    
    // DeclarationMethode → Modificateurs Type IDENT ( Parametres ) { Corps }
    private void declarationMethode() {
        while (verifierPlusieurs(TokenType.PUBLIC, TokenType.PRIVATE, TokenType.PROTECTED, TokenType.STATIC)) {
            avancer();
        }
        
        if (verifierPlusieurs(TokenType.INT, TokenType.DOUBLE, TokenType.FLOAT, TokenType.STRING, TokenType.BOOLEAN, TokenType.VOID)) {
            avancer();
        }
        
        consommer(TokenType.IDENT);
        consommer(TokenType.LPAR);
        
        // Paramètres
        if (!verifier(TokenType.RPAR)) {
            parametres();
        }
        
        consommer(TokenType.RPAR);
        consommer(TokenType.LBRACE);
        
        // Corps de la méthode
        while (!verifier(TokenType.RBRACE) && currentToken.type != TokenType.EOF) {
            if (verifierPlusieurs(TokenType.INT, TokenType.DOUBLE, TokenType.FLOAT, TokenType.STRING, TokenType.BOOLEAN)) {
                declarationVariable();
            } else if (currentToken.type == TokenType.IDENT) {
                affectation();
                consommer(TokenType.SEMI);
            } else if (currentToken.type == TokenType.DO) {
                boucleDoWhile();
            } else if (currentToken.type == TokenType.RETURN) {
                instructionReturn();
            } else if (verifierPlusieurs(TokenType.IF, TokenType.WHILE, TokenType.FOR)) {
                ignorerStructure();
            } else {
                avancer();
            }
        }
        
        consommer(TokenType.RBRACE);
    }
    
    // Parametres → Type IDENT { , Type IDENT }
    private void parametres() {
        if (verifierPlusieurs(TokenType.INT, TokenType.DOUBLE, TokenType.FLOAT, TokenType.STRING, TokenType.BOOLEAN)) {
            avancer();
            consommer(TokenType.IDENT);
            
            while (verifier(TokenType.COMMA)) {
                avancer();
                if (verifierPlusieurs(TokenType.INT, TokenType.DOUBLE, TokenType.FLOAT, TokenType.STRING, TokenType.BOOLEAN)) {
                    avancer();
                    consommer(TokenType.IDENT);
                }
            }
        }
    }
    
    // DeclarationVariable → Type IDENT [ = Expression ] ;
    private void declarationVariable() {
        avancer(); // Type
        consommer(TokenType.IDENT);
        
        if (verifier(TokenType.ASSIGN)) {
            consommer(TokenType.ASSIGN);
            expression();
        }
        
        consommer(TokenType.SEMI);
    }
    
    // Affectation → IDENT (=|+=|-=|*=|/=) Expression
    private void affectation() {
        consommer(TokenType.IDENT);
        
        if (verifierPlusieurs(TokenType.ASSIGN, TokenType.PLUS_ASSIGN, TokenType.MINUS_ASSIGN, 
                             TokenType.MUL_ASSIGN, TokenType.DIV_ASSIGN)) {
            avancer();
            expression();
        } else if (verifierPlusieurs(TokenType.INCREMENT, TokenType.DECREMENT)) {
            avancer(); // i++ ou i--
        } else {
            erreurs.add("ERREUR SYNTAXIQUE ligne " + currentToken.ligne + 
                       ": Opérateur d'affectation attendu");
        }
    }
    
    // BoucleDoWhile → do { Instructions } while ( Condition ) ;
    private void boucleDoWhile() {
        consommer(TokenType.DO);
        consommer(TokenType.LBRACE);
        
        // Instructions dans le bloc
        while (!verifier(TokenType.RBRACE) && currentToken.type != TokenType.EOF) {
            if (verifierPlusieurs(TokenType.INT, TokenType.DOUBLE, TokenType.FLOAT, TokenType.STRING, TokenType.BOOLEAN)) {
                declarationVariable();
            } else if (currentToken.type == TokenType.IDENT) {
                affectation();
                consommer(TokenType.SEMI);
            } else if (currentToken.type == TokenType.DO) {
                boucleDoWhile(); // Boucles imbriquées
            } else if (verifierPlusieurs(TokenType.IF, TokenType.WHILE, TokenType.FOR)) {
                ignorerStructure();
            } else if (verifierPlusieurs(TokenType.BREAK, TokenType.CONTINUE)) {
                avancer();
                consommer(TokenType.SEMI);
            } else {
                erreurs.add("ERREUR SYNTAXIQUE ligne " + currentToken.ligne + 
                           ": Instruction invalide dans le bloc do-while");
                avancer();
            }
        }
        
        consommer(TokenType.RBRACE);
        consommer(TokenType.WHILE);
        consommer(TokenType.LPAR);
        condition();
        consommer(TokenType.RPAR);
        consommer(TokenType.SEMI);
    }
    
    // Expression → Terme { (+|-) Terme }
    private void expression() {
        terme();
        
        while (verifierPlusieurs(TokenType.PLUS, TokenType.MINUS)) {
            avancer();
            terme();
        }
    }
    
    // Terme → Facteur { (*|/|%) Facteur }
    private void terme() {
        facteur();
        
        while (verifierPlusieurs(TokenType.MUL, TokenType.DIV, TokenType.MOD)) {
            avancer();
            facteur();
        }
    }
    
    // Facteur → IDENT | NUMBER | ( Expression ) | ++IDENT | --IDENT | IDENT++ | IDENT--
    private void facteur() {
        if (verifierPlusieurs(TokenType.INCREMENT, TokenType.DECREMENT)) {
            avancer();
            consommer(TokenType.IDENT);
        } else if (verifier(TokenType.IDENT)) {
            avancer();
            if (verifierPlusieurs(TokenType.INCREMENT, TokenType.DECREMENT)) {
                avancer();
            }
        } else if (verifier(TokenType.NUMBER)) {
            avancer();
        } else if (verifier(TokenType.LPAR)) {
            consommer(TokenType.LPAR);
            expression();
            consommer(TokenType.RPAR);
        } else if (verifier(TokenType.STRING_LITERAL)) {
            avancer();
        } else if (verifier(TokenType.BOOLEAN_LITERAL)) {
            avancer();
        } else {
            erreurs.add("ERREUR SYNTAXIQUE ligne " + currentToken.ligne + 
                       ": Facteur invalide");
            avancer();
        }
    }
    
    // Condition → Expression Comparateur Expression { (&&|||) Expression Comparateur Expression }
    private void condition() {
        expression();
        comparateur();
        expression();
        
        while (verifierPlusieurs(TokenType.AND, TokenType.OR)) {
            avancer();
            expression();
            comparateur();
            expression();
        }
    }
    
    // Comparateur → == | != | < | > | <= | >=
    private void comparateur() {
        if (verifierPlusieurs(TokenType.EQ, TokenType.NEQ, TokenType.LT, TokenType.GT, TokenType.LE, TokenType.GE)) {
            avancer();
        } else {
            erreurs.add("ERREUR SYNTAXIQUE ligne " + currentToken.ligne + 
                       ": Comparateur attendu");
            avancer();
        }
    }
    
    // InstructionReturn → return [ Expression ] ;
    private void instructionReturn() {
        consommer(TokenType.RETURN);
        
        if (!verifier(TokenType.SEMI)) {
            expression();
        }
        
        consommer(TokenType.SEMI);
    }
    
    // Ignorer les structures non analysées (if, while, for, switch)
    private void ignorerStructure() {
        TokenType type = currentToken.type;
        avancer();
        
        // Ignorer les parenthèses et le bloc
        int niveauParentheses = 0;
        int niveauAccolades = 0;
        
        while (currentToken.type != TokenType.EOF) {
            if (verifier(TokenType.LPAR)) niveauParentheses++;
            if (verifier(TokenType.RPAR)) niveauParentheses--;
            if (verifier(TokenType.LBRACE)) niveauAccolades++;
            if (verifier(TokenType.RBRACE)) {
                niveauAccolades--;
                if (niveauAccolades == 0) {
                    avancer();
                    break;
                }
            }
            avancer();
        }
    }
    
    public List<String> getErreurs() {
        return erreurs;
    }
}

    
    
    

