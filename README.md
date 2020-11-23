# crafting-interpreters

- Following along with the [Crafting Interpreters](https://craftinginterpreters.com/) book.
- https://timothyandrew.net/learning/notes/2020/november/crafting-interpreters

## Build

```bash
# 1. Run jlox
$ gradle build && java -jar build/libs/crafting.jar
# -- OR --
$ ./gradlew run

# 2. Generate Expr.java
$ cd exprgen
$ go build && ./exprgen > ../src/main/java/hello/Expr.java
```
