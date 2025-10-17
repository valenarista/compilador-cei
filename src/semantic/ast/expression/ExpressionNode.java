package semantic.ast.expression;

import lexical.Token;
import semantic.ast.sentence.SentenceNode;
import semantic.types.Type;

abstract public class ExpressionNode{
    public abstract boolean isVariable();
    public abstract Type check();
    public abstract int getLine();
    public abstract String getLexeme();
    public abstract boolean isAssign();
    public abstract boolean isOperandWithCall();
}
