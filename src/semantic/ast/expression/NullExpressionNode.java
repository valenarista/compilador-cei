package semantic.ast.expression;

import semantic.types.Type;

public class NullExpressionNode extends ExpressionNode{
    @Override
    public boolean isVariable() {
        return false;
    }

    @Override
    public Type check() {
        return null;
    }

    @Override
    public int getLine() {
        return 0;
    }

    @Override
    public String getLexeme() {
        return "";
    }
}
