package tests.lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.stream.Stream;
import lexer.readers.IReader;
import lexer.readers.SourceReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class SourceReaderTest {

    @Test
    void testEndOfFile() {
        try {
            IReader sourceReader = new SourceReader(
                    new BufferedReader(new StringReader("")));

            assertEquals('\0', sourceReader.read());
        } catch (IOException e) {
            fail("SourceReader did not correctly recognize end of file");
        }
    }

    @Test
    void testLineNumber() {
        try {
            IReader sourceReader = new SourceReader(
                    new BufferedReader(new StringReader("a\nb\nc\n")));

            assertEquals(1, sourceReader.getLineNumber());
            sourceReader.read(); // a
            assertEquals(1, sourceReader.getLineNumber());
            sourceReader.read(); // \n
            assertEquals(1, sourceReader.getLineNumber());
            sourceReader.read(); // b
            assertEquals(2, sourceReader.getLineNumber());
            sourceReader.read(); // \n
            assertEquals(2, sourceReader.getLineNumber());
            sourceReader.read(); // c
            assertEquals(3, sourceReader.getLineNumber());
            sourceReader.read(); // \n
            assertEquals(3, sourceReader.getLineNumber());
            sourceReader.read(); // \0
            assertEquals(4, sourceReader.getLineNumber());
        } catch (IOException e) {
            fail("SourceReader threw when evaluating line numbers");
        }
    }

    @Test
    void testColumn() {
        try {
            IReader sourceReader = new SourceReader(
                    new BufferedReader(new StringReader("012\n012\n01234567\n")));

            assertEquals(-1, sourceReader.getColumn());

            char character = sourceReader.read();

            while (character != '\0') {
                while (character != '\n') {
                    int expected = Integer.parseInt("" + character);
                    assertEquals(expected, sourceReader.getColumn());

                    character = sourceReader.read();
                }
                character = sourceReader.read();
            }
        } catch (IOException e) {
            fail("SourceReader threw when evaluating column numbers");
        }
    }

    private static Stream<Arguments> getSourceFiles() {
        return Stream.of(
                Arguments.of(
                        "abc\n123",
                        new char[] { 'a', 'b', 'c', '\n', '1', '2', '3', '\0' }),
                Arguments.of(
                        "+-++\\& \n",
                        new char[] { '+', '-', '+', '+', '\\', '&', ' ', '\n', '\0' }));
    }

    @ParameterizedTest
    @MethodSource("getSourceFiles")
    void testSourceReader(String source, char[] expected) {
        try {
            IReader sourceReader = new SourceReader(
                    new BufferedReader(new StringReader(source)));

            char character;
            int index = 0;
            while ((character = sourceReader.read()) != '\0') {
                assertEquals(expected[index++], character);
            }

            assertEquals(index, expected.length - 1);
            assertEquals(expected[index], character);
        } catch (IOException e) {
            fail("SourceReader failed: " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException ae) {
            fail("SourceReader continued reading past expected characters");
        }
    }
}