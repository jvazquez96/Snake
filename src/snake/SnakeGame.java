package snake;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

import static snake.StateHandler.loadGame;
import static snake.StateHandler.saveGame;

/**
 * The {@code SnakeGame} class is responsible for handling much of the game's logic.
 *
 * @author Brendan Jones
 */
public class SnakeGame extends JFrame {

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
    public static final float CYCLES_PER_SECOND = 9.0f;
    public static final long MILLION = 1000000L;

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
        brdBoard = new BoardPanel(this);
        sidSide = new SidePanel(this);
        shaShaker = new ShakeFrame(this);
        add(brdBoard, BorderLayout.CENTER);
        add(sidSide, BorderLayout.EAST);

        /*
         * Adds a new key listener to the frame to process input.
         */
        addKeyListener(new snakeKeyAdapter());

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
        rRandom = new Random();
        snake = new LinkedList<>();
        directions = new LinkedList<>();
        clkLogicTimer = new Clock(CYCLES_PER_SECOND);
        bNewGame = true;

        // Set the timer to paused initially.
        clkLogicTimer.setPaused(true);

        /*
         * This is the game loop. It will update and render the game and will
         * continue to run until the game window is closed.
         */
        while (true) {
            //Get the current frame's start time.
            final long start = System.nanoTime();

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
            final long delta = (System.nanoTime() - start) / MILLION;
            if (delta < lFRAME_TIME) {
                try {
                    Thread.sleep(lFRAME_TIME - delta);
                }
                catch (final Exception e) {
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
        final TileType collision = updateSnake();

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
            if ((collision == TileType.SnakeBody) || (collision == TileType
                    .BadFruit)) {
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
        final Direction direction = directions.peekFirst();

        /*
         * Here we calculate the new point that the snake's head will be at
         * after the update.
         */
        final Point head = new Point(snake.peekFirst());
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
        if (hasMovedOutOfBounds(head)) {
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
        if ((old != TileType.Fruit) && (snake.size() > iMIN_SNAKE_LENGTH)) {
            final Point tail = snake.removeLast();
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

    private boolean hasMovedOutOfBounds(final Point head) {
        if (head.x < 0) {
            return true;
        }
        if (head.x >= BoardPanel.iCOL_COUNT) {
            return true;
        }
        return (head.y < 0) || (head.y >= BoardPanel.iROW_COUNT);
    }

    /**
     * Resets the game's variables to their default states and starts a new game.
     */
    private void resetGame() {
        /*
         * Reset the iScore statistics. (Note that nextFruitPoints is reset in
         * the spawnFruit function later on).
         */
        iScore = 0;
        iFruitsEaten = 0;

        /*
         * Reset both the new game and game over flags.
         */
        bNewGame = false;
        bGameOver = false;
        bInit = true;

        /*
         * Create the head at the center of the board.
         */
        final Point head = new Point(BoardPanel.iCOL_COUNT / 2,
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
        int index = rRandom.nextInt((BoardPanel.iCOL_COUNT * BoardPanel
                .iROW_COUNT) - snake
                .size());

        while (iCounter > 0) {
            nextFruitScore = 100;
            //Randomize the factor for each value
            final int iRandom = (int) ((Math.random() * ((4 - 1) + 1)) + 1);
            findFreeTiles(iRandom, index);
            --iCounter;
            index = rRandom.nextInt((BoardPanel.iCOL_COUNT * BoardPanel
                    .iROW_COUNT) - snake
                    .size());
        }
        bInit = false;

    }


    private void spawnOneFruit() {
        //Randomize the value for one fruit
        nextFruitScore = 100;
        final int iRandom = (int) ((Math.random() * ((4 - 1) + 1)) + 1);
        final int index = rRandom.nextInt((BoardPanel.iCOL_COUNT * BoardPanel
                .iROW_COUNT) - snake
                .size());
        findFreeTiles(iRandom, index);
        for (int iC = 0; iC < iFactor; ++iC) {
            final Point head = new Point(snake.peekFirst());
            brdBoard.setTile(snake.peekFirst(), TileType.SnakeBody);
            snake.push(head);
            brdBoard.setTile(head, TileType.SnakeHead);
            if (directions.size() > 1) {
                directions.poll();
            }

        }
    }

    private void findFreeTiles(final int iRandom, final int index) {
        int freeFound = -1;
        for (int x = 0; x < BoardPanel.iCOL_COUNT; x++) {
            for (int y = 0; y < BoardPanel.iROW_COUNT; y++) {
                final TileType type = brdBoard.getTile(x, y);
                if ((type == null) || (type == TileType.Fruit)) {
                    ++freeFound;
                    if (freeFound == index) {
                        brdBoard.setTile(x, y, TileType.Fruit, iRandom);
                        break;
                    }
                }
            }
        }
    }

    private void spawnBadFruits() {
        int iCounter = 3;
        int index = rRandom.nextInt((BoardPanel.iCOL_COUNT * BoardPanel
                .iROW_COUNT) - snake
                .size());

        while (iCounter > 0) {
            int freeFound = -1;
            for (int x = 0; x < BoardPanel.iCOL_COUNT; x++) {
                for (int y = 0; y < BoardPanel.iROW_COUNT; y++) {
                    TileType type = brdBoard.getTile(x, y);
                    if ((type == null) || (type == TileType.BadFruit)) {
                        ++freeFound;
                        if (freeFound == index) {
                            brdBoard.setTile(x, y, TileType.BadFruit, -1);
                            break;
                        }
                    }
                }
            }
            --iCounter;
            index = rRandom.nextInt((BoardPanel.iCOL_COUNT * BoardPanel
                    .iROW_COUNT) - snake
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

    public List getSnake() {
        return snake;
    }

    public BoardPanel getBoard() {
        return brdBoard;
    }

    public void setNewGame(final boolean isNewGame) {
        bNewGame = isNewGame;
    }

    public void setIsGameOver(final boolean isGameOver) {
        bGameOver = isGameOver;
    }

    public void setNextFruitScore(final int nextFruitScore) {
        this.nextFruitScore = nextFruitScore;
    }

    public boolean isInit() {
        return bInit;
    }

    public void setInit(final boolean bInit) {
        this.bInit = bInit;
    }

    public int getFactor() {
        return iFactor;
    }

    public void setFactor(final int iFactor) {
        this.iFactor = iFactor;
    }

    public void setIsPaused(final boolean isPaused) {
        bPaused = isPaused;
    }

    public void setScore(final int score) {
        iScore = score;
    }

    public void setFruitsEaten(final int fruitsEaten) {
        iFruitsEaten = fruitsEaten;
    }

    public void setDirection(final Direction direction) {
        directions.addFirst(direction);
        //this.directions.addLast(directions);
    }

    public void setDirections(final LinkedList<Direction> directions) {
        this.directions = directions;
    }

    public void setSnake(final LinkedList<Point> snake) {
        this.snake.clear();
        this.snake = snake;
    }

    public void setBoard(final BoardPanel board) {
        brdBoard.clearBoard();
        brdBoard = board;
    }

    /**
     * Entry point of the program.
     *
     * @param args Unused.
     */
    public static void main(final String[] args) {
        final SnakeGame snake = new SnakeGame();
        snake.startGame();
    }

    public List<Direction> getDirections() {
        return directions;
    }

    private class snakeKeyAdapter extends KeyAdapter {

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
                    pressedUp();
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
                    pressedDown();
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
                    pressedLeft();
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
                    pressedRight();
                    break;

                /*
                 * If the game is not over, toggle the paused flag and update
                 * the clkLogicTimer's pause flag accordingly.
                 */
                case KeyEvent.VK_P:
                    pauseUnpause();
                    break;

                /*
                 * Reset the game if one is not currently in progress.
                 */
                case KeyEvent.VK_ENTER:
                    pressedEnter();
                    break;
                case KeyEvent.VK_G:
                    tryToSave();
                    break;
                case KeyEvent.VK_C:
                    tryToLoad();
                    break;
            }

        }

        private void tryToLoad() {
            // Pause the game before loading
            if (!bPaused) {
                bPaused = true;
                clkLogicTimer.setPaused(true);
            }
            loadGame(SnakeGame.this);
        }

        private void tryToSave() {
            if (!bGameOver && !bNewGame) {
                // Pause the game before saving
                if (!bPaused) {
                    bPaused = true;
                    clkLogicTimer.setPaused(true);
                }
                saveGame(SnakeGame.this);
            }
            else {
                JOptionPane.showMessageDialog(null,
                                              "You cannot save " +
                                                      "when you " +
                                                      "are " +
                                                      "outside a " +
                                                      "game",
                                              "Not in a game",
                                              JOptionPane.ERROR_MESSAGE);
            }
        }

        private void pressedEnter() {
            if (bNewGame || bGameOver) {
                // Unpause the game if it is paused
                if (bPaused) {
                    bPaused = false;
                    clkLogicTimer.setPaused(false);
                }
                resetGame();
            }
        }

        private void pauseUnpause() {
            if (!bGameOver) {
                bPaused = !bPaused;
                clkLogicTimer.setPaused(bPaused);
            }
        }

        private void pressedRight() {
            if (!bPaused && !bGameOver) {
                if (directions.size() < iMAX_DIRECTIONS) {
                    final Direction last = directions.peekLast();
                    if ((last != Direction.West) && (last !=
                            Direction.East)) {
                        directions.addLast(Direction.East);
                    }
                }
            }
        }

        private void pressedLeft() {
            if (!bPaused && !bGameOver) {
                if (directions.size() < iMAX_DIRECTIONS) {
                    final Direction last = directions.peekLast();
                    if ((last != Direction.East) && (last !=
                            Direction.West)) {
                        directions.addLast(Direction.West);
                    }
                }
            }
        }

        private void pressedDown() {
            if (!bPaused && !bGameOver) {
                if (directions.size() < iMAX_DIRECTIONS) {
                    final Direction last = directions.peekLast();
                    if ((last != Direction.North) && (last !=
                            Direction.South)) {
                        directions.addLast(Direction.South);
                    }
                }
            }
        }

        private void pressedUp() {
            if (!bPaused && !bGameOver) {
                if (directions.size() < iMAX_DIRECTIONS) {
                    final Direction last = directions.peekLast();
                    if ((last != Direction.South) && (last !=
                            Direction.North)) {
                        directions.addLast(Direction.North);
                    }
                }
            }
        }

    }
}