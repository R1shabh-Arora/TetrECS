package uk.ac.soton.comp1206.scene;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.Multimedia;


/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);


    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        //Awful title
        var title = new Text("TetrECS");
        title.getStyleClass().add("bigtitle");
        mainPane.setTop(title);

        //For now, let us just add a button that starts the game. I'm sure you'll do something way better.\

        var openingVBox = new VBox();

        var playButton = new Button("Play");
        playButton.getStyleClass().add("title");


        var leaderBoards = new Button("LeaderBoard");
        leaderBoards.getStyleClass().add("title");

        var playingGuide = new Button("Playing Guide");
        playingGuide.getStyleClass().add("title");

        var exitButton = new Button("Exit");
        exitButton.getStyleClass().add("title");

        openingVBox.setAlignment(Pos.CENTER);
        openingVBox.setSpacing(30);
        openingVBox.getChildren().addAll(playButton,leaderBoards,playingGuide,exitButton);
        mainPane.setCenter(openingVBox);

        //Bind the Play button action to the startGame method in the menu
        playButton.setOnAction(this::startGame);

        playingGuide.setOnAction(this::startInstructionScene);

        leaderBoards.setOnAction(this::startLeaderBoard);

        //Bind the Exit Button action to exit the game
        exitButton.setOnAction(this::exit);



    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {

        logger.info("Playing the Menu Music");
        Multimedia.playBackgroundMusic("menu.mp3");
        logger.info("The Music is been played");

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                KeyCode key = t.getCode();
                if (key == KeyCode.ESCAPE){
                    gameWindow.closeWindow();
                }
            }
        });
    }

    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void startGame(ActionEvent event) {
        gameWindow.startChallenge();
    }

    private void startInstructionScene(ActionEvent event){
        gameWindow.startInstructionScene();
    }

    /**
     * start scores scene
     * @param e action
     */
    public void startLeaderBoard(ActionEvent e){
        gameWindow.startScore();
    }
    
    private void exit(ActionEvent event){
        System.exit(0);
    }
}
