package compiler;

import exceptions.LexicalException;
import lexical.LexicalAnalyzer;
import lexical.LexicalAnalyzerMultiDetect;
import lexical.Token;
import lexical.TokenType;
import sourcemanager.SourceManager;
import sourcemanager.SourceManagerCharImpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class Main {
    public static void main(String[] args) {

        String filePath;
        SourceManager sourceManager = new SourceManagerCharImpl();
        LexicalAnalyzerMultiDetect lexicalAnalyzer;

        if (args.length > 0) {
            try{
                filePath = args[0];
                sourceManager.open(filePath);
                lexicalAnalyzer = new LexicalAnalyzerMultiDetect(sourceManager);
                Token token;
                do {
                    token = lexicalAnalyzer.getNextToken();
                    System.out.println(token.toString());
                } while (token.getType() != TokenType.eof);
                for(LexicalException errors : lexicalAnalyzer.getErrors()){
                    System.out.println(errors.getDetailedErrorMessage());
                }
                if(lexicalAnalyzer.getErrors().isEmpty())
                    System.out.println("[SinErrores]");
            } catch (LexicalException e) {
                System.out.println( e.getDetailedErrorMessage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}