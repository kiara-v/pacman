package pacman;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import java.util.LinkedList;

/**
 * This is the Ghost's class.
 * It inherits the MazeSquares class and implements the Collidable interface.
 * A ghost is modeled.
 */

public class Ghosts extends MazeSquares implements Collidable {
    private Rectangle _ghost;
    private MazeSquares[][] _map;
    private Direction[][] _directions;
    private Direction _currentDirection;
    private BoardCoordinate _currentCell;
    private Mode _mode;

    /**
     * This is the Ghost's constructor.
     * @param map
     * @param ghostDirection
     * @param mode
     * A 2D array of directions is instantiated with the same dimensions of the maze.
     * A new rectangle is instantiated.
     */
    public Ghosts(MazeSquares[][] map, Direction ghostDirection, Mode mode) {
        super();
        _map = map;
        _currentDirection = ghostDirection;
        _mode = mode;
        _directions = new Direction[Constants.MAZE_DIMENSION][Constants.MAZE_DIMENSION];
        _ghost = new Rectangle(Constants.SQUARE_WIDTH, Constants.SQUARE_WIDTH);
        _ghost.setStrokeWidth(4);
        _ghost.setStroke(Color.BLACK);
        _ghost.setStrokeType(StrokeType.INSIDE);
        _ghost.setStrokeLineCap(StrokeLineCap.ROUND);
    }

    /**
     * This method moves the ghost towards the target location.
     * @param target
     */
    public void moveGhost(BoardCoordinate target) {

        // This is so the ghost can travel through the tunnel.
        if (_currentDirection == Direction.LEFT && this.getXLoc() <= 0) {
            this.moveX(22);
        } else if (_currentDirection == Direction.RIGHT && this.getXLoc() >= Constants.SCENE_WIDTH-Constants.SQUARE_WIDTH) {
            this.moveX(-22);
        } else {
            /*
             * Based on the direction that the ghost's search method returns,
             *      the ghost moves one square in that direction and the directions array is cleared.
             */
            switch (search(target)) {
                case LEFT:
                    this.moveX(-1);
                    _currentDirection = Direction.LEFT;
                    clearDirections();
                    break;
                case RIGHT:
                    this.moveX(1);
                    _currentDirection = Direction.RIGHT;
                    clearDirections();
                    break;
                case UP:
                    this.moveY(-1);
                    _currentDirection = Direction.UP;
                    clearDirections();
                    break;
                case DOWN:
                    this.moveY(1);
                    _currentDirection = Direction.DOWN;
                    clearDirections();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Brings the ghost forward graphically.
     */
    public void toFront() {
        _ghost.toFront();
    }

    /**
     * BFS AHHHHHHH. Returns the direction of where the ghost can travel to get to the target cell the fastest.
     * @param target
     * @return
     * It queues the valid squares the ghost can move into to approach its target square.
     * It dequeues to get back to its current square and calculates the distance and updates the shortest path.
     * This is repeated until the queue is empty and the ghost has reached its target.
     */
    public Direction search(BoardCoordinate target) {
        LinkedList<BoardCoordinate> BoardLocations = new LinkedList<>();
        addNeighbors(BoardLocations);

        _directions[getGhostRow()][getGhostCol()] = _currentDirection;
        double minDistance = Double.POSITIVE_INFINITY;
        BoardCoordinate closestSquare = null;

        while (!BoardLocations.isEmpty()) {
            _currentCell = BoardLocations.removeFirst();

            double distX = Math.pow(target.getColumn() - _currentCell.getColumn(), 2);
            double distY = Math.pow(target.getRow() - _currentCell.getRow(), 2);

            // This is so the ghost also knows to use the tunnel squares as shortcuts to get to the target.
            if (_currentCell.getRow() == 11 && _currentCell.getColumn() <= 11) {
                distX = Math.pow(target.getColumn() - 22, 2);
            } else if (_currentCell.getRow() == 11 && _currentCell.getColumn() > 11) {
                distX = Math.pow(target.getColumn(), 2);
            }

            double curDistance = Math.sqrt(distX + distY);

            if (curDistance < minDistance) {
                closestSquare = _currentCell;
                minDistance = curDistance;

                int closestRow = closestSquare.getRow();
                int closestCol = closestSquare.getColumn();
                _currentDirection = _directions[closestRow][closestCol];
            }

            addNeighborsNeighbors(BoardLocations);
        }
        return _directions[closestSquare.getRow()][closestSquare.getColumn()];
    }

    /**
     * This method adds the valid neighbors of the ghost's neighbors to the queue.
     * @param queue
     */
    public void addNeighborsNeighbors(LinkedList queue) {
        if (canMove(_currentCell.getRow(), _currentCell.getColumn() + 1)) {
            BoardCoordinate rightCell = new BoardCoordinate(_currentCell.getRow(), _currentCell.getColumn() + 1, false);
            _directions[rightCell.getRow()][rightCell.getColumn()] = _currentDirection;
            queue.addLast(rightCell);
        }
        if (canMove(_currentCell.getRow() - 1, _currentCell.getColumn())) {
            BoardCoordinate topCell = new BoardCoordinate(_currentCell.getRow() - 1, _currentCell.getColumn(), false);
            _directions[topCell.getRow()][topCell.getColumn()] = _currentDirection;
            queue.addLast(topCell);
        }
        if (canMove(_currentCell.getRow(), _currentCell.getColumn() - 1)) {
            BoardCoordinate leftCell = new BoardCoordinate(_currentCell.getRow(), _currentCell.getColumn() - 1, false);
            _directions[leftCell.getRow()][leftCell.getColumn()] = _currentDirection;
            queue.addLast(leftCell);
        }
        if (canMove(_currentCell.getRow() + 1, _currentCell.getColumn())) {
            BoardCoordinate bottomCell = new BoardCoordinate(_currentCell.getRow() + 1, _currentCell.getColumn(), false);
            _directions[bottomCell.getRow()][bottomCell.getColumn()] = _currentDirection;
            queue.addLast(bottomCell);
        }
    }

    /**
     * This method adds the valid neighbors of the ghost, excluding squares that would require the ghost to do a 180.
     * @param queue
     */
    public void addNeighbors(LinkedList queue) {
        if (_currentDirection != Direction.LEFT && canMove(getGhostRow(), getGhostCol() + 1)) {
            BoardCoordinate rightCell = new BoardCoordinate(getGhostRow(), getGhostCol() + 1, false);
            _directions[rightCell.getRow()][rightCell.getColumn()] = Direction.RIGHT;
            queue.addLast(rightCell);
        }
        if (_currentDirection != Direction.DOWN && canMove(getGhostRow() - 1, getGhostCol())) {
            BoardCoordinate topCell = new BoardCoordinate(getGhostRow() - 1, getGhostCol(), false);
            _directions[topCell.getRow()][topCell.getColumn()] = Direction.UP;
            queue.addLast(topCell);
        }
        if (_currentDirection != Direction.RIGHT && canMove(getGhostRow(), getGhostCol() - 1)) {
            BoardCoordinate leftCell = new BoardCoordinate(getGhostRow(), getGhostCol() - 1, false);
            _directions[leftCell.getRow()][leftCell.getColumn()] = Direction.LEFT;
            queue.addLast(leftCell);
        }
        if (_currentDirection != Direction.UP && canMove(getGhostRow() + 1, getGhostCol())) {
            BoardCoordinate bottomCell = new BoardCoordinate(getGhostRow() + 1, getGhostCol(), false);
            _directions[bottomCell.getRow()][bottomCell.getColumn()] = Direction.DOWN;
            queue.addLast(bottomCell);
        }
    }

    /**
     * Returns if the ghost can move into a certain cell.
     * @param row
     * @param col
     * @return
     */
    public boolean canMove(int row, int col) {
        // Returns false if the cell is out of bounds.
        if (row < 0 || row >=23 || col < 0 || col >= 23) {
            return false;
        // Returns true if the cell is not a wall and there's no direction assigned (not visited).
        } else if (!_map[row][col].isWall() && _directions[row][col] == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Clears the directions array.
     */
    public void clearDirections() {
        // Saves the direction of the ghost's current cell so it knows not to do a 180.
        _currentDirection = _directions[getGhostRow()][getGhostCol()];
        for (int i = 0; i < Constants.MAZE_DIMENSION; i++) {
            for (int j = 0; j < Constants.MAZE_DIMENSION; j++) {
                _directions[i][j] = null;
            }
        }
    }

    /**
     * Moves the ghost horizontally by a certain amount of squares based on its' current location.
     * @param x
     */
    public void moveX(int x) {
        // Takes into account the length of each square when moving.
        _ghost.setX(_ghost.getX() + x * Constants.SQUARE_WIDTH);
    }

    /**
     * Moves the ghost vertically by a certain amount of squares based on its' current location.
     * @param y
     */
    public void moveY(int y) {
        // Takes into account the length of each square when moving.
        _ghost.setY(_ghost.getY() + y*Constants.SQUARE_WIDTH);
    }

    /**
     * Returns the ghost.
     * @return
     */
    public Rectangle getGhost() {
        return _ghost;
    }

    /**
     * Sets the ghost's x-location.
     * @param x
     */
    public void setXLoc(double x) {
        _ghost.setX(x);
    }

    /**
     * Gets the ghost's x-location.
     * @return
     */
    public double getXLoc() {
        return _ghost.getX();
    }

    /**
     * Sets the ghost's y-location.
     * @param y
     */
    public void setYLoc(double y) {
        _ghost.setY(y);
    }

    /**
     * Gets the ghost's y-location.
     * @return
     */
    public double getYLoc() {
        return _ghost.getY();
    }

    /**
     * Gets the ghost's current row.
     * @return
     */
    public int getGhostRow() {
        return (int) (_ghost.getY()/Constants.SQUARE_WIDTH);
    }

    /**
     * Gets the ghost's current column.
     * @return
     */
    public int getGhostCol() {
        return (int) (_ghost.getX()/Constants.SQUARE_WIDTH);
    }

    /**
     * Sets the ghost's color.
     * @param color
     */
    public void setColor(Color color) {
        _ghost.setFill(color);
    }

    /**
     * Returns the amount of points when collided with.
     * @return
     */
    @Override
    public int getPoints() {
        int points = 0;
        return points;
    }

    /**
     * I chose not to include the method in this class because it would depend on the mode, which is initialized
     *      in the game class.
     */
    @Override
    public void collide() {
    }

    /**
     * Returns Item type dot.
     * @return
     */
    @Override
    public Item type() {
        return Item.GHOST;
    }
}
