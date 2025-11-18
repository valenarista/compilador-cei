package semantic.ast.expression;

import semantic.types.Type;
import semantic.types.VoidType;

public class NullExpressionNode extends ExpressionNode{
    @Override
    public boolean isVariable() {
        return false;
    }

    @Override
    public Type check() {
        return new VoidType();
    }

    @Override
    public int getLine() {
        return 0;
    }

    @Override
    public String getLexeme() {
        return "";
    }

    @Override
    public boolean isAssign() {
        return false;
    }

    @Override
    public boolean isOperandWithCall() {
        return false;
    }

    @Override
    public void generateCode() {
        
    }
    @Override
    public void generateCode(boolean isLeftSide){
        generateCode();
    }
}
