/*
 * Course: CS2852 - 061
 * Winter
 * Lab 1 - AutoComplete
 * Name: Nigel Nelson
 * Created: 03/24/20
 */
package msoe.nelsonni.lab06;

import java.util.List;

/**
 * Interface used by the controller to initialize files, find matching words
 * for a prefix, and to find operation times
 */
public interface AutoCompleter {

    /**
     * Method that initializes a file to the program
     * @param filename a String representing the selected file
     */
    void initialize(String filename);

    /**
     * Method that finds words that match a prefix
     * @param prefix the String entered by the user
     * @return a list of all the matching words for a given prefix
     */
    List allThatBeginWith(String prefix);

    /**
     * Method that gets the time to execute the last operation
     * @return the last operation time
     */
    double getLastOperationTime();

    boolean contains(String target);

}
