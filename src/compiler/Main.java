package compiler;

import exceptions.LexicalException;
import exceptions.SyntacticException;
import lexical.LexicalAnalyzerMultiDetect;
import sourcemanager.SourceManager;
import sourcemanager.SourceManagerCharImpl;
import syntactic.SyntacticAnalyzer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        String filePath;
        SourceManager sourceManager = new SourceManagerCharImpl();
        LexicalAnalyzerMultiDetect lexicalAnalyzer = null;
        SyntacticAnalyzer syntacticAnalyzer;

        if (args.length > 0) {
            try{
                filePath = args[0];
                sourceManager.open(filePath);
                lexicalAnalyzer = new LexicalAnalyzerMultiDetect(sourceManager);
                syntacticAnalyzer = new SyntacticAnalyzer(lexicalAnalyzer);

                syntacticAnalyzer.inicial();

                if (!lexicalAnalyzer.getErrors().isEmpty()) {
                    for(LexicalException error : lexicalAnalyzer.getErrors()){
                        System.out.println(error.getDetailedErrorMessage());
                    }
                } else {
                    System.out.println("[SinErrores]");
                }


            } catch (SyntacticException e) {
                if(lexicalAnalyzer!=null && !lexicalAnalyzer.getErrors().isEmpty()){
                    for(LexicalException error : lexicalAnalyzer.getErrors()){
                        System.out.println(error.getDetailedErrorMessage());
                    }
                }
                System.out.println( e.getMessage() );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}