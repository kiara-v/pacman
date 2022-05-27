package pacman;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * This is the PaneOrganizer class.
 * The panes used for the game are instantiated here.
 */

public class PaneOrganizer {
    private BorderPane _root;

    /**
     * This is the PaneOrganizer's constructor.
     * A new BorderPane is instantiated as the root used to set the scene.
     * A vanilla pane is made for the game, and added to the root.
     * A quit button is created and added to the root.
     * Then a new Game is instantiated.
     */
    public PaneOrganizer() {
        _root = new BorderPane();
        BackgroundFill fill = new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(fill);
        _root.setBackground(background);

        Pane gamePane = new Pane();
        _root.getChildren().add(gamePane);

        Button quitButton = new Button("QUIT");
        quitButton.setFont(Font.font("Default", FontWeight.BOLD, 12));
        quitButton.setTextFill(Color.BLACK);
        quitButton.setTranslateX(Constants.SCENE_WIDTH/2 - 22);
        quitButton.setOnAction(new QuitHandler());
        _root.setBottom(quitButton);

        // The Game takes in the game pane and root as parameters so graphical items can be removed in the Game class.
        new Game(gamePane, _root);
    }

    /**
     * This is the private inner QuitHandler class.
     * When the quit button is pressed, the game exits.
     */

    private class QuitHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            System.exit(0);
            event.consume();
        }
    }

    /**
     * Returns the root.
     * @return
     */
    public Pane getRoot() {
        return _root;
    }
}
