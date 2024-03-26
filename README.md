# Enigma
 Implemented a fully functioning Enigma Machine in Java that encrypts/decrypts an input text file (written in an alphabet chosen by the user) based on an input configuration file 
 containing character mappings.

## Testing
The directory testing contains the scripts test-correct and test-error for testing the execution of enigma.Main.

- `bash test-correct` F1.in F2.in ... will run the program for each of the message files F1.in, F2.in ..., comparing the results to the corresponding output files F1.out, F2.out, .... The configuration files used are F1.conf, F2.conf, .... However, if any of these is missing, the file default.conf (from the same directory as the input file) is used instead.
- `bash test-error` F1.in F2.in ... will run the program for each of the message files F1.in, F2.in ..., checking that the program reports at least one error in each case. The configuration files are as for test-correct.
- For example: `bash testing/test-correct testing/correct/trivial1.in`

- Unittests: PermutationTest.java, MovingRotorTest.java
