import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class GameFrame extends JFrame {

    private JLabel header;
    private JLabel step;
    private JLabel points;
    private JPanel gamePanel;
    private JButton guess;

    JButton next = new JButton("Next");
    JButton newGame = null;
    private JLabel right = null;
    private JLabel wrong = null;

    private GridBagLayout gridbag;

    WordByItselfPanel wordByItselfPanel;
    WordByOptionsPanel wordByOptionsPanel;
    SongByItselfPanel songByItselfPanel;
    SongByOptionsPanel songByOptionsPanel;

    private GuessPanel currentPanel;
    private boolean rightAnswer;
    private MyAnswerListener answerListener;

    enum GuessPanel {
        WORD_ITSELF,
        WORD_OPTIONS,
        SONG_ITSELF,
        SONG_OPTIONS
    }

    public void setAnswerListener (MyAnswerListener listener) {
        answerListener = listener;
    }

    private void setPoints(int actualPoints) {
        points.setText("Points: " + actualPoints);
    }

    private void setStep(int actualStep) {
        step.setText("Step " + actualStep + " out of 10");
    }

    private void addRightLabel() {
        right = new JLabel("Right answer!");
        GridBagConstraints constraintsRight = new GridBagConstraints();
        constraintsRight.weightx = 1;
        constraintsRight.weighty = 1;
        constraintsRight.fill = GridBagConstraints.CENTER;
        constraintsRight.anchor = GridBagConstraints.NORTH;
        constraintsRight.gridx = 0;
        constraintsRight.gridy = 33;
        constraintsRight.gridwidth = 2;
        getContentPane().add(right, constraintsRight);
    }

    private void addWrongLabel() {
        wrong = new JLabel("Wrong answer!");
        GridBagConstraints constraintsWrong = new GridBagConstraints();
        constraintsWrong.weightx = 1;
        constraintsWrong.weighty = 1;
        constraintsWrong.fill = GridBagConstraints.CENTER;
        constraintsWrong.anchor = GridBagConstraints.NORTH;
        constraintsWrong.gridx = 0;
        constraintsWrong.gridy = 33;
        constraintsWrong.gridwidth = 2;
        getContentPane().add(wrong, constraintsWrong);
    }

    private void addNextButton() {
        GridBagConstraints constraintsNext = new GridBagConstraints();
        constraintsNext.weightx = 1;
        constraintsNext.weighty = 1;
        constraintsNext.fill = GridBagConstraints.CENTER;
        constraintsNext.anchor = GridBagConstraints.NORTH;
        constraintsNext.gridx = 0;
        constraintsNext.gridy = 32;
        constraintsNext.gridwidth = 2;
        getContentPane().add(next, constraintsNext);
    }

    private void addNewGameButton() {
        newGame = new JButton("New Game");
        GridBagConstraints constraintsNewGame = new GridBagConstraints();
        constraintsNewGame.weightx = 1;
        constraintsNewGame.weighty = 1;
        constraintsNewGame.fill = GridBagConstraints.CENTER;
        constraintsNewGame.anchor = GridBagConstraints.NORTH;
        constraintsNewGame.gridx = 0;
        constraintsNewGame.gridy = 32;
        constraintsNewGame.gridwidth = 2;
        getContentPane().add(newGame, constraintsNewGame);
    }

    private void addGuessButton() {
        guess = new JButton("Guess");
        GridBagConstraints constraintsGuess = new GridBagConstraints();
        constraintsGuess.weightx = 1;
        constraintsGuess.weighty = 1;
        constraintsGuess.fill = GridBagConstraints.CENTER;
        constraintsGuess.anchor = GridBagConstraints.NORTH;
        constraintsGuess.gridx = 0;
        constraintsGuess.gridy = 32;
        constraintsGuess.gridwidth = 2;
        getContentPane().add(guess, constraintsGuess);
        guess.addActionListener(new GuessListener());
    }

    public GameFrame() {
        gridbag = new GridBagLayout();
        getContentPane().setLayout(gridbag);

        header = new JLabel("Game");
        GridBagConstraints constraintsHeader = new GridBagConstraints();
        constraintsHeader.weightx = 1;
        constraintsHeader.weighty = 1;
        constraintsHeader.gridx = 0;
        constraintsHeader.gridy = 0;
        constraintsHeader.fill = GridBagConstraints.CENTER;
        constraintsHeader.anchor = GridBagConstraints.NORTH;
        constraintsHeader.gridwidth = 2;
        getContentPane().add(header, constraintsHeader);

        step = new JLabel("Step 1 out of 10");
        GridBagConstraints constraintsStep = new GridBagConstraints();
        constraintsStep.weightx = 1;
        constraintsStep.weighty = 1;
        constraintsStep.fill = GridBagConstraints.HORIZONTAL;
        constraintsStep.anchor = GridBagConstraints.NORTH;
        constraintsStep.gridx = 0;
        constraintsStep.gridy = 1;
        getContentPane().add(step, constraintsStep);

        points = new JLabel("Points: 0");
        GridBagConstraints constraintsPoints = new GridBagConstraints();
        constraintsPoints.weightx = 1;
        constraintsPoints.weighty = 1;
        constraintsPoints.fill = GridBagConstraints.HORIZONTAL;
        constraintsPoints.anchor = GridBagConstraints.NORTH;
        constraintsPoints.gridx = 1;
        constraintsPoints.gridy = 1;
        getContentPane().add(points, constraintsPoints);

        gamePanel = new JPanel();
        GridBagLayout gamePanelLayout = new GridBagLayout();
        gamePanel.setLayout(gamePanelLayout);
        GridBagConstraints constraintsGamePanel = new GridBagConstraints();
        constraintsGamePanel.weightx = 1;
        constraintsGamePanel.weighty = 1;
        constraintsGamePanel.fill = GridBagConstraints.BOTH;
        constraintsGamePanel.anchor = GridBagConstraints.NORTH;
        constraintsGamePanel.gridx = 0;
        constraintsGamePanel.gridy = 2;
        constraintsGamePanel.gridwidth = 2;
        constraintsGamePanel.gridheight = 30;
        getContentPane().add(gamePanel, constraintsGamePanel);

        setSize(640, 660);
    }

    private void fillListModel(DefaultListModel<String> listModel, SongTitle[] songTitles) {
        for (SongTitle songTitle: songTitles) {
            StringBuilder stringBuilder = new StringBuilder("Artists: ");
            for (int i = 0; i < songTitle.artistsNumber; i++) {
                stringBuilder.append(songTitle.artists[i]);
                stringBuilder.append(" ");
            }
            stringBuilder.append("Name: ");
            stringBuilder.append(songTitle.name);
            listModel.addElement(stringBuilder.toString());
        }
    }

    private class WordByItselfPanel{
        String[] rightAnswers;

        JLabel taskExplanation;
        JLabel translationLabel;
        JLabel songsEnum;
        JList<String> songsList;
        JLabel instructions;
        JLabel answerLabel;
        JTextField answerField;

        public WordByItselfPanel(WordByItself task) {
            gamePanel.removeAll();
            rightAnswers = task.rightAnswers;

            taskExplanation = new JLabel(task.taskExplanation);
            GridBagConstraints constraintsTask = new GridBagConstraints();
            constraintsTask.weightx = 1;
            constraintsTask.weighty = 1;
            constraintsTask.fill = GridBagConstraints.CENTER;
            constraintsTask.anchor = GridBagConstraints.NORTH;
            constraintsTask.gridx = 0;
            constraintsTask.gridy = 0;
            constraintsTask.gridwidth = 2;
            gamePanel.add(taskExplanation, constraintsTask);

            translationLabel = new JLabel("Translation: " + task.translation);
            GridBagConstraints constraintsTranslation = new GridBagConstraints();
            constraintsTranslation.weightx = 1;
            constraintsTranslation.weighty = 1;
            constraintsTranslation.fill = GridBagConstraints.HORIZONTAL;
            constraintsTranslation.anchor = GridBagConstraints.NORTH;
            constraintsTranslation.gridx = 0;
            constraintsTranslation.gridy = 1;
            constraintsTranslation.gridwidth = 2;
            gamePanel.add(translationLabel, constraintsTranslation);

            songsEnum = new JLabel("occurs in songs:");
            GridBagConstraints constraintsSongsEnum = new GridBagConstraints();
            constraintsSongsEnum.weightx = 1;
            constraintsSongsEnum.weighty = 1;
            constraintsSongsEnum.fill = GridBagConstraints.HORIZONTAL;
            constraintsSongsEnum.anchor = GridBagConstraints.NORTH;
            constraintsSongsEnum.gridx = 0;
            constraintsSongsEnum.gridy = 2;
            constraintsSongsEnum.gridwidth = 2;
            gamePanel.add(songsEnum, constraintsSongsEnum);

            songsList = new JList<>();
            DefaultListModel<String> listModel = new DefaultListModel<>();
            fillListModel(listModel, task.songs);
            songsList.setModel(listModel);
            GridBagConstraints constraintsList = new GridBagConstraints();
            constraintsList.weightx = 1;
            constraintsList.weighty = 1;
            constraintsList.fill = GridBagConstraints.BOTH;
            constraintsList.anchor = GridBagConstraints.NORTH;
            constraintsList.gridx = 0;
            constraintsList.gridy = 3;
            constraintsList.gridwidth = 2;
            constraintsList.gridheight = 10;
            gamePanel.add(songsList, constraintsList);

            instructions = new JLabel(task.answerExplanation);
            GridBagConstraints constraintsInstructions = new GridBagConstraints();
            constraintsList.weightx = 1;
            constraintsList.weighty = 1;
            constraintsList.fill = GridBagConstraints.HORIZONTAL;
            constraintsList.anchor = GridBagConstraints.NORTH;
            constraintsList.gridx = 0;
            constraintsList.gridy = 14;
            constraintsList.gridwidth = 2;
            gamePanel.add(instructions, constraintsInstructions);

            answerLabel = new JLabel("Your Answer");
            GridBagConstraints constraintsAnswerLabel = new GridBagConstraints();
            constraintsAnswerLabel.weightx = 1;
            constraintsAnswerLabel.weighty = 1;
            constraintsAnswerLabel.fill = GridBagConstraints.HORIZONTAL;
            constraintsAnswerLabel.anchor = GridBagConstraints.NORTH;
            constraintsAnswerLabel.gridx = 0;
            constraintsAnswerLabel.gridy = 15;
            gamePanel.add(answerLabel, constraintsAnswerLabel);

            answerField = new JTextField();
            GridBagConstraints constraintsAnswerField = new GridBagConstraints();
            constraintsAnswerField.weightx = 1;
            constraintsAnswerField.weighty = 1;
            constraintsAnswerField.fill = GridBagConstraints.HORIZONTAL;
            constraintsAnswerField.anchor = GridBagConstraints.NORTH;
            constraintsAnswerField.gridx = 1;
            constraintsAnswerField.gridy = 15;
            gamePanel.add(answerField, constraintsAnswerField);

            gamePanel.validate();
            gamePanel.repaint();
        }

        public boolean checkAnswer() {
            boolean right = false;
            String actualAnswer = answerField.getText();
            for(String rightAnswer: rightAnswers) {
                if (rightAnswer.equals(actualAnswer)) {
                    right = true;
                    break;
                }
            }
            return right;
        }

        public void showRightAnswer() {
            answerField.setText(rightAnswers[0]);
            answerField.setForeground(Color.RED);
        }
    }

    private class WordByOptionsPanel {
        SongTitle[] rightAnswers;
        SongTitle[] allSongs;

        JLabel taskExplanation;
        JLabel instructions;
        JLabel songsEnum;
        JList<String> songsList;

        public WordByOptionsPanel(WordByOptions task) {
            gamePanel.removeAll();
            rightAnswers = task.rightAnswers;
            allSongs = task.songs;

            taskExplanation = new JLabel(task.taskExplanation + " " + task.mainForm);
            GridBagConstraints constraintsTask = new GridBagConstraints();
            constraintsTask.weightx = 1;
            constraintsTask.weighty = 1;
            constraintsTask.fill = GridBagConstraints.CENTER;
            constraintsTask.anchor = GridBagConstraints.NORTH;
            constraintsTask.gridx = 0;
            constraintsTask.gridy = 0;
            gamePanel.add(taskExplanation, constraintsTask);

            instructions = new JLabel(task.answerExplanation);
            GridBagConstraints constraintsInstructions= new GridBagConstraints();
            constraintsInstructions.weightx = 1;
            constraintsInstructions.weighty = 1;
            constraintsInstructions.fill = GridBagConstraints.CENTER;
            constraintsInstructions.anchor = GridBagConstraints.NORTH;
            constraintsInstructions.gridx = 0;
            constraintsInstructions.gridy = 1;
            gamePanel.add(instructions, constraintsInstructions);

            songsEnum = new JLabel("Songs:");
            GridBagConstraints constraintsSongsEnum = new GridBagConstraints();
            constraintsSongsEnum.weightx = 1;
            constraintsSongsEnum.weighty = 1;
            constraintsSongsEnum.fill = GridBagConstraints.CENTER;
            constraintsSongsEnum.anchor = GridBagConstraints.NORTH;
            constraintsSongsEnum.gridx = 0;
            constraintsSongsEnum.gridy = 2;
            gamePanel.add(songsEnum, constraintsSongsEnum);

            songsList = new JList<>();
            DefaultListModel<String> listModel = new DefaultListModel<>();
            fillListModel(listModel, task.songs);
            songsList.setModel(listModel);
            songsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            GridBagConstraints constraintsSongsList = new GridBagConstraints();
            constraintsSongsList.weightx = 1;
            constraintsSongsList.weighty = 1;
            constraintsSongsList.fill = GridBagConstraints.BOTH;
            constraintsSongsList.anchor = GridBagConstraints.NORTH;
            constraintsSongsList.gridx = 0;
            constraintsSongsList.gridy = 3;
            constraintsSongsList.gridheight = 15;
            gamePanel.add(songsList, constraintsSongsList);

            gamePanel.validate();
            gamePanel.repaint();
        }

        public boolean checkAnswer() {
            int[] selectedIndices = songsList.getSelectedIndices();
            if (selectedIndices.length != rightAnswers.length) {
                return false;
            }
            for(int selectedIndex: selectedIndices) {
                boolean contains = Arrays.stream(rightAnswers).anyMatch(allSongs[selectedIndex]::equals);
                if(!contains) {
                    return false;
                }
            }
            return true;
        }

        public void showRightAnswer() {
            int[] selectedIndices = new int[rightAnswers.length];
            for (int k = 0; k < rightAnswers.length; k++) {
                for (int i = 0; i < allSongs.length; i++) {
                    if (rightAnswers[k].equals(allSongs[i])) {
                        selectedIndices[k] = i;
                        break;
                    }
                }
            }
            songsList.setSelectedIndices(selectedIndices);
            songsList.setSelectionForeground(Color.RED);
        }
    }

    private class SongByItselfPanel {
        SongTitle rightAnswer;

        JLabel taskExplanation;
        JLabel wordsLabel;
        JList<String> wordsList;
        JLabel instructions;
        JLabel songNameLabel;
        JTextField songNameField;
        JLabel artistLabel;
        JTextField artistField;

        public SongByItselfPanel(SongByItself task) {
            rightAnswer = task.rightAnswer;
            gamePanel.removeAll();

            taskExplanation = new JLabel(task.taskExplanation);
            GridBagConstraints constraintsTask = new GridBagConstraints();
            constraintsTask.weightx = 1;
            constraintsTask.weighty = 1;
            constraintsTask.fill = GridBagConstraints.CENTER;
            constraintsTask.anchor = GridBagConstraints.NORTH;
            constraintsTask.gridx = 0;
            constraintsTask.gridy = 0;
            constraintsTask.gridwidth = 2;
            gamePanel.add(taskExplanation, constraintsTask);

            wordsLabel = new JLabel("Words:");
            GridBagConstraints constraintsWordsLabel = new GridBagConstraints();
            constraintsWordsLabel.weightx = 1;
            constraintsWordsLabel.weighty = 1;
            constraintsWordsLabel.fill = GridBagConstraints.CENTER;
            constraintsWordsLabel.anchor = GridBagConstraints.NORTH;
            constraintsWordsLabel.gridx = 0;
            constraintsWordsLabel.gridy = 1;
            constraintsWordsLabel.gridwidth = 2;
            gamePanel.add(wordsLabel, constraintsWordsLabel);

            wordsList = new JList<>();
            DefaultListModel<String> listModel = new DefaultListModel<>();
            java.util.List<String> wordsToAdd = Arrays.asList(task.words);
            listModel.addAll(wordsToAdd);
            wordsList.setModel(listModel);
            wordsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            GridBagConstraints constraintsWordsList = new GridBagConstraints();
            constraintsWordsList.weightx = 1;
            constraintsWordsList.weighty = 1;
            constraintsWordsList.fill = GridBagConstraints.BOTH;
            constraintsWordsList.anchor = GridBagConstraints.NORTH;
            constraintsWordsList.gridx = 0;
            constraintsWordsList.gridy = 2;
            constraintsWordsList.gridheight = 10;
            constraintsWordsList.gridwidth = 2;
            gamePanel.add(wordsList, constraintsWordsList);

            instructions = new JLabel(task.answerExplanation);
            GridBagConstraints constraintsInstructions= new GridBagConstraints();
            constraintsInstructions.weightx = 1;
            constraintsInstructions.weighty = 1;
            constraintsInstructions.fill = GridBagConstraints.CENTER;
            constraintsInstructions.anchor = GridBagConstraints.NORTH;
            constraintsInstructions.gridx = 0;
            constraintsInstructions.gridy = 13;
            constraintsInstructions.gridwidth = 2;
            gamePanel.add(instructions, constraintsInstructions);

            songNameLabel = new JLabel("Song's name");
            GridBagConstraints constraintsSongLabel = new GridBagConstraints();
            constraintsSongLabel.weightx = 1;
            constraintsSongLabel.weighty = 1;
            constraintsSongLabel.fill = GridBagConstraints.HORIZONTAL;
            constraintsSongLabel.anchor = GridBagConstraints.NORTH;
            constraintsSongLabel.gridx = 0;
            constraintsSongLabel.gridy = 14;
            gamePanel.add(songNameLabel, constraintsSongLabel);

            songNameField = new JTextField();
            GridBagConstraints constraintsSongField = new GridBagConstraints();
            constraintsSongField.weightx = 1;
            constraintsSongField.weighty = 1;
            constraintsSongField.fill = GridBagConstraints.HORIZONTAL;
            constraintsSongField.anchor = GridBagConstraints.NORTH;
            constraintsSongField.gridx = 1;
            constraintsSongField.gridy = 14;
            gamePanel.add(songNameField, constraintsSongField);

            artistLabel = new JLabel("Artist");
            GridBagConstraints constraintsArtistLabel = new GridBagConstraints();
            constraintsArtistLabel.weightx = 1;
            constraintsArtistLabel.weighty = 1;
            constraintsArtistLabel.fill = GridBagConstraints.HORIZONTAL;
            constraintsArtistLabel.anchor = GridBagConstraints.NORTH;
            constraintsArtistLabel.gridx = 0;
            constraintsArtistLabel.gridy = 15;
            gamePanel.add(artistLabel, constraintsArtistLabel);

            artistField = new JTextField();
            GridBagConstraints constraintsArtistField = new GridBagConstraints();
            constraintsArtistField.weightx = 1;
            constraintsArtistField.weighty = 1;
            constraintsArtistField.fill = GridBagConstraints.HORIZONTAL;
            constraintsArtistField.anchor = GridBagConstraints.NORTH;
            constraintsArtistField.gridx = 1;
            constraintsArtistField.gridy = 15;
            gamePanel.add(artistField, constraintsArtistField);

            gamePanel.validate();
            gamePanel.repaint();
        }

        public boolean checkAnswer() {
            String name = songNameField.getText();
            String artist = artistField.getText();
            if (!name.equals(rightAnswer.name)) {
                return false;
            }
            return Arrays.stream(rightAnswer.artists).anyMatch(artist::equals);
        }

        public void showRightAnswer() {
            songNameField.setText(rightAnswer.name);
            songNameField.setForeground(Color.RED);
            if (rightAnswer.artistsNumber == 1){
                artistField.setText(rightAnswer.artists[0]);
            } else {
                artistField.setText(rightAnswer.artists[0] + " and others...");
            }
            artistField.setForeground(Color.RED);
        }
    }

    private class SongByOptionsPanel {
        String[] rightAnswers;

        JLabel taskExplanation;
        JLabel songLabel;
        JLabel wordsLabel;
        JList<String> wordsList;
        DefaultListModel<String> listModel;
        JLabel instructions;

        public SongByOptionsPanel(SongByOptions task) {
            rightAnswers = task.rightAnswers;
            gamePanel.removeAll();

            taskExplanation = new JLabel(task.taskExplanation);
            GridBagConstraints constraintsTask = new GridBagConstraints();
            constraintsTask.weightx = 1;
            constraintsTask.weighty = 1;
            constraintsTask.fill = GridBagConstraints.CENTER;
            constraintsTask.anchor = GridBagConstraints.NORTH;
            constraintsTask.gridx = 0;
            constraintsTask.gridy = 0;
            gamePanel.add(taskExplanation, constraintsTask);

            songLabel = new JLabel();
            StringBuilder stringBuilder = new StringBuilder("Song: ");
            stringBuilder.append(task.song.name);
            stringBuilder.append(" by ");
            for (int i = 0; i < task.song.artistsNumber; i++) {
                if (i > 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(task.song.artists[i]);
            }
            songLabel.setText(stringBuilder.toString());
            GridBagConstraints constraintsSong = new GridBagConstraints();
            constraintsSong.weightx = 1;
            constraintsSong.weighty = 1;
            constraintsSong.fill = GridBagConstraints.CENTER;
            constraintsSong.anchor = GridBagConstraints.NORTH;
            constraintsSong.gridx = 0;
            constraintsSong.gridy = 1;
            gamePanel.add(songLabel, constraintsSong);

            wordsLabel = new JLabel("Words:");
            GridBagConstraints constraintsWordsLabel = new GridBagConstraints();
            constraintsWordsLabel.weightx = 1;
            constraintsWordsLabel.weighty = 1;
            constraintsWordsLabel.fill = GridBagConstraints.CENTER;
            constraintsWordsLabel.anchor = GridBagConstraints.NORTH;
            constraintsWordsLabel.gridx = 0;
            constraintsWordsLabel.gridy = 2;
            gamePanel.add(wordsLabel, constraintsWordsLabel);

            wordsList = new JList<>();
            listModel = new DefaultListModel<>();
            java.util.List<String> wordsToAdd = Arrays.asList(task.words);
            listModel.addAll(wordsToAdd);
            wordsList.setModel(listModel);
            wordsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            GridBagConstraints constraintsWordsList = new GridBagConstraints();
            constraintsWordsList.weightx = 1;
            constraintsWordsList.weighty = 1;
            constraintsWordsList.fill = GridBagConstraints.BOTH;
            constraintsWordsList.anchor = GridBagConstraints.NORTH;
            constraintsWordsList.gridx = 0;
            constraintsWordsList.gridy = 3;
            constraintsWordsList.gridheight = 10;
            gamePanel.add(wordsList, constraintsWordsList);

            instructions = new JLabel(task.answerExplanation);
            GridBagConstraints constraintsInstructions= new GridBagConstraints();
            constraintsInstructions.weightx = 1;
            constraintsInstructions.weighty = 1;
            constraintsInstructions.fill = GridBagConstraints.CENTER;
            constraintsInstructions.anchor = GridBagConstraints.NORTH;
            constraintsInstructions.gridx = 0;
            constraintsInstructions.gridy = 14;
            gamePanel.add(instructions, constraintsInstructions);

            gamePanel.validate();
            gamePanel.repaint();
        }

        public boolean checkAnswer() {
            int[] selectedIndices = wordsList.getSelectedIndices();
            if (selectedIndices.length != rightAnswers.length) {
                return false;
            }
            for (int selectedIndex: selectedIndices) {
               boolean contains = Arrays.stream(rightAnswers).anyMatch(listModel.getElementAt(selectedIndex)::equals);
               if (!contains) {
                   return false;
               }
            }
            return true;
        }

        public void showRightAnswer() {
            int[] selectedIndices = new int[rightAnswers.length];
            for (int i = 0; i < rightAnswers.length; i++) {
                for (int j = 0; j < listModel.size(); j++) {
                    if(rightAnswers[i].equals(listModel.elementAt(j))) {
                        selectedIndices[i] = j;
                        break;
                    }
                }
            }
            wordsList.setSelectedIndices(selectedIndices);
            wordsList.setSelectionForeground(Color.RED);
        }
    }

    class TaskListener implements MyTaskListener {
        @Override
        public void OnMyEvent(Task completeTask, int step) {
            if (next.getParent() == getContentPane()) {
                getContentPane().remove(next);
                System.out.println("next removed");
            } else if (newGame != null && newGame.getParent() == getContentPane()) {
                getContentPane().remove(newGame);
                System.out.println("new game removed");
            }
            if (right != null && right.getParent() == getContentPane()) {
                getContentPane().remove(right);
                System.out.println("right removed");
            } else if (wrong != null && wrong.getParent() == getContentPane()) {
                getContentPane().remove(wrong);
                System.out.println("wrong removed");
            }
            setStep(step);
            addGuessButton();
            System.out.println("guess added");
            getContentPane().validate();
            getContentPane().repaint();
            if (completeTask instanceof WordByItself) {
                wordByItselfPanel = new WordByItselfPanel((WordByItself) completeTask);
                currentPanel = GuessPanel.WORD_ITSELF;
            } else if (completeTask instanceof  WordByOptions) {
                wordByOptionsPanel = new WordByOptionsPanel((WordByOptions) completeTask);
                currentPanel = GuessPanel.WORD_OPTIONS;
            } else if (completeTask instanceof SongByItself) {
                songByItselfPanel = new SongByItselfPanel((SongByItself) completeTask);
                currentPanel = GuessPanel.SONG_ITSELF;
            } else if (completeTask instanceof SongByOptions) {
                songByOptionsPanel = new SongByOptionsPanel((SongByOptions) completeTask);
                currentPanel = GuessPanel.SONG_OPTIONS;
            }
        }
    }

    private class GuessListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (currentPanel) {
                case WORD_ITSELF:
                    rightAnswer = wordByItselfPanel.checkAnswer();
                    break;
                case WORD_OPTIONS:
                    rightAnswer = wordByOptionsPanel.checkAnswer();
                    break;
                case SONG_ITSELF:
                    rightAnswer = songByItselfPanel.checkAnswer();
                    break;
                case SONG_OPTIONS:
                    rightAnswer = songByOptionsPanel.checkAnswer();
                    break;
            }
            answerListener.OnAnswer(rightAnswer);
        }
    }

    class NextStepListener implements MyNextStepListener {
        @Override
        public void onInformation(int points, boolean next) {
            setPoints(points);
            getContentPane().remove(guess);
            if (next){
                addNextButton();
            } else {
                addNewGameButton();
            }
            if (rightAnswer) {
                addRightLabel();
            } else {
                addWrongLabel();
                switch (currentPanel) {
                    case WORD_ITSELF:
                        wordByItselfPanel.showRightAnswer();
                        break;
                    case WORD_OPTIONS:
                        wordByOptionsPanel.showRightAnswer();
                        break;
                    case SONG_ITSELF:
                        songByItselfPanel.showRightAnswer();
                        break;
                    case SONG_OPTIONS:
                        songByOptionsPanel.showRightAnswer();
                        break;
                }
            }
            getContentPane().validate();
            getContentPane().repaint();
        }
    }
}
