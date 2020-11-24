package hello;

import hello.Expr.Binary;
import hello.Expr.Grouping;
import hello.Expr.Literal;
import hello.Expr.Unary;

class AstPrinter implements Expr.Visitor<String> {
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
