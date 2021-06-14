import java.util.*;

public class SongToGuess extends SongInformation implements Guessable{
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
    public SongByOptions guess(Vector<String> options, Random random) {
        if (words.length == 0) {
            return null;
        }

        int wrongWordsNumber = random.nextInt(3) + 2;
        int rightWordsNumber;
        if (words.length < 5) {
            rightWordsNumber = words.length;
        } else {
            rightWordsNumber = random.nextInt(3) + 3;
            List<String> listToShuffle = Arrays.asList(words);
            Collections.shuffle(listToShuffle);
            listToShuffle.toArray(words);
        }
        int wordsToChooseLength = wrongWordsNumber + rightWordsNumber;
        String[] wordsToChoose = new String[wordsToChooseLength];
        String[] rightAnswers = new String[rightWordsNumber];
        for(int j = 0; j < rightWordsNumber; j++) {
            wordsToChoose[j] = words[j];
            rightAnswers[j] = words[j];
        }
        for (int j = rightWordsNumber; j < wordsToChooseLength; j++) {
            boolean coincides;
            int wrongWordIndex;
            do {
                wrongWordIndex  = random.nextInt(options.size());
                coincides = false;
                for (int k = 0; k < j; k++) {
                    if (options.get(wrongWordIndex).equals(wordsToChoose[k])) {
                        coincides = true;
                        break;
                    }
                }
            } while (coincides);
            wordsToChoose[j] = options.get(wrongWordIndex);
        }
        List<String> listToShuffle = Arrays.asList(wordsToChoose);
        Collections.shuffle(listToShuffle);
        listToShuffle.toArray(wordsToChoose);

        SongByOptions completeTask = new SongByOptions();
        completeTask.taskExplanation = "Choose all the rare words belonging to the song";
        completeTask.song = new SongTitle();
        completeTask.song.name = name;
        completeTask.song.artistsNumber = artistsNumber;
        completeTask.song.artists = artists;
        completeTask.words = wordsToChoose;
        completeTask.answerExplanation = "Select all the appropriate words";
        completeTask.rightAnswers = rightAnswers;
        return completeTask;
    }
}
