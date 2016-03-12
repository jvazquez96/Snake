package snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * ShakeFrame
 * <p>
 * Helper class that shakes the entire Game by a set interval and amount
 *
 * @author Irvel
 * @author Jorge
 * @version 0.1
 */
public class ShakeFrame {
    /**
     * The SnakeGame instance that will be shaked
     */
    private SnakeGame sSnake;

    /**
     * The time that passes between the update of the Frame position
     */
    public static final int iUPDATE_TIME = 5;

    /**
     * The duration of the shaking event
     */
    public static final int iDURATION = 220;

    /**
     * How much will the Frame be displaced from its initial position
     */
    public static final int iSHAKE_FACTOR = 30;

    /**
     * The initial position of the Frame before the shake
     */
    private Point poiInitPos;

    /**
     * The start time
     */
    private long lStartTime;

    /**
     * A time that measures the amount of time that has passed since the start
     */
    private Timer timTime;

    /**
     * Creates an instance of ActionTime that tracks the time that has passed
     * since the call to startShaking.
     */
    private ShakeFrame.ActionTime timeListener;


    /**
     * Creates a new ShakeFrame instance
     *
     * @param tetInstance The SnakeGame instance to shake.
     */
    public ShakeFrame(SnakeGame tetInstance) {
        sSnake = tetInstance;
        timeListener = new ShakeFrame.ActionTime();
    }

    /**
     * Saves the initial time and starts the timer that will shake the Frame
     * in a predefinite duration.
     */
    public void startShaking() {
        /*
         * Save the original position of the Frame
         */
        poiInitPos = sSnake.getLocation();
        lStartTime = System.currentTimeMillis();
        timTime = new Timer(iUPDATE_TIME, timeListener);
        timTime.start();
    }

    /**
     * Stops the shaking and puts the frame back to its original location.
     */
    public void stopShake() {
        timTime.stop();
        sSnake.setLocation(poiInitPos);
        sSnake.repaint();
    }

    /**
     * ActionTime
     * <p>
     * Helper class that tracks the amount of time that passes and moves the
     * frame by the set duration and shake factor.
     *
     * @author Irvel
     * @author Jorge
     * @version 0.1
     */
    private class ActionTime implements ActionListener {
        /**
         * The offset in the X direction that the frame will be moved
         */
        private int iXOffset;

        /**
         * The offset in the Y direction that the frame will be moved
         */
        private int iYOffset;

        @Override
        public void actionPerformed(ActionEvent e) {
            int iDirection = 1;
            /*
             * Retrieve the elapsed time since the start of the shaking
             */
            long lElapsedTime = System.currentTimeMillis() - lStartTime;
            /*
             * Create a new random number generator
             */
            Random ranNumGenerator = new Random();

            /*
             * Randomly choose if the frame will be down right or up left
             */
            if (ranNumGenerator.nextInt(5) > 2) {
                iDirection *= -1;
            }
            /*
             * Get a random offset from the initial position with the Shake
             * factor as an upper limit
             */
            iXOffset = poiInitPos.x +
                    iDirection * ranNumGenerator.nextInt(iSHAKE_FACTOR);
            iYOffset = poiInitPos.y +
                    iDirection * ranNumGenerator.nextInt(iSHAKE_FACTOR);

            /*
             * Move and repaint the frame
             */
            sSnake.setLocation(iXOffset, iYOffset);
            sSnake.repaint();

            /*
             * Stop shaking if the elapsed time exeeds the duration
             */
            if (lElapsedTime > iDURATION) {
                stopShake();
            }
        }
    }
}