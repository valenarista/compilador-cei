package semantic.ast.sentence;


import exceptions.SemanticException;
import exceptions.SyntacticException;
import lexical.Token;
import semantic.ast.expression.ExpressionNode;
import semantic.declarable.Method;
import semantic.types.Type;
import static compiler.Main.symbolTable;

public class ReturnNode extends SentenceWithExpressionNode{
    private Type expectedType;
    private ExpressionNode expressionNode;
    private Token finalToken;

    public ReturnNode(ExpressionNode expressionNode){
        super(expressionNode);
        this.expressionNode = expressionNode;
        if (!(symbolTable.getCurrentInvocable() instanceof Method)) {
            throw new SemanticException("Error semantico en linea " + expressionNode.getLine() + ": Sentencia return fuera de metodo",expressionNode.getLexeme(),expressionNode.getLine());
        }
        expectedType = ((Method) symbolTable.getCurrentInvocable()).getReturnType();
    }

    @Override
    public void check() {
        Type returnType = expressionNode.check();
        if(expectedType.isPrimitive())
            if (!returnType.getName().equals(expectedType.getName())) {
                if(returnType.getName().equals("void"))
                    throw new SemanticException("Error semantico en linea " + expressionNode.getLine() + ": Sentencia return sin valor en metodo con retorno " + expectedType.getName(),finalToken.getLexeme(),finalToken.getLineNumber());
                throw new SemanticException("Error semantico en linea " + expressionNode.getLine() + ": Tipo de retorno incorrecto. Se esperaba " + expectedType.getName() + " pero se obtuvo " + returnType.getName(),finalToken.getLexeme(),finalToken.getLineNumber());
            }
        if (!returnType.isSubtypeOf(expectedType)) {
            if(!(returnType.getName().equals("null") && !expectedType.isPrimitive()))
                throw new SemanticException("Error semantico en linea " + expressionNode.getLine() + ": Tipo de retorno incorrecto. Se esperaba " + expectedType.getName() + " pero se obtuvo " + returnType.getName(), finalToken.getLexeme(), finalToken.getLineNumber());
        }
    }
    public void setFinalToken(Token finalToken) {
        this.finalToken = finalToken;
    }
    public String getLexeme(){
        return finalToken.getLexeme();
    }
    public int getLine(){
        return finalToken.getLineNumber();
    }
}
