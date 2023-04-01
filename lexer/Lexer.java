package lexer;

import lexer.readers.IReader;
import lexer.readers.SourceReader;

/**
 * The Lexer class is responsible for scanning the source file
 * which is a stream of characters and returning a stream of
 * tokens; each token object will contain the string (or access
 * to the string) that describes the token along with an
 * indication of its location in the source program to be used
 * for error reporting; we are tracking line numbers; white spaces
 * are space, tab, newlines
 */
public class Lexer implements ILexer {

    // next character to process
    private char ch;
    private IReader source;
    private int lineNo = 1;

    // positions in line of current token
    private int startPosition, endPosition;

    /**
     * Lexer constructor
     *
     * @param sourceFile is the name of the File to read the program source from
     */
    public Lexer(String sourceFile) throws Exception {
        this(new SourceReader(sourceFile));
    }

    public Lexer(IReader reader) throws Exception {
        TokenType.init();
        this.source = reader;
        nextChar();
    }

    public Token newToken(String tokenString, int start, int end, Tokens type) {
        return new Token(start, end, Symbol.symbol(tokenString, type), lineNo);
    }

    private void nextChar() {
        ch = source.read();
        endPosition++;
    }

    private void scanPastWhitespace() {
        while (Character.isWhitespace(ch) && !eofReached()) {
            if (ch == '\n')
                lineNo++;
            nextChar();
        }
    }

    private Token reservedWordOrIdentifier() {
        String identifier = "";

        do {
            identifier += ch;
            nextChar();
        } while (Character.isJavaIdentifierPart(ch) && !eofReached());

        return newToken(
                identifier,
                startPosition,
                endPosition - 1,
                Tokens.Identifier);
    }

    private Token integer() {
        String number = "";
        if (ch == '0') {
            nextChar();
            if (ch == 'x' || ch == 'X')
                return hexLit();
            number += "0";
        }
        while (Character.isDigit(ch) && !eofReached()) {
            number += ch;
            nextChar();
        }

        return newToken(number, startPosition, endPosition - 1, Tokens.INTeger);
    }

    private Token hexLit() {
        StringBuilder sb = new StringBuilder();
        sb.append("0");
        sb.append(ch);
        //consume x
        nextChar();
        for (int i = 0; i < 6; i++) {
            if ((Character.isDigit(ch) || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F')) && !eofReached()) {
                sb.append(ch);
                nextChar();
            } else
                return error(String.valueOf(ch));
        }
        //if there are more than 6 chars then error.
        if (Character.isDigit(ch) || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F'))
            return error(String.valueOf(ch));
        return newToken(sb.toString(), startPosition, endPosition - 1, Tokens.HexLit);

    }

    private Token stringLiteral() {
        StringBuilder sb = new StringBuilder();
        // consume '@'
        nextChar();
        // collect characters until closing '@' or EOF
        while (ch != '@' && !eofReached()) {
            //multi-line strings are allowed
            if (!Character.isISOControl(ch) || Character.isWhitespace(ch)) {
                // error: string literal cannot contain control characters
                sb.append(ch);
                nextChar();
            }
        }

        //ch might be a control character or EOF
        if (ch != '@') {
            // error: unterminated string literal
            return error("EOF");

        }

        // consume closing '@'
        nextChar();

        return newToken(sb.toString(), startPosition, endPosition - 1, Tokens.StringLit);
    }


    /**
     * Prints out an error string and returns the EOF token to halt lexing
     */
    private Token error(String errorString) {
        System.err.println(
                String.format("******** illegal character: %s", errorString));
        return newToken(null, startPosition, endPosition, Tokens.EOF);
    }

    private void ignoreComment() {
        int oldLine = source.getLineNumber();

        do {
            nextChar();
        } while (oldLine == source.getLineNumber() && !eofReached());
    }

    private boolean eofReached() {
        return ch == '\0';
    }

    private Token singleCharacterOperatorOrSeparator(String character) {
        Symbol symbol = Symbol.symbol(character, Tokens.BogusToken);

        // If symbol is still null, we did not find an operator in the symbol table,
        // and did not encounter the end of file, so this is an error
        if (symbol == null) {
            return error(character);
        } else {
            return newToken(
                    character,
                    startPosition,
                    // -1 since we got next character to test for 2 char operators
                    endPosition - 1,
                    symbol.getKind());
        }
    }

    private Token operatorOrSeparator() {
        String singleCharacter = "" + ch;

        if (eofReached()) {
            return newToken(singleCharacter, startPosition, endPosition, Tokens.EOF);
        }

        // We might have a two character operator, so we need to test for that first
        // by looking ahead one character.
        nextChar();

        String doubleCharacter = singleCharacter + ch;
        Symbol symbol = Symbol.symbol(doubleCharacter, Tokens.BogusToken);

        if (symbol == null) {
            // A two character operator was not found in the symbol table,
            // so this must be a single character operator (or invalid)
            return singleCharacterOperatorOrSeparator(singleCharacter);
        } else if (symbol.getKind() == Tokens.Comment) {
            ignoreComment();
            return nextToken();
        } else {
            // We have a valid, two character operator (advance past second char)
            nextChar();

            return newToken(
                    doubleCharacter,
                    startPosition,
                    endPosition - 1,
                    symbol.getKind());
        }
    }


    /**
     * @return the next Token found in the source file
     */
    public Token nextToken() {
        scanPastWhitespace();

        startPosition = source.getColumn();
        endPosition = startPosition;

        if (Character.isJavaIdentifierStart(ch)) {
            return reservedWordOrIdentifier();
        }

        if (Character.isDigit(ch)) {
            return integer();
        }

        if (ch == '@') {
            return stringLiteral();
        }

        return operatorOrSeparator();
    }

    /**
     * Used by the constrainer to build intrinsic trees
     */
    public Token anonymousIdentifierToken(String identifier) {
        return newToken(identifier, -1, -1, Tokens.Identifier);
    }

    public static void main(String args[]) {
        //Checking if the user provided any command-line argument
        if (args == null || args.length == 0) {
            System.out.println("Usage: java lexer.Lexer filename.x");
            return;
        }
        //The file name which is to be processed
        String fileName = args[0];
        try {
            Lexer lex = new Lexer(fileName);
            Token token = lex.nextToken();

            while (token.getKind() != Tokens.EOF) {
                String p = String.format(
                        "%-11s left: %-8d right: %-8d line: %-8d %s",
                        token.getSymbol(),
                        token.getLeftPosition(),
                        token.getRightPosition(),
                        token.getLineNumber(),
                        token.getKind()
                );

                System.out.println(p);

                token = lex.nextToken();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}