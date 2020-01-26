    ------------------------------------------------------------------------
Freshman Fall 2019 final project; worked together with partner Hannah Ahn
------------------------------------------------------------------------
IMPLEMENTATION PLAN:
Goals for Different Classes:

GUI components:
-Actual board (collection/array of buttons)
-Class GameBoard
-Scoreboard
-Words created
  Stores in JTextArea after cross-checking with Lexicon class
  Recursively updates
-Gameplay buttons
  If JButton startGame is pressed, program shuffles letters and starts solitaire game
  If JButton findPartner is pressed, check to see if JTextField containing player name is NOT empty, and if there’s a “group-name”, neither “player-name” nor “group-name” can contain quotes
  Create MultiPlayer class and follows Boggle Pairing Protocol (pg. 8)
-Player/group names
  Class MultiPlayer implements Boggle Pairing Protocol
  Create new Netconnection(“lohani.cs.williams.edu”, 13419)
  Send out request: PLAY "player-name" "group-name"
  “player-name” will be sent to the partner's client and displayed to the opponent 
  “group-name” tells the server that client only wants to play w/ partners of same group name 
  If “group name” == “” (empty), then play can be initiated with any other client who specifies no group name 
-JProgressBar
  Class Timer keeps track of the time remaining in gameplay (pg. 9)
  Use PaceMaker class of Squint library and tick() method to update JProgressBar
  Ends game and displays final score when JProgressBar is empty
-Randomize letters
  Class buttonGrid creates buttons and contains randomization & shuffling methods that places buttons in a grid format and shuffles letters around when appropriate button is clicked

Creating words
Determine if:
  *Button clicked is the same as the last letter clicked
  *Button clicked is one of the other buttons already used in the current word
  *Make 1D array to hold JButtons clicked while forming current word (big enough to hold 16 and resets for each new word)
  Button is adjacent to the last button added to the current word
 
Checks with the Lexicon class
  Private Lexicon myDict = new Lexicon();
  myDict.contains(“Some word”);   //returns boolean true/false
