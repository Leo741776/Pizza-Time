import javafx.animation.PathTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

// Handles pepper power up visual, spawn location, and traversal pattern
public class Pepper extends ImageView {

    int randomValueForStartingX = (int) (Math.random() * 100);
    int startingX = 0;

    public Pepper() {
        Image pepperImage = new Image(getClass().getResource("/assets/sprite/pepper.png").toExternalForm());
        this.setImage(pepperImage);
        this.setPreserveRatio(true);
        this.setFitWidth(75);
    }

    public void spawn(Pane gamePane) {

        startingX = (randomValueForStartingX <= 50) ? 256 : 512;

        this.setX(startingX);
        this.setY(-100);

        gamePane.getChildren().add(this);

        double minX = 0;
        double maxX = 768 - this.getFitWidth();
        double amplitude = 250;

        Polyline zigzagPattern = new Polyline(
                        clamp(startingX, minX, maxX), -100,
                        clamp(startingX + amplitude, minX, maxX), 128,
                        clamp(startingX, minX, maxX), 256,
                        clamp(startingX - amplitude, minX, maxX), 384,
                        clamp(startingX, minX, maxX), 512,
                        clamp(startingX + amplitude, minX, maxX), 640,
                        clamp(startingX, minX, maxX), 768,
                        clamp(startingX - amplitude, minX, maxX), 896,
                        clamp(startingX, minX, maxX), 1024
                );
        PathTransition zigzagMovement = new PathTransition(Duration.seconds(20), zigzagPattern, this);

        zigzagMovement.setOnFinished(e -> {
            gamePane.getChildren().remove(this);
        });

        zigzagMovement.play();
    }

    // Helper method to clamp x positions
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}