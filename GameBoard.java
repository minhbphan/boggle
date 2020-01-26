import squint.*;
import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.io.File;

public class GameBoard extends GUIManager{
    // Variables for the size of the program's window
    private final int WINDOW_WIDTH = 760, WINDOW_HEIGHT = 550;

    // Buttons and labels
    private final JButton startGame = new JButton( "Start New Game" );
    private final JButton findPartner = new JButton( "Find Partner" );

    private JLabel gameMode = new JLabel("",SwingConstants.CENTER);
    private final JLabel playerNameLabel = new JLabel( "Your Name:" );
    private final JLabel groupNameLabel = new JLabel( "Partner Group:" );
    private JTextField playerNameText = new JTextField(10);
    private JTextField groupNameText = new JTextField(10);

    // The grid of puzzle buttons
    private buttonGrid buttons;
    private NetConnection connected;
    //private boolean partnerFound;

    //The panels necessary for interface layout
    private JPanel gamePlay = new JPanel();
    private JPanel controlPanel = new JPanel();
    private JPanel playersPanel = new JPanel();

    public GameBoard(){
        // Create window to hold all the components
        this.createWindow( WINDOW_WIDTH, WINDOW_HEIGHT );
        contentPane.setLayout( new BorderLayout() );

        //Place the buttons in the center of the window
        buttons = new buttonGrid();
        contentPane.add(buttons);

        //Add control buttons to start game with optional multiplayer
        controlPanel.setLayout(new GridLayout(3,1));
        controlPanel.add(gameMode);
        controlPanel.add( startGame );
        controlPanel.add( findPartner );
        gamePlay.add( controlPanel, BorderLayout.WEST );

        //sets the multiplayer option in the bottom right 
        playersPanel.setLayout(new GridLayout(2,2));
        playersPanel.add( playerNameLabel);
        playersPanel.add( playerNameText );
        playersPanel.add( groupNameLabel);
        playersPanel.add( groupNameText );
        gamePlay.add( playersPanel, BorderLayout.EAST );
        contentPane.add( gamePlay, BorderLayout.SOUTH );
    }

    public void buttonClicked( JButton which ) {
        if(which == startGame) {
            if(startGame.getText() == "Start New Game"){
                // Starts solitaire game and changes button label to "End"
                buttons.startGame();
                gameMode.setText("Playing Solitaire");
                startGame.setText("End");
                findPartner.setEnabled(false);
            }else if(startGame.getText() == "End"){
                // If game is going on, ends game
                buttons.endGame();
                gameMode.setText("");
                startGame.setText("Start New Game");
                findPartner.setText("Find Partner");
                findPartner.setEnabled(true);
            }
        }else if (which == findPartner && !(playerNameText.getText().isEmpty())){
            // Starts looking for a partner to play a multiplayer game with
            String playerName = playerNameText.getText();
            String groupName = groupNameText.getText();
            gameMode.setText("Playing Multiplayer");
            buttons.playingMulti(playerName,groupName);
            findPartner.setEnabled(false);
            startGame.setText("End");
        }
    }

}