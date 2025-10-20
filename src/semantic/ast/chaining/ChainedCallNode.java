package semantic.ast.chaining;

import exceptions.SemanticException;
import lexical.Token;
import semantic.ast.expression.ExpressionNode;
import semantic.ast.reference.MethodCallNode;
import semantic.types.Type;

import java.util.List;
import static compiler.Main.symbolTable;

public class ChainedCallNode extends ChainingNode{
    private Token token;
    private String methodName;
    private ChainingNode optionalChaining;
    private MethodCallNode methodCallNode;

    public ChainedCallNode(Token token, String methodName) {
        this.token = token;
        this.methodName = methodName;
        methodCallNode = new MethodCallNode(token, methodName);
    }

    @Override
    public boolean isOperandWithCall() {
        if(optionalChaining != null) {
            return optionalChaining.isOperandWithCall();
        }
        return true;
    }
    public void setArgList(List<ExpressionNode> list) {
        methodCallNode.setArgList(list);
    }

    public Token getToken() {
        return token;
    }


    public ChainingNode getOptionalChaining() {
        return optionalChaining;
    }

    public void setOptChaining(ChainingNode chainingNode) {
        this.optionalChaining = chainingNode;
    }

    public MethodCallNode getMethodCallNode() {
        return methodCallNode;
    }
    public void setMethodCallNode(MethodCallNode methodCallNode) {
        this.methodCallNode = methodCallNode;
    }

    public Type check(Type previousType) {
        notPrimitiveCalling(previousType);
        checkMethodExistence(previousType, methodName, token);
        Type type = methodCallNode.check();

        if(optionalChaining != null){
            return optionalChaining.check(type);
        }
        return type;
    }

    public void notPrimitiveCalling(Type callingType){
        if(callingType.isPrimitive())
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": no se puede realizar una llamada a metodo en un tipo primitivo.",token.getLexeme(), token.getLineNumber());
    }
    public void checkMethodExistence(Type callingType, String methodName, Token token){
        String className = callingType.getName();
        if(!symbolTable.getClass(className).getMethods().containsKey(methodName)){
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": la clase " + className + " no contiene el metodo " + methodName + ".",token.getLexeme(), token.getLineNumber());
        }
    }
}
