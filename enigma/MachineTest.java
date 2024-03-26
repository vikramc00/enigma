package enigma;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;


/** The suite of all JUnit tests for the Machine class.
 *  @author Vikram Cherukuri
 */
public class MachineTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private void setMachine() {
        String cycles = "(AE) (BN) (CK) (DQ) (FU) (GY) (HW) "
                + "(IJ) (LO) (MP) (RX) (SZ) (TV)";
        Reflector B = new Reflector("B",
                new Permutation(cycles, UPPER));
        cycles = "(ALBEVFCYODJWUGNMQTZSKPR) (HIX)";
        FixedRotor beta = new FixedRotor("Beta",
                new Permutation(cycles, UPPER));
        cycles = "(ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)";
        MovingRotor iii = new MovingRotor("III",
                new Permutation(cycles, UPPER), "V");
        cycles = "(AEPLIYWCOXMRFZBSTGJQNH) (DV) (KU)";
        MovingRotor iv = new MovingRotor("IV",
                new Permutation(cycles, UPPER), "J");
        cycles = "(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)";
        MovingRotor I = new MovingRotor("I",
                new Permutation(cycles, UPPER), "Q");
        cycles = "(AFNIRLBSQWVXGUZDKMTPCOYJHE)";
        FixedRotor gamma = new FixedRotor("Gamma",
                new Permutation(cycles, UPPER));
        cycles = "(FIXVYOMW) (CDKLHUP) (ESZ) (BJ) (GR) (NT) (A) (Q)";
        MovingRotor ii = new MovingRotor("II",
                new Permutation(cycles, UPPER), "E");

        allRotors.add(B);
        allRotors.add(beta);
        allRotors.add(ii);
        allRotors.add(iv);
        allRotors.add(gamma);
        allRotors.add(iii);
        allRotors.add(I);
    }

    private ArrayList<Rotor> allRotors = new ArrayList<Rotor>();

    private Machine m = new Machine(UPPER, 5, 3, allRotors);

    private String[] rotors = {"B", "Beta", "III", "IV", "I"};

    private String[] rotors1 = {"B", "Beta", "III", "II", "I"};

    /* ***** TESTS ***** */
    @Test
    public void testInsertRotors() {
        setMachine();
        m.insertRotors(rotors);

        for (int i = 0; i < m.rotors().length; i++) {
            assertEquals(rotors[i], m.rotors()[i].name());
        }
    }

    @Test
    public void testSetRotors() {
        setMachine();
        m.insertRotors(rotors1);
        m.setRotors("BCDZ");

        for (int i = 1; i < m.rotors().length; i++) {
            assertEquals(m.rotors()[i].setting(),
                    m.alphabet().toInt("BCDZ".charAt(i - 1)));
        }
    }

    @Test
    public void testConvert() {
        setMachine();
        m.insertRotors(rotors1);
        m.setRotors("BCDZ");

        Permutation plugboard = new Permutation("", UPPER);
        m.setPlugboard(plugboard);

        String result = m.convert("AAAAA");

        assertEquals(result, "CXQWK");
    }
}
