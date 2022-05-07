package uk.ac.soton.comp1206.event;

/**
 * Listens for requests for restarting the timer in the challenge scene
 */
public interface GameLoopListener {
    /**
     * Stores the time to be represented int the UI timer
     * @param time time in milliseconds
     */
    public void setOnGameLoop(int time);
    
}
