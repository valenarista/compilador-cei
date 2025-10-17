package semantic.ast.expression;

import exceptions.SemanticException;
import semantic.types.Type;

public class AssignNode extends ExpressionNode{
    private ExpressionNode left;
    private ExpressionNode right;

    public AssignNode(ExpressionNode left, ExpressionNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Type check() {
        Type leftType = left.check();
        Type rightType = right.check();

        if(!left.isVariable()){
            throw new SemanticException("Error semantico en linea "+leftType.getLine()+" El operando izquierdo de una asignacion debe ser una variable",leftType.getName(),leftType.getLine());
        }


        if (!leftType.equals(rightType)) {
            throw new SemanticException("Error semantico en linea "+leftType.getLine()+" No se puede asignar un valor de tipo "+rightType.getName()+" a una variable de tipo "+leftType.getName(),leftType.getName(),leftType.getLine());
        }

        return leftType; // The type of the assignment expression is the type of the left side
    }

    @Override
    public int getLine() {
        return left.getLine();
    }

    @Override
    public String getLexeme() {
        return "corregir";
    }


    @Override
    public boolean isVariable() {
        return false;
    }
}
