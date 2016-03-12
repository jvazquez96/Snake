package snake;


import java.awt.*;
import java.io.*;
import java.util.LinkedList;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author JorgeVazquez
 */
public class StateHandler {
    
    /**
     * Saves the current game member variables to a binary file.
     * @param snakeGame
     */
    public static void saveGame(SnakeGame snakeGame) {
        try {
            /*
			 * Save a serialized version of the individual member variables
			 * in the received Snake instance
			 */
            ObjectOutputStream objOut = new ObjectOutputStream(
                    new FileOutputStream("saveGame.bin"));
            writeVariables(snakeGame, objOut);
            objOut.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void loadGame(SnakeGame snakeGame) {
        /*
	     * Load a serialized version of a previous game state from
	     * a binary file, and set each member variable in the received Snake
	     * instance
		 */
        try {
            ObjectInputStream objIn = new ObjectInputStream(
                    new FileInputStream("saveGame.bin"));
            readVariables(snakeGame, objIn);
            objIn.close();
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println("Could not load the previous game state");
            e.printStackTrace();
        }
    }
    private static void writeVariables(SnakeGame snakeGame, ObjectOutputStream objOut) throws IOException{
        objOut.writeObject(snakeGame.isNewGame());
        objOut.writeObject(snakeGame.isGameOver());
        objOut.writeObject(snakeGame.isPaused());
        objOut.writeObject(snakeGame.getScore());
        objOut.writeObject(snakeGame.getFruitsEaten());
        objOut.writeObject(snakeGame.getDirection());
        objOut.writeObject(snakeGame.getSnake());
        objOut.writeObject(snakeGame.getBoard().getT());
    }
    private static void readVariables(SnakeGame snakeGame, ObjectInputStream objIn) throws IOException, ClassNotFoundException{
        snakeGame.setNewGame((boolean) objIn.readObject());
        snakeGame.setIsGameOver((boolean) objIn.readObject());
        snakeGame.setIsPaused((boolean) objIn.readObject());
        snakeGame.setScore((int) objIn.readObject());
        snakeGame.setFruitsEaten((int) objIn.readObject());
        snakeGame.setDirection((Direction) objIn.readObject());
        snakeGame.setSnake((LinkedList<Point>) objIn.readObject());
        snakeGame.getBoard().setTiles((TileType[]) objIn.readObject());
       
    }
}
