package semantic;

import lexical.Token;

public class Parameter {
    private Token idToken;
    private Type type;
    private String name;

    public Parameter(Token idToken, Type type, String name) {
        this.idToken = idToken;
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public int getLine() {
        return idToken.getLineNumber();
    }
}
