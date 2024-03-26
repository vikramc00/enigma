package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Vikram Cherukuri
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;


        int num = 0;
        for (int i = 0; i < cycles.length(); i++) {
            if (cycles.charAt(i) == '(') {
                num++;
            }
        }
        _cycles = new String[num];

        if (cycles.length() != 0) {

            _cycles = cycles.split(" ");

            for (int i = 0; i < _cycles.length; i++) {
                _cycles[i] = _cycles[i].substring(1, _cycles[i].length() - 1);
            }

            for (int i = 0; i < cycles.length(); i++) {
                if (cycles.charAt(i) != '(' && cycles.charAt(i) != ')'
                        && cycles.charAt(i) != ' ') {
                    if (!_alphabet.contains(cycles.charAt(i))) {
                        throw new EnigmaException("cycles is not in alphabet");
                    }
                }
            }
        }
    }
    /** Return String array of cycles. */
    String[] cycles() {
        return _cycles;
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _cycles = new String[_cycles.length + 1];
        _cycles[_cycles.length - 1] = cycle;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char curr = _alphabet.toChar(wrap(p));
        if (!_alphabet.contains(curr)) {
            throw new EnigmaException("Character not in alphabet");
        }
        char result = curr;
        int checker = 0;
        for (int i = 0; i < _cycles.length; i++) {
            for (int j = 0; j < _cycles[i].length(); j++) {
                if (curr == _cycles[i].charAt(j)) {
                    if (j == _cycles[i].length() - 1) {
                        result = _cycles[i].charAt(0);
                    } else {
                        result = _cycles[i].charAt(j + 1);
                    }
                    checker++;
                    break;
                }
            }
            if (checker > 0) {
                break;
            }
        }
        return _alphabet.toInt(result);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char curr = _alphabet.toChar(wrap(c));
        if (!_alphabet.contains(curr)) {
            throw new EnigmaException("Character not in alphabet");
        }
        char result = curr;
        for (int i = 0; i < _cycles.length; i++) {
            for (int j = 0; j < _cycles[i].length(); j++) {
                if (curr == _cycles[i].charAt(j)) {
                    if (j == 0) {
                        result = _cycles[i].charAt(_cycles[i].length() - 1);
                    } else {
                        result = _cycles[i].charAt(j - 1);
                    }
                    break;
                }
            }
        }
        return _alphabet.toInt(result);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (!_alphabet.contains(p)) {
            throw new EnigmaException("Character not in alphabet");
        }

        int result = _alphabet.toInt(p);
        result = permute(result);
        return _alphabet.toChar(result);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        if (!_alphabet.contains(c)) {
            throw new EnigmaException("Character not in alphabet");
        }
        int result = _alphabet.toInt(c);
        result = invert(result);
        return _alphabet.toChar(result);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < _alphabet.size(); i++) {
            if (permute(i) == i) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Cycles of this permutation. */
    private String[] _cycles;
}
