package semantic.ast.sentence;

import java.util.ArrayList;
import java.util.List;

public class BlockNode extends SentenceNode{
    List<SentenceNode> sentences;
    public BlockNode() {
        sentences = new ArrayList<>();
    }
    public void addSentence(SentenceNode sentence) {
        sentences.add(sentence);
    }
    public List<SentenceNode> getSentences() {
        return sentences;
    }

}
