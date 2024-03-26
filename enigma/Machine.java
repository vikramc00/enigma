package enigma;

import java.util.Collection;


import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Vikram Cherukuri
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        _rotors = new Rotor[numRotors];

        if (numRotors <= 1) {
            throw new EnigmaException("There too few rotor slots available");
        }
        if (pawls < 0 || pawls >= numRotors) {
            throw new EnigmaException("The number of pawls is out of range");
        }
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    Collection<Rotor> allRotors() {
        return _allRotors;
    }

    /** Return the array of rotors. */
    Rotor[] rotors() {
        return _rotors;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        for (int i = 0; i < rotors.length; i++) {
            for (Rotor r: _allRotors) {
                if (rotors[i].equals(r.name())) {
                    _rotors[i] = r;
                }
            }
        }

        for (int i = 0; i < rotors.length; i++) {
            for (int j = 0; j < rotors.length; j++) {
                if (i == j) {
                    continue;
                } else if (rotors[i] == rotors[j]) {
                    throw new EnigmaException("Duplicate rotor name");
                }
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != numRotors() - 1) {
            throw new EnigmaException("String setting is of incorrect length");
        }

        for (int i = 1; i < numRotors(); i++) {
            char ch = setting.charAt(i - 1);
            _rotors[i].set(_alphabet.toInt(ch));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        int input = _plugboard.permute(c);

        for (int i = 1; i < numRotors() - 1; i++) {
            if (_rotors[i + 1].atNotch() && _rotors[i].rotates()) {
                _rotors[i].advance();
                if (i != numRotors() - 2) {
                    _rotors[i + 1].advance();
                    i++;
                }
            }
        }

        _rotors[numRotors() - 1].advance();

        for (int i = numRotors() - 1; i >= 0; i--) {
            input = _rotors[i].convertForward(input);
        }
        for (int i =  1; i < numRotors(); i++) {
            input = _rotors[i].convertBackward(input);
        }

        return _plugboard.permute(input);
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i++) {
            int input = _alphabet.toInt(msg.charAt(i));
            char ch = _alphabet.toChar(convert(input));
            result += Character.toString(ch);
        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotor slots in this machine. */
    private int _numRotors;

    /** Number of pawls in this machine. */
    private int _pawls;

    /** All available rotors in this machine. */
    private Collection<Rotor> _allRotors;

    /** Array of rotors ordered in this machine. */
    private Rotor[] _rotors;

    /** Plugboard of this machine. */
    private Permutation _plugboard;
}
