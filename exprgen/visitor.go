package main

import "fmt"

func defineVisitor(types []ExpressionType) {
	fmt.Println("  interface Visitor<R> {")
	for _, t := range types {
		fmt.Printf("    R visit(%s expr);\n", t.jtype)
	}
	fmt.Println("  }")
}
