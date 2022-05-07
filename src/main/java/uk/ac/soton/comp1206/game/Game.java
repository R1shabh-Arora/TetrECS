package uk.ac.soton.comp1206.game;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



import javafx.beans.property.SimpleIntegerProperty;

import uk.ac.soton.comp1206.Multimedia;
import uk.ac.soton.comp1206.NextPieceListener;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.LineClearedListener;


/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    /**
     * Making the logger for debugging
     */
    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Property for scores
     */
    public static SimpleIntegerProperty score = new SimpleIntegerProperty(0);
    /**
     * property for level
     */
    public static SimpleIntegerProperty level = new SimpleIntegerProperty(0);
    /**
     * Property for Multiplier
     */
    public static SimpleIntegerProperty multiplier = new SimpleIntegerProperty(1);
    /**
     * Property for lives
     */
    public static SimpleIntegerProperty lives = new SimpleIntegerProperty(3);

    //Dummy Score to keep track of the level
    int dummyScore = 0;

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    /**
     * Keeping track of the current piece
     */
    protected GamePiece currentPiece = spawnPiece();


    /**
     * Keeping track of the following piece
     */
    protected GamePiece followingPiece = GamePiece.createPiece(((int) (Math.random() * (14))));

    /**
     * An Instance of Next Game Piece Listener
     */
    protected NextPieceListener nextPieceListener;


    /**
     * The GameLoop Listener sends out the delay, triggering the timer to restart
     */
    protected GameLoopListener gameLooplistener;

    /**
     * The LineCleared Listener sends out the coordinates to apply the effect t
     */
    protected LineClearedListener lineClearedListener;

    /**
     * Initializing the timer
     */
    public Timer timer;


    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
        
    }

    /**
     * Assigning the Task to the timer
     */
     TimerTask task = new TimerTask() {

         @Override
         public void run() {
             gameLoop();

         }
     };

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
        nextPieceListener.nextPiece(currentPiece,followingPiece);
         timer = new Timer();
         logger.info("The timer is started");

         //Initialising the timer when the game starts
         timer.schedule(task, getTimeDelay());

         //
        gameLooplistener.setOnGameLoop(getTimeDelay());
    }
    
    
    /**
     * KeyBoard Controls
     * Handle what should happen when a particular block is Pressed
     * @param x X-Coordinate
     * @param y Y-Coordinate
     */
    public void blockPressed(int x, int y ) {
      

        if(grid.canPlayPiece(currentPiece, x, y)){

            //Listens out whenever the timer is to be restarted
            gameLooplistener.setOnGameLoop(getTimeDelay());

            //Place the current piece
            grid.playPiece(currentPiece,x,y);

            //Check for any lines that are needed to be cleared
            afterPiece();

            //This method is changing the current Piece to a new piece and getting a new piece for the following piece
            piecePlaced();

            //This method is supposed to reset the timer back if the piece is placed
            timer.cancel();
            resetTimer();

            //Listens out for the next incoming piece
             nextPieceListener.nextPiece(currentPiece,followingPiece);

        }else{
            Multimedia.playSound("fail.wav");

        }
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        if(grid.canPlayPiece(currentPiece, x, y)){

            //Listens out whenever the timer is to be restarted
             gameLooplistener.setOnGameLoop(getTimeDelay());

            //Place the current piece
            grid.playPiece(currentPiece,x,y);

            //Check for any lines that are needed to be cleared
            afterPiece();

            //This method is changing the current Piece to a new piece and getting a new piece for the following piece
            piecePlaced();

            //This method is supposed to reset the timer back if the piece is placed
            timer.cancel();
            resetTimer();

            // Listens out for the next incoming piece
             nextPieceListener.nextPiece(currentPiece,followingPiece);

        }else{
            Multimedia.playSound("fail.wav");

        }
       
    }
    /**
     * It makes the blocks partially transparent when the mouse enters that block
     * @param gameBlock The Block which is being hovered on at the moment
     */
    public void blockHovered(GameBlock gameBlock){

        gameBlock.colorLight();

    }
    /**
     * It paints the blocks back to their original color when it when mouse leaves the block
     * @param gameBlock The GameBlock which the mouse just left from
     */
    public void blockUnHovered(GameBlock gameBlock){
        gameBlock.paint();
    }
    
    

    /**
     * Instance of NextPieceListener which takes the next Piece generated to and adds it to the challenge scene
     * @param nextPiece Listener waiting for the next GamePiece
     */
    public void setNextPieceListener(NextPieceListener nextPiece){
        this.nextPieceListener = nextPiece;

    }

    /**
     * Instance of lineClearedListener which takes the coordinate the lines to be cleared
     * @param lineClearedListener Listener Waiting for Coordinated to apply a fadeout effect on them
     */
    public void setLineClearedListener(LineClearedListener lineClearedListener){
        this.lineClearedListener = lineClearedListener;
    }

    /**
     * Instance of GameLoopListener which takes in the Tme Delay to show that in a rectangle in the challenge scene
     * @param gameLoopListener Listener waiting for the Function gameLoop() to be called
     */
    public void setOnGameLoop(GameLoopListener gameLoopListener){
        this.gameLooplistener = gameLoopListener;
    }



    /**
     * A method to rotate the GamePiece
     */
    public void rotateCurrentPiece(){
        currentPiece.rotate();
        Multimedia.playSound("rotate.wav");

        nextPieceListener.nextPiece(currentPiece,followingPiece);
    }

    /**
     * A method to rotate the GamePiece in the Anti-Clockwise Direction
     */
    public void antiRotateCurrentPiece(){
        currentPiece.rotate(3);

        Multimedia.playSound("rotate.wav");

        nextPieceListener.nextPiece(currentPiece,followingPiece);

    }

    /**
     * This Method is to Shift the following piece to the current piece and generate a new piece for the following piece
     */
    public void piecePlaced(){
        currentPiece = followingPiece;
        followingPiece = spawnPiece();
        Multimedia.playSound("place.wav");

        nextPieceListener.nextPiece(currentPiece,followingPiece);


    }

    /**
     * This is the method to swap currentPiece and the followingPiece
     */
    public void swapCurrentPiece(){
        GamePiece temp = currentPiece;
        currentPiece = followingPiece;
        followingPiece = temp;
        
        Multimedia.playSound("place.wav");
        nextPieceListener.nextPiece(currentPiece,followingPiece);
    }


    /**
     * This method helps to clear any lines (If required)
     */
    public void afterPiece(){
        //Here we are going to check to see if we need to clear any lines

        var rowCounter = 0;
        var colCounter = 0;
        ArrayList<Integer> coltoClear = new ArrayList<>();
        ArrayList<Integer> rowtoClear = new ArrayList<>();

         ArrayList<GameBlockCoordinate> coordinatesToClear = new ArrayList<>();

        for(var x = 0;x < cols;x++){
            for(var y = 0;y < rows; y++){
                
                //Horizontal Lines
                if(grid.get(x,y) == 0) break;
                rowCounter++;

                if(rowCounter == rows){
                
                    rowtoClear.add(x);
                    for (int i = 0; i<5;i++) {
                        coordinatesToClear.add(new GameBlockCoordinate(x, i));
                    }

                }
            }
            rowCounter = 0;
        }
        for(var y = 0;y < rows;y++){
            for(var x = 0;x < cols; x++){
                
                //Vertical Lines
                if(grid.get(x,y) == 0) break;
                colCounter++;

                if(colCounter == cols){
                    coltoClear.add(y);
                    for (int i = 0; i<5;i++) {
                        coordinatesToClear.add(new GameBlockCoordinate(i, y));
                    }
                }
            }    
            colCounter = 0;
        }

        System.out.println(coordinatesToClear);


        //Managing the score
        if(!rowtoClear.isEmpty() || !coltoClear.isEmpty()){

        //Calling the LineCleared Listener to Fade them out
          lineClearedListener.lineClearedCoordinates(coordinatesToClear);

          //
          int numberOfLines = rowtoClear.size() + coltoClear.size();
        score(numberofBlocks(rowtoClear, coltoClear), numberOfLines);

        //Handling the level
        dummyScore += numberOfLines * numberofBlocks(rowtoClear, coltoClear)  * multiplier.get() * 10;
        }

        if(dummyScore >= 1000){
            level.set(level.get() + 1);
            dummyScore = 0;
            logger.info("Level Up!!!!!!!!");
            Multimedia.playSound("level.wav");
        }
        System.out.println(dummyScore);

        //Managing the multiplier
        if(!rowtoClear.isEmpty() || !coltoClear.isEmpty()){
            //If there is something to clear then  the multiplier is increased by 1
            logger.info("Multiplier + 1");
            multiplier.set(multiplier.get() + 1);
        }else{
            //If there is nothing to clear then the multiplier is set back to 1
            multiplier.set(1);
            logger.info("Multiplier = 1");
        }


        //Clearing the Row
        if(!rowtoClear.isEmpty()){
            logger.info("The fadeout");

            for(int a: rowtoClear){

                clearRow(a);
            }
            logger.info("Clearing row");
            Multimedia.playSound("clear.wav");
            logger.info("Playing the sound to clear the line");
        }

        //Clearing the Column
        if(!coltoClear.isEmpty()){
            logger.info("The fadeout");
            lineClearedListener.lineClearedCoordinates(coordinatesToClear);


            for(int b: coltoClear){

                clearColumn(b);
            }
            logger.info("Clearing column");
            Multimedia.playSound("clear.wav");
            logger.info("Playing the sound to clear the line");
        }
    }

    /**
     * This method is developed for getting the number of overlapping blocks (Helpful while clearing the lines)
     * @param rowtoClear An arrayList for Keeping track of the X-Coordinate
     * @param coltoClear An arrayList for Keeping track of the Y-Coordinate
     * @return the number of block involved while clearing the lines
     */
    public int numberofBlocks(ArrayList rowtoClear, ArrayList coltoClear){
        //Returns the number of Blocks in total (No overlapping)

        int blocksintheWay;
        int numberofBlocks;

        if(!rowtoClear.isEmpty() || !coltoClear.isEmpty()){
            blocksintheWay = rowtoClear.size() * coltoClear.size();
            numberofBlocks = 5 * (rowtoClear.size() + coltoClear.size()) - blocksintheWay;
        }else{
            numberofBlocks = 0;
             
        }
        return numberofBlocks;
    }   


    /**
     * This method does the scoring for us and return as an SimpleIntegerProperty
     * @param numberofBlocks The Number of blocks involved while clearing the lines
     * @param numberofLines The Number of Lines Involved while clearing
     * @return Score
     */
    public  SimpleIntegerProperty score(int numberofBlocks, int numberofLines){

    //Creating the function which will manage the score
    System.out.println(numberofBlocks);
    System.out.println(numberofLines);
    System.out.println(multiplier.get());

    score.setValue(score.getValue() + numberofLines * numberofBlocks * multiplier.get() * 10);    
    logger.info("Adding up the score");
        return score;
    
    }

    /**
     * This method clears the Rows individually (Helps while scoring)
     * @param x The X-Coordinate of the Row to be cleared
     */
    public void clearRow(int x){
        //Set all the values in that Row to 0 (Empty)
        
        for(int i = 0;i < 5;i++){
            getGrid().set(x,i,0);
            // gameBoard.getBlock(x,i).fadeOut();

        }
    }

    /**
     * This method clears the Column individually (Helps while Scoring)
     * @param y Y-Coordinate of the Column to be cleared
     */
    public void clearColumn(int y){
        //Set all the values in that Column to 0 (Empty)
        //Sort out any scoring that is needed 

        for(int i = 0;i < 5;i++){
            getGrid().set(i,y,0);
            // gameBoard.getBlock(i, y).fadeOut();
        }

    }
  

    
    
    /**
     * As the name suggests this method Spawns a new piece random piece for the game to progress
     * @return A new random game piece
     */
    public GamePiece spawnPiece(){
        //Generating new gamePiece by calling createPiece function
    
        int randomNumber = ((int) (Math.random() * (14)));
        GamePiece piece = GamePiece.createPiece(randomNumber);
        return piece;
    }

    /**
     * This method returns the current piece which is first in the queue to be placed in the GameBoard
     * @return Current Piece
     */
    public GamePiece currentPiece(){
        // Keeping track of the current GamePiece

        return currentPiece;

    }


    /**
     * Calculates the Delay
     * @return Delay
     */
    protected int getTimeDelay(){
        
        return Math.max(2500,12000 - 500*level.get());

    }

    /**
     * It is responsible for reducing a life, discarding the currentPiece and setting back the multiplier
     */
    protected void gameLoop() {

        if (lives.get() > 0) {
            logger.info("Entering GameLoop");

            //Losing a Life
            lives.set(lives.get() - 1);

            //Discard the first piece
            piecePlaced();

            //Setting the Multiplier back to 1
            multiplier.set(1);

            //Restarting the timer
            resetTimer();
            logger.info("The Time was up!");
            System.out.println(getTimeDelay());

            //Sending the signal when the timer is supposed to be restarted
            gameLooplistener.setOnGameLoop(getTimeDelay());
        }else{

            //Losing a Life
            lives.set(lives.get() - 1);


            logger.info("Game Over!!!!!!");


            timer.cancel();

            lives.set(3);


        }





    }

    /**
     * Helps to restart the timer
     */
    protected void resetTimer(){

        timer.purge();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                gameLoop();
            }
        }, getTimeDelay());



    }
  /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

 
    
       
    }

