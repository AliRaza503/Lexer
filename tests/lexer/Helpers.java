package tests.lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import lexer.Lexer;
import lexer.TokenType;
import lexer.readers.SourceReader;

public class Helpers {

    public static Lexer getTestLexer(String source)
            throws IOException, Exception {
        TokenType.init();
        return new Lexer(
                new SourceReader(new BufferedReader(new StringReader(source))));
    }

    public static String getTestOutput(String source, String expectedChar) {
        return String.format(
                "******** illegal character: %s\n",
                expectedChar);
    }
}
