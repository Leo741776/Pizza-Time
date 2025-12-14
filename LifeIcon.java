import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// Displays life icons
public class LifeIcon extends ImageView {

    public LifeIcon(double x, double y) {

        Image lifeIconImage = new Image(getClass().getResource("/assets/sprite/pizza.png").toExternalForm());
        this.setImage(lifeIconImage);
        this.setPreserveRatio(true);
        this.setFitWidth(50);

        this.setX(x);
        this.setY(y);
    }
}