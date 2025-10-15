package semantic.ast.sentence;

import semantic.types.Type;

public class EmptySentenceNode extends SentenceNode{
    @Override
    public Type check() {
        return null;
    }
}
