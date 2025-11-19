package semantic.ast.literal;

import lexical.Token;
import semantic.types.BooleanType;
import semantic.types.Type;

import static compiler.Main.symbolTable;

public class BooleanLiteralNode extends LiteralNode{
    private Token token;
    public BooleanLiteralNode(Token token) {
        this.token = token;
    }
    public Token getToken() {
        return token;
    }
    @Override
    public Type getType() {
        return new BooleanType();
    }


    public Type check() {
        return new BooleanType();
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
        if(token.getLexeme().equals("true")){
            symbolTable.instructionList.add("PUSH 1");
        }else{
            symbolTable.instructionList.add("PUSH 0");
        }
    }
}
