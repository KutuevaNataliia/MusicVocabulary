import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

public class General {
    public static int songsNumber;
    public static int wordsNumber;
    public static Vector<String> rareWords;
    public static Vector<WordInformation> wordsWithInformation;
    public static Vector<String> songIDs;
    public static LinkedList<String> favouriteWords;

    private static MainFrame frameMain;

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
            if (songsNumber < 10) {
                int difference = 10 - songsNumber;
                String message = "Add at least " + difference + " songs to start a game";
                JOptionPane.showMessageDialog(null, message);
            } else if (wordsWithInformation.size() < 10){
                 int difference = 10 - wordsWithInformation.size();
                 String message = "Add at least " + difference + " words with information to start a game";
                 JOptionPane.showMessageDialog(null, message);
            } else {
                game = new Game();
            }
        }
    }

    static class AddWordListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!frameMain.rareWordsList.isSelectionEmpty()) {
                String word = frameMain.rareWordsList.getSelectedValue();
                frameMain.modelFav.addElement(word);
                favouriteWords.add(word);
                dbConnection.open();
                dbConnection.addWordToFavourites(word);
                dbConnection.close();
            } else {
                JOptionPane.showMessageDialog(null, "No word chosen");
            }
        }
    }

    static class DeleteWordListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!frameMain.favWordsList.isSelectionEmpty()) {
                String word = frameMain.favWordsList.getSelectedValue();
                frameMain.modelFav.removeElement(word);
                favouriteWords.remove(word);
                dbConnection.open();
                dbConnection.deleteWordFromFavourites(word);
                dbConnection.close();
            } else {
                JOptionPane.showMessageDialog(null, "No word chosen");
            }
        }
    }

    static class ShowSongsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!frameMain.favWordsList.isSelectionEmpty()) {
                String word = frameMain.favWordsList.getSelectedValue();
                dbConnection.open();
                ArrayList<SongInformation> songs = dbConnection.getSongInformationsWithWord(word);
                dbConnection.close();
                SongsWithWordFrame frameSong = new SongsWithWordFrame(songs, songs.size());
                frameSong.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Select a word in \"Favourite words\"");
            }
        }
    }

    static class EditWordInformationListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!frameMain.rareWordsList.isSelectionEmpty()) {
                String word = frameMain.rareWordsList.getSelectedValue();
                WordInformationFrame wordForm = new WordInformationFrame(word);
                wordForm.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Select a word in \"Rare words\"");
            }
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
        wordsNumber = dbConnection.getAllWordsAmount();
        rareWords = dbConnection.getAllRareWords();
        favouriteWords = dbConnection.getFavourites();
        wordsWithInformation = dbConnection.getAllWordInformation();
        songIDs = dbConnection.getAllSongIDs();
        songsNumber = songIDs.size();
        dbConnection.close();
        frameMain = new MainFrame();
        frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameMain.setVisible(true);
    }

}
