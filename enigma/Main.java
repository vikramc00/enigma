package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collection;

import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Vikram Cherukuri
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        _machine = readConfig();
        if (!_input.hasNextLine()) {
            throw new EnigmaException("input must contain settings");
        }

        String settings = _input.nextLine();

        if (settings.charAt(0) != '*') {
            throw new EnigmaException("input must contain settings");
        }

        while (_input.hasNextLine()) {
            setUp(_machine, settings);
            while (_input.hasNextLine()) {
                String line = _input.nextLine();
                if (line.length() == 0) {
                    line = _machine.convert(line);
                    printMessageLine(line);
                    continue;
                } else if (line.charAt(0) == '*') {
                    settings = line;
                    break;
                }
                line = line.replaceAll("\\s", "");
                line = _machine.convert(line);
                printMessageLine(line);
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String alpha = _config.nextLine();

            if (alpha.contains("*") || alpha.contains("(")
                    || alpha.contains(")")) {
                throw new EnigmaException("Incorrect name format");
            }

            _alphabet = new Alphabet(alpha);

            if (!_config.hasNextInt()) {
                throw new EnigmaException("Bad Machine description");
            }
            int numRotors = _config.nextInt();

            if (!_config.hasNextInt()) {
                throw new EnigmaException("Bad Machine description");
            }
            int pawls = _config.nextInt();

            _config.nextLine();

            if (!_config.hasNext()) {
                throw new EnigmaException("Bad Machine description");
            }

            Collection<Rotor> allRotors = new ArrayList<>();

            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }

            return new Machine(_alphabet, numRotors, pawls, allRotors);

        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next();

            if (name.contains(" ") || name.contains("(")
                    || name.contains(")")) {
                throw new EnigmaException("Incorrect name format");
            }

            String notches = _config.next();

            String cycles = "", curr = "";
            while (_config.hasNext("\\(.+?\\)")) {
                curr = _config.next("\\(.+?\\)");
                cycles += curr;
            }

            for (int i = 0; i < cycles.length(); i++) {
                if (cycles.charAt(i) == ')' && i != cycles.length() - 1) {
                    cycles = cycles.substring(0, i + 1) + " "
                            + cycles.substring(i + 1, cycles.length());
                }
            }

            Permutation perm = new Permutation(cycles, _alphabet);

            if (notches.charAt(0) == 'M') {
                if (notches.charAt(1) == ' ') {
                    throw new EnigmaException("Space required between "
                            + "class and notches");
                }
                return new MovingRotor(name, perm,
                        notches.substring(1, notches.length()));
            } else if (notches.charAt(0) == 'N') {
                return new FixedRotor(name, perm);
            } else if (notches.charAt(0) == 'R') {
                return new Reflector(name, perm);
            } else {
                throw new EnigmaException("Incorrect rotor type");
            }

        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {

        if (settings.charAt(0) != '*') {
            throw new EnigmaException("Asterisk missing at first column");
        }
        String[] terms = settings.split(" ");
        String[] rotors = new String[M.numRotors()];

        for (int i = 0; i < rotors.length; i++) {
            rotors[i] = terms[i + 1];
        }
        M.insertRotors(rotors);

        int curr = rotors.length + 1;
        String setting = "";
        if (curr < terms.length) {
            setting = terms[curr];
        }

        M.setRotors(setting);
        curr++;
        if (curr < terms.length && terms[curr].charAt(0) != '(') {
            String ring = terms[curr];

            if (ring.length() != M.numRotors() - 1) {
                throw new EnigmaException("Incorrect length of ring settings");
            }
            for (int i = 0; i < ring.length(); i++) {
                M.rotors()[i + 1].setRing(ring.charAt(i));
            }
            curr++;
        }

        String cycles = "";
        for (int i = curr; i < terms.length; i++) {
            cycles += terms[i] + " ";
        }
        if (cycles.length() != 0) {
            cycles = cycles.substring(0, cycles.length() - 1);
        }
        M.setPlugboard(new Permutation(cycles, _alphabet));

        for (int i = 1; i <= M.rotors().length; i++) {
            if (M.rotors()[i - 1] instanceof MovingRotor) {
                if (i < M.numRotors() - M.numPawls() + 1 || i > M.numRotors()) {
                    throw new EnigmaException("Moving Rotor can't be here");
                }
            } else if (M.rotors()[i - 1] instanceof FixedRotor
                    && !(M.rotors()[i - 1] instanceof Reflector)) {
                if ((i < 2) || i > M.numRotors() - M.numPawls()) {
                    throw new EnigmaException("Fixed Rotor can't be here");
                }
            } else if (M.rotors()[i - 1] instanceof Reflector && i != 1) {
                throw new EnigmaException("Reflector in wrong place");
            } else if ((!M.rotors()[i - 1].reflecting()) && i == 0) {
                throw new EnigmaException("Reflector in wrong place");
            }
        }
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        msg = msg.replaceAll("\\s", "");

        String result = "";

        for (int i = 0; i < msg.length(); i++) {
            if (i % 5 == 0 && i != 0) {
                result += " ";
            }
            result += msg.substring(i, i + 1);
        }

        _output.println(result);
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** File for encoded/decoded messages. */
    private Machine _machine;
}
