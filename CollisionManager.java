import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Set;
import java.util.function.Consumer;

// Handles interactions between player sprite, enemy sprites, projectiles, & power ups. It also updates the score and life bar
public class CollisionManager {

    // Object checking method which is called around 60 times/second, it keeps track of every object on screen at any given time
    public static void update(
            Pane gamePane,
            Pane uiPane,
            PizzaSprite pizzaMain,
            GameState state,
            Text scoreText,
            Text highScoreText,
            LifeIcon lifeIcon1,
            LifeIcon lifeIcon2,
            LifeIcon lifeIcon3,
            AnimationTimer timer,
            Runnable onGameOver,
            Set<Enemy> activeEnemies,
            Consumer<Long> setFireCooldown,
            Runnable enablePepperShot,
            Runnable disablePepperShot
    ) {
        long damageCooldown = 1000; // Invulnerability time given once player sprite takes damage

        /*
        Assign each node currently on the pane to var and check its status using a for loop
        Calls the intersect() method from CollisionUtils to check for collision
         */
        for (var node : gamePane.getChildren()) {

            if (node instanceof Enemy enemy) {
                if (CollisionUtils.intersects(pizzaMain, enemy) && System.currentTimeMillis() - state.timeSinceLastTookDamage >= damageCooldown) {
                    SoundManager.playExplosionSound();
                    pizzaMain.flash();
                    state.life--;
                    state.timeSinceLastTookDamage = System.currentTimeMillis();
                }
            }

            if (node instanceof EnemyProjectile enemyProjectile) {
                if (CollisionUtils.intersects(pizzaMain, enemyProjectile) && System.currentTimeMillis() - state.timeSinceLastTookDamage >= damageCooldown) {
                    SoundManager.playExplosionSound();
                    pizzaMain.flash();
                    state.life--;
                    state.timeSinceLastTookDamage = System.currentTimeMillis();
                }
            }

            if (node instanceof Projectile projectile) {
                for (Enemy enemy : activeEnemies) {
                    if (enemy.getBoundsInParent().getMinY() >= 0 && CollisionUtils.intersects(projectile, enemy)) {
                        state.score += 5;
                        state.highScore = Math.max(state.highScore, state.score);

                        CollisionManager.spawnExplosion(
                                gamePane,
                                enemy.getBoundsInParent().getCenterX(),
                                enemy.getBoundsInParent().getCenterY()
                        );
                        SoundManager.playExplosionSound();
                        gamePane.getChildren().removeAll(enemy, projectile);
                        break;
                    }
                }
            }

            /*
            The 2 conditional statements below handle power up interactions
            Salt: speed up fire rate
            Pepper: spawns extra pizza slices which shoot alongside the player's sprite
             */
            if (node instanceof Salt salt) {
                if (CollisionUtils.intersects(salt, pizzaMain)) {
                    SoundManager.playPowerUpSound();
                    pizzaMain.flash();
                    setFireCooldown.accept(250L);
                    gamePane.getChildren().remove(salt);
                    PauseTransition reset = new PauseTransition(Duration.seconds(15));
                    reset.setOnFinished(e -> setFireCooldown.accept(750L));
                    reset.play();
                }
            }

            if (node instanceof Pepper pepper) {
                if (CollisionUtils.intersects(pepper, pizzaMain)) {
                    SoundManager.playPowerUpSound();
                    pizzaMain.flash();
                    gamePane.getChildren().remove(pepper);
                    enablePepperShot.run();
                    PauseTransition reset = new PauseTransition(Duration.seconds(15));
                    reset.setOnFinished(e -> disablePepperShot.run());
                    reset.play();
                }
            }
        }

        // Keeps the score updated after every interaction
        scoreText.setText("score\t\t" + state.score);
        highScoreText.setText("hi score\t\t" + state.highScore);

        // Controls the visual life bar as well as triggering game over and reset logic once life reaches 0
        switch (state.life) {
            case 2 -> uiPane.getChildren().remove(lifeIcon3);
            case 1 -> uiPane.getChildren().remove(lifeIcon2);
            case 0 -> {

                CollisionManager.spawnExplosion(
                        gamePane,
                        pizzaMain.getBoundsInParent().getCenterX(),
                        pizzaMain.getBoundsInParent().getCenterY()
                );

                SoundManager.playGameOverSound();

                uiPane.getChildren().remove(lifeIcon1);
                pizzaMain.setVisible(false);

                gamePane.getChildren().removeIf(node ->
                        node instanceof PizzaSprite && node != pizzaMain);

                timer.stop();

                onGameOver.run();
            }
        }
    }

    // Creates explosion once player or enemy sprites are destroyed
    public static void spawnExplosion(Pane gamePane, double centerX, double centerY) {
        ImageView explosion = new ImageView(new Image(CollisionManager.class.getResource("/assets/effect/explosion.png").toExternalForm()));

        explosion.setFitWidth(64);
        explosion.setFitHeight(64);
        explosion.setPreserveRatio(true);

        explosion.setX(centerX - explosion.getFitWidth() / 2);
        explosion.setY(centerY - explosion.getFitHeight() / 2);

        gamePane.getChildren().add(explosion);

        PauseTransition explosion1Animation = new PauseTransition(Duration.millis(300));
        explosion1Animation.setOnFinished(e -> gamePane.getChildren().remove(explosion));
        explosion1Animation.play();
    }
}

