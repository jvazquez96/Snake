package snake;


import javax.swing.*;
import java.awt.*;

/**
 * The {@code SidePanel} class is responsible for displaying statistics and
 * controls to the player.
 *
 * @author Brendan Jones
 */
public class SidePanel extends JPanel {

    /**
     * Serial Version UID.
     */
    private static final long lSerialVersionUID = -40557434900946408L;

    /**
     * The large font to draw with.
     */
    private static final Font fLARGE_FONT = new Font("Tahoma", Font.BOLD, 20);

    /**
     * The medium font to draw with.
     */
    private static final Font fMEDIUM_FONT = new Font("Tahoma", Font.BOLD, 16);

    /**
     * The small font to draw with.
     */
    private static final Font fSMALL_FONT = new Font("Tahoma", Font.BOLD, 12);

    /**
     * The SnakeGame instance.
     */
    private SnakeGame snkGame;

    /**
     * Creates a new SidePanel instance.
     *
     * @param snkGame The SnakeGame instance.
     */
    public SidePanel(SnakeGame snkGame) {
        this.snkGame = snkGame;

        setPreferredSize(new Dimension(300,
                                       BoardPanel.iROW_COUNT * BoardPanel.iTILE_SIZE));
        setBackground(Color.DARK_GRAY.darker().darker());
    }

    private static final int STATISTICS_OFFSET = 104;

    private static final int CONTROLS_OFFSET = 248;

    private static final int MESSAGE_STRIDE = 30;

    private static final int SMALL_OFFSET = 25;

    private static final int LARGE_OFFSET = 50;

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);

        /*
         * Set the color to draw the font in to white.
         */
        g.setColor(Color.WHITE);

        /*
         * Draw the game name onto the window.
         */
        g.setFont(fLARGE_FONT);

        drawWithShadow(g,
                       (getWidth() / 2) - (g.getFontMetrics()
                                            .stringWidth("Snake Game") / 2),
                       50,
                       "Snake Game");
        /*
         * Draw the categories onto the window.
         */
        g.setFont(fMEDIUM_FONT);
        drawWithShadow(g, SMALL_OFFSET, STATISTICS_OFFSET, "Statistics");
        drawWithShadow(g, SMALL_OFFSET, CONTROLS_OFFSET, "Controls");

        /*
         * Draw the category content onto the window.
         */
        g.setFont(fSMALL_FONT);

        //Draw the content for the statistics category.
        int drawY = STATISTICS_OFFSET;
        g.drawString("Total Score: " + snkGame.getScore(),
                     LARGE_OFFSET,
                     drawY += MESSAGE_STRIDE);
        g.drawString("Fruit Eaten: " + snkGame.getFruitsEaten(),
                     LARGE_OFFSET,
                     drawY += MESSAGE_STRIDE);
        g.drawString("Fruit Score: ", LARGE_OFFSET, drawY += MESSAGE_STRIDE);
        // Draw the score in a color that depends on the value of it
        if (snkGame.getNextFruitScore() > 72) {
            g.setColor(Color.GREEN.darker());
        }
        else {
            if (snkGame.getNextFruitScore() > 30) {
                g.setColor(Color.YELLOW);
            }
            else {
                if (snkGame.getNextFruitScore() > 0) {
                    g.setColor(Color.RED.darker());
                }
            }
        }
        g.drawString("" + snkGame.getNextFruitScore(), LARGE_OFFSET + 75, drawY);
        g.setColor(Color.WHITE);
        //Draw the content for the controls category.
        drawY = CONTROLS_OFFSET;
        g.drawString("Move Up: W / Up Arrowkey",
                     LARGE_OFFSET,
                     drawY += MESSAGE_STRIDE);
        g.drawString("Move Down: S / Down Arrowkey",
                     LARGE_OFFSET,
                     drawY += MESSAGE_STRIDE);
        g.drawString("Move Left: A / Left Arrowkey",
                     LARGE_OFFSET,
                     drawY += MESSAGE_STRIDE);
        g.drawString("Move Right: D / Right Arrowkey",
                     LARGE_OFFSET,
                     drawY += MESSAGE_STRIDE);
        g.drawString("Pause Game: P", LARGE_OFFSET, drawY += MESSAGE_STRIDE);
        g.drawString("Save Game: G", LARGE_OFFSET, drawY += MESSAGE_STRIDE);
        g.drawString("Load Game: C", LARGE_OFFSET, drawY + MESSAGE_STRIDE);
    }

    void drawWithShadow(Graphics g, int iX, int iY, String sMessage) {
        /*
         * Draw a light gray shadow before the main text
         */
        g.setColor(Color.LIGHT_GRAY);
        g.drawString(sMessage, iX, iY + 1);

        /*
         * Draw the main text in white with the preset font
         */
        g.setColor(Color.WHITE);
        g.drawString(sMessage, iX, iY);
    }


}