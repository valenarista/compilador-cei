package semantic.ast.sentence;

import semantic.declarable.Invocable;
import semantic.entity.EntityClass;
import semantic.types.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static compiler.Main.symbolTable;

public class BlockNode extends SentenceNode{
    private List<SentenceNode> sentences;
    private HashMap<String,VarLocalNode> varLocalMap;
    private EntityClass nestedInClass;
    private Invocable nestedInInvocable;
    private BlockNode parentBlock;

    public BlockNode() {
        sentences = new ArrayList<>();
        varLocalMap = new HashMap<>();
        nestedInClass = symbolTable.getCurrentClass();
        nestedInInvocable = symbolTable.getCurrentInvocable();
        parentBlock = symbolTable.getCurrentBlock();
    }

    public void addSentence(SentenceNode sentence) {
        sentences.add(sentence);
    }
    public List<SentenceNode> getSentences() {
        return sentences;
    }

    public HashMap<String, VarLocalNode> getVarLocalMap() {
        return varLocalMap;
    }
    public void setParentBlock(BlockNode parentBlock) {
        this.parentBlock = parentBlock;
    }
    public BlockNode getParentBlock() {
        return parentBlock;
    }
    public void setSentencesList(List<SentenceNode> sentences) {
        this.sentences = sentences;
    }
    public void addVarLocal(VarLocalNode varLocal) {
        varLocalMap.put(varLocal.getId(), varLocal);
    }

    @Override
    public void check() {
        symbolTable.setCurrentBlock(this);
        symbolTable.setCurrentInvocable(nestedInInvocable);
        symbolTable.setCurrentClass(nestedInClass.getName(),nestedInClass);

        for (SentenceNode sentence : sentences) {
            sentence.check();
        }
    }
}
