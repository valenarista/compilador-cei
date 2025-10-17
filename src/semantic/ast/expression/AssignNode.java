package semantic.ast.expression;

import exceptions.SemanticException;
import lexical.Token;
import semantic.types.Type;

public class AssignNode extends ExpressionNode{
    private ExpressionNode left;
    private ExpressionNode right;
    private Token token;
    private Type type;

    public AssignNode(ExpressionNode left, ExpressionNode right,Token token) {
        this.left = left;
        this.right = right;
        this.token = token;
    }

    @Override
    public Type check() {
        Type leftType = left.check();
        type = leftType;
        Type rightType = right.check();

        if(!left.isVariable()){
            throw new SemanticException("Error semantico en linea "+getLine()+" El operando izquierdo de una asignacion debe ser una variable",getLexeme(),getLine());
        }


        if (!leftType.getName().equals(rightType.getName())) {
            throw new SemanticException("Error semantico en linea "+getLine()+" No se puede asignar un valor de tipo "+rightType.getName()+" a una variable de tipo "+leftType.getName(),getLexeme(),getLine());
        }

        return leftType; // The type of the assignment expression is the type of the left side
    }

    @Override
    public int getLine() {
        return token.getLineNumber();
    }
    public Type getType(){
        return type;
    }

    @Override
    public String getLexeme() {
        return token.getLexeme();
    }

    @Override
    public boolean isAssign() {
        return true;
    }

    @Override
    public boolean isOperandWithCall() {
        return false;
    }


    @Override
    public boolean isVariable() {
        return false;
    }
}
