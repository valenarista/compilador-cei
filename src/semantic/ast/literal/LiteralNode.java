package semantic.ast.literal;

import semantic.ast.chaining.ChainingNode;
import semantic.ast.expression.OperandNode;
import semantic.types.Type;

abstract public class LiteralNode extends OperandNode {
    @Override
    public boolean isVariable() {
        return false;
    }
    public abstract Type getType();
    public abstract Type check();

    @Override
    public void setOptChaining(ChainingNode optChaining) {

    }

    @Override
    public boolean isAssign(){ return false; }
    @Override
    public boolean isOperandWithCall() { return false; }
    @Override
    public void generateCode(boolean isLeftSide){
        generateCode();
    }
}
