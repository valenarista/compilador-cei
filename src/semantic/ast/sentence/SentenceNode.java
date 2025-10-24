package semantic.ast.sentence;

abstract public class SentenceNode {
    abstract public void check();
    public boolean isAssign(){
        return false;
    }
    public int getLine(){
        return -1;
    }
    public String getLexeme(){
        return "";
    }
}
