package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlock;

/**
 * The Block Hovered listener is used to handle the event when a block in a GameBoard is Hovered. It passes the
 * GameBlock that was Hovered in the message
 */
public interface BlockHoveredbyKeyboard {

    /**
     * Handle a block Hovered event
     * @param block the block that was hovered
     */
    public void blockHovered(GameBlock block);

}
