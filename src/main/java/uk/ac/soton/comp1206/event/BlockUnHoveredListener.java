package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlock;

/**
 * The Block Unhovered listener is used to handle the event when a block in a GameBoard is Unhovered. It passes the
 * GameBlock that was Unhovered in the message
 */
public interface BlockUnHoveredListener {

    /**
     * Handle a block Unhovered event
     * @param block the block that was Unhovered
     */
    public void blockUnHovered(GameBlock block);


}
