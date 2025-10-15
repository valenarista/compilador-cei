package semantic.ast.expression;

import lexical.Token;
import semantic.ast.sentence.SentenceNode;
import semantic.types.Type;

abstract public class ExpressionNode{
    public abstract boolean isVariable();
    public abstract Type check();
}
