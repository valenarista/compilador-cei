package semantic.ast.sentence;

import lexical.Token;
import semantic.types.Type;

public class EmptySentenceNode extends SentenceNode{
    Token token;
    public EmptySentenceNode() {
    }
    public EmptySentenceNode(Token token) {
        this.token = token;
    }
    @Override
    public void check() {

    }

    @Override
    public String getLexeme() {
        return token.getLexeme();
    }
    public int getLine(){
        return token.getLineNumber();
    }
}
