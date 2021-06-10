import java.util.*;

public class SongToGuess extends SongInformation implements Guessable<String>{
    String[] words;

    public SongToGuess(SongInformation songInf, String[] rareWords) {
        this.spotifyId = songInf.spotifyId;
        this.name = songInf.name;
        this.text = songInf.text;
        this.artists = songInf.artists;
        this.artistsNumber = songInf.artistsNumber;
        words = rareWords;
    }

    //Угадать песню по редким словам
    @Override
    public SongByItself guess() {
        if (words.length == 0) {
            return null;
        }
        SongByItself completeTask = new SongByItself();
        completeTask.taskExplanation = "Guess song by given rare words";
        if (words.length < 6) {
            completeTask.words = words.clone();
        } else {
            List<String> listToShuffle = Arrays.asList(words);
            Collections.shuffle(listToShuffle);
            listToShuffle.toArray(words);
            completeTask.words = new String[5];
            for (int i = 0; i < 5; i++) {
                completeTask.words[i] = words[i];
            }
        }
        completeTask.answerExplanation = "Enter the song's name and one of the artists' names";
        completeTask.rightAnswer = new SongTitle();
        completeTask.rightAnswer.name = name;
        completeTask.rightAnswer.artistsNumber = artistsNumber;
        completeTask.rightAnswer.artists = artists;
        return completeTask;
    }

    //Угадать какие из редких слов есть в песне
    @Override
    public SongByOptions guess(String[] options, String[] rightAnswers) {
        if (words.length == 0) {
            return null;
        }
        SongByOptions completeTask = new SongByOptions();
        completeTask.taskExplanation = "Choose all the rare words belonging to the song";
        completeTask.song = new SongTitle();
        completeTask.song.name = name;
        completeTask.song.artistsNumber = artistsNumber;
        completeTask.song.artists = artists;
        completeTask.words = options;
        completeTask.answerExplanation = "Select all the appropriate words";
        completeTask.rightAnswers = rightAnswers;
        return completeTask;
    }
}
