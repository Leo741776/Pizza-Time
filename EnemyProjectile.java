import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

// Handles animation and visual of enemy projectiles
public class EnemyProjectile extends ImageView {

    private AnimationTimer behaviorTimer;   // Speed at which fired projectiles travel down screen

    public EnemyProjectile() {
        Image enemyProjectileImage = new Image(getClass().getResource("/assets/projectile/enemy_projectile.png").toExternalForm());
        this.setImage(enemyProjectileImage);
        this.setPreserveRatio(true);
        this.setFitWidth(50);

        // Cleanup mechanism to stop the projectile's animation once it leaves the screen
        this.parentProperty().addListener((unusedObs, unusedOldParent, newParent) -> {
            if (newParent == null) {
                stopAnimation();
            }
        });
    }

    // Creates an image of the projectile and animates it downwards from the enemy sprite's current location
    public void fire(double currentX, double currentY, Pane gamePane) {
        this.setX(currentX - this.getFitWidth() / 2);
        this.setY(currentY);

        gamePane.getChildren().add(this);

        behaviorTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                setY(getY() + 2.5);

                if (getY() > 1024) {
                    gamePane.getChildren().remove(EnemyProjectile.this);
                    stopAnimation();
                }
            }
        };

        behaviorTimer.start();
        SoundManager.playEnemyBlasterSound();
    }

    public void stopAnimation() {
        if (behaviorTimer != null) {
            behaviorTimer.stop();
        }
    }
}