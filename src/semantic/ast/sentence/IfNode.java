package semantic.ast.sentence;

import exceptions.SemanticException;
import semantic.ast.expression.ExpressionNode;
import semantic.types.BooleanType;
import semantic.types.Type;

import static compiler.Main.symbolTable;

public class IfNode extends SentenceNode{
    private ExpressionNode condition;
    private SentenceNode body;
    private SentenceNode elseBody;
    private static int labelCounter  = 0;

    public IfNode(ExpressionNode condition,SentenceNode body,SentenceNode elseBody){
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
    }

    @Override
    public void check() {
        if(!condition.check().getName().equals(new BooleanType().getName()))
            throw new SemanticException("Error semantico en linea "+condition.getLine()+" La condicion de un if debe ser de tipo booleano",condition.getLexeme(),condition.getLine());
        body.check();
        elseBody.check();
    }
    public void setBody(SentenceNode body) {
        this.body = body;
    }
    public SentenceNode getBody() {
        return body;
    }
    public SentenceNode getElseBody() {
        return elseBody;
    }
    public void generateCode(){
        int currentLabel = labelCounter++;
        String elseLabel = "else_" + currentLabel;
        String endIfLabel = "end_if_" + currentLabel;

        condition.generateCode();
        if(!(elseBody instanceof EmptySentenceNode)) {
            symbolTable.instructionList.add("BF "+elseLabel);

            body.generateCode();

            boolean bodyEndsInReturn = bodyEndsInReturn(body);
            boolean elseBodyEndsInReturn = bodyEndsInReturn(elseBody);

            if(!bodyEndsInReturn || !elseBodyEndsInReturn) {
                symbolTable.instructionList.add("JUMP " + endIfLabel);
            }

            symbolTable.instructionList.add(elseLabel+":");

            elseBody.generateCode();

            if(!bodyEndsInReturn || !elseBodyEndsInReturn) {
                symbolTable.instructionList.add(endIfLabel + ":");
            }
        } else {
            symbolTable.instructionList.add("BF "+endIfLabel);
            body.generateCode();
            symbolTable.instructionList.add(endIfLabel + ":");
        }
    }
    private boolean bodyEndsInReturn(SentenceNode body) {
        if (body instanceof ReturnNode) {
            return true;
        } else if (body instanceof BlockNode) {
            BlockNode block = (BlockNode) body;
            if (!block.getSentences().isEmpty()) {
                SentenceNode lastSentence = block.getSentences().get(block.getSentences().size() - 1);
                return bodyEndsInReturn(lastSentence);
            }
        }
        return false;
    }

}
