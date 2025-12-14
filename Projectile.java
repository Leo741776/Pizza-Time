import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

// Handles projectile visual and fire() method which displays an animated projectile
public class Projectile extends ImageView {

    public Projectile() {
        Image projectileImage = new Image(getClass().getResource("/assets/projectile/projectile.png").toExternalForm());
        this.setImage(projectileImage);
        this.setPreserveRatio(true);
        this.setFitWidth(50);
    }

    public void fire(double currentX, double currentY, Pane gamePane) {
        this.setX(currentX - this.getFitWidth() / 2);
        this.setY(currentY - 25);

        gamePane.getChildren().add(this);

        // Projectile animation running at around 60 FPS
        Timeline projectileTimeline = new Timeline(new KeyFrame(Duration.millis(16), ev -> {
            this.setY(this.getY() - 10);
            if (this.getY() < -100) {
                gamePane.getChildren().remove(this);
            }
        }));
        projectileTimeline.setCycleCount(Animation.INDEFINITE);
        projectileTimeline.play();
    }
}