import javafx.scene.media.AudioClip;

// Handles various audio clips used in different interactions
public class SoundManager {

    private static final AudioClip EXPLOSION_SOUND = new AudioClip(SoundManager.class.getResource("/assets/sound/explosion_sound.mp3").toExternalForm());
    private static final AudioClip ENEMY_BLASTER_SOUND = new AudioClip(SoundManager.class.getResource("/assets/sound/enemy_blaster_sound.mp3").toExternalForm());
    private static final AudioClip BLASTER_SOUND = new AudioClip(SoundManager.class.getResource("/assets/sound/blaster_sound.mp3").toExternalForm());
    private static final AudioClip GAME_OVER_SOUND = new AudioClip(SoundManager.class.getResource("/assets/sound/game_over_sound.mp3").toExternalForm());
    private static final AudioClip GAME_START_SOUND = new AudioClip(SoundManager.class.getResource("/assets/sound/game_start_sound.mp3").toExternalForm());
    private static final AudioClip POWER_UP_SOUND = new AudioClip(SoundManager.class.getResource("/assets/sound/power_up_sound.mp3").toExternalForm());

    public static void playExplosionSound() {
        EXPLOSION_SOUND.setVolume(0.25);
        EXPLOSION_SOUND.play();
    }

    public static void playEnemyBlasterSound() {
        ENEMY_BLASTER_SOUND.setVolume(0.25);
        ENEMY_BLASTER_SOUND.play();
    }

    public static void playBlasterSound() {
        BLASTER_SOUND.play();
    }

    public static void playGameOverSound() {
        GAME_OVER_SOUND.play();
    }

    public static void playGameStartSound() {
        GAME_START_SOUND.play();
    }

    public static void playPowerUpSound() {
        POWER_UP_SOUND.play();
    }
}
