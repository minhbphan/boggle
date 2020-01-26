import squint.*;
import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.io.File;

public class buttonGrid extends GUIManager {
    // Dimensions of the grid
    public final int WIDTH = 4;
    public final int HEIGHT = 4;

    //variables keeping track of gameplay components
    private String word = "";
    private String oppName = "";
    private JButton[] clicked = new JButton[16];
    private int using = 0;
    private int myScore;
    private int oppScore;
    private int progress = 180;
    private int remaining = 15;
    private int oneWindow = 0;

    //boolean keeping track of whether or not the game is running
    private boolean playing;
    private boolean multiplaying = false;
    //private boolean partnerFound = false;

    //connects to the Lexicon class to check if words are valid
    private Lexicon myDict = new Lexicon();
    // Array of all buttons
    private LetterButton [] allButtons = new LetterButton[ WIDTH*HEIGHT ];

    //imitates the cubesides when shuffling
    private final String [][] cubeSides = new String[][]{
            { "A", "A", "C", "I", "O", "T" },
            { "T", "Y", "A", "B", "I", "L" },
            { "J", "M", "O", "QU", "A", "B" },
            { "A", "C", "D", "E", "M", "P" },
            { "A", "C", "E", "L", "S", "R" },
            { "A", "D", "E", "N", "V", "Z" },
            { "A", "H", "M", "O", "R", "S" },
            { "B", "F", "I", "O", "R", "X" },
            { "D", "E", "N", "O", "S", "W" },
            { "D", "K", "N", "O", "T", "U" },
            { "E", "E", "F", "H", "I", "Y" },
            { "E", "G", "I", "N", "T", "V" },
            { "E", "G", "K", "L", "U", "Y" },
            { "E", "H", "I", "N", "P", "S" },
            { "E", "L", "P", "S", "T", "U" },
            { "G", "I", "L", "R", "U", "W" }
        };

    // Size of text displayed in puzzle pieces
    private final float FONT_SIZE = 20;
    // Font used for buttons
    private final Font BIGFONT = this.getFont().deriveFont( FONT_SIZE );
    // How many random moves to make when shuffling the puzzle pieces
    private int SHUFFLE_STEPS = 100;

    // Picks random moves while shuffling
    private Random letterChooser = new Random();

    //JPanels to help layout interface of game
    private JPanel labelsAndTimerPanel = new JPanel();
    private JProgressBar timer = new JProgressBar( 0, 180 );
    private JPanel labelsPanel = new JPanel();
    private JLabel creatingWordLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel scoreLabel = new JLabel("Score = ",SwingConstants.CENTER);
    private JPanel grid = new JPanel();
    private JTextArea wordsUsed = new JTextArea(15,10);

    private PaceMaker beat;
    // The complete list of words that have been entered
    private WordsList myList = new WordsList();
    private WordsList oppList = new WordsList();

    private Winner winWindow;

    private NetConnection connected;
    public buttonGrid() {
        contentPane.setLayout( new BorderLayout() );
        grid.setLayout( new GridLayout(HEIGHT, WIDTH, 0, 0) );
        // Place buttons representing pieces in a grid layout and array
        for ( int y = 0; y < HEIGHT; y++ ) {
            for ( int x = 0; x < WIDTH; x++ ) {
                LetterButton button = new LetterButton( x, y );
                allButtons[ y*WIDTH + x ] = button;
                grid.add( button );
            }
        }
        labelsPanel.setLayout( new GridLayout(1,2) );
        labelsPanel.add(creatingWordLabel, BorderLayout.WEST);
        labelsPanel.add(scoreLabel, BorderLayout.EAST);

        labelsAndTimerPanel.setLayout( new BorderLayout() );
        labelsAndTimerPanel.add(timer, BorderLayout.NORTH);
        labelsAndTimerPanel.add(labelsPanel, BorderLayout.SOUTH);

        contentPane.setLayout( new BorderLayout() );
        contentPane.add(labelsAndTimerPanel, BorderLayout.NORTH);
        contentPane.add(grid, BorderLayout.CENTER);
        contentPane.add(wordsUsed, BorderLayout.EAST);
    }

    // Updates the timer for the game
    public void tick(){
        timer.setValue(progress);
        progress--;
        if(timer.getValue() == 0){
            endGame();
            creatingWordLabel.setText("Time's Up! Click End Game & Try Again!");
        } else if (multiplaying && (myScore == 30 || oppScore == 30)) {
            // if a player is multiplaying and someone reaches 30 points before
            // the timer is over, game is ended
            endGame();
        }
    }

    //starts new game and resets window and variables
    public void startGame(){
        if(!playing){
            playing = true;
            using = 0;
            myScore = 0;
            oppScore = 0;
            oneWindow = 0;
            progress = 180;
            if(multiplaying){
                scoreLabel.setText("My Score = "+myScore+ "     " +oppName+ "'s Score = " +oppScore);
                creatingWordLabel.setText("Playing Against "+oppName);
            }else{
                scoreLabel.setText("Score = "+myScore);
                creatingWordLabel.setText("");
            }
            wordsUsed.setText("");
            clicked = new JButton[16];
            if(!multiplaying){
                shuffle();
            }
            //starts new timer for new game
            beat = new PaceMaker(1, this);
        }else{
            endGame();
        }
    }

    //ends the game
    public void endGame(){
        if(multiplaying){
            // opens a window that says who won and makes sure only one such window
            // is opened
            if( oneWindow == 1 && !oppName.isEmpty() ) {
                if( myScore > oppScore ) {
                    winWindow = new Winner("Game Ended: You Won!");
                } else if( myScore < oppScore ) {
                    winWindow = new Winner("Game Ended: You Lost! :(");
                } else if( myScore == oppScore ) {
                    winWindow = new Winner("Game Ended: You Tied!");
                }
            } else if( oppName.isEmpty() ) {
                // Changes label for when game is ended before partner is found
                creatingWordLabel.setText("Game Ended");
            }
            connected.close(); 
        } else {
            creatingWordLabel.setText("Game Ended");
        }
        playing = false;
        myList = new WordsList();
        word = "";
        //checks if there is a PaceMaker created
        if (beat != null) {
            beat.stop();
        }
    }

    // sets game in partner play mode
    public void playingMulti(String playerName, String groupName){
        multiplaying = true;
        connected = new NetConnection( "lohani.cs.williams.edu", 13419 );
        if( !playerName.contains("\"") && !groupName.contains("\"") ){
            connected.out.println( "PLAY " + "\""+playerName +"\" "+ "\""+groupName+"\"" );
            connected.addMessageListener(this);
            creatingWordLabel.setText("Finding a partner...");
        }else{
            creatingWordLabel.setText("Invalid Player/Group Name!");
        }
    }

    //checks to see if letters selected are adjacent to each and forms valid words
    public void buttonClicked(JButton which){
        if(playing){
            LetterButton whichLetter = (LetterButton) which;
            //checks to see if button isn't already selected and adjacent to last button clicked
            if((using == 0)||(!includes(which) && whichLetter.isAdjacentTo((LetterButton)clicked[using - 1]))){
                clicked[using] = which;
                using++;
                word += which.getText();
                creatingWordLabel.setText(word);
            } //checks to see if word created is valid and not been used before
            else if( whichLetter == (LetterButton)clicked[using - 1] ) {
                validateWord();
            }
        }
    }

    public void validateWord(){
        if(myDict.contains(word)){
            if(multiplaying){
                //sends valid word to server
                connected.out.println("WORD "+word);
            }
            if (!myList.contains( word )) {
                if ( !oppList.contains( word ) ) {
                    // adds new words to list and adds points
                    myList = new WordsList( word, myList );
                    myScore = myScore + scoreChange(word);
                    creatingWordLabel.setText("");
                }else if( oppList.contains( word ) ){
                    // if word has been played by opponent already, score is
                    // decremented from opponent
                    myList = new WordsList( word, myList );
                    oppScore = oppScore - scoreChange(word);
                    creatingWordLabel.setText("Word already played!");
                }
            } else if( myList.contains( word ) ) {
                creatingWordLabel.setText("Word already played!");
            }
        }else{
            creatingWordLabel.setText("Invalid word!");
        }
        //updates score and resets letters picked for new play
        if(multiplaying){
            scoreLabel.setText("My Score = "+myScore+ "     " +oppName+ "'s Score = " +oppScore);
        }else{
            scoreLabel.setText("Score = "+myScore);
        }
        wordsUsed.setText( myList.toString() );
        word = "";
        using = 0;
        clicked = new JButton[16];
    }

    //goes through clicked array and checks if button clicked has already been clicked
    public boolean includes(JButton which){
        boolean contains = false;
        for (int i = 0; i<clicked.length; i++){
            if(clicked[i] == which){
                contains = true;
            }
        }
        return contains;
    }

    //executes code whenever information is received from the server
    public void dataAvailable(){
        String responseFromServer = connected.in.nextLine();
        String oppWord = "";
        if( responseFromServer.startsWith( "START" ) ) {
            // gets the name of the opponent
            oppName = responseFromServer.substring(6);
            // receives the buttons for the board
            for ( int i = 0; i < 16; i++ )  {
                String receive = connected.in.nextLine();
                allButtons[i].setText(receive);
            }
            startGame();
        }else if(responseFromServer.startsWith( "WORD" )){
            // receives words that are played by opponent
            oppWord = responseFromServer.substring(5);
            if ( !oppList.contains(oppWord) ){
                if(!myList.contains(oppWord)){
                    // adds points for opponent if valid word is played
                    oppList = new WordsList( oppWord, oppList );
                    oppScore = oppScore + scoreChange(oppWord);
                    scoreLabel.setText("My Score = "+myScore+ "     " +oppName+ "'s Score = " +oppScore);
                }else if( myList.contains( oppWord ) ){
                    // if opponent's word has been played by player already, score is
                    // decremented from player
                    oppList = new WordsList( oppWord, oppList );
                    myScore = myScore - scoreChange(oppWord);
                    scoreLabel.setText("My Score = "+myScore+ "     " +oppName+ "'s Score = " +oppScore);
                }
            }
        }else if(responseFromServer.startsWith( "ERR" )){
            connected.close(); 
        }
    }

    //executes code whenever the connection to server is closed
    public void connectionClosed(){
        oneWindow++;
        endGame();
        multiplaying = false;
    }

    //calls on the randomizing method to shuffle letters
    public void shuffle(){
        remaining = 15;
        for ( int y = 0; y < HEIGHT; y++ ) {
            for ( int x = 0; x < WIDTH; x++ ) {
                LetterButton button = allButtons[ y*WIDTH + x ];
                button.setText(randomize());
            }
        }
    }

    // Make a sequence of random moves to shuffle the letters
    public String randomize( ) {
        int randArray = letterChooser.nextInt( remaining+1);
        int randLetter = letterChooser.nextInt(6);
        String alphabet = cubeSides[randArray][randLetter];
        for(int side = 0; side < 6; side++){
            String hold = cubeSides[randArray][side];
            cubeSides[randArray][side] = cubeSides[remaining][side];
            cubeSides[remaining][side] = hold;
        }
        remaining --;
        return alphabet;
    }

    // determines how many points should be added or taken away depending on the length
    // of the word that is played
    public int scoreChange(String word) {
        int length = word.length();
        if (length == 3 || length == 4) {
            return 1;
        } else if (length == 5) {
            return 2;
        } else if (length == 6) {
            return 3;
        } else if (length == 7) {
            return 5;
        } else if (length >= 8) {
            return 11;
        } else {
            return 0;
        }
    }
}
