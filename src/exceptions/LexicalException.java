package exceptions;

public class LexicalException extends RuntimeException {
    private String lexeme;
    private int lineNumber;
    private int columnNumber;
    private String lineText;
    private String reason;

    public LexicalException(String lexeme, int lineNumber, int columnNumber, String lineText, String reason) {
        super(buildErrorMessage(lexeme, lineNumber, reason, columnNumber));
        this.lexeme = lexeme;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.lineText = lineText;
        this.reason = reason;
    }

    public LexicalException(String lexeme, int lineNumber) {
        this(lexeme, lineNumber, -1, "", "simbolo valido");
    }

    private static String buildErrorMessage(String lexeme, int lineNumber, String reason, int columnNumber) {
        return String.format("Error Lexico en linea %d, columna %d: %s %s", lineNumber,columnNumber,lexeme, reason);
    }

    public String getDetailedErrorMessage() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("Error Lexico en linea %d, columna %d: %s  %s\n",
                lineNumber, columnNumber,lexeme, reason));

        if (lineText != null && !lineText.trim().isEmpty()) {
            sb.append("Detalle: ").append(lineText).append("\n");
            if (columnNumber > 0) {
                sb.append("         ");

                int errorPosition = lineText.indexOf(lexeme)+lexeme.length()-1;

                if (errorPosition == -1) {
                    errorPosition = Math.max(0, columnNumber + lexeme.length() - 1);
                }

                for (int i = 0; i < errorPosition; i++) {
                    sb.append(" ");
                }
                sb.append("^");
                sb.append("\n");
            }
        }

        //sb.append(String.format("[Error:%s|%d]", lexeme, lineNumber));

        return sb.toString();
    }

}