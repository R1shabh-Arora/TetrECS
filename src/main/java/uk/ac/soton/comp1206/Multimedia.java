package uk.ac.soton.comp1206;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Objects;

/**
 * The Multimedia class is responsible for managing all the Media of the game
 */
public class Multimedia {


    /**
     * A static Reference for the MultiMedia for gameSounds
     */
    public static MediaPlayer mediaPlayer;
    /**
     * A static Reference for the MultiMedia for gameSounds
     */
    public static MediaPlayer backgroundPlayerInitial;

    /**
     * Logger to log out any errors (Helps in Debugging)
     */
    private static final Logger logger = LogManager.getLogger(Multimedia.class);

    /***
     * Stops the audio when required
     */
    private static final BooleanProperty audioEnabledProperty = new SimpleBooleanProperty(true);

    /**
     * Player for playing the Background Music
     * @param music The FileName of the Music in the resources folder
     */
    public static void playBackgroundMusic(String music){
        if (!getAudioEnabled()) return;

         String toPlay = Objects.requireNonNull(Multimedia.class.getResource("/music/" + music)).toExternalForm();
         

         try{
             Media play1 = new Media(toPlay); //Part of Initial Background Music
             backgroundPlayerInitial = new MediaPlayer(play1); //Part of Initial Background Music
             backgroundPlayerInitial.setAutoPlay(true);
             backgroundPlayerInitial.setCycleCount(MediaPlayer.INDEFINITE);
             backgroundPlayerInitial.play();
             logger.info("Playing Audio: " + music);
             
         }catch(Exception e){
             setAudioEnabled(false);
             e.printStackTrace();
             logger.error(e.toString());
         }
    }

    /**
     * Played for playing the sounds
     * @param sound Filename for the sound saved in the resources
     */
    public static void playSound(String sound){
        if (!getAudioEnabled()) return;

        String toPlay = Objects.requireNonNull(Multimedia.class.getResource("/sounds/" + sound)).toExternalForm();
        logger.info("Playing audio: " + toPlay);

        try{
            
            Media play = new Media(toPlay);
            
            mediaPlayer = new MediaPlayer(play);
            mediaPlayer.play();
        }catch (Exception e){
             setAudioEnabled(false);
            e.printStackTrace();
            logger.error(e.toString());
        }
        
    }

    /**
     * Getter Method for the audioEnabledProperty
     * @return AudioEnabledProperty
     */
    public static BooleanProperty audioEnabledProperty(){
        return audioEnabledProperty;
    }

    /**
     * Setter Method for the audioEnableProperty
     * @param enabled Bool value
     */
    public static void setAudioEnabled(boolean enabled) {
        logger.info("Audio enabled set to: " + enabled);
        audioEnabledProperty().set(enabled);
    }

    /**
     * Getter method for the state of the audioEnabledProperty
     * @return The state of the Boolean Property
     */
    public static boolean getAudioEnabled() {
        return audioEnabledProperty().get();
    }
}
