package me.zoltiks.business;

import java.util.HashMap;
import java.util.Map;

public class FormulaEvaluator {

    public static double eval(String formula) throws IllegalArgumentException {
        return new Object() {
            int pos = -1;
            int ch;

            void nextChar() {
                ch = (++pos < formula.length()) ? formula.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < formula.length()) {
                    throw new RuntimeException("Unexpected character: '" + (char) ch + "'");
                }
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                while (true) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                while (true) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = pos;

                if (eat('(')) {
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing closing parenthesis ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(formula.substring(startPos, pos));
                } else {
                    throw new RuntimeException("Unexpected character: '" + (char) ch + "'");
                }

                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }
}