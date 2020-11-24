all: build/libs/crafting.jar

src/main/java/hello/Expr.java: exprgen/exprgen
	$< > $@

exprgen/exprgen: exprgen/main.go $(wildcard exprgen/*.go)
	go build -o $@ $<

build/libs/crafting.jar: src/main/java/hello/Expr.java $(wildcard src/main/java/hello/*.java)
	./gradlew build
