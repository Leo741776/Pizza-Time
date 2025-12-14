import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

import java.util.HashSet;
import java.util.Set;

/*
Controls overall game logic, keeping track of player and every object on screen
and handles updating everything each frame
 */
public class GameManager {

    private final Pane gamePane;    // Pane with game objects such as character sprites
    private final Pane uiPane;      // Pane with UI elements such as score
    private final UIManager uiManager;
    private final GameState gameState;

    private final Set<KeyCode> keysPressed = new HashSet<>();
    private final Set<Enemy> activeEnemies = new HashSet<>();
    private final Set<EnemyProjectile> activeEnemyProjectiles = new HashSet<>();
    private final Set<Projectile> activePlayerProjectiles = new HashSet<>();
    private final Set<Double> fireOffsets = new HashSet<>();    // Used when creating projectiles fired from clones once power up is activated

    private AnimationTimer gameTimer;   // Main game loop
    private PizzaSprite pizzaMain;      // Player sprite
    private PizzaSprite leftClone;      // Clones of player sprite, used once power up is activated
    private PizzaSprite rightClone;

    private boolean spacePressed = false;

    // Used in fire rate, spawn rate for enemies and power ups, and increasing difficulty as time progresses
    private long timeSinceLastFired = 0;
    private long timeSinceLastSpawned = 0;
    private long timeSincePowerUpLastSpawned = 0;
    private long lastSpawnDifficultyIncrease = 0;

    private long FIRE_COOLDOWN = 750;   // Player firing cooldown
    private long SPAWN_COOLDOWN = 1250; // Enemy spawning cooldown
    private final long POWER_UP_SPAWN_COOLDOWN = 20000;

    private final Runnable showContinueScreenCallback;  // Callback function that runs when the game ends

    public GameManager(Pane gamePane, Pane uiPane, UIManager uiManager, GameState gameState, Runnable showContinueScreenCallback) {
        this.gamePane = gamePane;
        this.uiPane = uiPane;
        this.uiManager = uiManager;
        this.gameState = gameState;
        this.showContinueScreenCallback = showContinueScreenCallback;
    }

    // The 2 functions below handle player input
    public void handleKeyPress(KeyCode code) {
        keysPressed.add(code);

        if (code == KeyCode.SPACE) {
            spacePressed = true;
        }
    }

    public void handleKeyRelease(KeyCode code) {
        keysPressed.remove(code);

        if (code == KeyCode.SPACE) {
            spacePressed = false;
        }
    }

    // Sets up the game screen with objects, UI, and background
    public void startGame() {
        lastSpawnDifficultyIncrease = System.currentTimeMillis();

        pizzaMain = new PizzaSprite((384 - (75 / 2.0)), 800);
        gamePane.getChildren().add(pizzaMain);
        uiManager.setupGameUI();
        uiManager.updateLives();

        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                updateGameLoop();
            }
        };
        gameTimer.start();
    }

    // Primary updating logic for every game interaction and element
    private void updateGameLoop() {
        long now = System.currentTimeMillis();

        updateDifficulty(now);
        handlePlayerMovement();
        handlePlayerFiring(now);
        handleEnemySpawning(now);
        handlePowerUpSpawning(now);

        activeEnemyProjectiles.removeIf(enemyProjectile -> !gamePane.getChildren().contains(enemyProjectile));
        activePlayerProjectiles.removeIf(projectile -> !gamePane.getChildren().contains(projectile));

        uiManager.updateScore();
        uiManager.updateLives();

        CollisionManager.update(
                gamePane,
                uiPane,
                pizzaMain,
                gameState,
                uiManager.getScoreText(),
                uiManager.getHighScoreText(),
                uiManager.getLifeIcon1(),
                uiManager.getLifeIcon2(),
                uiManager.getLifeIcon3(),
                gameTimer,
                showContinueScreenCallback,
                activeEnemies,
                newCooldown -> FIRE_COOLDOWN = newCooldown,
                this::enablePepperShot,
                this::disablePepperShot
        );
    }

    // Increases enemy spawn rate very 30 seconds
    private void updateDifficulty(long now) {
        if (now - lastSpawnDifficultyIncrease >= 30000 && SPAWN_COOLDOWN > 1000) {
            SPAWN_COOLDOWN -= 50;
            lastSpawnDifficultyIncrease = now;
        }
    }

    private void handlePlayerMovement() {
        double dx = 0;
        double dy = 0;
        double speed = 3;

        // Shift x/y direction depending on input
        if (keysPressed.contains(KeyCode.UP)) dy -= speed;
        if (keysPressed.contains(KeyCode.DOWN)) dy += speed;
        if (keysPressed.contains(KeyCode.LEFT)) dx -= speed;
        if (keysPressed.contains(KeyCode.RIGHT)) dx += speed;

        // Used for keeping player sprite within screen boundary, even if clones are currently activated
        double mainX = pizzaMain.getX();
        double mainY = pizzaMain.getY();
        double mainWidth = pizzaMain.getBoundsInParent().getWidth();
        double mainHeight = pizzaMain.getBoundsInParent().getHeight();

        double leftOffset = 0;
        double rightOffset = 0;
        if (leftClone != null) leftOffset = fireOffsets.stream().filter(o -> o < 0).min(Double::compareTo).orElse(-80.0);
        if (rightClone != null) rightOffset = fireOffsets.stream().filter(o -> o > 0).max(Double::compareTo).orElse(80.0);

        double leftEdge = mainX + leftOffset;
        double rightEdge = mainX + mainWidth + rightOffset;
        double topEdge = mainY;
        double bottomEdge = mainY + mainHeight;

        if (leftEdge + dx < 0) dx += - (leftEdge + dx);
        if (rightEdge + dx > gamePane.getWidth()) dx -= (rightEdge + dx - gamePane.getWidth());

        if (topEdge + dy < 0) dy += - (topEdge + dy);
        if (bottomEdge + dy > gamePane.getHeight()) dy -= (bottomEdge + dy - gamePane.getHeight());

        pizzaMain.setX(mainX + dx);
        pizzaMain.setY(mainY + dy);
    }


    // Handles fire rate with and without clones activated
    private void handlePlayerFiring(long now) {
        if (!spacePressed || now - timeSinceLastFired < FIRE_COOLDOWN) {
            return;
        }

        double baseX = pizzaMain.getX();
        double baseY = pizzaMain.getY();
        double halfWidth = pizzaMain.getFitWidth() / 2;

        fireProjectile(baseX + halfWidth, baseY);

        for (double offset : fireOffsets) {
            fireProjectile(baseX + halfWidth + offset, baseY);
        }

        SoundManager.playBlasterSound();
        timeSinceLastFired = now;
    }

    // Manages current on-screen enemies and their spawn rate
    private void handleEnemySpawning(long now) {
        if (now - timeSinceLastSpawned >= SPAWN_COOLDOWN) {
            Enemy enemy = new Enemy();
            enemy.spawn(gamePane);
            activeEnemies.add(enemy);
            timeSinceLastSpawned = now;
        }

        activeEnemies.removeIf(enemy -> !gamePane.getChildren().contains(enemy));

        for (Enemy enemy : activeEnemies) {
            enemy.fire(gamePane, activeEnemyProjectiles);
        }
    }

    private void fireProjectile(double x, double y) {
        Projectile projectile = new Projectile();
        projectile.fire(x, y, gamePane);
        activePlayerProjectiles.add(projectile);
    }

    // Randomizes between salt and pepper power up spawn
    private void handlePowerUpSpawning(long now) {
        if (now - timeSincePowerUpLastSpawned >= POWER_UP_SPAWN_COOLDOWN) {
            if (Math.random() >= 0.5) {
                Salt salt = new Salt();
                salt.spawn(gamePane);
            } else {
                Pepper pepper = new Pepper();
                pepper.spawn(gamePane);
            }
            timeSincePowerUpLastSpawned = now;
        }
    }

    // Adds clone sprite positions used for projectile firing
    public void enablePepperShot() {
        if (leftClone != null) {
            return;
        }

        fireOffsets.clear();
        fireOffsets.add(-80.0);
        fireOffsets.add(80.0);

        leftClone = new PizzaSprite(0, 0);
        rightClone = new PizzaSprite(0, 0);

        leftClone.xProperty().bind(pizzaMain.xProperty().add(-80));
        leftClone.yProperty().bind(pizzaMain.yProperty());

        rightClone.xProperty().bind(pizzaMain.xProperty().add(80));
        rightClone.yProperty().bind(pizzaMain.yProperty());

        gamePane.getChildren().addAll(leftClone, rightClone);
    }

    public void disablePepperShot() {
        fireOffsets.clear();

        if (leftClone != null) {
            gamePane.getChildren().remove(leftClone);
            leftClone = null;
        }

        if (rightClone != null) {
            gamePane.getChildren().remove(rightClone);
            rightClone = null;
        }
    }

    // Resets all values to default on game restart
    public void resetGame() {
        gameState.life = 3;
        gameState.score = 0;
        FIRE_COOLDOWN = 750;
        SPAWN_COOLDOWN = 1250;
        lastSpawnDifficultyIncrease = System.currentTimeMillis();

        fireOffsets.clear();

        if (leftClone != null) {
            gamePane.getChildren().remove(leftClone);
        }

        if (rightClone != null) {
            gamePane.getChildren().remove(rightClone);
        }

        gamePane.getChildren().removeIf(node ->
                node instanceof Enemy ||
                node instanceof Projectile ||
                node instanceof EnemyProjectile ||
                node instanceof Salt ||
                node instanceof Pepper ||
                node instanceof LifeIcon);

        activeEnemies.clear();
        activeEnemyProjectiles.clear();
        activePlayerProjectiles.clear();

        pizzaMain.setX(384 - 75 / 2.0);
        pizzaMain.setY(800);
        pizzaMain.setVisible(true);

        uiManager.hideContinueScreen();
        uiManager.setupGameUI();
        uiManager.updateLives();
        uiManager.updateScore();

        gameTimer.start();
    }
}

