package uk.ac.soton.comp1206;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * The GamePiece Listener is used to keep track of the incoming piece
 */
public interface NextPieceListener {
    
    /**
     * Handles the incoming piece
     * @param currentPiece It is the current piece which will be place on the board
     * @param followingPiece It is the following piece which will be place on the board
     */
    void nextPiece(GamePiece currentPiece, GamePiece followingPiece);
}