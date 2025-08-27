package sourcemanager;
//Author: Juan Dingevan

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SourceManagerCharImpl implements SourceManager {
    private BufferedReader reader;
    private int lineNumber;
    private int columnNumber;
    private StringBuilder currentLineBuilder;
    private boolean endOfFile;
    private int nextChar;
    private boolean hasNextChar;
    private boolean pendingNewLine;

    public SourceManagerCharImpl() {
        lineNumber = 0;
        columnNumber = 0;
        currentLineBuilder = new StringBuilder();
        endOfFile = false;
        nextChar = -1;
        hasNextChar = false;
        pendingNewLine = false;
    }

    @Override
    public void open(String filePath) throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
        reader = new BufferedReader(inputStreamReader);

        lineNumber = 1;
        columnNumber = 0;
        currentLineBuilder.setLength(0);
        endOfFile = false;
        hasNextChar = false;
        pendingNewLine = false;
    }

    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    @Override
    public char getNextChar() throws IOException {
        if (endOfFile) {
            return END_OF_FILE;
        }

        if (pendingNewLine) {
            lineNumber++;
            columnNumber = 0;
            pendingNewLine = false;
            currentLineBuilder.setLength(0);
        }

        int charCode;

        if (hasNextChar) {
            charCode = nextChar;
            hasNextChar = false;
        } else {
            charCode = reader.read();
        }

        if (charCode == -1) {
            endOfFile = true;
            return END_OF_FILE;
        }

        char currentChar = (char) charCode;

        columnNumber++;

        if (currentChar == '\n') {
            currentLineBuilder.append(currentChar);
            pendingNewLine = true;
        } else if (currentChar == '\r') {
            nextChar = reader.read();
            hasNextChar = true;

            if (nextChar == '\n') {
                currentLineBuilder.append('\r');
                currentLineBuilder.append('\n');
                pendingNewLine = true;
                hasNextChar = false;
                return '\n';
            } else {
                currentLineBuilder.append(currentChar);
                pendingNewLine = true;
                return '\n';
            }
        } else {
            currentLineBuilder.append(currentChar);
        }

        return currentChar;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public int getColumnNumber() {
        return columnNumber;
    }

    @Override
    public String getCurrentLineText() {
        String line = currentLineBuilder.toString();

        if (line.endsWith("\r\n")) {
            return line.substring(0, line.length() - 2);
        } else if (line.endsWith("\n") || line.endsWith("\r")) {
            return line.substring(0, line.length() - 1);
        }

        return line;
    }
}