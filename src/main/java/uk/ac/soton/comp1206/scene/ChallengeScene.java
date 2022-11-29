package uk.ac.soton.comp1206.scene;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;

import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.ac.soton.comp1206.Multimedia;
import uk.ac.soton.comp1206.NextPieceListener;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.game.Game;

import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.game.GamePiece;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {
    /**
     * Logger for debugging
     */
    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * An instance of Game class
     */
    protected Game game;

    /**
     * displaying the current Piece
     */
    private PieceBoard SmallPiece;

    /**
     * Displaying the following Piece
     */
    private PieceBoard SmallPiece2;

    /**
     * Keeping track of the Y-Coordinates while keyboard access
     */
    private int yCoordinate;

    /**
     * Keeping track of the X-Coordinates while keyboard access
     */
    private int xCoordinate;

    /**
     * Bool for turning on the Keyboard access
     */
    private boolean keyboardOn;

    /**
     * An Instance of the GameBoard
     */
    private GameBoard board;

    /**
     * Stack Pane on which the timer is being placed
     */
    private StackPane ts;

    /**
     * The rectangle on which the Timer is being placed
     */
    private Rectangle timer;

    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();
        Game.lives.addListener( (event) -> {
            if(Game.lives.getValue()  == -1){
                Platform.runLater(gameWindow::startScore);

            }
        });

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        //Creating the Main Stack Pane
        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2, gameWindow.getWidth() / 2);
        mainPane.setCenter(board);

//        mainPane.setBottom(progressBar);

        //Checking for RightClicks on Main GameBoard
        board.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                game.rotateCurrentPiece();
            }
        });


        //This is the Score Board to the left side of the scene
        mainPane.setRight(createScoreBoard());

        //This Board shows the level
        mainPane.setLeft(createLevelBoard());

        //Handle block on gameBoard grid being clicked
        board.setOnBlockClick(this::blockClicked);

        //Handle when the cursor moves over the blocks
        board.setOnBlockHovered(this::blockHovered);

        //Handle when the cursor moves away from the blocks
        board.setOnBlockUnHovered(this::blockUnHovered);

        game.setOnGameLoop(this::gameLoop);

        ts = new StackPane();
        mainPane.setBottom(ts);

//        Label label = new Label("Hello");
        timer = new Rectangle();
        timer.setHeight(20);
        timer.setWidth(32);
        timer.setFill(Color.RED);


        BorderPane.setMargin(ts,new Insets(4.5,4.5,4.5,4.5));
        ts.getChildren().add(timer);
        StackPane.setAlignment(timer,Pos.CENTER_LEFT);



        //looks out for the piece been places
        NextPieceListener nextPieceListener = new NextPieceListener() {
            public void nextPiece(GamePiece nextIncomingPiece, GamePiece followingPiece) {

                logger.info("Next incoming Piece " + nextIncomingPiece);
                SmallPiece.addToSmallGrid(nextIncomingPiece);
                SmallPiece2.addToSmallGrid(followingPiece);

            }
        };

        //Looks out for the  Lines Cleared
        LineClearedListener lineClearedListener = new LineClearedListener() {
            @Override
            public void lineClearedCoordinates(ArrayList<GameBlockCoordinate> coordinates) {
                board.fadeOut(coordinates);
            }
        };


        //Handles when a piece is placed
        game.setNextPieceListener(nextPieceListener);

        //Handles when a line is cleared
        game.setLineClearedListener(lineClearedListener);

    }


    /**
     * Handle when a block is clicked
     *
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
        logger.info("A block is been clicked");
    }

    /**
     * Handle when a block is Hovered
     *
     * @param gameBlock the Game Block that was clocked
     */
    private void blockHovered(GameBlock gameBlock) {
        game.blockHovered(gameBlock);
    }

    /**
     * Handle when a block is UnHovered
     *
     * @param gameBlock the Game Block that was clocked
     */
    private void blockUnHovered(GameBlock gameBlock) {
        game.blockUnHovered(gameBlock);
    }


    /**
     * This method is responsible for Taking in the time in milliseconds and displaying them on the timeBar present on the bottom of Challenge Scene
     * @param loop Is the time in milliseconds
     */
    protected void gameLoop(int loop){

        logger.info("Entering the Game Loop");
        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(this.timer.fillProperty(), Color.GREEN)), new KeyFrame(Duration.ZERO, new KeyValue(this.timer.widthProperty(), this.ts.getWidth())), new KeyFrame(new Duration((double)loop * 0.5D), new KeyValue(this.timer.fillProperty(), Color.YELLOW)), new KeyFrame(new Duration((double)loop * 0.75D), new KeyValue(this.timer.fillProperty(), Color.RED)), new KeyFrame(new Duration(loop), new KeyValue(this.timer.widthProperty(), 0)));
        timeline.play();
    }


    /**
     * setting the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");


        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();
        Multimedia.playBackgroundMusic("game.wav");
        game.setOnGameLoop(this::gameLoop);


        //Here is the main KeyBoard Controls
        getScene().setOnKeyPressed((e) -> {
            switch (e.getCode()) {
                case LEFT -> keyboardControls("Left");
                case A -> keyboardControls("Left");
                case RIGHT -> keyboardControls("Right");
                case D -> keyboardControls("Right");
                case UP -> keyboardControls("Up");
                case W -> keyboardControls("Up");
                case DOWN -> keyboardControls("Down");
                case S -> keyboardControls("Down");
                case ENTER -> enterPressed();
                case X -> enterPressed();
                case SPACE -> game.swapCurrentPiece();
                case R -> game.swapCurrentPiece();
                case Z -> game.antiRotateCurrentPiece();
                case OPEN_BRACKET -> game.antiRotateCurrentPiece();
                case E -> game.rotateCurrentPiece();
                case C -> game.rotateCurrentPiece();
                case CLOSE_BRACKET -> game.rotateCurrentPiece();
                case ESCAPE -> moveOut();

            }
        });

    }

    /**
     * Taking back to the Menu Scene and cancelling the timer
     */
    public void moveOut(){
        gameWindow.startMenu();
        game.timer.cancel();
    }

    /**
     * This method takes in the X and Y Coordinates where the arrow keys target and Places the piece there
     * Similar to blockClicked, but it takes the coordinates
     */
    public void enterPressed() {
        game.blockPressed(xCoordinate, yCoordinate);
    }

    /**
     * This function is basically responsible for moving around the grid with the help of arrow keys
     *
     * @param keyStroke Responsible for getting the keyCode
     */
    public void keyboardControls(String keyStroke) {
        if (keyboardOn) {
            if (keyStroke.equals("Up")) {
                if (yCoordinate != 0) {
                    GameBlock gb = board.getBlock(xCoordinate,yCoordinate);
                    gb.paint();
                    yCoordinate -= 1;
                    GameBlock gbnew = board.getBlock(xCoordinate,yCoordinate);
                    gbnew.colorLight();
                }
            }
            if (keyStroke.equals("Left")) {
                if (xCoordinate != 0) {
                    GameBlock gb = board.getBlock(xCoordinate,yCoordinate);
                    gb.paint();
                    xCoordinate -= 1;
                    GameBlock gbnew = board.getBlock(xCoordinate,yCoordinate);
                    gbnew.colorLight();
                }
            }
            if (keyStroke.equals("Down")) {
                if (yCoordinate < game.getCols() - 1) {
                    GameBlock gb = board.getBlock(xCoordinate,yCoordinate);
                    gb.paint();
                    yCoordinate += 1;
                    GameBlock gbnew = board.getBlock(xCoordinate,yCoordinate);
                    gbnew.colorLight();
                }
            }
            if (keyStroke.equals("Right")) {
                if (xCoordinate < game.getCols() - 1) {
                    GameBlock gb = board.getBlock(xCoordinate,yCoordinate);
                    gb.paint();
                    xCoordinate += 1;
                    GameBlock gbnew = board.getBlock(xCoordinate,yCoordinate);
                    gbnew.colorLight();
                }
            }
            logger.info("X-Coordinate" + xCoordinate + " And Y-Coordinate = " + yCoordinate);
        } else {
            logger.info("KeyBoard Mode Activated");
            keyboardOn = true;
            xCoordinate = 0;
            yCoordinate = 0;

            GameBlock gbnew = board.getBlock(xCoordinate,yCoordinate);
            gbnew.colorLight();

        }
    }



    /**
     * This is a method to Display teh Score, Lives and the current and following incoming pieces
     *
     * @return VBox Score, Lives and 2 Grids which shows the incoming pieces
     */
    public VBox createScoreBoard() {
        var scores = new VBox();
        scores.setPadding(new Insets(4, 4, 4, 4));
        scores.setAlignment(Pos.TOP_CENTER);
        scores.getStyleClass().add("score");

        //Adding the small Grids which will show the next incoming piece
        SmallPiece = new PieceBoard(gameWindow.getWidth() / 6, gameWindow.getWidth() / 6);
        SmallPiece2 = new PieceBoard(gameWindow.getWidth() / 9, gameWindow.getWidth() / 9);
        SmallPiece2.toFront();

        // Changing the piece when we leftClick on thw small piece board
        SmallPiece.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                game.swapCurrentPiece();
            }
        });

        //Changing the piece when we leftClick on thw small piece board
        SmallPiece2.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                game.swapCurrentPiece();
            }
        });

        //When the Mouse Enters the Board, the focus goes out from the keyBoard and moves to the mouse
        board.setOnMouseEntered((event) -> {
            keyboardOn = false;
            logger.info("The Keyboard Access is Disabled");
            GameBlock gb = board.getBlock(xCoordinate,yCoordinate);
            gb.paint();

        });

        var currentScoreText = new Text("Current Score");
        currentScoreText.getStyleClass().add("heading");
        var currentLivesText = new Text("Lives");
        currentLivesText.getStyleClass().add("heading");

        var livesText = new Text();
        livesText.textProperty().bind(Game.lives.asString());
        livesText.getStyleClass().add("score");
        scores.getChildren().addAll(currentLivesText, livesText);

        var scoreText = new Text();
        scoreText.setTextAlignment(TextAlignment.CENTER);
        scoreText.textProperty().bind(Game.score.asString());
        scoreText.getStyleClass().add("score");
        scores.setSpacing(10);

        var cp = new Text("Current Piece");
        cp.getStyleClass().add("heading");

        var fp = new Text("Following Piece");
        fp.getStyleClass().add("heading");

        scores.getChildren().addAll(currentScoreText, scoreText, cp, SmallPiece, fp, SmallPiece2);

        return scores;
    }

    /**
     * This Is a method to create a VBox and nicely display the level
     *
     * @return Level VBox
     */
    public VBox createLevelBoard() {
        var level = new VBox();

        level.setAlignment(Pos.TOP_CENTER);
        level.getStyleClass().add("level");

        var currentLevelText = new Text("Level");
        currentLevelText.getStyleClass().add("heading");

        var levelText = new Text();
        levelText.setTextAlignment(TextAlignment.CENTER);
        levelText.textProperty().bind(Game.level.asString());
        levelText.getStyleClass().add("myscore");
        level.getChildren().addAll(currentLevelText, levelText);

        return level;
    }




}


