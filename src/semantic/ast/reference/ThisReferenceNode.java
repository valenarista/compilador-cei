package semantic.ast.reference;

import exceptions.SemanticException;
import lexical.Token;
import semantic.ast.chaining.ChainingNode;
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

        if(optChaining != null) {
//            return optChaining.check( );

        }
        return null; // Retornar el tipo de la clase actual
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
