package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlock;

/**
 * The Block Pressed listener is used to handle the event when a block in a GameBoard is Pressed. It passes the
 * GameBlock that was Pressed in the message
 */
public interface BlockPressedListener{

    /**
     * Handle a block Pressed event
     * @param block the block that was Pressed
     */
    public void blockPressed(GameBlock block);
}
