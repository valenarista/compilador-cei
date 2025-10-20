package semantic.ast.sentence;


import exceptions.SemanticException;
import exceptions.SyntacticException;
import semantic.ast.expression.ExpressionNode;
import semantic.declarable.Method;
import semantic.types.Type;
import static compiler.Main.symbolTable;

public class ReturnNode extends SentenceWithExpressionNode{
    private Type expectedType;
    private ExpressionNode expressionNode;

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
        if (!returnType.getName().equals(expectedType.getName())) {
            throw new SemanticException("Error semantico en linea " + expressionNode.getLine() + ": Tipo de retorno incorrecto. Se esperaba " + expectedType + " pero se obtuvo " + returnType,expressionNode.getLexeme(),expressionNode.getLine());
        }
    }
}
