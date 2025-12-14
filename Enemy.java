import javafx.animation.PathTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

import java.util.Set;

// Handles enemy sprite visuals, movement, spawning, and firing
public class Enemy extends ImageView {

    private int randomValueForStartingX = (int) (Math.random() * 100);
    private int randomValueForDirection = (int) (Math.random() * 100);
    private int startingX = 0;
    private long timeSinceLastFired = 0;    // For incrementing projectile fire rate
    private final long FIRE_COOLDOWN = 750; // Cooldown timer for fire rate

    public Enemy() {
        Image enemyImage = new Image(getClass().getResource("/assets/sprite/enemy.png").toExternalForm());
        this.setImage(enemyImage);
        this.setPreserveRatio(true);
        this.setFitWidth(75);
    }

    public void spawn(Pane gamePane) {

        // Randomizes which side of the screen the enemy spawns at
        startingX = (randomValueForStartingX <= 50) ? 256 : 512;

        this.setX(startingX);
        this.setY(-100);

        gamePane.getChildren().add(this);

        // Screen boundaries for enemy movement
        double minX = 0;
        double maxX = 768 - this.getFitWidth();
        double amplitude = 250;

        Polyline zigzagPattern;

        // Sets zigzag pattern that enemy follows
        if (randomValueForDirection <= 50) {
            zigzagPattern = new Polyline(
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
        } else {
            zigzagPattern = new Polyline(
                    clamp(startingX, minX, maxX), -100,
                    clamp(startingX - amplitude, minX, maxX), 128,
                    clamp(startingX, minX, maxX), 256,
                    clamp(startingX + amplitude, minX, maxX), 384,
                    clamp(startingX, minX, maxX), 512,
                    clamp(startingX - amplitude, minX, maxX), 640,
                    clamp(startingX, minX, maxX), 768,
                    clamp(startingX + amplitude, minX, maxX), 896,
                    clamp(startingX, minX, maxX), 1024
            );
        }

        PathTransition zigzagMovement = new PathTransition(Duration.seconds(15), zigzagPattern, this);

        zigzagMovement.setOnFinished(e -> gamePane.getChildren().remove(this));
        zigzagMovement.play();
    }

    // Helper method to clamp x positions
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    // Uses time since last shot was fired compared to firing cooldown timer to spawn projectile
    public void fire(Pane gamePane, Set<EnemyProjectile> projectileSet) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - timeSinceLastFired >= FIRE_COOLDOWN) {

            var scenePosition = this.localToScene(this.getBoundsInLocal());
            double currentX = scenePosition.getMinX() + (this.getFitWidth() / 2);
            double currentY = scenePosition.getMaxY();

            if (currentY > 0 && currentY < gamePane.getHeight()) {
                if (currentTime - timeSinceLastFired >= FIRE_COOLDOWN) {
                    EnemyProjectile projectile = new EnemyProjectile();
                    projectile.fire(currentX, currentY, gamePane);
                    projectileSet.add(projectile);
                    timeSinceLastFired = currentTime;
                }
            }
        }
    }
}