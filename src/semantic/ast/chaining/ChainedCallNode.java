package semantic.ast.chaining;

import exceptions.SemanticException;
import lexical.Token;
import semantic.ast.expression.ExpressionNode;
import semantic.declarable.Method;
import semantic.declarable.Parameter;
import semantic.entity.EntityClass;
import semantic.types.Type;

import java.util.List;
import static compiler.Main.symbolTable;

public class ChainedCallNode extends ChainingNode{
    private Token token;
    private String methodName;
    private ChainingNode optionalChaining;
    private List<ExpressionNode> argList;
    private Method cachedMethod;
    private Type cachedCallingType;

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
        if(optionalChaining != null) {
            return optionalChaining.isVariable();
        }
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
        this.cachedCallingType = previousType;

        Method existingMethod = checkMethodExistence(previousType, methodName, token);
        this.cachedMethod = existingMethod;

        if(!symbolTable.getCurrentClass().getName().equals(previousType.getName()) && !existingMethod.isPublic())
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": el metodo " + methodName + " es privado y no se puede acceder desde el metodo actual.",token.getLexeme(), token.getLineNumber());
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

    @Override
    public void generateCode() {
        System.out.println("DEBUG ChainedCallNode.generateCode() para: " + methodName);
        System.out.println("  -> optionalChaining es null? " + (optionalChaining == null));
        if(optionalChaining != null) {
            System.out.println("  -> optionalChaining tipo: " + optionalChaining.getClass().getSimpleName());
        }
        if(cachedMethod == null) {
            throw new RuntimeException("Error interno: método no fue cacheado durante check() para " + methodName);
        }

        System.out.println("DEBUG ChainedCallNode: Generando llamada encadenada a " + methodName);
        System.out.println("  -> Tipo del objeto: " + (cachedCallingType != null ? cachedCallingType.getName() : "null"));
        System.out.println("  -> Offset del método: " + cachedMethod.getOffset());

        System.out.println("DEBUG: Llamando a método " + methodName + " con offset " + cachedMethod.getOffset() + " de clase " + cachedCallingType.getName());
        System.out.println("DEBUG: Label del método: " + cachedMethod.getLabel());

        if(cachedMethod.isStaticMethod()){
            System.out.println("  -> ENTRANDO A BRANCH ESTÁTICO");

            symbolTable.instructionList.add("POP");

            if(!cachedMethod.getReturnType().getName().equals("void")) {
                symbolTable.instructionList.add("RMEM 1");
            }
            for(ExpressionNode arg : argList) {
                arg.generateCode();
            }
            symbolTable.instructionList.add("PUSH " + cachedMethod.getLabel());
            symbolTable.instructionList.add("CALL");
        }else{
            System.out.println("  -> ENTRANDO A BRANCH DE INSTANCIA");

            boolean hasReturnValue = !cachedMethod.getReturnType().getName().equals("void");
            boolean needsRMEM = hasReturnValue && !hasChainedCallWithReturn();

            if(needsRMEM) {
                symbolTable.instructionList.add("RMEM 1");
                symbolTable.instructionList.add("SWAP");
            }

            for(ExpressionNode arg : argList) {
                arg.generateCode();
                symbolTable.instructionList.add("SWAP");
            }

            symbolTable.instructionList.add("DUP");
            symbolTable.instructionList.add("LOADREF 0 ");
            symbolTable.instructionList.add("LOADREF " + cachedMethod.getOffset());
            symbolTable.instructionList.add("CALL");

        }

        if(optionalChaining != null) {
            optionalChaining.generateCode();

        }
    }
    private boolean hasChainedCallWithReturn() {
        System.out.println("DEBUG hasChainedCallWithReturn:");
        System.out.println("  -> optionalChaining: " + optionalChaining);
        System.out.println("  -> optionalChaining instanceof ChainedCallNode: " + (optionalChaining instanceof ChainedCallNode));

        if(optionalChaining instanceof ChainedCallNode) {
            ChainedCallNode next = (ChainedCallNode) optionalChaining;
            System.out.println("  -> next.cachedMethod: " + next.cachedMethod);
            System.out.println("  -> next.methodReturnsValue(): " + next.methodReturnsValue());
            return next.methodReturnsValue();
        }
        return false;
    }

    @Override
    public void generateCode(boolean isLeftSide) {
        if(cachedMethod == null) {
            throw new RuntimeException("Error interno: método no fue cacheado durante check() para " + methodName);
        }

        System.out.println("DEBUG ChainedCallNode: Generando llamada encadenada a " + methodName);
        System.out.println("  -> Tipo del objeto: " + (cachedCallingType != null ? cachedCallingType.getName() : "null"));
        System.out.println("  -> Offset del método: " + cachedMethod.getOffset());

        if(cachedMethod.isStaticMethod()){
            System.out.println("  -> ENTRANDO A BRANCH ESTÁTICO con "+isLeftSide);

            symbolTable.instructionList.add("POP");

            if(!cachedMethod.getReturnType().getName().equals("void")) {
                symbolTable.instructionList.add("RMEM 1");
            }
            for(ExpressionNode arg : argList) {
                arg.generateCode();
            }
            symbolTable.instructionList.add("PUSH " + cachedMethod.getLabel());
            symbolTable.instructionList.add("CALL");
        } else {
            System.out.println("  -> ENTRANDO A BRANCH DE INSTANCIA con "+isLeftSide);
            if (!cachedMethod.getReturnType().getName().equals("void")) {
                symbolTable.instructionList.add("RMEM 1");
                symbolTable.instructionList.add("SWAP");
            }

            for (ExpressionNode arg : argList) {
                arg.generateCode();
                symbolTable.instructionList.add("SWAP");
            }

            symbolTable.instructionList.add("DUP");
            symbolTable.instructionList.add("LOADREF 0 ");
            symbolTable.instructionList.add("LOADREF " + cachedMethod.getOffset());
            symbolTable.instructionList.add("CALL");
        }
        if(optionalChaining != null) {
            optionalChaining.generateCode(isLeftSide);
        }
    }
    public boolean methodReturnsValue() {
        if(cachedMethod != null) {
            return !cachedMethod.getReturnType().getName().equals("void");
        }
        return false;
    }

}
