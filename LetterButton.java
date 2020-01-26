import javax.swing.JButton;
public class LetterButton extends JButton{
    // Remember the location of this button in the puzzle grid
    private int row;
    private int column;

    // Create a new button.
    public LetterButton( int theColumn, int theRow ) {
        row = theRow;
        column = theColumn;
    }

    // Return the row number of this button
    public int getRow() {
        return row;
    }
    // Return the column number of this button
    public int getColumn() {
        return column;
    }

    // Return true if other button is adjacent to this button horizontally or vertically
    public boolean isAdjacentTo( LetterButton other ) {
        return other != this &&
        Math.abs(row - other.getRow()) <= 1 && Math.abs(column - other.getColumn()) <= 1 ;
    }
}
