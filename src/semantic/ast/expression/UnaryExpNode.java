package semantic.ast.expression;

import lexical.Token;
import semantic.types.Type;

public class UnaryExpNode extends CompExpNode{
    private Token operator;
    private OperandNode operand;

    public UnaryExpNode(Token operator, OperandNode operand) {
        this.operator = operator;
        this.operand = operand;
    }
    public UnaryExpNode(OperandNode operand) {
        this.operand = operand;
    }

    @Override
    public Type check() {

        return null;
    }

    @Override
    public String getType() {
        return "";
    }
}
