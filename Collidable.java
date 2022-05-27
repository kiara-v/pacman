package pacman;

/**
 * This is the Collidable interface. It is implemented in the Dot, Energizer, and Ghosts classes.
 */

public interface Collidable {

    // Dots, Energizers, and Ghosts all collide with Pacman, but in different ways.
    public int getPoints();
    public void collide();

    // Returns the type of item the Collidable item is.
    public Item type();
}
