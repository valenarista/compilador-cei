package semantic.ast.expression;

import lexical.Token;
import semantic.ast.chaining.ChainingNode;
import semantic.types.Type;

abstract public class OperandNode extends ExpressionNode{
    abstract public Token getToken();
    abstract public void setOptChaining(ChainingNode optChaining);

    @Override
    public boolean isConstructorCall() {
        return false;
    }

    public boolean isStaticCall() {
        return false;
    }
}
