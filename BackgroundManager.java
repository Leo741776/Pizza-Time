import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

// Handles infinite scrolling background
public class BackgroundManager {

    private final Pane gamePane;    // Pane which holds game objects (player sprite, enemy sprites, power ups, etc.)
    private final ImageView background1;    // 2 identical background images are used for scrolling animations
    private final ImageView background2;

    // Imports background images
    public BackgroundManager(Pane gamePane) {
        this.gamePane = gamePane;

        Image backgroundImage = new Image(getClass().getResource("/assets/background/background_image.png").toExternalForm());

        this.background1 = new ImageView(backgroundImage);
        this.background2 = new ImageView(backgroundImage);

        setupBackground();
        gamePane.getChildren().addAll(background1, background2);
    }

    // Fix background images to window size
    private void setupBackground() {
        background1.setPreserveRatio(false);
        background1.fitWidthProperty().bind(gamePane.widthProperty());
        background1.fitHeightProperty().bind(gamePane.heightProperty());
        background1.setY(0);

        background2.setPreserveRatio(false);
        background2.fitWidthProperty().bind(gamePane.widthProperty());
        background2.fitHeightProperty().bind(gamePane.heightProperty());
        background2.setY(gamePane.getPrefHeight());
    }

    /*
    Method which keeps two identical background images connected to one another vertically, shifting
    them down and wrapping the image which goes past the bottom of the screen back around to the top of the previous image
     */

    public void startScrolling() {

        // Timeline instance is used to animate scrolling background, with the scrolling rate going at around 60 FPS
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            double paneHeight = gamePane.getHeight();

            background1.setY(background1.getY() + 2);
            background2.setY(background2.getY() + 2);

            if (background1.getY() >= paneHeight) {
                background1.setY(background2.getY() - paneHeight);
            }

            if (background2.getY() >= paneHeight) {
                background2.setY(background1.getY() - paneHeight);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}

