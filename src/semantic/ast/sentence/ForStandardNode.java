package semantic.ast.sentence;

import exceptions.SemanticException;
import semantic.ast.expression.ExpressionNode;
import semantic.types.Type;

public class ForStandardNode extends SentenceNode{
    private SentenceNode initialization;
    private ExpressionNode condition;
    private ExpressionNode update;
    private SentenceNode body;

    public ForStandardNode(SentenceNode initialization, ExpressionNode condition, ExpressionNode update) {
        this.initialization = initialization;
        this.condition = condition;
        this.update = update;
    }
    @Override
    public void check() {
        initialization.check();
        if(!initialization.isAssign())
            throw new SemanticException("Error semantico en linea " + initialization.getLine() + ": La expresion de inicializacion del ciclo for debe ser una asignacion.",initialization.getLexeme(),initialization.getLine());
        Type conditionType = condition.check();
        if(!conditionType.getName().equals("boolean")){
            throw new SemanticException("Error semantico en linea " + condition.getLine() + ": La condicion del ciclo for debe ser de tipo boolean, pero se encontro tipo " + conditionType.getName(),condition.getLexeme(),condition.getLine());
        }
        update.check();
        if(!update.isAssign())
            throw new SemanticException("Error semantico en linea " + update.getLine() + ": La expresion de actualizacion del ciclo for debe ser una asignacion.",update.getLexeme(),update.getLine());
        body.check();
    }
    public void setBody(SentenceNode body) {
        this.body = body;
    }
}
