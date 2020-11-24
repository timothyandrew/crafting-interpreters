package main

import (
	"fmt"
	"strings"
)

type ExpressionContents struct {
	jtype string
	name  string
}

type ExpressionType struct {
	jtype    string
	contents []ExpressionContents
}

func types() []ExpressionType {
	return []ExpressionType{
		{jtype: "Binary", contents: []ExpressionContents{
			{jtype: "Expr", name: "left"},
			{jtype: "Token", name: "operator"},
			{jtype: "Expr", name: "right"},
		}},
		{jtype: "Grouping", contents: []ExpressionContents{
			{jtype: "Expr", name: "expression"},
		}},
		{jtype: "Literal", contents: []ExpressionContents{
			{jtype: "Object", name: "value"},
		}},
		{jtype: "Unary", contents: []ExpressionContents{
			{jtype: "Token", name: "operator"},
			{jtype: "Expr", name: "right"},
		}},
	}
}

func buildExpressionTypes(expressionTypes []ExpressionType) {
	for _, expressionType := range expressionTypes {
		fmt.Println("  static class", expressionType.jtype, "extends Expr {")

		for _, field := range expressionType.contents {
			fmt.Printf("    final %s %s;\n", field.jtype, field.name)
		}

		fmt.Printf("\n    %s(", expressionType.jtype)
		var fieldParams []string
		for _, field := range expressionType.contents {
			fieldParams = append(fieldParams, fmt.Sprintf("%s %s", field.jtype, field.name))
		}
		fmt.Printf("%s", strings.Join(fieldParams, ", "))
		fmt.Printf(") {")

		fmt.Println("")

		for _, field := range expressionType.contents {
			fmt.Printf("      this.%s = %s;\n", field.name, field.name)
		}

		fmt.Println("\n    }\n")

		fmt.Println("    <R> R accept(Visitor<R> visitor) {")
		fmt.Println("      return visitor.visit(this);")
		fmt.Println("    }")

		fmt.Println("\n  }\n")
	}
}

func main() {
	fmt.Println("package hello;\n")
	fmt.Println("abstract class Expr {")

	fmt.Println("  abstract <R> R accept(Visitor<R> visitor);")

	var types = types()

	buildExpressionTypes(types)
	defineVisitor(types)

	fmt.Println("}")
}
