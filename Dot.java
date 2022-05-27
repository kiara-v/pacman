package pacman;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * This is the Dot class.
 * It inherits the MazeSquares class and implements the Collidable interface.
 * A dot is modeled.
 */

public class Dot extends MazeSquares implements Collidable {
    private Circle _dot;
    private Pane _gamePane;

    /**
     * This is the Dot's constructor, which takes in the game pane as a parameter.
     * A new circle is instantiated.
     */
    public Dot(Pane gamePane) {
        super();
        _gamePane = gamePane;
        _dot = new Circle(Constants.DOT_RAD, Color.WHITE);
    }

    /**
     * Returns the dot.
     * @return
     */
    public Circle getDot() {
        return _dot;
    }

    /**
     * Sets the dot's x-location.
     * @param x
     */
    public void setXLoc(double x) {
        _dot.setCenterX(x + Constants.CIRCLE_OFFSET);
    }

    /**
     * Gets the dot's x-location.
     * @return
     */
    public double getXLoc() {
        return _dot.getCenterX();
    }

    /**
     * Sets the dot's y-location.
     * @param y
     */
    public void setYLoc(double y) {
        _dot.setCenterY(y + Constants.CIRCLE_OFFSET);
    }

    /**
     * Gets the dot's y-location.
     * @return
     */
    public double getYLoc() {
        return _dot.getCenterY();
    }

    /**
     * Returns 10 points when eaten.
     * @return
     */
    @Override
    public int getPoints() {
        return 10;
    }

    /**
     * Removes the dot graphically when eaten.
     */
    @Override
    public void collide() {
        _gamePane.getChildren().remove(_dot);
    }

    /**
     * Returns Item type dot.
     * @return
     */
    @Override
    public Item type() {
        return Item.DOT;
    }
}
