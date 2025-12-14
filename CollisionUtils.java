import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;

// Handles collision detection
public class CollisionUtils {

    public static boolean intersects(ImageView a, ImageView b, double padding) {
        Bounds b1 = a.getBoundsInParent();
        Bounds b2 = b.getBoundsInParent();

        // Create a variable hitbox for second object for better tuned gameplay
        Bounds shrunkBounds = new BoundingBox(
                b2.getMinX() + padding,
                b2.getMinY() + padding,
                b2.getWidth() - 2 * padding,
                b2.getHeight() - 2 * padding
        );

        return b1.intersects(shrunkBounds);
    }

    public static boolean intersects(ImageView a, ImageView b) {
        return intersects(a, b, 0);
    }
}
