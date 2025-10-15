package semantic.ast.expression;

import lexical.Token;
import semantic.types.Type;

abstract public class OperandNode extends ExpressionNode{
    abstract public Token getToken();
}
