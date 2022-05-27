package pacman;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;

/**
 * This is the MazeSquares class.
 * A maze square is modeled.
 */

public class MazeSquares {
    private Rectangle _square;
    private boolean _isWall;
    private ArrayList<Collidable> _collidables;

    /**
     * This is the MazeSquare's constructor.
     * An arraylist of type Collidable items is instantiated.
     * A rectangle is instantiated.
     */
    public MazeSquares() {
        _collidables = new ArrayList<Collidable>();
        _square = new Rectangle(Constants.SQUARE_WIDTH, Constants.SQUARE_WIDTH, Color.BLACK);
        // Sets the boolean _isWall to false automatically since not every maze square is a wall.
        _isWall = false;
    }

    /**
     * Adds an item to the Collidable arraylist.
     * @param item
     */
    public void addToCollidables(Collidable item) {
        _collidables.add(item);
    }

    /**
     * Removes an item from the Collidable arraylist.
     * @param item
     */
    public void removeFromCollidables(Collidable item) {
        _collidables.remove(item);
    }

    /**
     * Returns the Collidable arraylist.
     * @return
     */
    public ArrayList<Collidable> getCollidables() {
        return _collidables;
    }

    /**
     * Sets the MazeSquare's x-location.
     * @param x
     */
    public void setXLoc(double x) {
        _square.setX(x);
    }

    /**
     * Gets the MazeSquare's x-location.
     * @return
     */
    public double getXLoc() {
        return _square.getX();
    }

    /**
     * Sets the MazeSquare's y-location.
     * @param y
     */
    public void setYLoc(double y) {
        _square.setY(y);
    }

    /**
     * Gets the MazeSquare's y-location.
     * @return
     */
    public double getYLoc() {
        return _square.getY();
    }

    /**
     * Returns the maze square.
     * @return
     */
    public Rectangle getSquare() {
        return _square;
    }

    /**
     * Makes the MazeSquare a wall by changing its color and setting the _isWall boolean to true.
     */
    public void makeWall() {
        _square.setFill(Color.DARKBLUE);
        _isWall = true;
    }

    /**
     * Returns a boolean if the MazeSquare is a wall.
     * @return
     */
    public boolean isWall() {
        return _isWall;
    }
}
