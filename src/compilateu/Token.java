/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilateu;

public class Token {
    public TokenType type;
    public String value;
    public int ligne;
    public int colonne;

    public Token(TokenType type, String value, int ligne, int colonne) {
        this.type = type;
        this.value = value;
        this.ligne = ligne;
        this.colonne = colonne;
    }

    @Override
    public String toString() {
        return String.format("%-20s | %-15s | Ligne:%-3d Colonne:%-3d", type, "\"" + value + "\"", ligne, colonne);
    }
}