package semantic.ast.sentence;


import exceptions.SemanticException;
import semantic.ast.expression.ExpressionNode;
import semantic.types.BooleanType;
import semantic.types.Type;
import static compiler.Main.symbolTable;

public class WhileNode extends SentenceNode{
    private ExpressionNode condition;
    private SentenceNode body;
    private static int labelCounter  = 0;
    public WhileNode(ExpressionNode condition, SentenceNode body) {
        this.condition = condition;
        this.body = body;
    }
    @Override
    public void check() {
        Type condType = condition.check();
        if (condType == null || !condType.getName().equals(new BooleanType().getName())) {
            throw new SemanticException("Error semantico en linea " + condition.getLine() + ": La condicion del while debe ser de tipo booleano",condition.getLexeme(),condition.getLine());
        }
        body.check();
    }

    public void setBody(SentenceNode body) {
        this.body = body;
    }
    public SentenceNode getBody() {
        return body;
    }
    public ExpressionNode getCondition() {
        return condition;
    }
    public void generateCode(){
        int currentLabel = labelCounter++;
        String startWhileLabel = "start_while_" + currentLabel;
        String endWhileLabel = "end_while_" + currentLabel;

        System.out.println("DEBUG WhileNode: Generando while #" + currentLabel);
        System.out.println("  -> startWhileLabel: " + startWhileLabel);
        System.out.println("  -> endWhileLabel: " + endWhileLabel);

        System.out.println("  -> Generando etiqueta " + startWhileLabel + ":");
        symbolTable.instructionList.add(startWhileLabel+":");

        condition.generateCode();

        System.out.println("  -> Generando BF a " + endWhileLabel);
        symbolTable.instructionList.add("BF "+endWhileLabel);

        body.generateCode();

        System.out.println("  -> Generando JUMP a " + startWhileLabel);
        symbolTable.instructionList.add("JUMP "+startWhileLabel);

        System.out.println("  -> Generando etiqueta " + endWhileLabel + ":");
        symbolTable.instructionList.add(endWhileLabel+":");
    }
}
