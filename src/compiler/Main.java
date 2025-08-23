package compiler;

import lexical.LexicalAnalyzer;
import sourcemanager.SourceManager;
import sourcemanager.SourceManagerImpl;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String filePath;
        SourceManager sourceManager = new SourceManagerImpl();
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(sourceManager);

    }
}