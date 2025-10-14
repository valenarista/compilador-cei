package semantic.declarable;

import lexical.Token;
import semantic.ast.sentence.BlockNode;

import java.util.List;

public interface Invocable {
    String getName();
    void setBlock(BlockNode block);
    BlockNode getBlock();
    int getLine();
    void estaBienDeclarado();
    List<Parameter> getParamList();
    Token getVisibility();
    void addParameter(Parameter param);

}
