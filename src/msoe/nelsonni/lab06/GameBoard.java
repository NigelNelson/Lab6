/*
 * Course: CS2852 - 061
 * Winter
 * Lab 6 - Recursion
 * Name: Nigel Nelson
 * Created: 04/28/20
 */
package msoe.nelsonni.lab06;


import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Class that controls the boggle-style game board
 */
public class GameBoard {
    /**
     * Conversion factor for nanoseconds to seconds
     */
    private static final int CONVERSION_FACTOR = 1000000000;
    /**
     * Largest possible word length
     */
    private static final int ACCEPTABLE_LIST_LENGTH = 15;
    /**
     * A Y height for Grid that is too large
     */
    private static final int ODDLY_LARGE_GRID = 50;
    AutoCompleter strategy;
    Character[][] chars;
    int charsXDimension;
    int charsYDimension;
    double startTime;
    double endTIme;

    /**
     * Constructor for the GameBoard class
     *
     * @param strategy the AutoCompleter instance
     * @throws IllegalArgumentException when initialize is not called
     *                                  in the autocompleter instance.
     */
    public GameBoard(AutoCompleter strategy) throws IllegalArgumentException {
        try {
            strategy.getLastOperationTime();
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException("AutoCompleter interface" +
                    " must be initialized before being used");
        }
        this.strategy = strategy;
    }

    /**
     * Method that initializes a new boggle-style game board
     *
     * @param path the location of the grid file
     * @throws IllegalArgumentException when a dictionary file is passed in
     */
    public void load(Path path) {
        try (Scanner in = new Scanner(path)) {
            int row = 0;
            int col = 0;
            String line;

            charsYDimension = 0;
            while (in.hasNextLine()) {
                line = in.nextLine();
                charsXDimension = line.length();
                charsYDimension++;
            }

            chars = new Character[charsXDimension][charsYDimension];

            if (charsYDimension > ODDLY_LARGE_GRID) {
                throw new IllegalArgumentException();
            }


            Scanner sc = new Scanner(path);


            while (sc.hasNextLine()) {
                line = sc.nextLine();
                for (int i = 0; i < line.length(); i++) {
                    chars[col][row] = line.charAt(i);
                    col++;
                }
                col = 0;
                row++;
            }


        } catch (IOException | IllegalArgumentException e) {
            showReadFailureAlert();
        }
    }

    /**
     * Method that uses recursion to find words in the game board
     *
     * @param row          the y coordinate of the current char
     * @param col          the x coordinate of the current char
     * @param visitedFlags a double array that is the same size as the game board,
     *                     which keeps track of which chars have been visited
     * @param partialWord  the collection of chars that have resulted from the
     *                     recursive search
     * @return a list of words found in the game board
     */
    private List<String> recursiveSearch(int row, int col, boolean[][] visitedFlags,
                                         String partialWord) {


        List<int[]> neighbors = getNeighbors(col, row);
        List<String> words = new ArrayList<>();
        visitedFlags[col][row] = true;

        partialWord += (chars[col][row]);

        boolean isLongEnough = partialWord.length() > 2;
        boolean isShortEnough = partialWord.length() < ACCEPTABLE_LIST_LENGTH + 1;

        if (strategy.contains(partialWord) && isLongEnough && isShortEnough) {
            words.add(partialWord);
        }


        neighbors = neighbors.stream().filter(ints ->
                !visitedFlags[ints[0]][ints[1]]).collect(Collectors.toList());

        if (!neighbors.isEmpty() && partialWord.length() < ACCEPTABLE_LIST_LENGTH) {
            for (int[] ints : neighbors) {
                words.addAll(recursiveSearch(ints[1], ints[0], visitedFlags, partialWord));
            }
        }

        visitedFlags[col][row] = false;


        return words;
    }

    /**
     * Method that returns how long findWords() takes to run
     *
     * @return the time to call findWords()
     */
    public double getOperationTime() {
        return ((endTIme - startTime) / CONVERSION_FACTOR);
    }


    /**
     * Method that finds what available neighbors around a char
     *
     * @param col the x coordinate of the char
     * @param row the y coordinate of the char
     * @return a list of coordinates of where the
     * recursive search can go
     */
    private ArrayList<int[]> getNeighbors(int col, int row) {

        ArrayList<int[]> neighborCoordinates = new ArrayList<>();

        boolean canGoUp = (row != 0);
        boolean canGoRight = (col != charsXDimension - 1);
        boolean canGoDown = (row != charsYDimension - 1);
        boolean canGoLeft = (col != 0);

        if (canGoUp) {
            neighborCoordinates.add(new int[] {col, row - 1});
            if (canGoRight) {
                neighborCoordinates.add(new int[] {col + 1, row - 1});
            } else if (canGoLeft) {
                neighborCoordinates.add(new int[] {col - 1, row - 1});
            }
        }
        if (canGoRight) {
            neighborCoordinates.add(new int[] {col + 1, row});
            if (canGoDown) {
                neighborCoordinates.add(new int[] {col + 1, row + 1});
            }
        }
        if (canGoDown) {
            neighborCoordinates.add(new int[] {col, row + 1});
            if (canGoLeft) {
                neighborCoordinates.add(new int[] {col - 1, row + 1});
            }
        }
        if (canGoLeft) {
            neighborCoordinates.add(new int[] {col - 1, row});
        }


        return neighborCoordinates;

    }


    /**
     * Method that finds what matching words are
     * withing the gameboard for the given AutoCompleter instance
     *
     * @return A list of words from the gameboard that match words
     * in the dictionary file of AutoCompleter instance
     */
    public List<String> findWords() {

        startTime = System.nanoTime();
        int row = 0;
        int col = 0;

        List<String> correctWords = new ArrayList<>();

        for (Character[] stringArray : chars) {
            for (Character c : stringArray) {
                correctWords.addAll(recursiveSearch(row, col, getEmptyBooleanGrid(), ""));
                row++;
            }
            col++;
            row = 0;
        }

        endTIme = System.nanoTime();
        return correctWords;
    }

    /**
     * A method to get a boolean grid that is the same size
     * as the gameboard
     *
     * @return a gameboard sized boolean grid of all falses
     */
    private boolean[][] getEmptyBooleanGrid() {
        return new boolean[charsXDimension][charsYDimension];
    }


    /**
     * Method that displays an alert if the program is not able to read a selected file
     */
    private static void showReadFailureAlert() {
        System.out.println("Error in the file that was initialized");
    }
}
