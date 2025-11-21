package semantic.ast.reference;

import exceptions.SemanticException;
import lexical.Token;
import semantic.ast.chaining.ChainingNode;
import semantic.ast.expression.ExpressionNode;
import semantic.declarable.Method;
import semantic.types.Type;

import java.util.List;
import static compiler.Main.symbolTable;

public class StaticMethodCallNode extends ReferenceNode {
    private ChainingNode optionalChaining;
    private Token classToken;
    private Token methodToken;
    private List<ExpressionNode> argList;
    private boolean needsRMEM = true;

    public StaticMethodCallNode(Token classToken, Token methodToken){
        this.classToken = classToken;
        this.methodToken = methodToken;
    }

    @Override
    public Type check() {
        Type type = checkStaticMethodValidity(classToken, methodToken);
        if(optionalChaining != null){
            return optionalChaining.check(type);
        }
        return type;
    }
    private Type checkStaticMethodValidity(Token classToken, Token methodToken) {
        String className = classToken.getLexeme();
        String methodName = methodToken.getLexeme();

        if(symbolTable.getClass(className)==null){
            throw new SemanticException("Error semantico en linea " + classToken.getLineNumber() + ": la clase '" + className + "' no existe.", classToken.getLexeme(), classToken.getLineNumber());
        }
        if(!symbolTable.getClass(className).getMethods().containsKey(methodName)){
            throw new SemanticException("Error semantico en linea " + methodToken.getLineNumber() + ": el metodo estatico '" + methodName + "' no existe en la clase '" + className + "'.", methodToken.getLexeme(), methodToken.getLineNumber());
        }
        if(!symbolTable.getClass(className).getMethods().get(methodName).isStaticMethod()){
            throw new SemanticException("Error semantico en linea " + methodToken.getLineNumber() + ": el metodo '" + methodName + "' no es estatico.", methodToken.getLexeme(), methodToken.getLineNumber());
        }

        List<semantic.declarable.Parameter> origArgList = symbolTable.getClass(className).getMethods().get(methodName).getParamList();
        int index = 0;
        if(argList.size() != origArgList.size()){
            throw new SemanticException("Error semantico en linea " + methodToken.getLineNumber() + ": el numero de argumentos no coincide con el numero de parametros esperados.", methodToken.getLexeme(), methodToken.getLineNumber());
        }
        for(ExpressionNode arg : argList) {
            Type type = arg.check();
            if (!type.isSubtypeOf(origArgList.get(index).getType())) {
                throw new SemanticException("Error semantico en linea " + methodToken.getLineNumber() + ": el tipo del argumento " + (index + 1) + " no coincide con el tipo del parametro esperado.", methodToken.getLexeme(), methodToken.getLineNumber());
            }
            index++;
        }
        return symbolTable.getClass(className).getMethods().get(methodName).getReturnType();
    }


    @Override
    public void setOptChaining(ChainingNode chainingNode) {
        optionalChaining = chainingNode;
    }

    public List<ExpressionNode> getArgList() {
        return argList;
    }
    public void setArgList(List<ExpressionNode> argList) {
        this.argList = argList;
    }

    @Override
    public int getLine() {
        return classToken.getLineNumber();
    }

    @Override
    public String getLexeme() {
        return methodToken.getLexeme();
    }

    @Override
    public boolean isAssign() {
        return false;
    }

    @Override
    public boolean isOperandWithCall() {
        if(optionalChaining != null) {
            return optionalChaining.isOperandWithCall();
        }
        return true;
    }
    public boolean isStaticCall(){
        return true;
    }
    public void setNeedsRMEM(boolean needsRMEM){
        this.needsRMEM = needsRMEM;
    }

    @Override
    public void generateCode() {
        String methodName = methodToken.getLexeme();
        Method method = symbolTable.getClass(classToken.getLexeme()).getMethods().get(methodName);
        if(!method.getReturnType().getName().equals("void") && needsRMEM){
            System.out.println("LIBERANDO MEMORIA PARA LLAMADA A METODO ESTATICO");
            symbolTable.instructionList.add("RMEM 1");
        }
        for(ExpressionNode arg : argList){
            arg.generateCode();
        }

        symbolTable.instructionList.add("PUSH "+ method.getLabel());
        symbolTable.instructionList.add("CALL");

        if(optionalChaining != null){
            optionalChaining.generateCode();
        }

    }

    @Override
    public Token getToken() {
        return methodToken;
    }

    @Override
    public void generateCode(boolean isLeftSide){
        generateCode();
    }
    @Override
    public boolean isVariable() {
        if(optionalChaining != null) {
            return optionalChaining.isVariable();
        }
        return false;
    }
}
