package semantic.ast.reference;

import semantic.ast.expression.OperandNode;
import semantic.types.Type;

abstract public class ReferenceNode extends OperandNode {
    @Override
    abstract public Type check();
}
