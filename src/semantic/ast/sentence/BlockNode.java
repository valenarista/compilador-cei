package semantic.ast.sentence;

import semantic.types.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockNode extends SentenceNode{
    private List<SentenceNode> sentences;
    private HashMap<String,VarLocalNode> varLocalMap;

    public BlockNode() {
        sentences = new ArrayList<>();
        varLocalMap = new HashMap<>();
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
    public void setSentencesList(List<SentenceNode> sentences) {
        this.sentences = sentences;
    }
    public void addVarLocal(VarLocalNode varLocal) {
        varLocalMap.put(varLocal.getId(), varLocal);
    }

    @Override
    public Type check() {
        for (SentenceNode sentence : sentences) {
            sentence.check();
        }
        return null;
    }
}
