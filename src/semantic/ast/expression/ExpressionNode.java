package semantic.ast.expression;

import lexical.Token;
import semantic.ast.sentence.SentenceNode;
import semantic.types.Type;

abstract public class ExpressionNode{
    boolean isLeftSide = false;
    public abstract boolean isVariable();
    public abstract Type check();
    public abstract int getLine();
    public abstract String getLexeme();
    public abstract boolean isAssign();
    public abstract boolean isOperandWithCall();
    public abstract void generateCode();

    public void setIsLeftSide(boolean b){
        isLeftSide = b;
    }
    public boolean getIsLeftSide(){
        return isLeftSide;
    }
}
