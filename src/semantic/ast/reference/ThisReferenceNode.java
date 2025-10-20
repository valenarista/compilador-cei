package semantic.ast.reference;

import exceptions.SemanticException;
import lexical.Token;
import lexical.TokenType;
import semantic.ast.chaining.ChainingNode;
import semantic.types.ReferenceType;
import semantic.types.Type;
import static compiler.Main.symbolTable;

public class ThisReferenceNode extends ReferenceNode{
    private Token token;
    private String className;
    private ChainingNode optChaining;
    public ThisReferenceNode(Token token, String className) {
        this.token = token;
    }

    @Override
    public Type check() {
        if(symbolTable.getCurrentInvocable().isStaticMethod())
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": no se puede usar 'this' en un metodo estatico", token.getLexeme(), token.getLineNumber());

        String currentClassName = symbolTable.getCurrentClass().getName();
        Type thisType = new ReferenceType(new Token(TokenType.classID,currentClassName, token.getLineNumber()));
        if(optChaining != null) {
            return optChaining.check(thisType);
        }
        return thisType;
    }

    @Override
    public void setOptChaining(ChainingNode chainingNode) {
        optChaining = chainingNode;
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
    public boolean isAssign() {
        return false;
    }

    @Override
    public boolean isOperandWithCall() {
        if(optChaining != null) {
            return optChaining.isOperandWithCall();
        }
        return false;
    }

    @Override
    public Token getToken() {
        return token;
    }

    @Override
    public boolean isVariable() {
        return false;
    }
}
