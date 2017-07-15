package pl.pydyniak.mathEvaluator;

import static pl.pydyniak.mathEvaluator.TokenType.*;

class Token {
    private TokenType tokenType;
    private String value;

    Token(TokenType tokenType) {
        this(tokenType, getValueBasedOnTokenType(tokenType));
    }

    private static String getValueBasedOnTokenType(TokenType tokenType) {
        switch(tokenType) {
            case PLUS:
                return "+";
            case SUBSTRACT:
                return "-";
            case MULTIPLY:
                return "*";
            case DIVIDE:
                return "/";
            case MINUS_UNARY:
                return "#";
            case LEFT_PARENTHESIS:
                return "(";
            case RIGHT_PARENTHESIS:
                return ")";
            case MODULO:
                return "%";
            default:
                throw new UnsupportedOperationException();
        }
    }

    Token(TokenType tokenType, String value) {
        this.tokenType = tokenType;
        this.value = value;
    }

    TokenType getTokenType() {
        return tokenType;
    }

    String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Token{" +
                "tokenType=" + tokenType +
                ", value='" + value + '\'' +
                '}';
    }
}