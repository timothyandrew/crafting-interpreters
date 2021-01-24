package hello;

public class Interpreter implements Expr.Visitor<Object> {
    public void interpret(Expr expression) {
        try {
            System.out.println(evaluate(expression));
        } catch (RuntimeError e) {
            Lox.runtimeError(e);
        }
    }

    @Override
    public Object visit(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
        }

        if (left instanceof Double && right instanceof Double) {
            Double leftDouble = (double)left;
            Double rightDouble = (double)right;

            return switch (expr.operator.type) {
                case PLUS -> leftDouble + rightDouble;
                case MINUS -> leftDouble - rightDouble;
                case STAR -> leftDouble * rightDouble;
                case SLASH -> leftDouble / rightDouble;
                case GREATER -> leftDouble > rightDouble;
                case GREATER_EQUAL -> leftDouble >= rightDouble;
                case LESS -> leftDouble < rightDouble;
                case LESS_EQUAL -> leftDouble <= rightDouble;
                default -> null;
            };
        } else if (left instanceof String && right instanceof String) {
            String leftString = (String)left;
            String rightString = (String)right;

            return switch (expr.operator.type) {
                case PLUS -> leftString + rightString;
                default -> null;
            };
        } else {
            // Prefer throwing over calling a static method on `Lox` because we _want_ to unwind
            // the stack all the way when a runtime error occurs.
            throw new RuntimeError(
                    expr.operator,
                    String.format("Don't know how to perform operation (%s) on types %s and %s.",
                            expr.operator.lexeme,
                            left.getClass(),
                            right.getClass()
                    )
            );
        }
    }

    @Override
    public Object visit(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visit(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visit(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        return switch (expr.operator.type) {
            case MINUS -> -(double) right;
            case BANG -> !isTruthy(right);
            // Panic, this signals a unary operator that we've tokenized but don't
            // support interpretation for yet.
            default -> null;
        };
    }

    @Override
    public Object visit(Expr.Ternary expr) {
        return null;
    }

    private Object evaluate(Expr expression) {
        return expression.accept(this);
    }

    private boolean isTruthy(Object obj) {
        if (obj instanceof Boolean) {
            return (boolean)obj;
        }

       return obj != null;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) { return true; }
        if (a == null || b == null) { return false; }
        return a.equals(b);
    }
}
