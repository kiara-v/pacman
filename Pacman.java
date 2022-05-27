package pacman;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;

/**
 * This is the Pacman class.
 */

public class Pacman {
    private Circle _pacman;

    /**
     * This is the Pacman's constructor.
     * A new circle is instantiated.
     */
    public Pacman() {
        _pacman = new Circle(Constants.SQUARE_WIDTH/2-3, Color.BLACK);
        _pacman.setStrokeWidth(4);
        _pacman.setStroke(Color.GOLD);
        _pacman.setStrokeType(StrokeType.INSIDE);
        _pacman.setStrokeLineCap(StrokeLineCap.ROUND);
    }

    /**
     * Returns pacman.
     * @return
     */
    public Circle getPacman() {
        return _pacman;
    }

    /**
     * Moves pacman horizontally by a certain amount of squares based on its' current location.
     * @param x
     */
    public void moveX(int x) {
        // Takes into account the length of each square when moving.
        this.setXLoc(_pacman.getCenterX() + x*Constants.SQUARE_WIDTH);
    }

    /**
     * Moves pacman vertically by a certain amount of squares based on its' current location.
     * @param y
     */
    public void moveY(int y) {
        // Takes into account the length of each square when moving.
        this.setYLoc(_pacman.getCenterY() + y * Constants.SQUARE_WIDTH);
    }

    /**
     * Sets pacman's x-location.
     * @param x
     */
    public void setXLoc(double x) {
        _pacman.setCenterX(x);
    }

    /**
     * Gets pacman's x-location.
     * @return
     */
    public double getXLoc() {
        return _pacman.getCenterX();
    }

    /**
     * Sets pacman's y-location.
     * @param y
     */
    public void setYLoc(double y) {
        _pacman.setCenterY(y);
    }

    /**
     * Gets pacman's y-location.
     * @return
     */
    public double getYLoc() {
        return _pacman.getCenterY();
    }

    /**
     * Brings pacman to front.
     */
    public void toFront() {
        _pacman.toFront();
    }
}
