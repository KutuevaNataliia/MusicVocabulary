import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

public class General {
    static int songsNumber;
    static int wordsNumber;
    static Vector<String> rareWords;
    static Vector<String> favouriteWords;
    static Vector<WordInformation> wordsWithInformation;
    static Vector<String> songIDs;

    private static Start frameMain;

    private static DbConnection dbConnection;
    private static Game game;

    static class UpdateActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            GetUsersSavedTracks getTracks = new GetUsersSavedTracks();
            getTracks.updateSongs();
            dbConnection.open();
            int newSongsNumber = dbConnection.getSongsAmount();
            if (newSongsNumber > songsNumber) {
                songsNumber = newSongsNumber;
                frameMain.songsAmount.setText("Songs " + songsNumber);
                wordsNumber = dbConnection.getAllWordsAmount();
                frameMain.wordsAmount.setText("Words " + wordsNumber);
                rareWords = dbConnection.getAllRareWords();
                frameMain.modelAllRareWords.clear();
                fillDefaultModel(frameMain.modelAllRareWords);
                songIDs = dbConnection.getAllSongIDs();
                JOptionPane.showMessageDialog(null, "Data updated");
            } else {
                JOptionPane.showMessageDialog(null, "Data is up-to-date");
            }
            dbConnection.close();

        }
    }

    static class PlayActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            game = new Game();
            game.play();
        }
    }

    static class AddWordListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String word = frameMain.rareWordsList.getSelectedValue();
            frameMain.modelFav.addElement(word);
            favouriteWords.add(word);
            dbConnection.open();
            dbConnection.addWordToFavourites(word);
            dbConnection.close();
        }
    }

    static class DeleteWordListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String word = frameMain.favWordsList.getSelectedValue();
            frameMain.modelFav.removeElement(word);
            favouriteWords.remove(word);
            dbConnection.open();
            dbConnection.deleteWordFromFavourites(word);
            dbConnection.close();
        }
    }

    static class ShowSongsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String word = frameMain.favWordsList.getSelectedValue();
            dbConnection.open();
            ArrayList<SongInformation> songs = dbConnection.getSongsWithWord(word);
            dbConnection.close();
            SongsWithWord frameSong = new SongsWithWord(songs, songs.size());
            frameSong.setVisible(true);
        }
    }

    static class EditWordInformationListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String word = frameMain.rareWordsList.getSelectedValue();
            WordInformationForm wordForm = new WordInformationForm(word);
            wordForm.setVisible(true);
        }
    }

    public static void fillDefaultModel(DefaultListModel<String> model) {
        for (String rareWord : rareWords) {
            model.addElement(rareWord);
        }
    }

    public static void main(String[] args) {
        dbConnection = new DbConnection();
        dbConnection.open();
        songsNumber = dbConnection.getSongsAmount();
        wordsNumber = dbConnection.getAllWordsAmount();
        rareWords = dbConnection.getAllRareWords();
        favouriteWords = dbConnection.getFavourites();
        wordsWithInformation = dbConnection.getAllWordInformation();
        songIDs = dbConnection.getAllSongIDs();
        dbConnection.close();
        frameMain = new Start();
        frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameMain.setVisible(true);
    }

}
