package semantic.ast.chaining;

import semantic.types.Type;

abstract public class ChainingNode  {
    public Type check(Type check) {
        return null;
    }
    abstract public boolean isOperandWithCall();
}
