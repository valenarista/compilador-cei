package semantic.ast.literal;

import lexical.Token;
import semantic.types.NullType;
import semantic.types.Type;

import static compiler.Main.symbolTable;

public class NullLiteralNode extends LiteralNode{
    private Token token;
    public NullLiteralNode(Token token) {
        this.token = token;
    }
    public Token getToken() {
        return token;
    }
    @Override
    public Type getType() {
        return new NullType(token);
    }

    @Override
    public Type check() {
        return new NullType(token);
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
        symbolTable.instructionList.add("PUSH "+token.getLexeme());
    }
}
