package hello;

import static hello.TokenType.AND;
import static hello.TokenType.BANG;
import static hello.TokenType.BANG_EQUAL;
import static hello.TokenType.CLASS;
import static hello.TokenType.COMMA;
import static hello.TokenType.DOT;
import static hello.TokenType.ELSE;
import static hello.TokenType.EOF;
import static hello.TokenType.EQUAL;
import static hello.TokenType.EQUAL_EQUAL;
import static hello.TokenType.FALSE;
import static hello.TokenType.FOR;
import static hello.TokenType.FUN;
import static hello.TokenType.GREATER;
import static hello.TokenType.GREATER_EQUAL;
import static hello.TokenType.IDENTIFIER;
import static hello.TokenType.IF;
import static hello.TokenType.LEFT_BRACE;
import static hello.TokenType.LEFT_PAREN;
import static hello.TokenType.LESS;
import static hello.TokenType.LESS_EQUAL;
import static hello.TokenType.NIL;
import static hello.TokenType.NUMBER;
import static hello.TokenType.OR;
import static hello.TokenType.PLUS;
import static hello.TokenType.PRINT;
import static hello.TokenType.RETURN;
import static hello.TokenType.RIGHT_BRACE;
import static hello.TokenType.RIGHT_PAREN;
import static hello.TokenType.SEMICOLON;
import static hello.TokenType.SLASH;
import static hello.TokenType.STAR;
import static hello.TokenType.SUPER;
import static hello.TokenType.THIS;
import static hello.TokenType.TRUE;
import static hello.TokenType.VAR;
import static hello.TokenType.WHILE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Scanner {
	String in;
	List<Token> tokens = new ArrayList<>();

	// Start of current lexeme
	int start;
	// Current char of current lexeme
	int current;
	// Line number
	int line;

	private static final Map<String, TokenType> keywords;

	// TIL!
	static {
		keywords = new HashMap<>();

		keywords.put("and", AND);
		keywords.put("class", CLASS);
		keywords.put("else", ELSE);
		keywords.put("false", FALSE);
		keywords.put("for", FOR);
		keywords.put("fun", FUN);
		keywords.put("if", IF);
		keywords.put("nil", NIL);
		keywords.put("or", OR);
		keywords.put("print", PRINT);
		keywords.put("return", RETURN);
		keywords.put("super", SUPER);
		keywords.put("this", THIS);
		keywords.put("true", TRUE);
		keywords.put("var", VAR);
		keywords.put("while", WHILE);
	}

	Scanner(String in) {
		this.in = in;
	}

	public List<Token> scanTokens() {
		while (!isAtEnd()) {
			start = current;
			scanToken();
		}

		tokens.add(new Token(EOF, "", null, line));
		return tokens;

	}

	private void scanToken() {
		char c = advance();

		switch (c) {
			// Single-char
			case '(':
				addToken(LEFT_PAREN);
				break;
			case ')':
				addToken(RIGHT_PAREN);
				break;
			case '{':
				addToken(LEFT_BRACE);
				break;
			case '}':
				addToken(RIGHT_BRACE);
				break;
			case ',':
				addToken(COMMA);
				break;
			case '.':
				addToken(DOT);
				break;
			case '+':
				addToken(PLUS);
				break;
			case ';':
				addToken(SEMICOLON);
				break;
			case '*':
				addToken(STAR);
				break;

			// Multi-char
			case '!':
				addToken(match('=') ? BANG_EQUAL : BANG);
				break;
			case '=':
				addToken(match('=') ? EQUAL_EQUAL : EQUAL);
				break;
			case '<':
				addToken(match('=') ? LESS_EQUAL : LESS);
				break;
			case '>':
				addToken(match('=') ? GREATER_EQUAL : GREATER);
				break;

			// Literals
			case '"':
				string();
				break;

			// Comments / divison
			case '/':
				// Line comment
				if (match('/')) {
					while (peek() != '\n' && !isAtEnd()) {
						advance();
					}

				// Block comment
				} else if (match('*')) {
					while (!isAtEnd() && peek() != '*' && peekNext() != '/') {
						if (peek() == '\n') {
							line++;
						}

						advance();
					}

					if (isAtEnd()) {
						Lox.error(line, "Unterminated block comment!");
						return;
					}

					advance();
					advance();
				} else {
					addToken(SLASH);
				}
				break;

			case ' ':
			case '\r':
			case '\t':
				break;

			case '\n':
				line++;
				break;

			default:
				if (isDigit(c)) {
					number();
				} else if (isAlpha(c)) {
					identifier();
				} else {
					Lox.error(line, "Invalid character!");
				}
				break;
		}
	}

	private void identifier() {
		while (isAlphaNumeric(peek())) {
			advance();
		}

		String s = in.substring(start, current);

		if (keywords.containsKey(s.toLowerCase())) {
			addToken(keywords.get(s.toLowerCase()));
		} else {
			addToken(IDENTIFIER);
		}
	}

	private boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
	}

	private boolean isAlphaNumeric(char c) {
		return isAlpha(c) || isDigit(c);
	}

	private void string() {
		while (peek() != '"' && !isAtEnd()) {
			if (peek() == '\n') {
				line++;
			}

			advance();
		}

		if (isAtEnd()) {
			Lox.error(line, "Unterminated string!");
			return;
		}

		// Advance over the closing "
		advance();

		String s = in.substring(start + 1, current - 1);
		addToken(TokenType.STRING, s);
	}

	private void number() {
		while (isDigit(peek())) {
			advance();
		}

		if (peek() == '.' && isDigit(peekNext())) {
			advance();

			while (isDigit(peek())) {
				advance();
			}
		}

		Double n = Double.parseDouble(in.substring(start, current));
		addToken(NUMBER, n);
	}

	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	// Peek at current character
	private char peek() {
		if (isAtEnd()) {
			return '\0';
		} else {
			return in.charAt(current);
		}
	}

	private char peekNext() {
		if (current + 1 >= in.length()) {
			return '\0';
		} else {
			return in.charAt(current + 1);
		}
	}

	private boolean match(char c) {
		if (peek() != c) {
			return false;
		}

		current++;
		return true;
	}

	private char advance() {
		current++;
		// Multi-byte?
		return in.charAt(current - 1);
	}

	private void addToken(TokenType type) {
		addToken(type, null);
	}

	private void addToken(TokenType type, Object literal) {
		String lexeme = in.substring(start, current);
		tokens.add(new Token(type, lexeme, literal, line));
	}

	private boolean isAtEnd() {
		return current >= in.length();
	}
}
