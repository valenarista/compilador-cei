package semantic.types;

import lexical.Token;
import static compiler.Main.symbolTable;

public class ReferenceType implements Type{
    private Token classIdName;

    public ReferenceType(Token name) {
        classIdName = name;
    }
    public boolean isPrimitive() {
        return false;
    }

    public String getName() {
        return classIdName.getLexeme();
    }
    public int getLine() {
        return classIdName.getLineNumber();
    }

    @Override
    public boolean isSubtypeOf(Type rightType) {
        if(rightType instanceof ReferenceType rightRefType){
            if(symbolTable.checkCompatibility(this,rightRefType)){
                return true;
            }
        }
        return false;
    }
}
