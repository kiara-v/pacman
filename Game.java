package pacman;

import cs15.fnl.pacmanSupport.SquareType;
import cs15.fnl.pacmanSupport.SupportMap;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import javax.sound.sampled.Clip;
import java.util.LinkedList;

// For music :)

/**
 * This is the Game class.
 */

public class Game {
    private MazeSquares[][] _map;
    private Pane _gamePane;
    private Pacman _pacman;
    private Ghosts _blinky;
    private Ghosts _inky;
    private Ghosts _pinky;
    private Ghosts _clyde;
    private Timeline _timeline;
    private Timeline _penTimeline;
    private Direction _pacmanDirection;
    private Label _score;
    private Label _lives;
    private Label _label;
    private BorderPane _root;
    private int _points;
    private int _lifeCount;
    private Direction _ghostDirection;
    private LinkedList<Ghosts> _eatenGhosts;
    private Mode _mode;
    private int _chase;
    private int _scatter;
    private int _frightened;
    private int _eaten;
    private KeyHandler _keyHandler;
    private Clip _clip;
    private int _level;
    private double _duration;

    /**
     * This is the Game's constructor, which takes in the game pane and root as parameters.
     * The mode, the counters for each mode, number of dots/energizers the pacman has eaten,
     *      the level number, the clip for music (so it can pause when the game is paused), pacman's initial direction,
     *      and duration for the TimeHandler are initialized.
     * A linked list of ghosts that pacman eats is initialized.
     * The KeyHandler is initialized as an instance variable so that it may be removed when the game is over.
     * The map and labels are made.
     * Pacman adds the KeyHandler.
     * The timeline and pen timeline are setup.
     * Music is added for *effect*
     */
    public Game(Pane gamePane, BorderPane root) {
        _gamePane = gamePane;
        _root = root;
        _clip = null;
        _mode = Mode.CHASE;
        _chase = 0;
        _scatter = 0;
        _frightened = 0;
        _eaten = 0;
        _level = 1;
        _duration = Constants.DURATION;
        _eatenGhosts = new LinkedList<>();
        _keyHandler = new KeyHandler();
        this.makeMap();
        this.makeLabels();

        _pacman.getPacman().addEventHandler(KeyEvent.KEY_PRESSED, _keyHandler);
        _pacman.getPacman().setFocusTraversable(true);
        _pacmanDirection = Direction.DOWN;
//        music();
        this.setupTimeline();
        this.setupPen();
    }

    /**
     * Sets up the pen timeline so that every four seconds, a ghost is released.
     */
    public void setupPen() {
        KeyFrame kf = new KeyFrame(Duration.seconds(4), new PenHandler());
        _penTimeline = new Timeline(kf);
        _penTimeline.setCycleCount(Animation.INDEFINITE);
    }

    /**
     * This is the private inner PenHandler class.
     * If there are eaten ghosts inside the pen, the ghost is released from the pen
     *      and removed from the eaten ghosts arraylist.
     */
    private class PenHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if (!_eatenGhosts.isEmpty()) {
                _eatenGhosts.getFirst().setXLoc(11 * Constants.SQUARE_WIDTH);
                _eatenGhosts.getFirst().setYLoc(8 * Constants.SQUARE_WIDTH);
                _eatenGhosts.removeFirst();
            }
        }
    }

    /**
     * Resets the game after pacman is eaten.
     */
    public void reset() {
        // The queue of eaten ghosts is cleared since the original order of ghosts will be released when the game resets.
        while (!_eatenGhosts.isEmpty()) {
            _eatenGhosts.removeFirst();
        }

        // The mode and mode counters are reset just like they were in the beginning of the game.
        _mode = Mode.CHASE;
        _chase = 0;
        _frightened = 0;
        _scatter = 0;

        _map[_blinky.getGhostRow()][_blinky.getGhostCol()].removeFromCollidables(_blinky);
        _blinky.setXLoc(11*Constants.SQUARE_WIDTH);
        _blinky.setYLoc(8*Constants.SQUARE_WIDTH);

        _map[_pinky.getGhostRow()][_pinky.getGhostCol()].removeFromCollidables(_pinky);
        _pinky.setXLoc(10*Constants.SQUARE_WIDTH);
        _pinky.setYLoc(10*Constants.SQUARE_WIDTH);
        _eatenGhosts.addLast(_pinky);

        _map[_inky.getGhostRow()][_inky.getGhostCol()].removeFromCollidables(_inky);
        _inky.setXLoc(11*Constants.SQUARE_WIDTH);
        _inky.setYLoc(10*Constants.SQUARE_WIDTH);
        _eatenGhosts.addLast(_inky);

        _map[_clyde.getGhostRow()][_clyde.getGhostCol()].removeFromCollidables(_clyde);
        _clyde.setXLoc(12*Constants.SQUARE_WIDTH);
        _clyde.setYLoc(10*Constants.SQUARE_WIDTH);
        _eatenGhosts.addLast(_clyde);

        _blinky.setColor(Color.DEEPPINK);
        _inky.setColor(Color.CYAN);
        _pinky.setColor(Color.LAWNGREEN);
        _clyde.setColor(Color.LIGHTCORAL);

        _pacman.setXLoc(11*Constants.SQUARE_WIDTH + Constants.CIRCLE_OFFSET);
        _pacman.setYLoc(17*Constants.SQUARE_WIDTH + Constants.CIRCLE_OFFSET);
        _pacmanDirection = Direction.DOWN;

        // The timeline is paused so that the player can get ready to play again.
        _timeline.pause();
        // The pen timeline is stopped so that a ghost is not released right away when the game is unpaused.
        _penTimeline.stop();
        _label.setText("press SPACE to continue");
        _label.setTranslateX(-10);
    }

    /**
     * Sets up the timeline in order for the game to run.
     */
    public void setupTimeline() {
        KeyFrame kf = new KeyFrame(Duration.seconds(_duration), new TimeHandler());
        _timeline = new Timeline(kf);
        _timeline.setCycleCount(Animation.INDEFINITE);
//        _clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /**
     * This is the private inner TimeHandler class.
     * The label's text is removed.
     * It moves pacman and the ghosts, while checking for collisions.
     */

    private class TimeHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            _label.setText("");

            movePacman();
            checkCollision();
            moveGhosts();
            checkCollision();
            changeMode();
            gameOver();

            event.consume();
        }
    }

    /**
     * The mode is changed based on the counters.
     */
    public void changeMode() {
        switch (_mode) {
            case FRIGHTENED:
                _frightened++;
                if (_frightened >= 40) {
                    _mode = Mode.CHASE;
                    _frightened = 0;
                }
                break;
            case SCATTER:
                _scatter++;
                if (_scatter >= 40) {
                    _mode = Mode.CHASE;
                    _scatter = 0;
                }
                break;
            case CHASE:
                _chase++;
                if (_chase >= 100) {
                    _mode = Mode.SCATTER;
                    _chase = 0;
                }
                break;
            default:
                break;
        }
    }

    /**
     * Based on pacman's direction, which changes based on the KeyHandler,
     *      pacman moves one square in that direction.
     */
    public void movePacman() {
        switch (_pacmanDirection) {
            case LEFT:
                // So pacman can use the tunnel.
                if (_pacman.getXLoc() <= Constants.CIRCLE_OFFSET) {
                    _pacman.moveX(22);
                } else if (canMove(-1,0)) {
                    _pacman.moveX(-1);
                }
                break;
            case RIGHT:
                // So pacman can use the tunnel.
                if (_pacman.getXLoc() >= Constants.SCENE_WIDTH-Constants.CIRCLE_OFFSET) {
                    _pacman.moveX(-22);
                } else if (canMove(1,0)) {
                    _pacman.moveX(1);
                }
                break;
            case UP:
                if (canMove(0,-1)) {
                    _pacman.moveY(-1);
                }
                break;
            case DOWN:
                if (canMove(0,1)) {
                    _pacman.moveY(1);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Moves the ghosts based on the different modes.
     * Chase mode moves the ghost to target pacman's location (with different offsets for each ghost).
     * Scatter mode moves each ghost to a different corner of the maze.
     * Frightened mode changes the target of each ghost to a randomized corner of the maze
     *      (repeats so it's always moving around the maze).
     */
    public void moveGhosts() {
        int pacmanRow = (int) _pacman.getYLoc() / Constants.SQUARE_WIDTH;
        int pacmanCol = (int) _pacman.getXLoc() / Constants.SQUARE_WIDTH;
        switch (_mode) {
            case CHASE:
                _map[_blinky.getGhostRow()][_blinky.getGhostCol()].removeFromCollidables(_blinky);
                _blinky.moveGhost(new BoardCoordinate(pacmanRow, pacmanCol, true));
                _map[_blinky.getGhostRow()][_blinky.getGhostCol()].addToCollidables(_blinky);

                _map[_inky.getGhostRow()][_inky.getGhostCol()].removeFromCollidables(_inky);
                _inky.moveGhost(new BoardCoordinate(pacmanRow, pacmanCol + 2, true));
                _map[_inky.getGhostRow()][_inky.getGhostCol()].addToCollidables(_inky);

                _map[_pinky.getGhostRow()][_pinky.getGhostCol()].removeFromCollidables(_pinky);
                _pinky.moveGhost(new BoardCoordinate(pacmanRow - 4, pacmanCol, true));
                _map[_pinky.getGhostRow()][_pinky.getGhostCol()].addToCollidables(_pinky);

                _map[_clyde.getGhostRow()][_clyde.getGhostCol()].removeFromCollidables(_clyde);
                _clyde.moveGhost(new BoardCoordinate(pacmanRow + 1, pacmanCol - 2, true));
                _map[_clyde.getGhostRow()][_clyde.getGhostCol()].addToCollidables(_clyde);

                _blinky.setColor(Color.DEEPPINK);
                _inky.setColor(Color.CYAN);
                _pinky.setColor(Color.LAWNGREEN);
                _clyde.setColor(Color.LIGHTCORAL);
                break;
            case SCATTER:
                _map[_blinky.getGhostRow()][_blinky.getGhostCol()].removeFromCollidables(_blinky);
                _blinky.moveGhost(new BoardCoordinate(1,1,true));
                _map[_blinky.getGhostRow()][_blinky.getGhostCol()].addToCollidables(_blinky);

                _map[_inky.getGhostRow()][_inky.getGhostCol()].removeFromCollidables(_inky);
                _inky.moveGhost(new BoardCoordinate(1,21,true));
                _map[_inky.getGhostRow()][_inky.getGhostCol()].addToCollidables(_inky);

                _map[_pinky.getGhostRow()][_pinky.getGhostCol()].removeFromCollidables(_pinky);
                _pinky.moveGhost(new BoardCoordinate(21,1,true));
                _map[_pinky.getGhostRow()][_pinky.getGhostCol()].addToCollidables(_pinky);

                _map[_clyde.getGhostRow()][_clyde.getGhostCol()].removeFromCollidables(_clyde);
                _clyde.moveGhost(new BoardCoordinate(21,21,true));
                _map[_clyde.getGhostRow()][_clyde.getGhostCol()].addToCollidables(_clyde);

                _blinky.setColor(Color.DEEPPINK);
                _inky.setColor(Color.CYAN);
                _pinky.setColor(Color.LAWNGREEN);
                _clyde.setColor(Color.LIGHTCORAL);
                break;
            case FRIGHTENED:
                _map[_blinky.getGhostRow()][_blinky.getGhostCol()].removeFromCollidables(_blinky);
                _blinky.moveGhost(randomCorner());
                _map[_blinky.getGhostRow()][_blinky.getGhostCol()].addToCollidables(_blinky);

                _map[_inky.getGhostRow()][_inky.getGhostCol()].removeFromCollidables(_inky);
                _inky.moveGhost(randomCorner());
                _map[_inky.getGhostRow()][_inky.getGhostCol()].addToCollidables(_inky);

                _map[_pinky.getGhostRow()][_pinky.getGhostCol()].removeFromCollidables(_pinky);
                _pinky.moveGhost(randomCorner());
                _map[_pinky.getGhostRow()][_pinky.getGhostCol()].addToCollidables(_pinky);

                _map[_clyde.getGhostRow()][_clyde.getGhostCol()].removeFromCollidables(_clyde);
                _clyde.moveGhost(randomCorner());
                _map[_clyde.getGhostRow()][_clyde.getGhostCol()].addToCollidables(_clyde);

                _blinky.setColor(Color.POWDERBLUE);
                _inky.setColor(Color.POWDERBLUE);
                _pinky.setColor(Color.POWDERBLUE);
                _clyde.setColor(Color.POWDERBLUE);
                break;
            default:
                break;
        }
    }

    /**
     * Returns a random corner of the maze.
     * @return
     */
    public BoardCoordinate randomCorner() {
        BoardCoordinate target = null;
        int rand = (int) (Math.random() * 10);
        switch (rand) {
            case 0: case 1: case 2:
                target = new BoardCoordinate(1,1,true);
                break;
            case 3: case 4: case 5:
                target = new BoardCoordinate(1,21,true);
                break;
            case 6: case 7:
                target = new BoardCoordinate(21,21,true);
                break;
            case 8: case 9:
                target = new BoardCoordinate(21,1,true);
                break;
            default:
                break;
        }
        return target;
    }

    /**
     * Returns a boolean based on if pacman can move into a certain cell (if it's not a wall).
     * @param moveX
     * @param moveY
     * @return
     */
    public boolean canMove(int moveX, int moveY) {
        double newY = _pacman.getYLoc() - Constants.CIRCLE_OFFSET + (moveY*Constants.SQUARE_WIDTH);
        double newX = _pacman.getXLoc() - Constants.CIRCLE_OFFSET + (moveX*Constants.SQUARE_WIDTH);

        int row = (int) (newY/Constants.SQUARE_WIDTH);
        int col = (int) (newX/Constants.SQUARE_WIDTH);

        if (col >= 0 && col < 23) {
            if (!_map[row][col].isWall()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if there is a collidable item in the square that pacman is in.
     */
    public void checkCollision() {
        double xLoc = _pacman.getXLoc();
        double yLoc = _pacman.getYLoc();
        int row = (int) ((yLoc - Constants.CIRCLE_OFFSET)/Constants.SQUARE_WIDTH);
        int col = (int) ((xLoc - Constants.CIRCLE_OFFSET)/Constants.SQUARE_WIDTH);

        for (int i = 0; i < _map[row][col].getCollidables().size(); i++) {
            Collidable item = _map[row][col].getCollidables().get(i);
            item.collide();

            if (item.type() == Item.GHOST) {
                // If in frightened mode, the ghosts are eaten and moved into the pen.
                if (_mode == Mode.FRIGHTENED) {
                    _eatenGhosts.addLast((Ghosts) item);
                    _map[((Ghosts) item).getGhostRow()][((Ghosts) item).getGhostCol()].removeFromCollidables(item);
                    ((Ghosts) item).setXLoc(11*Constants.SQUARE_WIDTH);
                    ((Ghosts) item).setYLoc(11*Constants.SQUARE_WIDTH);
                // If in chase or scatter mode, pacman loses a life and the game is reset.
                } else if (_mode == Mode.CHASE || _mode == Mode.SCATTER) {
                    _map[((Ghosts) item).getGhostRow()][((Ghosts) item).getGhostCol()].removeFromCollidables(item);
                    _lifeCount = _lifeCount - 1;
                    reset();
                }
            // If the item is an energizer, the mode changes to frightened.
            } else if (item.type() == Item.ENERGIZER) {
                _mode = Mode.FRIGHTENED;
            }

            if (item.type() == Item.ENERGIZER || item.type() == Item.DOT) {
                _eaten++;
            }

            _points = _points + item.getPoints();
            _score.setText("SCORE: " + _points);
            _lives.setText("LIVES: " + _lifeCount);
        }
        _map[row][col].getCollidables().clear();
    }

    /**
     * Checks whether the game is over.
     * Stops the game when pacman loses all three lives by stopping the timelines and making sure pacman can't move.
     * If pacman eats all the dots and energizers in the game, the game continues onto the next level.
     */
    public void gameOver() {
        // Stops the music when the quit button is pressed cuz dear god it won't stop playing otherwise.
        if (System.getProperties().containsValue(0)) {
            _clip.stop();
        }
        if (_lifeCount <= 0) {
            reset();
            _timeline.stop();
            _penTimeline.stop();
            _pacman.getPacman().removeEventHandler(KeyEvent.KEY_PRESSED, _keyHandler);
            _label.setText("GAME OVER LOSER :(");
            _label.setFont(Font.font("Default", FontWeight.BOLD, 35));
            _label.setTranslateX(-40);
            _label.setTextFill(Color.RED);
            _label.setTranslateY(-240);
            _score.setText("FINAL SCORE: " + _points);
            _score.setTranslateX(90);
            _lives.setOpacity(0.3);
            _gamePane.setOpacity(0.35);
//            _clip.stop();
        } else if (_eaten >= 186) {
            nextLevel();
        }
    }

    /**
     * When the next level is called, the timeline speeds up so pacman and the ghosts move faster.
     * The game is reset, three lives are given back, and the number of eaten items is reset since
     *      the map's dots and energizers are remade.
     */
    public void nextLevel() {
        _level++;
        reset();
        _label.setText("LEVEL " + _level + " okrrrr <3");
        _label.setTranslateX(-15);
        _lifeCount = 3;
        _eaten = 0;

        for (int row = 0; row < Constants.MAZE_DIMENSION; row++) {
            for (int col = 0; col < Constants.MAZE_DIMENSION; col++) {

                switch (SupportMap.getSupportMap()[row][col]) {
                    case DOT:
                        Dot dot = new Dot(_gamePane);
                        _map[row][col].addToCollidables(dot);
                        dot.setXLoc(_map[row][col].getXLoc());
                        dot.setYLoc(_map[row][col].getYLoc());
                        _gamePane.getChildren().add(dot.getDot());
                        break;
                    case ENERGIZER:
                        Energizer energizer = new Energizer(_gamePane);
                        _map[row][col].addToCollidables(energizer);
                        energizer.setXLoc(_map[row][col].getXLoc());
                        energizer.setYLoc(_map[row][col].getYLoc());
                        _gamePane.getChildren().add(energizer.getEnergizer());
                        break;
                }
            }
        }
        _blinky.toFront();
        _inky.toFront();
        _pinky.toFront();
        _clyde.toFront();
        _pacman.toFront();

        _duration = _duration - 0.05;

        // The last level makes the walls black so you're really confused idk.
        if (_level == 5) {
            for (int row = 0; row < Constants.MAZE_DIMENSION; row++) {
                for (int col = 0; col < Constants.MAZE_DIMENSION; col++) {

                    if (SupportMap.getSupportMap()[row][col] == SquareType.WALL) {
                        MazeSquares wall = new MazeSquares();
                        wall.setXLoc(_map[row][col].getXLoc());
                        wall.setYLoc(_map[row][col].getYLoc());
                        _gamePane.getChildren().add(wall.getSquare());
                    }
                }
            }
            // The duration is set to the previous level's speed.
            _duration = _duration + 0.05;
            _label.setText("hehe try this loser");
        }

        // A new timeline is setup so pacman can eat the new dots/energizers.
        setupTimeline();

        if (_duration <= 0.04) {
            reset();
            _timeline.stop();
            _penTimeline.stop();
            _pacman.getPacman().removeEventHandler(KeyEvent.KEY_PRESSED, _keyHandler);
            _label.setText("WINNER *WOOT WOOT* :)");
            _label.setFont(Font.font("Default", FontWeight.BOLD, 33));
            _label.setTranslateX(-55);
            _label.setTextFill(Color.RED);
            _label.setTranslateY(-240);
            _score.setText("FINAL SCORE: " + _points);
            _score.setTranslateX(90);
            _lives.setOpacity(0.3);
            _gamePane.setOpacity(0.35);
            _clip.stop();
        }
    }

    /**
     * This is the inner private KeyHandler class.
     * Based on the arrow key pressed, pacman's direction if set.
     */

    private class KeyHandler implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent event) {
            KeyCode keyPressed = event.getCode();
            switch (keyPressed) {
                case LEFT:
                    if (canMove(-1,0)) {
                        _pacmanDirection = Direction.LEFT;
                    }
                    break;
                case RIGHT:
                    if (canMove(1,0)) {
                        _pacmanDirection = Direction.RIGHT;
                    }
                    break;
                case UP:
                    if (canMove(0,-1)) {
                        _pacmanDirection = Direction.UP;
                    }
                    break;
                case DOWN:
                    if (canMove(0,1)) {
                        _pacmanDirection = Direction.DOWN;
                    }
                    break;
                // Pauses the game and the music.
                case SPACE:
                    if (_timeline.getCurrentRate() != 0) {
                        _timeline.pause();
                        _penTimeline.pause();
                        _label.setText("you are paused my dude");
                        _gamePane.setOpacity(0.5);
//                        _clip.stop();
                    } else {
                        _timeline.play();
                        _penTimeline.play();
                        _gamePane.setOpacity(1);
//                        _clip.start();
                    }
                    break;
                // Quits the game cuz I got too lazy to press quit.
                case ESCAPE: case ENTER: case Q:
                    System.exit(0);
                    break;
                default:
                    break;
            }
            event.consume();
        }
    }

    /**
     * Creates the score and lives labels .
     * Another label is made to write directions or whatnot.
     */
    public void makeLabels() {
        _points = 0;
        _score = new Label("SCORE: " + _points);
        _score.setTextFill(Color.YELLOW);
        _score.setFont(Font.font("Default", FontWeight.BOLD, 20));
        _score.setTranslateX(Constants.SCENE_WIDTH/5);
        _score.setTranslateY(Constants.SCENE_WIDTH - 25);
        _root.setLeft(_score);

        _lifeCount = 3;
        _lives = new Label("LIVES: " + _lifeCount);
        _lives.setTextFill(Color.YELLOW);
        _lives.setFont(Font.font("Default", FontWeight.BOLD, 20));
        _lives.setTranslateX(-Constants.SCENE_WIDTH/5);
        _lives.setTranslateY(Constants.SCENE_WIDTH - 25);
        _root.setRight(_lives);

        _label = new Label("press SPACE to start");
        _label.setTextFill(Color.YELLOW);
        _label.setFont(Font.font("Default", FontWeight.BOLD, 15));
        _label.setTranslateY(Constants.SQUARE_WIDTH*2);
        _label.setTranslateX(-10);
        _root.setCenter(_label);
    }

    /**
     * Adds music to the game for ~ i m m e r s i o n ~
     * Thank you to intelliJ for all the try/catch statements.
     */
//    public void music() {
//
//        AudioInputStream audioInputStream = null;
//        try {
//            audioInputStream = AudioSystem.getAudioInputStream(new File("C:\\Users\\vongm\\Downloads\\Pac-man theme remix - By Arsenic1987.wav"));
//        } catch (UnsupportedAudioFileException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            _clip = AudioSystem.getClip();
//        } catch (LineUnavailableException e) {
//            e.printStackTrace();
//        }
//        try {
//            _clip.open(audioInputStream);
//        } catch (LineUnavailableException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        _clip.start();
//        _clip.loop(Clip.LOOP_CONTINUOUSLY);
//    }

    /**
     * Makes the map based on the locations of items in the support map.
     */
    public void makeMap() {
        _map = new MazeSquares[Constants.MAZE_DIMENSION][Constants.MAZE_DIMENSION];

        for (int row = 0; row < Constants.MAZE_DIMENSION; row++) {
            for (int col = 0; col < Constants.MAZE_DIMENSION; col++) {
                MazeSquares square = new MazeSquares();
                _map[row][col] = square;
                square.setXLoc(col * Constants.SQUARE_WIDTH);
                square.setYLoc(row * Constants.SQUARE_WIDTH);
            }
        }

        for (int row = 0; row < Constants.MAZE_DIMENSION; row++) {
            for (int col = 0; col < Constants.MAZE_DIMENSION; col++) {

                switch (SupportMap.getSupportMap()[row][col]) {
                    case WALL:
                        MazeSquares wall = new MazeSquares();
                        wall.makeWall();
                        wall.setXLoc(_map[row][col].getXLoc());
                        wall.setYLoc(_map[row][col].getYLoc());
                        _map[row][col] = wall;
                        _gamePane.getChildren().add(wall.getSquare());
                        break;
                    case DOT:
                        Dot dot = new Dot(_gamePane);
                        _map[row][col].addToCollidables(dot);
                        dot.setXLoc(_map[row][col].getXLoc());
                        dot.setYLoc(_map[row][col].getYLoc());
                        _gamePane.getChildren().add(dot.getDot());
                        break;
                    case ENERGIZER:
                        Energizer energizer = new Energizer(_gamePane);
                        _map[row][col].addToCollidables(energizer);
                        energizer.setXLoc(_map[row][col].getXLoc());
                        energizer.setYLoc(_map[row][col].getYLoc());
                        _gamePane.getChildren().add(energizer.getEnergizer());
                        break;
                    case FREE:
                        MazeSquares free = new MazeSquares();
                        free.setXLoc(_map[row][col].getXLoc());
                        free.setYLoc(_map[row][col].getYLoc());
                        _map[row][col] = free;
                        _gamePane.getChildren().add(free.getSquare());
                        break;
                    case PACMAN_START_LOCATION:
                        _pacman = new Pacman();
                        _pacman.setXLoc(_map[row][col].getXLoc() + Constants.CIRCLE_OFFSET);
                        _pacman.setYLoc(_map[row][col].getYLoc() + Constants.CIRCLE_OFFSET);
                        _gamePane.getChildren().add(_pacman.getPacman());
                        break;
                    case GHOST_START_LOCATION:
                        _ghostDirection = Direction.DOWN;
                        _blinky = new Ghosts(_map, _ghostDirection, _mode);
                        _map[row-2][col].addToCollidables(_blinky);
                        _blinky.setColor(Color.DEEPPINK);
                        _blinky.setXLoc(_map[row][col].getXLoc());
                        _blinky.setYLoc(_map[row][col].getYLoc() - 2*Constants.SQUARE_WIDTH);

                        _pinky = new Ghosts(_map, _ghostDirection, _mode);
                        _map[row][col-1].addToCollidables(_pinky);
                        _pinky.setColor(Color.LAWNGREEN);
                        _pinky.setXLoc(_map[row][col].getXLoc() - Constants.SQUARE_WIDTH);
                        _pinky.setYLoc(_map[row][col].getYLoc());
                        _eatenGhosts.addLast(_pinky);

                        _inky = new Ghosts(_map, _ghostDirection, _mode);
                        _map[row][col].addToCollidables(_inky);
                        _inky.setColor(Color.CYAN);
                        _inky.setXLoc(_map[row][col].getXLoc());
                        _inky.setYLoc(_map[row][col].getYLoc());
                        _eatenGhosts.addLast(_inky);

                        _clyde = new Ghosts(_map, _ghostDirection, _mode);
                        _map[row][col+1].addToCollidables(_clyde);
                        _clyde.setColor(Color.LIGHTCORAL);
                        _clyde.setXLoc(_map[row][col].getXLoc() + Constants.SQUARE_WIDTH);
                        _clyde.setYLoc(_map[row][col].getYLoc());
                        _eatenGhosts.addLast(_clyde);

                        _gamePane.getChildren().addAll(_blinky.getGhost(), _inky.getGhost(), _pinky.getGhost(), _clyde.getGhost());
                        break;
                    default:
                        break;
                }
            }
        }
        // Brings the ghosts and pacman to the front so that the maze squares don't block them.
        _blinky.toFront();
        _inky.toFront();
        _pinky.toFront();
        _clyde.toFront();
        _pacman.toFront();
    }
}
