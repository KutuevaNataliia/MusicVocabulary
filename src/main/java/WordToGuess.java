import java.util.*;

public class WordToGuess extends WordInformation implements Guessable{
    SongTitle[] songs;

    //Переделать конструктор и соответствующие методы !!!
    public WordToGuess(WordInformation wordInf, SongTitle[] songs) {
        this.mainForm = wordInf.mainForm;
        this.translation = wordInf.translation;
        this.additionalForms = wordInf.additionalForms;
        this.additionalFormsNumber = wordInf.additionalFormsNumber;
        this.songs = songs;
    }
    //Угадать слово по переводу и песням, в которых оно есть
    @Override
    public WordByItself guess() {
        WordByItself completeTask = new WordByItself();
        completeTask.taskExplanation = "Guess the word by translation and songs containing this word";
        completeTask.answerExplanation = "Enter the main form of the word or as it is in a song";
        completeTask.translation = translation;
        completeTask.rightAnswers = new String[additionalFormsNumber + 1];
        completeTask.rightAnswers[0] = mainForm;
        for (int i = 0; i <additionalFormsNumber; i++) {
            completeTask.rightAnswers[i + 1] = additionalForms[i];
        }
        completeTask.songs = songs;
        return completeTask;
    }

    //Угадать, в каких песнях встречается слово
    @Override
    public WordByOptions guess(Vector<String> options, Random random) {
        int rightSongsNumber;
        if (songs.length < 5) {
            rightSongsNumber = songs.length;
        } else {
            rightSongsNumber = random.nextInt(3) + 3;
            List<SongTitle> listToShuffle = Arrays.asList(songs);
            Collections.shuffle(listToShuffle);
            listToShuffle.toArray(songs);
        }
        int wrongSongsNumber = random.nextInt(3) + 2;
        SongTitle[] songsToChoose = new SongTitle[rightSongsNumber + wrongSongsNumber];

        SongTitle[] rightAnswers = new SongTitle[rightSongsNumber];
        for (int j = 0; j < rightSongsNumber; j++) {
            songsToChoose[j] = songs[j];
            rightAnswers[j] = songs[j];
        }

        DbConnection dbConnection = new DbConnection();
        dbConnection.open();
        for (int j = rightSongsNumber; j < songsToChoose.length; j++) {
            int wrongSongIndex;
            boolean coincides;
            String wrongID;
            do {
                coincides = false;
                wrongSongIndex = random.nextInt(options.size());
                wrongID = options.get(wrongSongIndex);
                if (Arrays.asList(songs).contains(wrongID)) {
                    continue;
                }
                for (int k = rightSongsNumber; k < j; k++) {
                    if (wrongID.equals(songsToChoose[k].spotifyId)) {
                        coincides = true;
                        break;
                    }
                }
            } while(coincides);

            SongTitle wrongSongTitle = dbConnection.getSongTitleByID(wrongID);
            songsToChoose[j] = wrongSongTitle;
        }
        dbConnection.close();
        List<SongTitle> listToShuffle = Arrays.asList(songsToChoose);
        Collections.shuffle(listToShuffle);
        listToShuffle.toArray(songsToChoose);

        WordByOptions completeTask = new WordByOptions();
        completeTask.taskExplanation = "Guess what songs contain the word";
        completeTask.answerExplanation = "Select all the appropriate songs";
        completeTask.mainForm = mainForm;
        completeTask.songs = songsToChoose;
        completeTask.rightAnswers = rightAnswers;
        return completeTask;
    }
}
