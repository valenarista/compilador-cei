package semantic.ast.reference;

import lexical.Token;
import semantic.types.Type;

public class StaticMethodCallNode extends ReferenceNode{
    @Override
    public Type check() {
        return null;
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
        return true;
    }

    @Override
    public Token getToken() {
        return null;
    }


    @Override
    public boolean isVariable() {
        return false;
    }
}
