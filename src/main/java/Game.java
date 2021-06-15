import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class Game {
    private final ArrayDeque<Guessable> tasks = new ArrayDeque<>();
    Vector<String> songIDs;
    Vector<String> rareWords;
    Vector<WordInformation> wordsWithInformation;

    private final Random random;

    private final GameFrame gameFrame;

    private int points = 0;
    private int tries = 0;

    private MyTaskListener myTaskListener;
    private MyNextStepListener myNextStepListener;

    public void setMyTaskListener(MyTaskListener myTaskListener) {
        this.myTaskListener = myTaskListener;
    }

    public void setMyNextStepListener(MyNextStepListener myNextStepListener) {
        this.myNextStepListener = myNextStepListener;
    }

    public Game() {
        rareWords = General.rareWords;
        songIDs = General.songIDs;
        wordsWithInformation = General.wordsWithInformation;
        random = new Random(System.currentTimeMillis());
        fillQueue();
        gameFrame = new GameFrame();
        GameFrame.TaskListener taskListener = gameFrame.new TaskListener();
        GameFrame.NextStepListener nextStepListener = gameFrame.new NextStepListener();
        gameFrame.setAnswerListener(new AnswerListener());
        gameFrame.next.addActionListener(new NextListener());
        gameFrame.newGame.addActionListener(new NewGameListener());
        setMyTaskListener(taskListener);
        setMyNextStepListener(nextStepListener);
        gameFrame.setVisible(true);
        play();
    }

    private void fillQueue() {
        DbConnection dbConnection = new DbConnection();
        dbConnection.open();
        for (int i = 0; i < 10; i++) {
            generateTask(dbConnection);
        }
        dbConnection.close();
    }

    private void generateTask(DbConnection dbConnection) {
        boolean taskType = random.nextBoolean();
        if (taskType) {
            int randomSong = random.nextInt(songIDs.size());
            SongToGuess songToGuess = makeSongToGuess(randomSong, dbConnection);
            tasks.add(songToGuess);
        } else {
            int randomIndex = random.nextInt(wordsWithInformation.size());
            WordToGuess wordToGuess = makeWordToGuess(wordsWithInformation.get(randomIndex), dbConnection);
            tasks.add(wordToGuess);
        }
    }

    private void play() {
        Task completeTask = null;
        do {
            Guessable task = tasks.poll();
            boolean guessType = random.nextBoolean();
            if (guessType) {
                if (task != null) {
                    completeTask = task.guess();
                }
            } else {
                if (task instanceof SongToGuess) {
                    completeTask = task.guess(rareWords, random);
                } else if (task instanceof WordToGuess) {
                    completeTask = task.guess(songIDs, random);
                }
            }
            if (completeTask == null) {
                DbConnection dbConnection = new DbConnection();
                dbConnection.open();
                generateTask(dbConnection);
                dbConnection.close();
            }
        }while (completeTask == null);
        myTaskListener.onMyEvent(completeTask, tries + 1);
    }

    class NewGameListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            points = 0;
            tries = 0;
            fillQueue();
            play();
        }
    }

    class NextListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            play();
        }
    }

    class AnswerListener implements MyAnswerListener {
        @Override
        public void onAnswer(boolean right) {
            if (right) {
                points++;
            }
            tries++;
            myNextStepListener.onInformation(points, tries < 10);
        }
    }

    private SongToGuess makeSongToGuess(int index, DbConnection dbConnection) {
        String songID = songIDs.elementAt(index);
        SongInformation songInf = dbConnection.getSongInformationByID(songID);
        String[] wordsInSong = dbConnection.getWordsInSong(songID);
        return new SongToGuess(songInf, wordsInSong);
    }

    private WordToGuess makeWordToGuess(WordInformation wordInf, DbConnection dbConnection) {
        ArrayList<SongTitle> songsWithWord = dbConnection.getSongTitlesWithWord(wordInf.mainForm);
        for(String form: wordInf.additionalForms) {
            ArrayList<SongTitle> additional = dbConnection.getSongTitlesWithWord(form);
            songsWithWord = joinArrayLists(songsWithWord, additional);
        }
        SongTitle[] songs = new SongTitle[songsWithWord.size()];
        songs = songsWithWord.toArray(songs);
        return new WordToGuess(wordInf, songs);
    }

    private <T> ArrayList<T> joinArrayLists(
            List<T> listA, List<T> listB) {
        boolean aEmpty = (listA == null) || listA.isEmpty();
        boolean bEmpty = (listB == null) || listB.isEmpty();
        if (aEmpty && bEmpty) {
            return new ArrayList<>();
        } else if (aEmpty) {
            return new ArrayList<>(listB);
        } else if (bEmpty) {
            return new ArrayList<>(listA);
        } else {
            ArrayList<T> result = new ArrayList<>(listA.size() + listB.size());
            result.addAll(listA);
            result.addAll(listB);
            return  result;
        }
    }

}
