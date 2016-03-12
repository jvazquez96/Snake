package snake;


import javax.swing.*;
import java.awt.*;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.Point2D;
import java.util.Random;

/**
 * The {@code BoardPanel} class is responsible for managing and displaying the
 * contents of the game board.
 * @author Brendan Jones
 *
 */
public class BoardPanel extends JPanel {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -1102632585936750607L;

    /**
     * The number of columns on the board. (Should be odd so we can start in
     * the center).
     */
    public static final int iCOL_COUNT = 25;

    /**
     * The number of rows on the board. (Should be odd so we can start in
     * the center).
     */
    public static final int iROW_COUNT = 25;

    /**
     * The size of each tile in pixels.
     */
    public static final int iTILE_SIZE = 20;

    /**
     * The number of extra pixels that the glow of a tile takes up.
     */
    public static final int iGLOW_OFFSET = 1;

    /**
     * The number of pixels to offset the eyes from the sides.
     */
    private static final int iEYE_LARGE_INSET = iTILE_SIZE / 3;

    /**
     * The number of pixels to offset the eyes from the front.
     */
    private static final int iEYE_SMALL_INSET = iTILE_SIZE / 6;

    /**
     * The length of the eyes from the base (small inset).
     */
    private static final int iEYE_LENGTH = iTILE_SIZE / 5;

    /**
     * The font to draw the text with.
     */
    private static final Font FONT = new Font("Tahoma", Font.BOLD, 26);
    public static final float ALPHA_THRESHOLD = 0.80f;
    public static final float BRIGHT_RADIAL = 0.10f;
    public static final float DARK_RADIAL = .90f;

    /**
     * The current amount of alpha a tile is being drawn with in order to
     * animate a "shining" effect.
     */
    private float fAlphaAmount;

    /**
     * How much is the current alpha being modified with the drawing of each
     * tile.
     */
    private float fAlphaFactor;

    /**
     * The level of displacement from the gradient center to animate motion.
     */
    private float iGradientModifier;

    /**
     * The background image to be displayed
     */
    private Image imgBackground;

    /**
     * Integer to chose which image to paint
     */
    private int iImageChoice;

    /**
     * The current fruit color
     */
    private Color cFruitColor;


    /**
     * Amount to displace the background to give a parallax effect
     */
    private float iBackgroundDisplacement;


    /**
     * Rate and direction at which the background is being displaced
     */
    private float iDisplacementFactor;

    /**
     * The SnakeGame instance.
     */
    private SnakeGame snkGame;

    /**
     * The array of tiles that make up this board.
     */
    private TileType[] tiles;

    public void setTiles(TileType[] tilMat){
        tiles = tilMat;
    }

    public TileType[] getT() {
        return tiles;
    }

    /**
     * Creates a new BoardPanel instance.
     * @param snkGame The SnakeGame instance.
     */
    public BoardPanel(final SnakeGame snkGame) {
        this.snkGame = snkGame;
        this.tiles = new TileType[iROW_COUNT * iCOL_COUNT];
        this.fAlphaAmount = 0.4f;
        this.cFruitColor = getRandomColor();
        this.fAlphaFactor = 0.06f;
        this.iGradientModifier = 0;
        this.iImageChoice = 1;
        this.iBackgroundDisplacement = -2.0f;
        this.iDisplacementFactor = -0.3f;
        setPreferredSize(new Dimension(iCOL_COUNT * iTILE_SIZE, iROW_COUNT * iTILE_SIZE));
        setBackground(Color.DARK_GRAY.darker().darker());
    }

    /**
     * Clears all of the tiles on the board and sets their values to null.
     */
    public void clearBoard() {
        for(int i = 0; i < tiles.length; i++) {
            tiles[i] = null;
        }
    }

    /**
     * Sets the tile at the desired coordinate.
     * @param point The coordinate of the tile.
     * @param type The type to set the tile to.
     */
    public void setTile(final Point point, final TileType type) {
        setTile(point.x, point.y, type,0);
    }

    /**
     * Sets the tile at the desired coordinate.
     * @param x The x coordinate of the tile.
     * @param y The y coordinate of the tile.
     * @param type The type to set the tile to.
     */
    public void setTile(final int x, final int y, final TileType type, final int iValue) {
        tiles[(y * iROW_COUNT) + x] = type;
        if (type != null){
            type.setValue(iValue);
        }
    }

    /**
     * Sets the current alpha amount
     * @param fAlphaAmount The alpha amount
     */
    public void setAlphaAmount(final float fAlphaAmount) {
        this.fAlphaAmount = fAlphaAmount;
    }

    /**
     * Sets the current alpha factor
     * @param fAlphaFactor The alpha factor
     */
    public void setAlphaFactor(final float fAlphaFactor) {
        this.fAlphaFactor = fAlphaFactor;
    }



    /**
     * Gets the tile at the desired coordinate.
     * @param x The x coordinate of the tile.
     * @param y The y coordinate of the tile.
     * @return
     */
    public TileType getTile(int x, int y) {
        return tiles[(y * iROW_COUNT) + x];
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateAlphaValue();
        drawTiles(g);
        drawGrid(g);
        drawMessage(g);
    }

    private void drawMessage(final Graphics g) {
    /*
     * Show a message on the screen based on the current game state.
     */
        if(snkGame.isGameOver() || snkGame.isNewGame() || snkGame.isPaused()) {
            g.setColor(Color.WHITE);

            /*
             * Get the center coordinates of the board.
             */
            final int centerX = getWidth() / 2;
            final int centerY = getHeight() / 2;

            /*
             * Allocate the messages for and set their values based on the game
             * state.
             */
            String largeMessage = "";
            String smallMessage = "";
            if(snkGame.isNewGame()) {
                largeMessage = "Snake Game!";
                smallMessage = "Press Enter to Start";
            } else if(snkGame.isGameOver()) {
                largeMessage = "Game Over!";
                smallMessage = "Press Enter to Restart";
            } else if(snkGame.isPaused()) {
                largeMessage = "Paused";
                smallMessage = "Press P to Resume";
            }

            /*
             * Draw a light gray shadow before the main text
             */
            g.setFont(FONT);
            g.setColor(Color.LIGHT_GRAY);
            g.drawString(largeMessage,
                         centerX - (g.getFontMetrics()
                                     .stringWidth(largeMessage) / 2),
                         (centerY - 50) + 1);
            g.drawString(smallMessage,
                         centerX - (g.getFontMetrics()
                                     .stringWidth(smallMessage) / 2),
                         centerY + 50 + 1);

            /*
             * Set the message font and draw the messages in the center of the board.
             */
            g.setColor(Color.WHITE);
            g.drawString(largeMessage,
                         centerX - (g.getFontMetrics()
                                     .stringWidth(largeMessage) / 2), centerY - 50);
            g.drawString(smallMessage,
                         centerX - (g.getFontMetrics()
                                     .stringWidth(smallMessage) / 2), centerY + 50);
        }
    }

    private void drawTiles(Graphics g) {
    /*
     * Loop through each tile on the board and draw it if it
     * is not null.
     */
        for(int x = 0; x < iCOL_COUNT; x++) {
            for(int y = 0; y < iROW_COUNT; y++) {
                TileType type = getTile(x, y);
                if(type != null) {
                    // Draw tile and glow
                    drawTile(x * iTILE_SIZE, y * iTILE_SIZE, type, g, fAlphaAmount);
                }
            }
        }
    }

    private void drawGrid(Graphics g) {
    /*
     * Draw the grid on the board. This makes it easier to see exactly
     * where we in relation to the fruit.
     *
     * The panel is one pixel too small to draw the bottom and right
     * outlines, so we outline the board with a rectangle separately.
     */
        g.setColor(Color.DARK_GRAY);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        for(int x = 0; x < iCOL_COUNT; x++) {
            for(int y = 0; y < iROW_COUNT; y++) {
                g.drawLine(x * iTILE_SIZE, 0, x * iTILE_SIZE, getHeight());
                g.drawLine(0, y * iTILE_SIZE, getWidth(), y * iTILE_SIZE);
            }
        }
    }

    private void updateAlphaValue() {
        fAlphaAmount += fAlphaFactor;
    /*
     * When the alpha has reached the maximum value, start decreasing
     * it and viceversa.
     */
        if(fAlphaAmount >= ALPHA_THRESHOLD){
            fAlphaAmount = 0.75f;
            fAlphaFactor *= -1;
            cFruitColor = getRandomColor();
        }
        else if(fAlphaAmount <= 0.200f){
            fAlphaAmount = 0.3f;
            fAlphaFactor *= -1;
            cFruitColor = getRandomColor();
        }
        if(iGradientModifier > 12){
            iGradientModifier = 0;
        }
    }

    /**
     * Draws a tile onto the board.
     * @param iX The x coordinate of the tile (in pixels).
     * @param iY The y coordinate of the tile (in pixels).
     * @param type The type of tile to draw.
     * @param g The graphics object to draw to.
     */
    private void drawTile(int iX, int iY, TileType type, Graphics g,
                          final float fAlphaValue) {
        /*
         * Create a new Graphics2D instance to allow alpha to be drawn into
         * the object. Then save the current composite to restore normal
         * non-alpha painting.
         */
        Graphics2D g2d = (Graphics2D) g;
        Composite cCurrentComposite = g2d.getComposite();


        /*
         * Because each type of tile is drawn differently, it's easiest
         * to just run through a switch statement rather than come up with some
         * overly complex code to handle everything.
         */
        switch(type) {
            /*
             * A fruit is depicted as a small red circle that with a bit of padding
             * on each side.
             */
            case Fruit:
                g2d.setColor(Color.MAGENTA.brighter());
                g2d.fillOval(iX + 2, iY + 2, iTILE_SIZE - 4, iTILE_SIZE - 4);

                /*
                 * Create a radial gradient with the light and dark colors to give the
                 * tile a more dynamic look. The increasing iGradientModifier gives the
                 * effect of the tile shining
                 */
                final Point2D center = new Point2D.Float((iX / 2) + iGradientModifier,
                                                         iY / 2);
                iGradientModifier += .1;
                final float radius = 14;
                final float[] dist = {BRIGHT_RADIAL, DARK_RADIAL};
                final Color[] colors = {cFruitColor.brighter(), cFruitColor.darker
                        ()};
                RadialGradientPaint paint =
                        new RadialGradientPaint(center,
                                                radius,
                                                dist,
                                                colors,
                                                CycleMethod.REFLECT);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,

                                                            fAlphaValue));
                g2d.setPaint(paint);
                g2d.fillOval(iX + 1,
                             iY + 1,
                             iTILE_SIZE - 2,
                             iTILE_SIZE - 2);
                g2d.setComposite(cCurrentComposite);
                break;

            /*
            * A bad fruit is depicted as a small blue circe that with a bit of padding
            * on each side
            */
            case BadFruit:
                final Random rRandom = new Random();
                int iNegativizer = rRandom.nextInt(2);
                if (iNegativizer != 1) {
                    iNegativizer = -1;
                }
                g.setColor(Color.CYAN.darker().darker());
                final int iEnlarge = 40;
                g.fillRect((iX + 2 + (rRandom.nextInt(2) * iNegativizer)) -
                                   getExpansion(iEnlarge / 2),
                           (iY + 2 + (rRandom.nextInt(2) * iNegativizer)) -
                                   getExpansion(iEnlarge / 2),
                           (iTILE_SIZE - 4) + getExpansion(iEnlarge),
                           (iTILE_SIZE - 4) + getExpansion(iEnlarge));
                break;

            /*
             * The snake body is depicted as a green square that takes up the
             * entire tile.
             */
            case SnakeBody:
                g.setColor(getRandomColor().brighter().brighter());
                g.fillRect(iX, iY, iTILE_SIZE, iTILE_SIZE);
                break;

            /*
             * The snake head is depicted similarly to the body, but with two
             * lines (representing eyes) that indicate it's direction.
             */
            case SnakeHead:
                //Fill the tile in with green.
                g.setColor(getRandomColor().brighter().brighter());
                g.fillRect(iX, iY, iTILE_SIZE, iTILE_SIZE);

                //Set the color to black so that we can start drawing the eyes.
                g.setColor(Color.BLACK);

                /*
                 * The eyes will always 'face' the direction that the snake is
                 * moving.
                 *
                 * Vertical lines indicate that it's facing North or South, and
                 * Horizontal lines indicate that it's facing East or West.
                 *
                 * Additionally, the eyes will be closer to whichever edge it's
                 * facing.
                 *
                 * Drawing the eyes is fairly simple, but is a bit difficult to
                 * explain. The basic process is this:
                 *
                 * First, we add (or subtract) iEYE_SMALL_INSET to or from the
                 * side of the tile representing the direction we're facing. This
                 * will be constant for both eyes, and is represented by the
                 * variable 'baseX' or 'baseY' (depending on orientation).
                 *
                 * Next, we add (or subtract) iEYE_LARGE_INSET to and from the two
                 * neighboring directions (Example; East and West if we're facing
                 * north).
                 *
                 * Finally, we draw a line from the base offset that is iEYE_LENGTH
                 * pixels in length at whatever the offset is from the neighboring
                 * directions.
                 *
                 */
                switch (snkGame.getDirection()) {
                    case North: {
                        final int baseY = iY + iEYE_SMALL_INSET;
                        g.drawLine(iX + iEYE_LARGE_INSET,
                                   baseY,
                                   iX + iEYE_LARGE_INSET,
                                   baseY + iEYE_LENGTH);
                        g.drawLine((iX + iTILE_SIZE) - iEYE_LARGE_INSET,
                                   baseY,
                                   (iX + iTILE_SIZE) - iEYE_LARGE_INSET,
                                   baseY + iEYE_LENGTH);
                        break;
                    }

                    case South: {
                        final int baseY = (iY + iTILE_SIZE) - iEYE_SMALL_INSET;
                        g.drawLine(iX + iEYE_LARGE_INSET,
                                   baseY,
                                   iX + iEYE_LARGE_INSET,
                                   baseY - iEYE_LENGTH);
                        g.drawLine((iX + iTILE_SIZE) - iEYE_LARGE_INSET,
                                   baseY,
                                   (iX + iTILE_SIZE) - iEYE_LARGE_INSET,
                                   baseY - iEYE_LENGTH);
                        break;
                    }
                    case West: {
                        final int baseX = iX + iEYE_SMALL_INSET;
                        g.drawLine(baseX,
                                   iY + iEYE_LARGE_INSET,
                                   baseX + iEYE_LENGTH,
                                   iY + iEYE_LARGE_INSET);
                        g.drawLine(baseX,
                                   (iY + iTILE_SIZE) - iEYE_LARGE_INSET,
                                   baseX + iEYE_LENGTH,
                                   (iY + iTILE_SIZE) - iEYE_LARGE_INSET);
                        break;
                    }

                    case East: {
                        final int baseX = (iX + iTILE_SIZE) - iEYE_SMALL_INSET;
                        g.drawLine(baseX,
                                   iY + iEYE_LARGE_INSET,
                                   baseX - iEYE_LENGTH,
                                   iY + iEYE_LARGE_INSET);
                        g.drawLine(baseX,
                                   (iY + iTILE_SIZE) - iEYE_LARGE_INSET,
                                   baseX - iEYE_LENGTH,
                                   (iY + iTILE_SIZE) - iEYE_LARGE_INSET);
                        break;
                    }
                }
                break;
        }
    }

    private Color getRandomColor(){
        final Random rRandom = new Random();
        final float fRed = rRandom.nextFloat();
        final float fBlue = rRandom.nextFloat();
        final float fGreen = rRandom.nextFloat();
        return new Color(fRed, fBlue, fGreen);
    }

    // Creates an expansion value based on the current alpha value
    int getExpansion(final int iAmplifier){
        return (int) (fAlphaAmount * iAmplifier * fAlphaFactor);
    }

}