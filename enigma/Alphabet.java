package enigma;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Vikram Cherukuri
 */
class Alphabet {
    /** Chars of this alphabet. */
    private char[] _chars;

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _chars = new char[chars.length()];
        for (int i = 0; i < chars.length(); i++) {
            _chars[i] = chars.charAt(i);
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _chars.length;
    }

    /** Returns the chars of the alphabet. */
    char[] getChars() {
        return _chars;
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        for (int i = 0; i < size(); i++) {
            if (ch == _chars[i]) {
                return true;
            }
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index > size() - 1) {
            throw new EnigmaException("Index out of range");
        }
        return _chars[index];
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        if (!contains(ch)) {
            throw new EnigmaException("Character not in alphabet");
        }
        int result = -1;
        for (int i = 0; i < _chars.length; i++) {
            if (ch == _chars[i]) {
                result = i;
            }
        }
        return result;
    }

}
