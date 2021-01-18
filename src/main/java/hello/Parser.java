package hello;

import java.util.List;
import static hello.TokenType.*;

public class Parser {
    private static class ParseError extends RuntimeException {}
    private List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    Expr parse() {
        try {
            return expression();
        } catch (ParseError p) {
            return null;
        }
    }

    private Expr expression() {
        return ternaryConditional();
    }

    private Expr ternaryConditional() {
        Expr test = equality();

        if (match(QUESTION)) {
            Expr left = ternaryConditional();
            if (match(COLON)) {
                Expr right = ternaryConditional();
                return new Expr.Ternary(test, left, right);
            } else {
                throw error(peek(), "Ternary without :");
            }
        }

        return test;
    }

    private Expr equality() {
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();

            // This doesn't allow (x == y == z), which seems correct, but doesn't the
            // grammar allow it?
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while (match(PLUS, MINUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
        Expr expr = unary();

        while (match(STAR, SLASH)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(MINUS, BANG)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    private Expr primary() {
        if (match(FALSE)) { return new Expr.Literal(false); }
        if (match(TRUE)) { return new Expr.Literal(true); }
        if (match(NIL)) { return new Expr.Literal(null); }

        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Unbalanced parens!");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expected an expression");
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) { return; }

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            advance();
        }
    }

    // Bookkeeping / Traversal

    private Token peek() {
        return tokens.get(current);
    }

    private Token consume(TokenType type, String message) {
        if(!match(type)) {
            // current--;
            throw error(previous(), message);
        }

        return previous();
    }

    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) { return false; }
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) { current++; }
        return previous();
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean isAtEnd() {
        return current >= tokens.size();
    }
}
