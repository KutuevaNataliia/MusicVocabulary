import javax.swing.*;
import java.awt.*;
import java.util.*;

public class MainFrame extends JFrame {

    JLabel songsAmount;
    JLabel wordsAmount;
    JButton update;
    JLabel rareWordsListLabel;
    JLabel favWordsListLabel;
    JList<String> rareWordsList;
    DefaultListModel<String> modelAllRareWords;
    JList<String> favWordsList;
    SortedListModel modelFav;
    JButton addWord;
    JButton deleteWord;
    JButton showSongs;
    JButton editWordInformation;
    JButton play;

    public MainFrame() {

        GridBagLayout gridbag = new GridBagLayout();
        getContentPane().setLayout(gridbag);

        songsAmount = new JLabel("Songs " + General.songsNumber, SwingConstants.RIGHT);
        GridBagConstraints constraintSongs = new GridBagConstraints();
        constraintSongs.fill = GridBagConstraints.EAST;
        constraintSongs.anchor = GridBagConstraints.NORTHWEST;
        //constraint1.insets = new Insets()
        constraintSongs.weightx = 1;
        constraintSongs.weighty = 1;
        constraintSongs.gridx = 0;
        constraintSongs.gridy = 0;

        getContentPane().add(songsAmount, constraintSongs);

        wordsAmount = new JLabel("Words " + General.wordsNumber);
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
        update.addActionListener(new General.UpdateActionListener());

        rareWordsListLabel = new JLabel("Rare words:");
        GridBagConstraints constraintRareWordsLabel = new GridBagConstraints();
        constraintRareWordsLabel.fill = GridBagConstraints.HORIZONTAL;
        constraintRareWordsLabel.anchor = GridBagConstraints.NORTH;
        constraintRareWordsLabel.gridx = 0;
        constraintRareWordsLabel.gridy = 3;
        constraintRareWordsLabel.weightx = 1;
        constraintRareWordsLabel.weighty = 1;
        getContentPane().add(rareWordsListLabel, constraintRareWordsLabel);

        favWordsListLabel = new JLabel("Favourite words:");
        GridBagConstraints constraintSelectedWordsLabel = new GridBagConstraints();
        constraintSelectedWordsLabel.fill = GridBagConstraints.HORIZONTAL;
        constraintSelectedWordsLabel.anchor = GridBagConstraints.NORTH;
        constraintSelectedWordsLabel.gridx = 1;
        constraintSelectedWordsLabel.gridy = 3;
        constraintSelectedWordsLabel.weightx = 1;
        constraintSelectedWordsLabel.weighty = 1;
        getContentPane().add(favWordsListLabel, constraintSelectedWordsLabel);

        modelAllRareWords = new DefaultListModel<>();
        General.fillDefaultModel(modelAllRareWords);
        rareWordsList = new JList<>(modelAllRareWords);
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

        modelFav = new SortedListModel();
        modelFav.addAll(General.favouriteWords);
        favWordsList = new JList<>();
        favWordsList.setModel(modelFav);
        favWordsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane selectedWordsPane = new JScrollPane(favWordsList);
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
        addWord.addActionListener(new General.AddWordListener());

        deleteWord = new JButton("Delete word from favourites");
        GridBagConstraints constraintDeleteWord = new GridBagConstraints();
        constraintDeleteWord.fill = GridBagConstraints.HORIZONTAL;
        constraintDeleteWord.anchor = GridBagConstraints.NORTH;
        constraintDeleteWord.weightx = 1;
        constraintDeleteWord.weighty = 1;
        constraintDeleteWord.gridx = 1;
        constraintDeleteWord.gridy = 18;
        getContentPane().add(deleteWord, constraintDeleteWord);
        deleteWord.addActionListener(new General.DeleteWordListener());

        showSongs = new JButton("Show songs containing the word");
        GridBagConstraints constraintShowSongs = new GridBagConstraints();
        constraintShowSongs.fill = GridBagConstraints.HORIZONTAL;
        constraintShowSongs.anchor = GridBagConstraints.NORTH;
        constraintShowSongs.weightx = 1;
        constraintShowSongs.weighty = 1;
        constraintShowSongs.gridx = 1;
        constraintShowSongs.gridy = 19;
        getContentPane().add(showSongs, constraintShowSongs);
        showSongs.addActionListener(new General.ShowSongsListener());

        editWordInformation = new JButton("Edit word information");
        GridBagConstraints constraintsWordInformation = new GridBagConstraints();
        constraintsWordInformation.fill = GridBagConstraints.HORIZONTAL;
        constraintsWordInformation.anchor = GridBagConstraints.NORTH;
        constraintsWordInformation.weightx = 1;
        constraintsWordInformation.weighty = 1;
        constraintsWordInformation.gridx = 0;
        constraintsWordInformation.gridy = 19;
        getContentPane().add(editWordInformation, constraintsWordInformation);
        editWordInformation.addActionListener(new General.EditWordInformationListener());

        play = new JButton("Play");
        GridBagConstraints constraintsPlay = new GridBagConstraints();
        constraintsPlay.fill = GridBagConstraints.HORIZONTAL;
        constraintsPlay.anchor = GridBagConstraints.NORTH;
        constraintsPlay.weightx = 1;
        constraintsPlay.weighty = 1;
        constraintsPlay.gridx = 0;
        constraintsPlay.gridy = 20;
        constraintsPlay.gridwidth = 2;
        getContentPane().add(play, constraintsPlay);
        play.addActionListener(new General.PlayActionListener());

        setSize(640, 480);
    }

    static class SortedListModel extends AbstractListModel<String> {
        SortedSet<String> model;

        public SortedListModel() {
            model = new TreeSet<>();
        }

        public int getSize() {
            return model.size();
        }

        public String getElementAt(int index) {
            return (String) model.toArray()[index];
        }

        public void addElement(String element) {
            if (model.add(element)) {
                fireContentsChanged(this, 0, getSize());
            }
        }

        public void addAll(LinkedList<String> elements) {
            model.addAll(elements);
            fireContentsChanged(this, 0, getSize());
        }

        public void clear() {
            model.clear();
            fireContentsChanged(this, 0, getSize());
        }

        public boolean contains(String element) {
            return model.contains(element);
        }

        public String firstElement() {
            return model.first();
        }

        public String lastElement() {
            return model.last();
        }

        public void removeElement(String element) {
            boolean removed = model.remove(element);
            if (removed) {
                fireContentsChanged(this, 0, getSize());
            }
        }
    }

}