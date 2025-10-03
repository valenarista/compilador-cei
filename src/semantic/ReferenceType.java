package semantic;

import lexical.Token;

public class ReferenceType implements Type{
    private Token classIdName;

    public ReferenceType(Token name) {
        classIdName = name;
    }

    public Token getName() {
        return classIdName;
    }
}
