package semantic.ast.sentence;

import semantic.ast.reference.ConstructorCallNode;
import semantic.declarable.Invocable;
import semantic.entity.EntityClass;
import semantic.types.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static compiler.Main.symbolTable;

public class BlockNode extends SentenceNode{
    private List<SentenceNode> sentences;
    private LinkedHashMap<String,VarLocalNode> varLocalMap;
    private EntityClass nestedInClass;
    private Invocable nestedInInvocable;
    private BlockNode parentBlock;
    private int lastLocalVarOffset = 0;

    public BlockNode() {
        sentences = new ArrayList<>();
        varLocalMap = new LinkedHashMap<>();
        nestedInClass = symbolTable.getCurrentClass();
        nestedInInvocable = symbolTable.getCurrentInvocable();
        parentBlock = null;
    }

    public void addSentence(SentenceNode sentence) {
        sentences.add(sentence);
    }
    public List<SentenceNode> getSentences() {
        return sentences;
    }

    public LinkedHashMap<String, VarLocalNode> getVarLocalMap() {
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

    public int getLine(){
        if(!sentences.isEmpty())
            return sentences.get(0).getLine();
        else
            return 0;
    }
    public String getLexeme(){
        if(!sentences.isEmpty())
            return sentences.get(0).getLexeme();
        else
            return "";
    }

    @Override
    public void check() {
        BlockNode previousBlock = symbolTable.getCurrentBlock();
        Invocable previousInvocable = symbolTable.getCurrentInvocable();
        EntityClass previousClass = symbolTable.getCurrentClass();

        this.parentBlock = previousBlock;
        symbolTable.setCurrentBlock(this);
        symbolTable.setCurrentInvocable(nestedInInvocable);
        if(nestedInClass != null)
            symbolTable.setCurrentClass(nestedInClass.getName(),nestedInClass);

        for (SentenceNode sentence : sentences) {
            sentence.check();
        }

        symbolTable.setCurrentBlock(previousBlock);
        symbolTable.setCurrentInvocable(previousInvocable);
        if(previousClass != null)
            symbolTable.setCurrentClass(previousClass.getName(),previousClass);


    }

    public void generateCode() {
        if(parentBlock!=null)
            lastLocalVarOffset = parentBlock.lastLocalVarOffset;
        BlockNode previousBlock = symbolTable.getCurrentBlock();
        symbolTable.setCurrentBlock(this);
        int localVarCount = 0;
        for (SentenceNode sentence : sentences) {
            if(sentence instanceof VarLocalNode) {
                VarLocalNode varLocal = (VarLocalNode) sentence;
                varLocal.setOffset(lastLocalVarOffset--);
                localVarCount++;
                symbolTable.instructionList.add("RMEM 1");
                sentence.generateCode();
            } else {

                sentence.generateCode();
            }
        }
        if(parentBlock!=null && localVarCount > 0)
            symbolTable.instructionList.add("FMEM " + localVarCount);
        symbolTable.setCurrentBlock(previousBlock);
    }

}
