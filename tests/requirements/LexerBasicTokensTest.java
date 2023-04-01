package tests.requirements;

// Spring 2023 Tests
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import lexer.Lexer;
import lexer.Token;
import lexer.Tokens;
import tests.lexer.Helpers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class LexerBasicTokensTest {
    private static Stream<Arguments> provideBasicTokenTests() {
        return Stream.of(
                Arguments.of(" > ", Tokens.Greater),
                Arguments.of(" >= ", Tokens.GreaterEqual),
                Arguments.of(" [ ", Tokens.LeftBracket),
                Arguments.of(" ] ", Tokens.RightBracket),
                Arguments.of(" % ", Tokens.Modulo),
                Arguments.of(" -> ", Tokens.Arrow),
                Arguments.of(" string ", Tokens.StringType),
                Arguments.of(" hex ", Tokens.HexType),
                Arguments.of(" unless ", Tokens.Unless),
                Arguments.of(" select ", Tokens.Select));
    }

    @ParameterizedTest
    @MethodSource("provideBasicTokenTests")
    void testBasicTokens(String source, Tokens tokenType) {
        try {
            Lexer lexer = Helpers.getTestLexer(source);
            Token token = lexer.nextToken();

            assertEquals(tokenType, token.getKind());
            assertEquals(1, token.getLineNumber());
            assertEquals(source.trim(), token.getSymbol().toString());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}