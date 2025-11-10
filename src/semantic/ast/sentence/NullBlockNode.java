package semantic.ast.sentence;

import semantic.types.Type;

public class NullBlockNode extends BlockNode {
    public NullBlockNode() {
        super();
    }
    @Override
    public void check() {
        // Nothing to check in a null block
    }
}

