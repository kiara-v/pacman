package pacman;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;

/**
 * This is the Energizer class.
 * It inherits the MazeSquares class and implements the Collidable interface.
 * An energizer is modeled.
 */

public class Energizer extends MazeSquares implements Collidable {
    private Circle _energizer;
    private Pane _gamePane;

    /**
     * This is the Energizer's constructor, which takes in the game pane as a parameter.
     * A new circle is instantiated and the Item enum is made an energizer.
     */
    public Energizer(Pane gamePane) {
        super();
        _gamePane = gamePane;
        _energizer = new Circle(Constants.ENERGIZER_RAD, Color.BLACK);
        _energizer.setStrokeWidth(4);
        _energizer.setStroke(Color.WHITE);
        _energizer.setStrokeType(StrokeType.OUTSIDE);
        _energizer.setStrokeLineCap(StrokeLineCap.ROUND);
    }

    /**
     * Returns the energizer.
     * @return
     */
    public Circle getEnergizer() {
        return _energizer;
    }

    /**
     * Sets the enrgizer's x-location.
     * @param x
     */
    public void setXLoc(double x) {
        _energizer.setCenterX(x + Constants.CIRCLE_OFFSET);
    }

    /**
     * Gets the energizer's x-location.
     * @return
     */
    public double getXLoc() {
        return _energizer.getCenterX();
    }

    /**
     * Sets the energizer's y-location.
     * @param y
     */
    public void setYLoc(double y) {
        _energizer.setCenterY(y + Constants.CIRCLE_OFFSET);
    }

    /**
     * Gets the energizer's y-location.
     * @return
     */
    public double getYLoc() {
        return _energizer.getCenterY();
    }

    /**
     * Returns 100 points when eaten.
     * @return
     */
    @Override
    public int getPoints() {
        return 100;
    }

    /**
     * Removes the energizer graphically when eaten.
     */
    @Override
    public void collide() {
        _gamePane.getChildren().remove(_energizer);
    }

    /**
     * Returns Item type energizer.
     * @return
     */
    @Override
    public Item type() {
        return Item.ENERGIZER;
    }
}