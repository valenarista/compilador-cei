package semantic.ast.literal;

import semantic.ast.expression.OperandNode;
import semantic.types.Type;

abstract public class LiteralNode extends OperandNode {
    @Override
    public boolean isVariable() {
        return false;
    }

    public abstract Type getType();

    public abstract Type check();
}
