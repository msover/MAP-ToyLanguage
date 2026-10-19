package interpreter.model.expression.enums;

public enum LogicOperator {
    AND("&&"),
    OR("||");

    private final String symbol;

    LogicOperator(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return this.symbol;
    }
}
