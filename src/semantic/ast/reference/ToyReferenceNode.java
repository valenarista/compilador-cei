package semantic.ast.reference;

import lexical.Token;
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
    public Token getToken() {
        return null;
    }


    @Override
    public boolean isVariable() {
        return false;
    }
}
