package semantic.ast.reference;

import exceptions.SemanticException;
import lexical.Token;
import semantic.ast.chaining.ChainingNode;
import semantic.ast.expression.ExpressionNode;
import semantic.declarable.Method;
import semantic.declarable.Parameter;
import semantic.types.Type;

import java.util.List;

import static compiler.Main.symbolTable;

public class MethodCallNode extends ReferenceNode{
    private Token token;
    private String methodName;
    private ChainingNode optChaining;
    private List<ExpressionNode> argList;

    public MethodCallNode(Token token, String methodName) {
        this.token = token;
        this.methodName = methodName;
    }
    public void setArgList(List<ExpressionNode> argList) {
        this.argList = argList;
    }
    public List<ExpressionNode> getArgList() {
        return argList;
    }
    public Token getToken() {
        return token;
    }
    public String getMethodName() {
        return methodName;
    }

    @Override
    public Type check() {
        Method method = symbolTable.getCurrentClass().getMethods().get(methodName);
        List<Parameter> origArgList = method.getParamList();
        int index = 0;
        if(argList.size() != origArgList.size()){
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": el numero de argumentos no coincide con el numero de parametros esperados.",token.getLexeme(), token.getLineNumber());
        }
        for(ExpressionNode arg : argList){
            Type type = arg.check();
            if(!type.isSubtypeOf(origArgList.get(index).getType())){
                throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": el tipo del argumento " + (index+1) + " no coincide con el tipo del parametro esperado.",token.getLexeme(), token.getLineNumber());
            }
            index++;
        }
        if(optChaining != null) {
            return optChaining.check(method.getReturnType());
        }
        return method.getReturnType();
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
        return true;
    }

    @Override
    public boolean isVariable() {
        return false;
    }
}
