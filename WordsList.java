// Class used to hold a list of Strings of words users created
public class WordsList {
    private String firstWord;            // The first word created
    private WordsList restOfWords;      // The rest of the words
    private boolean empty = false;     // true if nothing in list

    // Create an empty list
    public WordsList( ){
        empty = true;
    }

    // Create a larger list from a new word and an existing list
    public WordsList( String newSite, WordsList existingList ) {
        firstWord = newSite;
        restOfWords = existingList;
    }

    // Produces a single String containing all the entries in the list separated by new lines
    public String toString() {
        if ( empty ) {
            return "";
        } else {
            return firstWord + "\n" + restOfWords.toString();
        }
    }

    // determines whether the collection contains a given entry
    public boolean contains( String site ) {
        if ( empty ) 
            return false;
        else if ( firstWord.equals( site ) )
            return true;
        else 
            return restOfWords.contains( site );
    }

}