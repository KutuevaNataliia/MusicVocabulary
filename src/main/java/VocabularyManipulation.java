import java.util.ArrayList;

public class VocabularyManipulation {
    DbConnection dbConnection;

    public String[] rareWords;

    public VocabularyManipulation() {
        dbConnection = new DbConnection();
        getRareWords();
    }

    public void printVocabulary() {
        dbConnection.open();
        ArrayList<WordFrequencyPair> words = dbConnection.getAllVocabulary();
        for (WordFrequencyPair pair: words) {
            System.out.println(pair.word + " " + pair.frequency);
        }
        dbConnection.close();
    }

    public void getRareWords() {
        dbConnection.open();
        rareWords = dbConnection.getAllRareWords();
        dbConnection.close();
    }
}
