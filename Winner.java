import squint.*;
import javax.swing.*;

public class Winner extends GUIManager {
    // Variables for the size of the program's window
    private final int WINDOW_WIDTH = 200, WINDOW_HEIGHT = 50;

    private JLabel label = new JLabel();

    public Winner(String display ) {
        // Creates a window that tells the player who won
        this.createWindow( WINDOW_WIDTH, WINDOW_HEIGHT );
        label.setText(display);
        contentPane.add(label);
    }

}
