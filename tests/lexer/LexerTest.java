package tests.lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;
import lexer.ILexer;
import lexer.Token;
import lexer.Tokens;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class LexerTest {

    private static Stream<Arguments> provideOperatorsAndSeparators() {
        return Stream.of(
                Arguments.of(" + ", Tokens.Plus, 1, 1),
                Arguments.of(" - ", Tokens.Minus, 1, 1),
                Arguments.of(" / ", Tokens.Divide, 1, 1),
                Arguments.of(" * ", Tokens.Multiply, 1, 1),
                Arguments.of(" & ", Tokens.And, 1, 1),
                Arguments.of(" | ", Tokens.Or, 1, 1),
                Arguments.of(" = ", Tokens.Assign, 1, 1),
                Arguments.of(" == ", Tokens.Equal, 1, 2),
                Arguments.of(" != ", Tokens.NotEqual, 1, 2),
                Arguments.of(" < ", Tokens.Less, 1, 1),
                Arguments.of(" <= ", Tokens.LessEqual, 1, 2),
                Arguments.of(" , ", Tokens.Comma, 1, 1),
                Arguments.of(" ( ", Tokens.LeftParen, 1, 1),
                Arguments.of(" ) ", Tokens.RightParen, 1, 1),
                Arguments.of(" { ", Tokens.LeftBrace, 1, 1),
                Arguments.of(" } ", Tokens.RightBrace, 1, 1),
                // We expect EOF in this case because we only have a comment, which should
                // be ignored by the lexer. Lexer would normally reset start position
                // when next token encountered, but here we use string's length
                // (column 4 is where we encounter EOF)
                Arguments.of(" // ", Tokens.EOF, 4, 4));
    }

    @ParameterizedTest
    @MethodSource("provideOperatorsAndSeparators")
    void testOperatorsAndSeparators(
            String source,
            Tokens tokenType,
            int expectedStart,
            int expectedEnd) {
        try {
            ILexer lexer = Helpers.getTestLexer(source);
            Token token = lexer.nextToken();

            assertEquals(tokenType, token.getKind());
            assertEquals(expectedStart, token.getLeftPosition());
            assertEquals(expectedEnd, token.getRightPosition());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static Stream<Arguments> provideKeywords() {
        return Stream.of(
                Arguments.of(" program  ", Tokens.Program, 1, 7),
                Arguments.of(" int  ", Tokens.Int, 1, 3),
                Arguments.of(" boolean  ", Tokens.BOOLean, 1, 7),
                Arguments.of(" if  ", Tokens.If, 1, 2),
                Arguments.of(" then  ", Tokens.Then, 1, 4),
                Arguments.of(" else  ", Tokens.Else, 1, 4),
                Arguments.of(" while  ", Tokens.While, 1, 5),
                Arguments.of(" function  ", Tokens.Function, 1, 8),
                Arguments.of(" return  ", Tokens.Return, 1, 6));
    }

    @ParameterizedTest
    @MethodSource("provideKeywords")
    void testKeywords(
            String source,
            Tokens tokenType,
            int expectedStart,
            int expectedEnd) {
        try {
            ILexer lexer = Helpers.getTestLexer(source);
            Token token = lexer.nextToken();

            assertEquals(tokenType, token.getKind());
            assertEquals(expectedStart, token.getLeftPosition());
            assertEquals(expectedEnd, token.getRightPosition());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    void testIdentifiers() {
        String source = "";
        String[] identifiers = new String[] {
                "_underscore",
                "i",
                "ab",
                "abalamahalamatandra",
        };
        int[][] offsets = new int[identifiers.length][];

        for (int i = 0; i < identifiers.length; i++) {
            source += String.format(" %s", identifiers[i]);

            int start = 1;
            if (i > 0) {
                start = offsets[i - 1][1] + 2;
            }

            offsets[i] = new int[] { start, start + identifiers[i].length() - 1 };
        }

        try {
            ILexer lexer = Helpers.getTestLexer(source);

            for (int i = 0; i < offsets.length; i++) {
                Token token = lexer.nextToken();

                assertEquals(Tokens.Identifier, token.getKind());
                assertEquals(offsets[i][0], token.getLeftPosition());
                assertEquals(offsets[i][1], token.getRightPosition());
                assertTrue(identifiers[i].equals(token.getSymbol().toString()));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    void testIntegers() {
        try {
            ILexer lexer = Helpers.getTestLexer(" 12345 ");
            Token token = lexer.nextToken();

            assertEquals(Tokens.INTeger, token.getKind());
            assertEquals(1, token.getLeftPosition());
            assertEquals(5, token.getRightPosition());
            assertTrue("12345".equals(token.getSymbol().toString()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}