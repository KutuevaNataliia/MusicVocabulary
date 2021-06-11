import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class Game {
    private LinkedList<Guessable> tasks = new LinkedList<>();
    Vector<String> songIDs;
    Vector<String> words;
    Vector<WordInformation> wordsWithInformation;

    private final Random random;

    GameFrame gameFrame;

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
        words = General.rareWords;
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
            tasks.addLast(songToGuess);
        } else {
            int randomIndex = random.nextInt(wordsWithInformation.size());
            WordToGuess wordToGuess = makeWordToGuess(wordsWithInformation.get(randomIndex), dbConnection);
            tasks.addLast(wordToGuess);
        }
    }

    public void play() {
        Task completeTask = null;
        do {
            Guessable task = tasks.pollFirst();
            System.out.println("task got");
            if (task == null) {
                System.out.println("Null task!!!");
            }
            boolean guessType = random.nextBoolean();
            if (guessType) {
                System.out.println("no options required");
                if (task != null) {
                    completeTask = task.guess();
                }
                System.out.println("complete without options");
            } else {
                System.out.println("options required");
                if (task instanceof SongToGuess) {
                    System.out.println("song options required");
                    completeTask = makeSongOptionsTask((SongToGuess) task);
                    System.out.println("complete song options");
                } else if (task instanceof WordToGuess) {
                    System.out.println("word options required");
                    completeTask = makeWordOptionsTask((WordToGuess) task);
                    System.out.println("complete word options");
                }
            }
            if (completeTask == null) {
                System.out.println("Task must be changed");
                DbConnection dbConnection = new DbConnection();
                dbConnection.open();
                generateTask(dbConnection);
                dbConnection.close();
            }
        }while (completeTask == null);
        myTaskListener.OnMyEvent(completeTask, tries + 1);
        System.out.println("Task ok" + tries);
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
        public void OnAnswer(boolean right) {
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

    private <T> void shuffleArray(T[] arr) {
        for (int i = arr.length - 1; i >= 1; i--) {
            int j = random.nextInt(i + 1);
            T temp = arr[j];
            arr[j] = arr[i];
            arr[i] = temp;
        }
    }

    private SongByOptions makeSongOptionsTask(SongToGuess songToGuess) {
        int wrongWordsNumber = random.nextInt(3) + 2;
        int rightWordsNumber;
        String[] wordsInSong = songToGuess.words;
        if (wordsInSong.length < 5) {
            rightWordsNumber = wordsInSong.length;
        } else {
            rightWordsNumber = random.nextInt(3) + 3;
        }
        int wordsToChooseLength = wrongWordsNumber + rightWordsNumber;
        String[] wordsToChoose = new String[wordsToChooseLength];
        String[] rightAnswers = new String[rightWordsNumber];
        for(int j = 0; j < rightWordsNumber; j++) {
            wordsToChoose[j] = wordsInSong[j];
            rightAnswers[j] = wordsInSong[j];
        }
        for (int j = rightWordsNumber; j < wordsToChooseLength; j++) {
            boolean coincides;
            int wrongWordIndex;
            do {
                wrongWordIndex  = random.nextInt(words.size());
                coincides = false;
                for (int k = 0; k < j; k++) {
                    if (words.get(wrongWordIndex).equals(wordsToChoose[k])) {
                        coincides = true;
                        break;
                    }
                }
            } while (coincides);
            wordsToChoose[j] = words.get(wrongWordIndex);
        }
        shuffleArray(wordsToChoose);
        return songToGuess.guess(wordsToChoose, rightAnswers);
    }

    private WordByOptions makeWordOptionsTask(WordToGuess wordToGuess) {
        SongTitle[] songsWithWords = wordToGuess.songs;
        int rightSongsNumber;
        if (songsWithWords.length < 5) {
            rightSongsNumber = songsWithWords.length;
        } else {
            rightSongsNumber = random.nextInt(3) + 3;
        }
        int wrongSongsNumber = random.nextInt(3) + 2;
        SongTitle[] songsToChoose = new SongTitle[rightSongsNumber + wrongSongsNumber];

        SongTitle[] rightAnswers = new SongTitle[rightSongsNumber];
        for (int j = 0; j < rightSongsNumber; j++) {
            songsToChoose[j] = songsWithWords[j];
            rightAnswers[j] = songsWithWords[j];
        }

        DbConnection dbConnection = new DbConnection();
        dbConnection.open();
        for (int j = rightSongsNumber; j < songsToChoose.length; j++) {
            int wrongSongIndex;
            boolean coincides;
            do {
                coincides = false;
                wrongSongIndex = random.nextInt(songIDs.size());
                for (int k = 0; k < j; k++) {
                    if (songIDs.get(wrongSongIndex).equals(songsToChoose[k].spotifyId)) {
                        coincides = true;
                        break;
                    }
                }
            } while(coincides);

            SongTitle wrongSongTitle = dbConnection.getSongTitleByID(songIDs.elementAt(wrongSongIndex));
            songsToChoose[j] = wrongSongTitle;
        }
        dbConnection.close();
        shuffleArray(songsToChoose);
        return wordToGuess.guess(songsToChoose, rightAnswers);
    }
}
