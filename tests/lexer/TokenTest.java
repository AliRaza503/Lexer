package tests.lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lexer.Symbol;
import lexer.Token;
import lexer.Tokens;
import org.junit.jupiter.api.Test;

public class TokenTest {

    private static final int LEFT_POSITION = 7, RIGHT_POSITION = 42;

    @Test
    void testGetKind() {
        Token token = new Token(
                LEFT_POSITION,
                RIGHT_POSITION,
                Symbol.symbol("+", Tokens.Plus)
        );

        assertTrue(Tokens.Plus == token.getSymbol().getKind());
    }

    @Test
    void testGetLeftPosition() {
        Token token = new Token(
                LEFT_POSITION,
                RIGHT_POSITION,
                Symbol.symbol("+", Tokens.Plus)
        );

        assertEquals(LEFT_POSITION, token.getLeftPosition());
    }

    @Test
    void testGetRightPosition() {
        Token token = new Token(
                LEFT_POSITION,
                RIGHT_POSITION,
                Symbol.symbol("+", Tokens.Plus)
        );

        assertEquals(RIGHT_POSITION, token.getRightPosition());
    }

    @Test
    void testGetSymbol() {
        Token token = new Token(
                LEFT_POSITION,
                RIGHT_POSITION,
                Symbol.symbol("+", Tokens.Plus)
        );

        assertTrue(Symbol.symbol("+", Tokens.Plus) == token.getSymbol());
    }
}
