import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Start extends JFrame {

    public VocabularyManipulation manipulation;

    JLabel songsAmount;
    JLabel wordsAmount;
    JButton update;
    JLabel rareWordsListLabel;
    JLabel selectedWordsListLabel;
    JList<String> rareWordsList;
    JList<String> selectedWordsList;
    JButton addWord;
    JButton deleteWord;
    JButton showSongs;

    public Start() {
        manipulation = new VocabularyManipulation();
        GridBagLayout gridbag = new GridBagLayout();
        getContentPane().setLayout(gridbag);

        songsAmount = new JLabel("Songs ", SwingConstants.RIGHT);
        GridBagConstraints constraintSongs = new GridBagConstraints();
        constraintSongs.fill = GridBagConstraints.EAST;
        constraintSongs.anchor = GridBagConstraints.NORTHWEST;
        //constraint1.insets = new Insets()
        constraintSongs.weightx = 1;
        constraintSongs.weighty = 1;
        constraintSongs.gridx = 0;
        constraintSongs.gridy = 0;

        getContentPane().add(songsAmount, constraintSongs);

        wordsAmount = new JLabel("Words ");
        GridBagConstraints constraintWords = new GridBagConstraints();
        constraintWords.fill = GridBagConstraints.EAST;
        constraintWords.anchor = GridBagConstraints.NORTHWEST;
        constraintWords.gridx = 1;
        constraintWords.gridy = 0;
        constraintWords.weightx = 1;
        constraintWords.weighty = 1;
        getContentPane().add(wordsAmount, constraintWords);

        update = new JButton("Update");
        GridBagConstraints constraintUpdate = new GridBagConstraints();
        constraintUpdate.anchor = GridBagConstraints.NORTH;
        constraintUpdate.fill = GridBagConstraints.HORIZONTAL;
        constraintUpdate.weightx = 1;
        constraintUpdate.weighty = 1;
        constraintUpdate.gridx = 0;
        constraintUpdate.gridy = 1;
        constraintUpdate.gridwidth = 2;
        getContentPane().add(update, constraintUpdate);
        update.addActionListener(new UpdateActionListener());

        /*JTextField URLField = new JTextField(" ");
        GridBagConstraints constraintURL = new GridBagConstraints();
        constraintURL.fill = GridBagConstraints.HORIZONTAL;
        constraintURL.anchor = GridBagConstraints.NORTH;
        constraintURL.gridx = 0;
        constraintURL.gridy = 2;
        constraintURL.weightx = 1;
        constraintURL.weighty = 1;
        constraintURL.gridwidth = 2;
        getContentPane().add(URLField, constraintURL);*/

        rareWordsListLabel = new JLabel("Rare words:");
        GridBagConstraints constraintRareWordsLabel = new GridBagConstraints();
        constraintRareWordsLabel.fill = GridBagConstraints.HORIZONTAL;
        constraintRareWordsLabel.anchor = GridBagConstraints.NORTH;
        constraintRareWordsLabel.gridx = 0;
        constraintRareWordsLabel.gridy = 3;
        constraintRareWordsLabel.weightx = 1;
        constraintRareWordsLabel.weighty = 1;
        getContentPane().add(rareWordsListLabel, constraintRareWordsLabel);

        selectedWordsListLabel = new JLabel("Selected words:");
        GridBagConstraints constraintSelectedWordsLabel = new GridBagConstraints();
        constraintSelectedWordsLabel.fill = GridBagConstraints.HORIZONTAL;
        constraintSelectedWordsLabel.anchor = GridBagConstraints.NORTH;
        constraintSelectedWordsLabel.gridx = 1;
        constraintSelectedWordsLabel.gridy = 3;
        constraintSelectedWordsLabel.weightx = 1;
        constraintSelectedWordsLabel.weighty = 1;
        getContentPane().add(selectedWordsListLabel, constraintSelectedWordsLabel);


        rareWordsList = new JList<>(manipulation.rareWords);
        rareWordsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane rareWordsPane = new JScrollPane(rareWordsList);
        GridBagConstraints constraintAllWordsPane = new GridBagConstraints();
        constraintAllWordsPane.fill = GridBagConstraints.HORIZONTAL;
        constraintAllWordsPane.anchor = GridBagConstraints.NORTH;
        constraintAllWordsPane.weightx = 1;
        constraintAllWordsPane.weighty = 1;
        constraintAllWordsPane.gridx = 0;
        constraintAllWordsPane.gridy = 4;
        constraintAllWordsPane.gridheight = 13;
        getContentPane().add(rareWordsPane, constraintAllWordsPane);

        String[] selectedWords = {"five", "ten", "twelve"};
        selectedWordsList =  new JList<>(selectedWords);
        selectedWordsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane selectedWordsPane = new JScrollPane(selectedWordsList);
        GridBagConstraints constraintSelectedWords = new GridBagConstraints();
        constraintSelectedWords.fill = GridBagConstraints.HORIZONTAL;
        constraintSelectedWords.anchor = GridBagConstraints.NORTH;
        constraintSelectedWords.weightx = 1;
        constraintSelectedWords.weighty = 1;
        constraintSelectedWords.gridx = 1;
        constraintSelectedWords.gridy = 4;
        constraintSelectedWords.gridheight = 13;
        getContentPane().add(selectedWordsPane, constraintSelectedWords);

        addWord = new JButton("Add word to favourites");
        GridBagConstraints constraintAddWord = new GridBagConstraints();
        constraintAddWord.fill = GridBagConstraints.HORIZONTAL;
        constraintAddWord.anchor = GridBagConstraints.NORTH;
        constraintAddWord.weightx = 1;
        constraintAddWord.weighty = 1;
        constraintAddWord.gridx = 0;
        constraintAddWord.gridy = 18;
        getContentPane().add(addWord, constraintAddWord);

        deleteWord = new JButton("Delete word from favourites");
        GridBagConstraints constraintDeleteWord = new GridBagConstraints();
        constraintDeleteWord.fill = GridBagConstraints.HORIZONTAL;
        constraintDeleteWord.anchor = GridBagConstraints.NORTH;
        constraintDeleteWord.weightx = 1;
        constraintDeleteWord.weighty = 1;
        constraintDeleteWord.gridx = 1;
        constraintDeleteWord.gridy = 18;
        getContentPane().add(deleteWord, constraintDeleteWord);

        showSongs = new JButton("Show songs containing the word");
        GridBagConstraints constraintShowSongs = new GridBagConstraints();
        constraintShowSongs.fill = GridBagConstraints.HORIZONTAL;
        constraintShowSongs.anchor = GridBagConstraints.NORTH;
        constraintShowSongs.weightx = 1;
        constraintShowSongs.weighty = 1;
        constraintShowSongs.gridx = 0;
        constraintShowSongs.gridy = 19;
        constraintShowSongs.gridwidth = 2;
        getContentPane().add(showSongs, constraintShowSongs);


        setSize(640, 480);
    }

    private class UpdateActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            GetUsersSavedTracks getTracks = new GetUsersSavedTracks();
            getTracks.updateSongs();
            manipulation.getRareWords();
            rareWordsList.updateUI();
        }
    }

    public static void main(String[] args)
    {
        Start frameMain = new Start();
        frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameMain.setVisible(true);
    }
}
