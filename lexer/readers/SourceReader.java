package lexer.readers;

import java.io.*;

/**
 * This class is used to manage the source program input stream;
 * each read request will return the next usable character; it
 * maintains the source column position of the character
 */
public class SourceReader implements IReader {

    private BufferedReader source;
    // line number of source program
    private int lineNumber = 1;
    // position of last character processed
    private int column = -1;
    private StringBuffer currentLine = new StringBuffer();
    private boolean completedLine = false;

    /**
     * Construct a new SourceReader
     *
     * @param sourceFile the String describing the user's source file
     * @exception IOException is thrown if there is an I/O problem
     */
    public SourceReader(String sourceFile) throws IOException {
        this(new BufferedReader(new FileReader(sourceFile)));
        System.out.println("Source file: " + sourceFile);
        System.out.println("user.dir: " + System.getProperty("user.dir"));
    }

    public SourceReader(BufferedReader reader) throws IOException {
        this.source = reader;
    }

    public void close() {
        try {
            source.close();
        } catch (Exception e) {
            /* no-op */
        }
    }

    private char advance() throws IOException {
        column++;

        int i = source.read();

        if (i == -1) {
            return '\0';
        }
        currentLine.append((char) i);

        return (char) i;
    }

    /**
     * read next char; track line #
     *
     * @return the character just read in
     * @IOException is thrown for IO problems such as end of file
     */
    public char read() {
        try {
            if (completedLine) {
                lineNumber++;
                column = -1;
                completedLine = false;
            }

            char character = advance();

            if (character == '\r') {
                character = advance();
            }

            if (character == '\n') {
                currentLine.delete(0, currentLine.length());
                completedLine = true;
            }

            return character;
        } catch (Exception e) {
            return '\0';
        }
    }

    public int getColumn() {
        return column;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}