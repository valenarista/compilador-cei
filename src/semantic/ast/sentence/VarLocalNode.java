package semantic.ast.sentence;

import exceptions.SemanticException;
import lexical.Token;
import semantic.ast.expression.ExpressionNode;
import semantic.types.NullType;
import semantic.types.Type;

import static compiler.Main.symbolTable;

public class VarLocalNode extends SentenceNode{
    private Type type;
    private String id;
    private ExpressionNode value;
    private Token token;
    private Token assignToken;
    private int offset;

    public VarLocalNode(Type type, Token token) {
        this.type = type;
        this.id = token.getLexeme();
        this.token = token;
    }
    public VarLocalNode(Token token, ExpressionNode value, Token assignToken) {
        this.id = token.getLexeme();
        this.value = value;
        this.token = token;
        this.assignToken = assignToken;
    }

    @Override
    public String getLexeme() {
        return assignToken.getLexeme();
    }

    @Override
    public int getLine() {
        return assignToken.getLineNumber();
    }

    public Type getType() {
        return type;
    }
    public String getId() {
        return id;
    }
    @Override
    public boolean isAssign() {
        return true;
    }
    public void check(){
        type = value.check();
        if(value.isAssign())
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": No se puede asignar otra asignacion a una variable local",assignToken.getLexeme(),assignToken.getLineNumber());
        if(type == null || type.getName().equals("null") )
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": No se puede asignar null a una variable local",assignToken.getLexeme(),assignToken.getLineNumber());
        if(type.getName().equals("void"))
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": No se puede asignar void a una variable local",assignToken.getLexeme(),assignToken.getLineNumber());
        if(symbolTable.getCurrentInvocable().getParamList().stream().anyMatch(p -> p.getName().equals(id))){
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": La variable local '" + id + "' ya ha sido declarada como parametro del metodo actual.",token.getLexeme(),token.getLineNumber());
        }
        if(symbolTable.getCurrentBlock().getVarLocalMap().containsKey(id)){
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": La variable local '" + id + "' ya ha sido declarada en el bloque actual.",token.getLexeme(),token.getLineNumber());
        }
        checkNoParentBlocksContainVar();
        symbolTable.getCurrentBlock().addVarLocal(this);
    }
    private void checkNoParentBlocksContainVar(){
        var parentBlock = symbolTable.getCurrentBlock().getParentBlock();
        while(parentBlock != null){
            if(parentBlock.getVarLocalMap().containsKey(id)){
                throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": La variable local '" + id + "' ya ha sido declarada en un bloque padre.",token.getLexeme(),token.getLineNumber());
            }
            parentBlock = parentBlock.getParentBlock();
        }
    }

    public void generateCode() {
        if(value != null){
            value.generateCode();
            symbolTable.instructionList.add("STORE " + offset);
        }
    }

    public int getOffset() {
        return offset;
    }
    public void setOffset(int offset) {
        this.offset = offset;
    }
}
