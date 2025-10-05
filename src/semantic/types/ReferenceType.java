package semantic.types;

import lexical.Token;

public class ReferenceType implements Type{
    private Token classIdName;

    public ReferenceType(Token name) {
        classIdName = name;
    }
    public boolean isPrimitive() {
        return false;
    }

    public String getName() {
        return classIdName.getLexeme();
    }
    public int getLine() {
        return classIdName.getLineNumber();
    }
}
