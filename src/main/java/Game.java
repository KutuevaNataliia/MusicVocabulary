import javax.swing.*;
import java.util.*;

public class Game {
    private LinkedList<Guessable> tasks = new LinkedList<>();
    Vector<String> songIDs;
    Vector<String> words;
    Vector<WordInformation> wordsWithInformation;

    private Random random;

    private int points = 0;
    private int tries = 0;

    public Game() {
        words = General.rareWords;
        songIDs = General.songIDs;
        wordsWithInformation = General.wordsWithInformation;
        random = new Random(System.currentTimeMillis());
        DbConnection dbConnection = new DbConnection();
        dbConnection.open();
        for (int i = 0; i < 10; i++) {
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
        dbConnection.close();
    }

    public void play() {
        for (int i = 0; i < 10; i++) {
            Guessable task = tasks.pollFirst();
            boolean guessType = random.nextBoolean();
            boolean right = true;
            if (guessType) {
                right = task.guess();
            } else {
                if (task instanceof SongToGuess) {
                    right = guessSongByArray((SongToGuess) task);
                } else if (task instanceof WordToGuess){
                    right = guessWordByArray((WordToGuess) task);
                }
            }
            if (right) {
                points++;
                System.out.println("Right!\n");
            } else {
                System.out.println("Wrong!\n");
            }
            tries++;
            System.out.println(points + " points out of " + tries);
        }
    }


    private SongToGuess makeSongToGuess(int index, DbConnection dbConnection) {
        String songID = songIDs.elementAt(index);
        SongInformation songInf = dbConnection.getSongInformationByID(songID);
        String[] wordsInSong = dbConnection.getWordsInSong(songID);
        return new SongToGuess(songInf, wordsInSong);
    }

    private WordToGuess makeWordToGuess(WordInformation wordInf, DbConnection dbConnection) {
        ArrayList<SongInformation> songsWithWord = dbConnection.getSongsWithWord(wordInf.mainForm);
        for(String form: wordInf.additionalForms) {
            ArrayList<SongInformation> additional = dbConnection.getSongsWithWord(form);
            songsWithWord = joinArrayLists(songsWithWord, additional);
        }
        SongInformation[] songs = new SongInformation[songsWithWord.size()];
        songs = songsWithWord.toArray(songs);
        return new WordToGuess(wordInf, songs);
    }

    private ArrayList<SongInformation> joinArrayLists(
            List<SongInformation> listA, List<SongInformation> listB) {
        boolean aEmpty = (listA == null) || listA.isEmpty();
        boolean bEmpty = (listB == null) || listB.isEmpty();
        if (aEmpty && bEmpty) {
            return new ArrayList<>();
        } else if (aEmpty) {
            return new ArrayList<>(listB);
        } else if (bEmpty) {
            return new ArrayList<>(listA);
        } else {
            ArrayList<SongInformation> result = new ArrayList<>(listA.size() + listB.size());
            result.addAll(listA);
            result.addAll(listB);
            return  result;
        }
    }

    private void shuffleArrayOfStrings(String[] arr) {
        for (int i = arr.length - 1; i >= 1; i--) {
            int j = random.nextInt(i + 1);
            String temp = arr[j];
            arr[j] = arr[i];
            arr[i] = temp;
        }
    }

    private boolean guessSongByArray(SongToGuess songToGuess) {
        int wrongWordsNumber = random.nextInt(3) + 2;
        String[] wordsToChoose;
        int rightWordsNumber;
        String[] wordsInSong = songToGuess.words;
        if (wordsInSong.length < 5) {
            rightWordsNumber = wordsInSong.length;
        } else {
            rightWordsNumber = random.nextInt(3) + 3;
        }
        int wordsToChooseLength = wrongWordsNumber + rightWordsNumber;
        wordsToChoose = new String[wordsToChooseLength];
        for(int j = 0; j < rightWordsNumber; j++) {
            wordsToChoose[j] = wordsInSong[j]; //must be shuffled!!!
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
        shuffleArrayOfStrings(wordsToChoose);
        return songToGuess.guess(wordsToChoose);
    }

    private boolean guessWordByArray(WordToGuess wordToGuess) {
        ArrayList<SongInformation> songsWithWords = new ArrayList<>();
        Collections.addAll(songsWithWords, wordToGuess.songs);
        int rightSongsNumber;
        if (songsWithWords.size() < 5) {
            rightSongsNumber = songsWithWords.size();
        } else {
            rightSongsNumber = random.nextInt(3) + 3;
        }
        int wrongSongsNumber = random.nextInt(3) + 2;
        ArrayList<SongInformation> songsToChoose = new ArrayList<>();
        for (int j = 0; j < rightSongsNumber; j++) {
            songsToChoose.add(songsWithWords.get(j));
        }
        DbConnection dbConnection = new DbConnection();
        dbConnection.open();
        for (int j = 0; j < wrongSongsNumber; j++) {
            int wrongSongIndex;
            boolean coincides;
            do {
                coincides = false;
                wrongSongIndex = random.nextInt(songIDs.size());
                for(SongInformation songInf: songsToChoose) {
                    if (songInf.spotifyId.equals(songIDs.elementAt(wrongSongIndex))) {
                        coincides = true;
                        break;
                    }
                }
            } while(coincides);

            SongInformation wrongSongInf = dbConnection.getSongInformationByID(songIDs.elementAt(wrongSongIndex));
            songsToChoose.add(wrongSongInf);
        }
        dbConnection.close();
        Collections.shuffle(songsToChoose);
        return wordToGuess.guess(songsToChoose.toArray());
    }
}
