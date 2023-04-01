package tests.requirements;

// Spring 2023 Tests
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.stream.Stream;
import lexer.Lexer;
import lexer.Token;
import lexer.Tokens;
import tests.lexer.Helpers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class StringTokensTest {
    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setErr(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardErr);
    }

    private static Stream<Arguments> provideValidTokenSources() {
        return Stream.of(
                Arguments.of("@@", ""),
                Arguments.of("@a@", "a"),
                Arguments.of("@0xabcdefg@", "0xabcdefg"),
                Arguments.of("@1.12E+123@", "1.12E+123"),
                Arguments.of(
                        "@supercalifragilisticexpealadocious@",
                        "supercalifragilisticexpealadocious"),
                Arguments.of("@string with spaces@", "string with spaces"),
                Arguments.of("@multiline\nstring\n\n1234@", "multiline\nstring\n\n1234"));
    }

    @ParameterizedTest
    @MethodSource("provideValidTokenSources")
    void testValidStringTokens(String source, String expected) {
        try {
            Lexer lexer = Helpers.getTestLexer(String.format(" %s ", source));
            Token token = lexer.nextToken();

            assertEquals(Tokens.StringLit, token.getKind());
            assertEquals(expected, token.getSymbol().toString());
            assertEquals(1, token.getLeftPosition());
            assertEquals(source.length(), token.getRightPosition());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    void testMissingDelimiter() {
        try {
            String source = "@asdfasdf";
            Lexer lexer = Helpers.getTestLexer(String.format(" %s ", source));

            lexer.nextToken();
            // This test may be wrong; validate
            assertEquals(
                    String.format(
                            "******** illegal character: %s%s",
                            Tokens.EOF,
                            System.lineSeparator()),
                    outputStreamCaptor.toString());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
