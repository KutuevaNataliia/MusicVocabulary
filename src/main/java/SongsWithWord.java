import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SongsWithWord extends JFrame {

    JLabel[] artists;
    GridBagConstraints[] constraintsArtists;
    JLabel[] names;
    GridBagConstraints[] constraintsNames;
    JTextArea[] texts;
    JScrollPane[] scrollPanes;
    GridBagConstraints[] constraintsTexts;
    private final int songsAmount;

    public SongsWithWord(ArrayList<SongInformation> songInfs, int songsAmount) {

        GridBagLayout gridbag = new GridBagLayout();
        getContentPane().setLayout(gridbag);

        this.songsAmount = songsAmount;

        SongInformation[] songs = new SongInformation[songsAmount];
        songs = songInfs.toArray(songs);

        artists = new JLabel[songsAmount];
        constraintsArtists = new GridBagConstraints[songsAmount];
        names = new JLabel[songsAmount];
        constraintsNames = new GridBagConstraints[songsAmount];
        texts = new JTextArea[songsAmount];
        scrollPanes = new JScrollPane[songsAmount];
        constraintsTexts = new GridBagConstraints[songsAmount];

        for (int i = 0; i < songsAmount; i++) {
            StringBuilder stringBuilderArtists = new StringBuilder();
            for (int j = 0; j < songs[i].artistsNumber; j++) {
                stringBuilderArtists.append(" ");
                stringBuilderArtists.append(songs[i].artists[j]);
            }
            artists[i] = new JLabel();
            artists[i].setText("Artists:" + stringBuilderArtists);
            constraintsArtists[i] = new GridBagConstraints();
            constraintsArtists[i].fill = GridBagConstraints.HORIZONTAL;
            constraintsArtists[i].anchor = GridBagConstraints.NORTHWEST;
            constraintsArtists[i].weightx = 1;
            constraintsArtists[i].weighty = 1;
            constraintsArtists[i].gridx = i;
            constraintsArtists[i].gridy = 0;
            getContentPane().add(artists[i], constraintsArtists[i]);

            names[i] = new JLabel();
            names[i].setText("Name: " + songs[i].name);
            constraintsNames[i] = new GridBagConstraints();
            constraintsNames[i].fill = GridBagConstraints.HORIZONTAL;
            constraintsNames[i].anchor = GridBagConstraints.NORTHWEST;
            constraintsNames[i].weightx = 1;
            constraintsNames[i].weighty = 1;
            constraintsNames[i].gridx = i;
            constraintsNames[i].gridy = 1;
            getContentPane().add(names[i], constraintsNames[i]);

            texts[i] = new JTextArea();
            texts[i].setText(songs[i].text);
            texts[i].setRows(20);
            texts[i].setEditable(false);
            scrollPanes[i] = new JScrollPane(texts[i]);
            //scrollPanes[i].add(texts[i]);
            constraintsTexts[i] = new GridBagConstraints();
            constraintsTexts[i].fill = GridBagConstraints.HORIZONTAL;
            constraintsTexts[i].anchor = GridBagConstraints.NORTHWEST;
            constraintsTexts[i].weightx = 1;
            constraintsTexts[i].weighty = 1;
            constraintsTexts[i].gridx = i;
            constraintsTexts[i].gridy = 2;
            constraintsTexts[i].gridheight = 20;
            getContentPane().add(scrollPanes[i], constraintsTexts[i]);

            setSize(420 * songsAmount, 600);
        }
    }
}
