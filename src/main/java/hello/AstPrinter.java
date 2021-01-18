package hello;

import hello.Expr.*;

class AstPrinter implements Expr.Visitor<String> {
    @Override
    public String visit(Ternary expr) {
        return parenthesize(
                expr.test.accept(this),
                "?",
                expr.left.accept(this),
                ":",
                expr.right.accept(this));
    }

    @Override
    public String visit(Binary expr) {
        return parenthesize(expr.operator.lexeme,
                expr.left.accept(this),
                expr.right.accept(this));
    }

    @Override
    public String visit(Grouping expr) {
        return parenthesize("group", expr.expression.accept(this));
    }

    @Override
    public String visit(Literal expr) {
        return expr.value.toString();
    }

    @Override
    public String visit(Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right.accept(this));
    }

    private String parenthesize(String... vals) {
        return "(" + String.join(" ", vals) + ")";
    }
}
