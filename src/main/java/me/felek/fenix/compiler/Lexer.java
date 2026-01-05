package me.felek.fenix.compiler;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    public static List<Token> tokenize(String text) {
        List<Token> toks = new ArrayList<>();
        int i = 0;

        while (i < text.length()) {
            char c = text.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (c == ';') {
                while(c != '\n') {
                    i++;
                    c = text.charAt(i);
                }
            }

            if (c == '"') {
                StringBuilder sb = new StringBuilder();
                i++;
                while (i < text.length() && text.charAt(i) != '"') {
                    sb.append(text.charAt(i));
                    i++;
                }
                i++;
                toks.add(new Token(sb.toString(), TokenType.STRING));
                continue;
            }

            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();

                if (c == '0' && i + 1 < text.length() && (text.charAt(i + 1) == 'x' || text.charAt(i + 1) == 'X')) {
                    sb.append("0x");
                    i += 2;
                    while (i < text.length() && isHexDigit(text.charAt(i))) {
                        sb.append(text.charAt(i));
                        i++;
                    }
                } else {
                    while (i < text.length() && Character.isDigit(text.charAt(i))) {
                        sb.append(text.charAt(i));
                        i++;
                    }
                }
                toks.add(new Token(sb.toString(), TokenType.NUMBER));
                continue;
            }

            if ((c == 'R' || c == 'r') && i + 1 < text.length() && Character.isDigit(text.charAt(i + 1))) {
                StringBuilder sb = new StringBuilder();
                i++;
                while (i < text.length() && Character.isDigit(text.charAt(i))) {
                    sb.append(text.charAt(i));
                    i++;
                }
                toks.add(new Token(sb.toString(), TokenType.REGISTER));
                continue;
            }

            if (Character.isAlphabetic(c)) {
                StringBuilder sb = new StringBuilder();
                while (i < text.length() && (Character.isAlphabetic(text.charAt(i)) || Character.isDigit(text.charAt(i)))) {
                    sb.append(text.charAt(i));
                    i++;
                }
                toks.add(new Token(sb.toString().toUpperCase(), TokenType.OPERATION));
                continue;
            }

            i++;
        }
        return toks;
    }
    private static boolean isHexDigit(char c) {
        return Character.isDigit(c) ||
                (c >= 'a' && c <= 'f') ||
                (c >= 'A' && c <= 'F');
    }
}