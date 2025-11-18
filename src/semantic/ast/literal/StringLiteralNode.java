package semantic.ast.literal;

import lexical.Token;
import lexical.TokenType;
import semantic.types.ReferenceType;
import semantic.types.Type;

import static compiler.Main.symbolTable;

public class StringLiteralNode extends LiteralNode {
    private Token token;

    public StringLiteralNode(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public Type getType() {
        return new ReferenceType(new Token(TokenType.classID, "String", token.getLineNumber()));
    }

    @Override
    public Type check() {
        return getType();
    }
    @Override
    public int getLine() {
        return token.getLineNumber();
    }
    @Override
    public String getLexeme() {
        return token.getLexeme();
    }

    @Override
    public void generateCode() {
        String stringLabel = symbolTable.getOrCreateStringLiteralLabel(token.getLexeme());
        symbolTable.instructionList.add("PUSH "+stringLabel);
    }
}
