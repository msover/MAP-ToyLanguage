package interpreter.model.expression.enums;

public enum RelationalOperator {
    LESS_STRICT("<"),
    LESS_EQUAL("<="),
    IS_EQUAL("=="),
    NOT_EQUAL("!="),
    GREATER_STRICT(">"),
    GREATER_EQUAL(">=");

    private final String symbol;

    RelationalOperator(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return this.symbol;
    }
}
