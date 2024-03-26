package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/**
 * The suite of all JUnit tests for the Permutation class.
 *  @author Vikram Cherukuri
 */
public class PermutationTest {


    Permutation getNewPermutation(String cycles, Alphabet alphabet) {
        return new Permutation(cycles, alphabet);
    }

    Alphabet getNewAlphabet(String chars) {
        return new Alphabet(chars);
    }

    Alphabet getNewAlphabet() {
        return new Alphabet();
    }

    /**
     * Testing time limit.
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /**
     * Check that PERM has an ALPHABET whose size is that of
     * FROMALPHA and TOALPHA and that maps each character of
     * FROMALPHA to the corresponding character of FROMALPHA, and
     * vice-versa. TESTID is used in error messages.
     */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha,
                           Permutation perm, Alphabet alpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                    e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                    c, perm.invert(e));
            int ci = alpha.toInt(c), ei = alpha.toInt(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                    ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                    ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */


    @Test
    public void checkIdTransform() {
        Alphabet alpha = getNewAlphabet();
        Permutation perm = getNewPermutation("", alpha);
        checkPerm("identity", UPPER_STRING, UPPER_STRING, perm, alpha);
    }

    @Test
    public void testAlphabet() {
        String chars = "ABCDEFG";
        Alphabet a = getNewAlphabet(chars);
        for (int i = 0; i < chars.length(); i++) {
            assertEquals(a.getChars()[i], chars.charAt(i));
        }
    }

    @Test
    public void testContains() {
        Alphabet a = getNewAlphabet("ABCDEFG");
        assertTrue(a.contains('E'));
        assertFalse(a.contains('Z'));
    }

    @Test
    public void testPermutation() {
        Alphabet alpha = getNewAlphabet("ABCDE");
        Permutation p = getNewPermutation("(EDA) (CB)", alpha);

        assertEquals(p.cycles()[0], "EDA");
        assertEquals(p.cycles()[1], "CB");

    }

    @Test
    public void testPermuteChar() {
        Alphabet alpha = getNewAlphabet("ATC");
        Permutation p = getNewPermutation("", alpha);

        assertEquals('A', p.permute('A'));
        assertEquals('T', p.permute('T'));
        assertEquals('C', p.permute('C'));
    }

    @Test
    public void testInvertChar() {
        Alphabet alpha = getNewAlphabet("ABCDE");
        Permutation p = getNewPermutation("(EDA) (CB)", alpha);

        assertEquals('E', p.invert('D'));
        assertEquals('D', p.invert('A'));
        assertEquals('A', p.invert('E'));
        assertEquals('C', p.invert('B'));
        assertEquals('B', p.invert('C'));
    }

    @Test
    public void testDerangement() {
        Alphabet alpha = getNewAlphabet("ABCDE");
        Alphabet alpha1 = getNewAlphabet("ABCDEF");


        Permutation p1 = getNewPermutation("(EDA) (CB)", alpha);
        Permutation p2 = getNewPermutation("(EDA) (CB) (F)", alpha1);
        Permutation p3 = getNewPermutation("(EDA) (CB)", alpha1);

        assertTrue(p1.derangement());
        assertFalse(p2.derangement());
        assertFalse(p3.derangement());
    }

    @Test(expected = EnigmaException.class)
    public void testCycleNotInAlphabet() {
        Alphabet alpha = getNewAlphabet("ABCD");

        Permutation p = getNewPermutation("(BACD) (FE)", alpha);
    }

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet() {
        Alphabet alpha = getNewAlphabet("ABCD");
        Permutation p = getNewPermutation("(BACD)", alpha);
        p.invert('F');
    }
}
