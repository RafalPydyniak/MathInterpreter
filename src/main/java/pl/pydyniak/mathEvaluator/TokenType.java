package pl.pydyniak.mathEvaluator;

enum TokenType {
    INTEGER(false, 0),
    PLUS(true, 1),
    MULTIPLY(true, 2),
    DIVIDE(true, 2),
    MODULO(true, 2),
    SUBSTRACT(true, 1),
    LEFT_PARENTHESIS(false, 0),
    RIGHT_PARENTHESIS(false, 0),
    MINUS_UNARY(true, 3); //# is sign for unary minus

    private boolean operator;
    private int precedence;

    TokenType(boolean operator, int precedence) {
        this.operator = operator;
        this.precedence = precedence;
    }

    public boolean isOperator() {
        return operator;
    }

    public int getPrecedence() {
        return precedence;
    }
}
