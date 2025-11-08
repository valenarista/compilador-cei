package semantic.ast.reference;

import lexical.Token;
import semantic.ast.chaining.ChainingNode;
import semantic.types.Type;

public class ToyReferenceNode extends ReferenceNode{
    public ToyReferenceNode(){
        super();
    }
    @Override
    public Type check() {
        return null;
    }

    @Override
    public void setOptChaining(ChainingNode chainingNode) {

    }

    @Override
    public void generateCode() {

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
    public Token getToken() {
        return null;
    }


    @Override
    public boolean isVariable() {
        return false;
    }
}
