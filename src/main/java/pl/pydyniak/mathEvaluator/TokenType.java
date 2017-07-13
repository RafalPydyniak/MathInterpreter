package pl.pydyniak.mathEvaluator;

enum TokenType {
    INTEGER,
    PLUS,
    MULTIPLY,
    DIVIDE,
    SUBSTRACT,
    LEFT_PARENTHESIS,
    RIGHT_PARENTHESIS,
    MINUS_UNARY; //# is sign for unary minus


    public boolean isOperator() {
        return equals(PLUS) || equals(DIVIDE) || equals(SUBSTRACT) || equals(MULTIPLY) || equals(MINUS_UNARY);
    }

    public int getPrecedence() {
        if (equals(PLUS) || equals(SUBSTRACT)) {
            return 1;
        } else if (equals(MULTIPLY) || equals(DIVIDE)) {
            return 2;
        } else if (equals(MINUS_UNARY)) {
            return 3;
        }
        return 0;
    }
}
