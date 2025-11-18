package semantic.ast.reference;

import exceptions.SemanticException;
import lexical.Token;
import semantic.ast.chaining.ChainingNode;
import semantic.ast.expression.ExpressionNode;
import semantic.declarable.Method;
import semantic.declarable.Parameter;
import semantic.entity.EntityClass;
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
        if(method == null){
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": el metodo '" + methodName + "' no esta declarado en la clase '" + symbolTable.getCurrentClass().getName() + "'.",token.getLexeme(), token.getLineNumber());
        }
        if(symbolTable.getCurrentClass().getInheritedMethods().get(methodName)!= null){
            if(!method.isPublic())
                throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": el metodo " + methodName + " es privado y no se puede acceder desde la clase actual.",token.getLexeme(), token.getLineNumber());
        }

        if(symbolTable.getCurrentInvocable().isStaticMethod() && !method.isStaticMethod())
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": no se puede llamar al metodo '" + methodName + "' en un metodo estatico.",token.getLexeme(), token.getLineNumber());

        List<Parameter> origArgList = method.getParamList();
        int index = 0;
        if(argList.size() != origArgList.size()){
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": el numero de argumentos no coincide con el numero de parametros esperados.",token.getLexeme(), token.getLineNumber());
        }
        for(ExpressionNode arg : argList){
            Type type = arg.check();
            if(!(type.isSubtypeOf(origArgList.get(index).getType()))){
                if(!(type.getName().equals("null") && !origArgList.get(index).getType().isPrimitive()))
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
        if(optChaining != null) {
            return optChaining.isVariable();
        }
        return false;
    }

    @Override
    public void generateCode(){
        System.out.println("DEBUG: Generando llamada a " + methodName);
        System.out.println("DEBUG: Cantidad de argumentos: " + argList.size());

        Method method = findMethod(methodName);
        if(method.isStaticMethod()) {
            if(!method.getReturnType().getName().equals("void"))
                symbolTable.instructionList.add("RMEM 1");

            for (int i = 0; i < argList.size(); i++) {
                argList.get(i).generateCode();
            }


            String methodLabel = method.getLabel();
            symbolTable.instructionList.add("PUSH " + methodLabel);
            symbolTable.instructionList.add("CALL");

        } else {
            symbolTable.instructionList.add("LOAD 3");

            if(!method.getReturnType().getName().equals("void")){
                symbolTable.instructionList.add("RMEM 1; Reservando espacio para el valor de retorno");
                symbolTable.instructionList.add("SWAP");
            }

            for (ExpressionNode arg : argList) {
                arg.generateCode();
                symbolTable.instructionList.add("SWAP");
            }

            symbolTable.instructionList.add("DUP");
            symbolTable.instructionList.add("LOADREF 0");
            symbolTable.instructionList.add("LOADREF "+method.getOffset());
            symbolTable.instructionList.add("CALL");

        }
        if(optChaining != null){
            optChaining.generateCode();
        }

    }
    @Override
    public void generateCode(boolean isLeftSide){
        generateCode();
    }
    private Method findMethod(String methodName){
        EntityClass currentClass = symbolTable.getCurrentClass();
        while(currentClass != null){
            if(currentClass.getMethods().containsKey(methodName)){
                return currentClass.getMethods().get(methodName);
            }
            if(currentClass.getHerencia() != null){
                currentClass = symbolTable.getClass(currentClass.getHerencia().getLexeme());
            } else {
                break;
            }
        }
        return null;
    }
    private String findMethodOwnerClass(String methodName){

        String currentClassName = symbolTable.getCurrentClass().getName();
        EntityClass currentClass = symbolTable.getCurrentClass();

        while(currentClass != null){
            if(currentClass.getMethods().containsKey(methodName) &&
                    !currentClass.getInheritedMethods().containsKey(methodName)){
                return currentClass.getName();
            }
            if(currentClass.getHerencia() != null){
                currentClass = symbolTable.getClass(currentClass.getHerencia().getLexeme());
            } else {
                break;
            }
        }
        return currentClassName;
    }
}
