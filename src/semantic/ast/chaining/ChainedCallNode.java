package semantic.ast.chaining;

import exceptions.SemanticException;
import lexical.Token;
import semantic.ast.expression.ExpressionNode;
import semantic.declarable.Method;
import semantic.declarable.Parameter;
import semantic.types.Type;

import java.util.List;
import static compiler.Main.symbolTable;

public class ChainedCallNode extends ChainingNode{
    private Token token;
    private String methodName;
    private ChainingNode optionalChaining;
    private List<ExpressionNode> argList;

    public ChainedCallNode(Token token, String methodName) {
        this.token = token;
        this.methodName = methodName;

    }

    @Override
    public boolean isOperandWithCall() {
        if(optionalChaining != null) {
            return optionalChaining.isOperandWithCall();
        }
        return true;
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    public void setArgList(List<ExpressionNode> list) {
        this.argList = list;
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


    public Type check(Type previousType) {
        notPrimitiveCalling(previousType);
        Method existingMethod = checkMethodExistence(previousType, methodName, token);
        int index = 0;
        List<Parameter> origArgList = existingMethod.getParamList();
        if(argList.size() != origArgList.size()){
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": el numero de argumentos no coincide con el numero de parametros esperados.",token.getLexeme(), token.getLineNumber());
        }
        for(ExpressionNode arg : argList) {
            Type type = arg.check();
            if (!type.isSubtypeOf(origArgList.get(index).getType())) {
                throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": el tipo del argumento " + (arg.getLexeme()) + " no coincide con el tipo del parametro esperado.", token.getLexeme(), token.getLineNumber());
            }
            index++;
        }
        Type type = existingMethod.getReturnType();

        if(optionalChaining != null){
            return optionalChaining.check(type);
        }
        return type;
    }

    public void notPrimitiveCalling(Type callingType){
        if(callingType.isPrimitive())
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": no se puede realizar una llamada a metodo en un tipo primitivo.",token.getLexeme(), token.getLineNumber());
    }
    public Method checkMethodExistence(Type callingType, String methodName, Token token){
        String className = callingType.getName();
        if(!symbolTable.getClass(className).getMethods().containsKey(methodName)){
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": la clase " + className + " no contiene el metodo " + methodName + ".",token.getLexeme(), token.getLineNumber());
        }
        return symbolTable.getClass(className).getMethods().get(methodName);
    }
}
