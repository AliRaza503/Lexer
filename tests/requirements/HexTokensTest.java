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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class HexTokensTest {
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    //changed System.setOut to System.setErr
    @BeforeEach
    public void setUp() {
        System.setErr(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    //Changed 2nd and 5th argument (discarded the ending g symbol)
    private static Stream<Arguments> provideValidTokenSources() {
        return Stream.of(
                Arguments.of("0x123456"),
                Arguments.of("0xabcdef"),
                Arguments.of("0x1A2b3C"),
                Arguments.of("0X123456"),
                Arguments.of("0Xabcdef"),
                Arguments.of("0X1A2b3C"));
    }

    @ParameterizedTest
    @MethodSource("provideValidTokenSources")
    void testValidHexTokens(String source) {
        try {
            Lexer lexer = Helpers.getTestLexer(String.format(" %s ", source));
            Token token = lexer.nextToken();

            assertEquals(Tokens.HexLit, token.getKind());
            assertEquals(source, token.getSymbol().toString());
            assertEquals(1, token.getLeftPosition());
            assertEquals(source.length(), token.getRightPosition());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static Stream<Arguments> provideInvalidTokenSources() {
        return Stream.of(
                Arguments.of("0xX123456", "X"),
                Arguments.of("0xx123456", "x"),
                Arguments.of("0xg12345", "g"),
                Arguments.of("0x12345g", "g"),
                Arguments.of("0x1234567", "7"),
                Arguments.of("0x12345 ", " "));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidTokenSources")
    void testInvalidTokens(String source, String expectedChar) {
        try {
            Lexer lexer = Helpers.getTestLexer(String.format(" %s ", source));

            lexer.nextToken();
            assertEquals(
                    Helpers.getTestOutput(source, expectedChar),
                    outputStreamCaptor.toString());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}