/*
 * Course: CS2852 - 061
 * Winter
 * Lab 6 - Recursion
 * Name: Nigel Nelson
 * Created: 04/28/20
 */
package msoe.nelsonni.lab06;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The class that is responsible for when an enhanced for loop
 *is selected to be used in the AutoComplete gui
 */
public class EnhancedMethod implements AutoCompleter {

    /**
     * Conversion factor for nanoseconds to seconds
     */
    private static final int CONVERSION_FACTOR = 1000000000;

    File file;
    List<String> specifiedList;
    ArrayList<String> matches = new ArrayList<>();
    double operationTime;
    double startTime;
    double endTime;

    /**
     * Constructor for the EnhancedMethod class that takes a unspecified list
     * as an argument
     * @param type
     */
    public EnhancedMethod(List type) {
        specifiedList = type;

    }

    /**
     * Method that overrides the AutoComplete interface's initialize method
     * Takes a filename as a string and calls the correct method
     * to initialize the chosen file type
     * @param filename a String representing the chosen file
     */
    @Override
    public void initialize(String filename) {

        startTime = System.nanoTime();

        file = new File(filename);

        String extension = filename.substring(filename.lastIndexOf("."));

        if (extension.equals(".txt")) {
            loadTextFile(file);
        } else if (extension.equals(".csv")) {
            loadCFile(file);
        } else {
            showUnsupportedFileTypeAlert();
        }

        endTime = System.nanoTime();

        operationTime = (endTime - startTime);
    }

    /**
     * Method that overrides the AutoComplete interface's allThatBeginWith method
     * Takes a prefix and returns a list of all of the matching words that
     * begin with that prefix
     * @param prefix the String that the user enters in the gui
     * @return a list with all of the matching words from the last initialized
     * file
     */
    @Override
    public List allThatBeginWith(String prefix) {

        startTime = System.nanoTime();

        int prefixLength = prefix.length();

        if (specifiedList != null) {

            matches.clear();

            for (String word : specifiedList) {
                if (word.length() >= prefixLength) {
                    if ((word.substring(0, prefixLength).equalsIgnoreCase(prefix))) {
                        matches.add(word);
                    }
                }
            }
        } else {
            throw new IllegalStateException("Must call initialize() prior to calling this method");
        }

        endTime = System.nanoTime();
        operationTime = (endTime - startTime);

        return matches;


    }

    /**
     * Method that overrides the AutoComplete interface's getLastOperation method
     * @return the time in seconds for the last call to initialize or allThatBeginWith
     * @throws IllegalStateException if a user tries to prematurely call getLastOperationTime
     */
    @Override
    public double getLastOperationTime() {
        if (file != null) {
            return operationTime / CONVERSION_FACTOR;
        } else {
            throw new IllegalStateException("Must call initialize() prior to calling this method");
        }
    }

    @Override
    public boolean contains(String target) {
        boolean isThereAMatch = false;
        if (file != null) {

            for (String word: specifiedList){
                if (word.equalsIgnoreCase(target)){
                    isThereAMatch = true;
                }
            }
            return isThereAMatch;
        } else {
            throw new IllegalStateException("Must call initialize() prior to calling this method");
        }
    }

    /**
     * Method that is called when a .txt file is selected to be initialized
     *
     * @param file the file that is selected by the user
     * @throws IllegalArgumentException when a grid file is attempted to be initialized
     */
    private void loadTextFile(File file) throws IllegalArgumentException {
        try (Scanner in = new Scanner(file)) {

            specifiedList.clear();

            while (in.hasNext()) {
                specifiedList.add(in.next());
            }
            if (specifiedList.size() == specifiedList.get(0).length()) {
                throw new IllegalArgumentException("A grid File was " +
                        "likely uploaded as a dictionary file");
            }
        } catch (FileNotFoundException | IllegalArgumentException e) {
            showReadFailureAlert();
        }
    }

    /**
     * Method that is called when a .csv file is selected to be initialized
     * @param file the file that is selected by the user
     */
    private void loadCFile(File file) {

        String wordWithComma;
        String wordWithoutComma;

        try (Scanner in = new Scanner(file)) {

            specifiedList.clear();

            while (in.hasNextLine()) {
                wordWithComma = in.nextLine();
                wordWithoutComma = wordWithComma.substring(wordWithComma.indexOf(",") + 1);
                specifiedList.add(wordWithoutComma);
            }

        } catch (FileNotFoundException e) {
            showReadFailureAlert();
        }

    }

    /**
     * Method that displays an alert if the program is not able to read a selected file
     */
    private static void showReadFailureAlert() {
        System.out.println("Error in the file that was initialized");
    }

    /**
     * Method that displays an alert if the program encounters an unsupported file
     */
    private static void showUnsupportedFileTypeAlert() {
        System.out.println("The file entered could not be saved");
    }


}
