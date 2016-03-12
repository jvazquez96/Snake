package snake;


import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.LinkedList;


public class StateHandler {

    /**
     * Saves the current game member variables to a binary file.
     *
     * @param snakeGame
     */
    public static void saveGame(SnakeGame snakeGame) {
        String sName = JOptionPane.showInputDialog("Please input your " +
                                                               "username");
        if (sName != null){
            try {
                /*
                 * Save a serialized version of the individual member variables
                 * in the received Snake instance
                 */
                // Asks for the name of the user to save

                sName = sName.trim().toLowerCase() ;
                ObjectOutputStream objOut = new ObjectOutputStream(
                        new FileOutputStream(sName + "_saveGame.bin"));
                writeVariables(snakeGame, objOut);
                objOut.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            }
        }

    public static void loadGame(SnakeGame snakeGame) {
        /*
         * Load a serialized version of a previous game state from
         * a binary file, and set each member variable in the received Snake
         * instance
         */
        // Asks for the name of the user to load
        String sName = JOptionPane.showInputDialog("Please input the username" +
                                                           " you used to save" +
                                                           " a previous game");
        if (sName != null){
        
            sName = sName.trim().toLowerCase();
            try {
                ObjectInputStream objIn = new ObjectInputStream(
                        new FileInputStream(sName + "_saveGame.bin"));
                readVariables(snakeGame, objIn);
                objIn.close();
            }
            catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null,
                                              "A previous saved game from the " +
                                                      "user \"" + sName + "\" was" +
                                                      " not found",
                                              "Username not found.",
                                              JOptionPane.ERROR_MESSAGE);
                System.out.println("Could not load the previous game state");
            }
            }
    }

    private static void writeVariables(SnakeGame snakeGame, ObjectOutputStream objOut) throws
                                                                                       IOException {
        objOut.writeObject(snakeGame.isNewGame());
        objOut.writeObject(snakeGame.isGameOver());
        objOut.writeObject(snakeGame.isPaused());
        objOut.writeObject(snakeGame.getScore());
        objOut.writeObject(snakeGame.getFruitsEaten());
        objOut.writeObject(snakeGame.getNextFruitScore());
        objOut.writeObject(snakeGame.isInit());
        objOut.writeObject(snakeGame.getDirections());
        objOut.writeObject(snakeGame.getFactor());
        objOut.writeObject(snakeGame.getSnake());
        objOut.writeObject(snakeGame.getBoard().getT());
    }

    private static void readVariables(SnakeGame snakeGame, ObjectInputStream objIn) throws
                                                                                    IOException,
                                                                                    ClassNotFoundException {
        snakeGame.setNewGame((boolean) objIn.readObject());
        snakeGame.setIsGameOver((boolean) objIn.readObject());
        snakeGame.setIsPaused((boolean) objIn.readObject());
        snakeGame.setScore((int) objIn.readObject());
        snakeGame.setFruitsEaten((int) objIn.readObject());
        snakeGame.setNextFruitScore((int) objIn.readObject());
        snakeGame.setInit((boolean) objIn.readObject());
        snakeGame.setDirections((LinkedList<Direction>) objIn.readObject());
        snakeGame.setFactor((int) objIn.readObject());
        snakeGame.setSnake((LinkedList<Point>) objIn.readObject());
        snakeGame.getBoard().setTiles((TileType[]) objIn.readObject());

    }
}
