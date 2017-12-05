package edu.ucsb.cs56.pconrad.parsing.tokenizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.io.PrintStream;
import java.io.InputStream;



public class TokenizerTest {

    private TokenFactory tf = new DefaultTokenFactory();

    @Test
    public void testOneErrorToken() {
        assertArrayEquals(new Token[] { tf.makeErrorToken("$") },
                          Tokenizer.tokenizeToArray("$"));
    }


    @Test
    public void testOneValidFollowedByOneErrorToken() {
        assertArrayEquals(new Token[] {
                              tf.makeIntToken("2"),
                              tf.makeErrorToken("$")
                          },
                          Tokenizer.tokenizeToArray("2$"));
    }

    @Test
    public void testTwoPlusTwoEquals() {
        assertArrayEquals(new Token[] {
                              tf.makeIntToken("2"),
                              tf.makePlusToken(),
                              tf.makeIntToken("2"),
                              tf.makeErrorToken("=")
                          },
                          Tokenizer.tokenizeToArray("2+2="));
    }

    @Test
    public void testTwoPlusTwoEqualsWithSpace() {
        assertArrayEquals(new Token[] {
                              tf.makeIntToken("2"),
                              tf.makePlusToken(),
                              tf.makeIntToken("2"),
                              tf.makeErrorToken("=")
                          },
                          Tokenizer.tokenizeToArray(" 2 + 2 = "));
    }

    @Test
    public void testInterleavedIllegalCharsAndWhiteSpace() {
        assertArrayEquals(new Token[] {
                              tf.makeErrorToken("a"),
                              tf.makeErrorToken("b"),
                              tf.makeErrorToken("c"),
                              tf.makeErrorToken("d"),
                              tf.makeErrorToken("e"),
                              tf.makeErrorToken("f"),
                              tf.makeErrorToken("g"),
                              tf.makeErrorToken("h"),
                              tf.makeErrorToken("i"),
                          },
                          Tokenizer.tokenizeToArray(" ab c d ef ghi "));
    }

    @Test
    public void testInterleavedIllegalCharsLegalTokensAndWhiteSpace() {
        assertArrayEquals(new Token[] {
                              tf.makeErrorToken("a"),
                              tf.makeErrorToken("b"),
                              tf.makeIntToken("12345"),
                              tf.makeErrorToken("d"),
                              tf.makeErrorToken("e"),
                              tf.makeErrorToken("f"),
                              tf.makeErrorToken("g"),
                              tf.makeErrorToken("h"),
                              tf.makePlusToken(),
                              tf.makeErrorToken("i"),
                          },
                          Tokenizer.tokenizeToArray(" ab 12345 d ef gh+i "));
    }





    @Test
    public void testSingleDigitIntToken() {
        assertArrayEquals(new Token[] { tf.makeIntToken("0") },
                          Tokenizer.tokenizeToArray("0"));
    }


    @Test
    public void testTwoDigitIntToken() {
        assertArrayEquals(new Token[] { tf.makeIntToken("12") },
                          Tokenizer.tokenizeToArray("12"));
    }

    @Test
    public void testTwoPlusTwo() {
        assertArrayEquals(new Token[] {
                              tf.makeIntToken("2"),
                              tf.makePlusToken(),
                              tf.makeIntToken("2")
                          },
                          Tokenizer.tokenizeToArray("2+2"));
    }


    @Test
    public void testTwoPlusTwoWithWhiteSpace() {
        assertArrayEquals(new Token[] {
                              tf.makeIntToken("2"),
                              tf.makePlusToken(),
                              tf.makeIntToken("2")
                          },
                          Tokenizer.tokenizeToArray(" 2 + 2 "));
    }

    @Test
    public void twoDigitArithmeticMixed() {
        assertArrayEquals(new Token[] {
                              tf.makeIntToken("12"),
                              tf.makePlusToken(),
                              tf.makeIntToken("34"),
                              tf.makeTimesToken(),
                              tf.makeIntToken("56"),
                              tf.makeMinusToken(),
                              tf.makeIntToken("78"),
                              tf.makeDivideToken(),
                              tf.makeIntToken("90"),
                          },
                          Tokenizer.tokenizeToArray(" 12  + 34*56 -78/90 "));
    }

    @Test
    public void fullSetNoWhiteSpace() {
        assertArrayEquals(new Token[] {
                              tf.makeIntToken("1234"),
                              tf.makePlusToken(),
                              tf.makeTimesToken(),
                              tf.makeMinusToken(),
                              tf.makeIntToken("5678"),
                              tf.makeDivideToken(),
                              tf.makeLParenToken(),
                              tf.makeRParenToken(),
                              tf.makeIntToken("3333"),
                          },
                          Tokenizer.tokenizeToArray("1234+*-5678/()3333"));
    }

    @Test
    public void fullSetLotsOfWhiteSpace() {
        assertArrayEquals(new Token[] {
                              tf.makeIntToken("12"),
                              tf.makeIntToken("34"),
                              tf.makePlusToken(),
                              tf.makeTimesToken(),
                              tf.makeMinusToken(),
                              tf.makeIntToken("5678"),
                              tf.makeDivideToken(),
                              tf.makeLParenToken(),
                              tf.makeRParenToken(),
                              tf.makeIntToken("3333"),
                          },
                          Tokenizer.tokenizeToArray(" 12 34 + *  -   5678  / ( )  3333  "));
    }

    @Test
    public void testOneToken() {
        assertArrayEquals(new Token[] { tf.makePlusToken() },
                          Tokenizer.tokenizeToArray("+"));
    }

    @Test
    public void testTwoSameTokens() {
        assertArrayEquals(new Token[] { tf.makePlusToken(),
                                        tf.makePlusToken()
                                      },
                          Tokenizer.tokenizeToArray("++"));
    }

    @Test
    public void testSingleWhitespace() {
        assertArrayEquals(new Token[] { },
                          Tokenizer.tokenizeToArray(" "));
    }

    @Test
    public void testSingleDigit() {
        assertArrayEquals(new Token[] { tf.makeIntToken("1") },
                          Tokenizer.tokenizeToArray("1"));
    }

    @Test
    public void testTwoDigit() {
        assertArrayEquals(new Token[] { tf.makeIntToken("12") },
                          Tokenizer.tokenizeToArray("12"));
    }


    @Test
    public void testSequenceOfTokensLecture() {
        assertArrayEquals(new Token[] {
                              tf.makePlusToken(),
                              tf.makeMinusToken(),
                              tf.makePlusToken(),
                              tf.makeIntToken("12"),
                              tf.makePlusToken()
                          },
                          Tokenizer.tokenizeToArray("+-+12+"));
    }

    @Test
    public void testInvalidSingleToken() {
        assertArrayEquals(new Token[] {
                              tf.makeErrorToken("c"),
                          },
                          Tokenizer.tokenizeToArray("c"));
    }

    @Test
    public void testInvalidTokenAfterValidToken() {
        assertArrayEquals(new Token[] {
                              tf.makeIntToken("12"),
                              tf.makeErrorToken("c"),
                          },
                          Tokenizer.tokenizeToArray("12c"));
    }

    @Test
    public void testValidInvalidValidInvalid() {
        assertArrayEquals(new Token[] {
                              tf.makeIntToken("12"),
                              tf.makeErrorToken("c"),
                              tf.makeIntToken("45"),
                              tf.makeErrorToken("d"),
                          },
                          Tokenizer.tokenizeToArray("12c45d"));
    }



    /**
       test the <code>public static void main(String [] args)</code> class
       of Main with given input for System.in, and return the output from
       System.out

       @param system_in the output you want to test as input to System.in
       @param args the command line args for the main class
       @return the output that is written to System.out
       @throws java.io.UnsupportedEncodingException May throw this if UTF_8 is not supported.

     */

    public String testMainWithSystemInAndOut(String system_in, String [] args)
    throws java.io.UnsupportedEncodingException {

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayInputStream inContent =
            new ByteArrayInputStream(system_in.getBytes(StandardCharsets.UTF_8.name()));

        PrintStream saveOut = System.out;
        InputStream saveIn = System.in;

        System.setOut(new PrintStream(outContent));
        System.setIn(inContent);

        Tokenizer.main(args);

        String returnValue = outContent.toString();
        System.setOut(saveOut);
        System.setIn(saveIn);

        return returnValue;
    }

	public static final String newline = System.getProperty("line.separator");
	
    @Test
    public void testMainWithEmptyArgs() throws java.io.UnsupportedEncodingException  {
        String outContent = testMainWithSystemInAndOut("", new String[0]);
        assertEquals("Tokenizing: 2+2" + newline + "[IntToken(2), PlusToken, IntToken(2)]", outContent.trim());
    }

    @Test
    public void testMainWithOneArgs() throws java.io.UnsupportedEncodingException  {
        String outContent = testMainWithSystemInAndOut("", new String[] {"-3"});
        assertEquals("Tokenizing: -3" + newline + "[MinusToken, IntToken(3)]", outContent.trim());
    }



}
