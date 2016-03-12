package snake;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Random;

import static snake.StateHandler.loadGame;
import static snake.StateHandler.saveGame;

/**
 * The {@code SnakeGame} class is responsible for handling much of the game's logic.
 *
 * @author Brendan Jones
 */
public class SnakeGame extends JFrame implements Serializable {

    /**
     * The Serial Version UID.
     */
    private static final long serialVersionUID = 6678292058307426314L;

    /**
     * The number of milliseconds that should pass between each frame.
     */
    private static final long lFRAME_TIME = 1000L / 50L;

    /**
     * The minimum length of the snake. This allows the snake to grow
     * right when the game starts, so that we're not just a head moving
     * around on the board.
     */
    private static final int iMIN_SNAKE_LENGTH = 5;

    /**
     * The maximum number of directions that we can have polled in the
     * direction list.
     */
    private static final int iMAX_DIRECTIONS = 3;

    /**
     * The BoardPanel instance.
     */
    private BoardPanel brdBoard;

    /**
     * The SidePanel instance.
     */
    private SidePanel sidSide;

    /**
     * The random number generator (used for spawning fruits).
     */
    private Random rRandom;

    /**
     * The Clock instance for handling the game logic.
     */
    private Clock clkLogicTimer;


    /**
     * Whether or not we're running a new game.
     */
    private boolean bNewGame;

    /**
     * Whether or not the game is over.
     */
    private boolean bGameOver;

    /**
     * Whether or not the game is paused.
     */
    private boolean bPaused;

    /**
     * The shaker helper object for the frame
     */
    private ShakeFrame shaShaker;

    /**
     * The list that contains the points for the snake.
     */
    private LinkedList<Point> snake;

    /**
     * The list that contains the queued directions.
     */
    private LinkedList<Direction> directions;

    /**
     * The current iScore.
     */
    private int iScore;

    /**
     * The number of fruits that we've eaten.
     */
    private int iFruitsEaten;

    /**
     * The number of points that the next fruit will award us.
     */
    private int nextFruitScore;
    /**
     * Check if we are the first time playing
     */
    private boolean bInit;
    /**
     * A random number assigned to each fruit
     */
    private int iFactor;

    /**
     * Action when the snake goes up
     */
    private void up(){
        if (!bPaused && !bGameOver) {
           if (directions.size() < iMAX_DIRECTIONS) {
                Direction last = directions.peekLast();
                if (last != Direction.South && last != Direction.North) {
                    directions.addLast(Direction.North);
                }
            }
        }
    }
    /**
     * Action when the snake goes down
     */
    private void down(){
        if (!bPaused && !bGameOver) {
           if (directions.size() < iMAX_DIRECTIONS) {
            Direction last = directions.peekLast();
                if (last != Direction.North && last != Direction.South) {
                    directions.addLast(Direction.South);
                }
            }
        }        
    }
    /**
     * Action when the snake goes left
     */
    private void left(){
        if (!bPaused && !bGameOver) {
            if (directions.size() < iMAX_DIRECTIONS) {
                Direction last = directions.peekLast();
                if (last != Direction.East && last != Direction.West) {
                   directions.addLast(Direction.West);
                }
            }
        }
    }
    /**
     * Action when the snake goes right
     */
    private void right(){
        if (!bPaused && !bGameOver) {
            if (directions.size() < iMAX_DIRECTIONS) {
                Direction last = directions.peekLast();
                if (last != Direction.West && last != Direction.East) {
                   directions.addLast(Direction.East);
                }
            }
        }    
    }
    /**
     * Creates a new SnakeGame instance. Creates a new window,
     * and sets up the controller input.
     */
    private SnakeGame() {
        super("Snake Remake");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        bInit = true;

        /*
         * Initialize the game's panels and add them to the window.
         */
        this.brdBoard = new BoardPanel(this);
        this.sidSide = new SidePanel(this);
        this.shaShaker = new ShakeFrame(this);
        add(brdBoard, BorderLayout.CENTER);
        add(sidSide, BorderLayout.EAST);

        /*
         * Adds a new key listener to the frame to process input.
         */
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    /*
                     * If the game is not paused, and the game is not over...
                     *
                     * Ensure that the direction list is not full, and that the most
                     * recent direction is adjacent to North before adding the
                     * direction to the list.
                     */
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_UP:
                        up();
                        break;

                    /*
                     * If the game is not paused, and the game is not over...
                     *
                     * Ensure that the direction list is not full, and that the most
                     * recent direction is adjacent to South before adding the
                     * direction to the list.
                     */
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_DOWN:
                        down();
                        break;

                    /*
                     * If the game is not paused, and the game is not over...
                     *
                     * Ensure that the direction list is not full, and that the most
                     * recent direction is adjacent to West before adding the
                     * direction to the list.
                     */
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_LEFT:
                        left();
                        break;

                    /*
                     * If the game is not paused, and the game is not over...
                     *
                     * Ensure that the direction list is not full, and that the most
                     * recent direction is adjacent to East before adding the
                     * direction to the list.
                     */
                    case KeyEvent.VK_D:
                    case KeyEvent.VK_RIGHT:
                        right();
                        break;

                    /*
                     * If the game is not over, toggle the paused flag and update
                     * the clkLogicTimer's pause flag accordingly.
                     */
                    case KeyEvent.VK_P:
                        if (!bGameOver) {
                            bPaused = !bPaused;
                            clkLogicTimer.setPaused(bPaused);
                        }
                        break;

                    /*
                     * Reset the game if one is not currently in progress.
                     */
                    case KeyEvent.VK_ENTER:
                        if (bNewGame || bGameOver) {
                            // Unpause the game if it is paused
                            if (bPaused) {
                                bPaused = false;
                                clkLogicTimer.setPaused(false);
                            }
                            resetGame();
                        }
                        break;
                    case KeyEvent.VK_G:
                        if (!bGameOver && !bNewGame) {
                            // Pause the game before saving
                            if (!bPaused) {
                                bPaused = true;
                                clkLogicTimer.setPaused(true);
                            }
                            saveGame(SnakeGame.this);
                        }
                        else{
                            JOptionPane.showMessageDialog(null,
                                                          "You cannot save " +
                                                                  "when you " +
                                                                  "are " +
                                                                  "outside a " +
                                                                  "game",
                                                          "Not in a game",
                                                          JOptionPane.ERROR_MESSAGE);
                        }
                        break;
                    case KeyEvent.VK_C:
                        // Pause the game before loading
                        if(!bPaused) {
                            bPaused = true;
                            clkLogicTimer.setPaused(true);
                        }
                        loadGame(SnakeGame.this);
                        break;
                }

            }

        });

        /*
         * Resize the window to the appropriate size, center it on the
         * screen and display it.
         */
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Starts the game running.
     */
    private void startGame() {
        /*
         * Initialize everything we're going to be using.
         */
        this.rRandom = new Random();
        this.snake = new LinkedList<>();
        this.directions = new LinkedList<>();
        this.clkLogicTimer = new Clock(9.0f);
        this.bNewGame = true;

        // Set the timer to paused initially.
        clkLogicTimer.setPaused(true);

        /*
         * This is the game loop. It will update and render the game and will
         * continue to run until the game window is closed.
         */
        while (true) {
            //Get the current frame's start time.
            long start = System.nanoTime();

            //Update the logic timer.
            clkLogicTimer.update();

            /*
             * If a cycle has elapsed on the logic timer, then update the game.
             */
            if (clkLogicTimer.hasElapsedCycle()) {
                updateGame();
            }

            //Repaint the board and side panel with the new content.
            brdBoard.repaint();
            sidSide.repaint();

            /*
             * Calculate the delta time between since the start of the frame
             * and sleep for the excess time to cap the frame rate. While not
             * incredibly accurate, it is sufficient for our purposes.
             */
            long delta = (System.nanoTime() - start) / 1000000L;
            if (delta < lFRAME_TIME) {
                try {
                    Thread.sleep(lFRAME_TIME - delta);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Updates the game's logic.
     */
    private void updateGame() {
        /*
         * Gets the type of tile that the head of the snake collided with. If
         * the snake hit a wall, SnakeBody will be returned, as both conditions
         * are handled identically.
         */
        TileType collision = updateSnake();

        /*
         * Here we handle the different possible collisions.
         *
         * Fruit: If we collided with a fruit, we increment the number of
         * fruits that we've eaten, update the iScore, and spawn a new fruit.
         *
         * SnakeBody: If we collided with our tail (or a wall), we flag that
         * the game is over and pause the game.
         *
         * If no collision occurred, we simply decrement the number of points
         * that the next fruit will give us if it's high enough. This adds a
         * bit of skill to the game as collecting fruits more quickly will
         * yield a higher iScore.
         */
        if (collision == TileType.Fruit) {
            iFruitsEaten++;
            iScore += nextFruitScore * collision.getValue();
            iFactor = collision.getValue();
            spawnFruit();
        }
        else {
            if (collision == TileType.SnakeBody || collision == TileType.BadFruit) {
                shaShaker.startShaking();
                bGameOver = true;
                clkLogicTimer.setPaused(true);
            }
            else {
                if (nextFruitScore > 10) {
                    nextFruitScore--;
                }
            }
        }

    }

    /**
     * Updates the snake's position and size.
     *
     * @return Tile tile that the head moved into.
     */
    private TileType updateSnake() {

        /*
         * Here we peek at the next direction rather than polling it. While
         * not game breaking, polling the direction here causes a small bug
         * where the snake's direction will change after a game over (though
         * it will not move).
         */
        Direction direction = directions.peekFirst();

        /*
         * Here we calculate the new point that the snake's head will be at
         * after the update.
         */
        Point head = new Point(snake.peekFirst());
        switch (direction) {
            case North:
                head.y--;
                break;

            case South:
                head.y++;
                break;

            case West:
                head.x--;
                break;

            case East:
                head.x++;
                break;
        }

        /*
         * If the snake has moved out of bounds ('hit' a wall), we can just
         * return that it's collided with itself, as both cases are handled
         * identically.
         */
        if (head.x < 0 || head.x >= BoardPanel.iCOL_COUNT || head.y < 0 || head.y >= BoardPanel.iROW_COUNT) {
            return TileType.SnakeBody; //Pretend we collided with our body.
        }

        /*
         * Here we get the tile that was located at the new head position and
         * remove the tail from of the snake and the board if the snake is
         * long enough, and the tile it moved onto is not a fruit.
         *
         * If the tail was removed, we need to retrieve the old tile again
         * incase the tile we hit was the tail piece that was just removed
         * to prevent a false game over.
         */
        TileType old = brdBoard.getTile(head.x, head.y);
        if (old != TileType.Fruit && snake.size() > iMIN_SNAKE_LENGTH) {
            Point tail = snake.removeLast();
            brdBoard.setTile(tail, null);
            old = brdBoard.getTile(head.x, head.y);
        }

        /*
         * Update the snake's position on the board if we didn't collide with
         * our tail:
         *
         * 1. Set the old head position to a body tile.
         * 2. Add the new head to the snake.
         * 3. Set the new head position to a head tile.
         *
         * If more than one direction is in the queue, poll it to read new
         * input.
         */
        if (old != TileType.SnakeBody) {
            brdBoard.setTile(snake.peekFirst(), TileType.SnakeBody);
            snake.push(head);
            brdBoard.setTile(head, TileType.SnakeHead);
            if (directions.size() > 1) {
                directions.poll();
            }
        }
        return old;
    }

    /**
     * Resets the game's variables to their default states and starts a new game.
     */
    private void resetGame() {
        /*
         * Reset the iScore statistics. (Note that nextFruitPoints is reset in
         * the spawnFruit function later on).
         */
        this.iScore = 0;
        this.iFruitsEaten = 0;

        /*
         * Reset both the new game and game over flags.
         */
        this.bNewGame = false;
        this.bGameOver = false;
        this.bInit = true;

        /*
         * Create the head at the center of the board.
         */
        Point head = new Point(BoardPanel.iCOL_COUNT / 2,
                               BoardPanel.iROW_COUNT / 2);

        /*
         * Clear the snake list and add the head.
         */
        snake.clear();
        snake.add(head);

        /*
         * Clear the board and add the head.
         */
        brdBoard.clearBoard();
        brdBoard.setTile(head, TileType.SnakeHead);

        /*
         * Clear the directions and add north as the
         * default direction.
         */
        directions.clear();
        directions.add(Direction.North);

        /*
         * Reset the logic timer.
         */
        clkLogicTimer.reset();

        /*
         * Spawn a new fruit.
         */
        spawnFruit();
        /*
         * Spawn bads fruits.
         */
        spawnBadFruits();
    }

    /**
     * Gets the flag that indicates whether or not we're playing a new game.
     *
     * @return The new game flag.
     */
    public boolean isNewGame() {
        return bNewGame;
    }

    /**
     * Gets the flag that indicates whether or not the game is over.
     *
     * @return The game over flag.
     */
    public boolean isGameOver() {
        return bGameOver;
    }

    /**
     * Gets the flag that indicates whether or not the game is paused.
     *
     * @return The paused flag.
     */
    public boolean isPaused() {
        return bPaused;
    }

    /**
     * Spawns a new fruit onto the board.
     */
    private void spawnFruit() {
                
        /*
         * Get a random index based on the number of free spaces left on the board.
         */
        int index = rRandom.nextInt(BoardPanel.iCOL_COUNT * BoardPanel.iROW_COUNT - snake
                .size());

        /*
         * While we could just as easily choose a random index on the board
         * and check it if it's free until we find an empty one, that method
         * tends to hang if the snake becomes very large.
         *
         * This method simply loops through until it finds the nth free index
         * and selects uses that. This means that the game will be able to
         * locate an index at a relatively constant rate regardless of the
         * size of the snake.
         */
        if (bInit) {
            spawnMultipleFruits();
        }
        else {
            spawnOneFruit();
        }
    }

    private void spawnMultipleFruits() {
        int iCounter = 3;
        int index = rRandom.nextInt(BoardPanel.iCOL_COUNT * BoardPanel.iROW_COUNT - snake
                .size());
        while (iCounter > 0) {
            this.nextFruitScore = 100;
            //Randomize the factor for each value
            int iRandom = (int) (Math.random() * ((4 - 1) + 1) + 1);
            int freeFound = -1;
            for (int x = 0; x < BoardPanel.iCOL_COUNT; x++) {
                for (int y = 0; y < BoardPanel.iROW_COUNT; y++) {
                    TileType type = brdBoard.getTile(x, y);
                    if (type == null || type == TileType.Fruit) {
                        if (++freeFound == index) {
                            brdBoard.setTile(x, y, TileType.Fruit, iRandom);
                            break;
                        }
                    }
                }
            }
            --iCounter;
            index = rRandom.nextInt(BoardPanel.iCOL_COUNT * BoardPanel.iROW_COUNT - snake
                    .size());
        }
        bInit = false;

    }


    private void spawnOneFruit() {
        //Randomize the value for one fruit
        //this.nextFruitScore = (int) (Math.random() * ((100-0) + 1) + 0);
        this.nextFruitScore = 100;
        int iRandom = (int) (Math.random() * ((4 - 1) + 1) + 1);
        int freeFound = -1;
        int index = rRandom.nextInt(BoardPanel.iCOL_COUNT * BoardPanel.iROW_COUNT - snake
                .size());
        for (int x = 0; x < BoardPanel.iCOL_COUNT; x++) {
            for (int y = 0; y < BoardPanel.iROW_COUNT; y++) {
                TileType type = brdBoard.getTile(x, y);
                if (type == null || type == TileType.Fruit) {
                    if (++freeFound == index) {
                        brdBoard.setTile(x, y, TileType.Fruit, iRandom);
                        break;
                    }
                }
            }
        }
        for (int iC = 0; iC < iFactor; ++iC) {
            Point head = new Point(snake.peekFirst());
            TileType tilOld = brdBoard.getTile(head.x, head.y);
            brdBoard.setTile(snake.peekFirst(), TileType.SnakeBody);
            snake.push(head);
            brdBoard.setTile(head, TileType.SnakeHead);
            if (directions.size() > 1) {
                directions.poll();
            }

        }
    }

    private void spawnBadFruits() {
        int iCounter = 3;
        int index = rRandom.nextInt(BoardPanel.iCOL_COUNT * BoardPanel.iROW_COUNT - snake
                .size());
        while (iCounter > 0) {
            int freeFound = -1;
            for (int x = 0; x < BoardPanel.iCOL_COUNT; x++) {
                for (int y = 0; y < BoardPanel.iROW_COUNT; y++) {
                    TileType type = brdBoard.getTile(x, y);
                    if (type == null || type == TileType.BadFruit) {
                        if (++freeFound == index) {
                            brdBoard.setTile(x, y, TileType.BadFruit, -1);
                            break;
                        }
                    }
                }
            }
            --iCounter;
            index = rRandom.nextInt(BoardPanel.iCOL_COUNT * BoardPanel.iROW_COUNT - snake
                    .size());
        }

    }

    /**
     * Gets the current iScore.
     *
     * @return The iScore.
     */
    public int getScore() {
        return iScore;
    }

    /**
     * Gets the number of fruits eaten.
     *
     * @return The fruits eaten.
     */
    public int getFruitsEaten() {
        return iFruitsEaten;
    }

    /**
     * Gets the next fruit iScore.
     *
     * @return The next fruit iScore.
     */
    public int getNextFruitScore() {
        return nextFruitScore;
    }

    /**
     * Gets the current direction of the snake.
     *
     * @return The current direction.
     */
    public Direction getDirection() {
        return directions.peek();
    }

    public LinkedList getSnake() {
        return snake;
    }

    public BoardPanel getBoard() {
        return this.brdBoard;
    }

    public void setNewGame(boolean isNewGame) {
        this.bNewGame = isNewGame;
    }

    public void setIsGameOver(boolean isGameOver) {
        this.bGameOver = isGameOver;
    }

    public void setNextFruitScore(int nextFruitScore) {
        this.nextFruitScore = nextFruitScore;
    }

    public boolean isInit() {
        return bInit;
    }

    public void setInit(boolean bInit) {
        this.bInit = bInit;
    }

    public int getFactor() {
        return iFactor;
    }

    public void setFactor(int iFactor) {
        this.iFactor = iFactor;
    }

    public void setIsPaused(boolean isPaused) {
        this.bPaused = isPaused;
    }

    public void setScore(int score) {
        this.iScore = score;
    }

    public void setFruitsEaten(int fruitsEaten) {
        this.iFruitsEaten = fruitsEaten;
    }

    public void setDirection(Direction direction) {
        this.directions.addFirst(direction);
        //this.directions.addLast(directions);
    }

    public void setDirections(LinkedList<Direction> directions) {
        this.directions = directions;
    }

    public void setSnake(LinkedList<Point> snake) {
        this.snake.clear();
        this.snake = snake;
    }

    public void setBoard(BoardPanel board) {
        this.brdBoard.clearBoard();
        this.brdBoard = board;
    }

    /**
     * Entry point of the program.
     *
     * @param args Unused.
     */
    public static void main(String[] args) {
        SnakeGame snake = new SnakeGame();
        snake.startGame();
    }

    public LinkedList<Direction> getDirections() {
        return directions;
    }
}