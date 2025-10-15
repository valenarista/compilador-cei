package semantic.ast.sentence;

import lexical.Token;
import semantic.ast.expression.ExpressionNode;
import semantic.types.Type;

public class VarLocalNode extends SentenceNode{
    private Type type;
    private String id;
    private ExpressionNode value;
    private Token token;

    public VarLocalNode(Type type, Token token) {
        this.type = type;
        this.id = token.getLexeme();
        this.token = token;
    }
    public VarLocalNode(Token token, ExpressionNode value) {
        this.id = token.getLexeme();
        this.value = value;
        this.token = token;
    }
    public Type getType() {
        return type;
    }
    public String getId() {
        return id;
    }
    public void check(){

    }
}
