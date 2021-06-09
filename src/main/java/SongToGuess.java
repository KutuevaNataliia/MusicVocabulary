import java.util.Arrays;
import java.util.Scanner;

public class SongToGuess extends SongInformation implements Guessable{
    String[] words;

    public SongToGuess(SongInformation songInf, String[] rareWords) {
        this.spotifyId = songInf.spotifyId;
        this.name = songInf.name;
        this.text = songInf.text;
        this.artists = songInf.artists;
        this.artistsNumber = songInf.artistsNumber;
        words = rareWords;
        Arrays.sort(words);
    }

    //Угадать песню по редким словам
    @Override
    public boolean guess() {
        System.out.println("Guess song by rare words\nRare words:");
        for (String word: words) {
            System.out.println(" " + word);
        }
        System.out.println("Print the song's name and at least one of the artists' names\n");
        Scanner scanner = new Scanner(System.in);
        String inputStr = scanner.nextLine();
        if (!inputStr.contains(name)) {
            return false;
        } else {
            for(String artist: artists) {
                if (inputStr.contains(artist)) {
                    return true;
                }
            }
        }
        return false;
    }

    //Угадать какие из редких слов есть в песне
    @Override
    public boolean guess(Object[] options) {
        System.out.println("Choose rare words belonging to the song by ");
        for(int i = 0; i < artistsNumber; i++) {
            System.out.print(artists[i] + " ");
        }
        System.out.println("called " + name);
        for (Object object: options) {
            if (object instanceof String) {
                System.out.println(object + " ");
            }
        }
        System.out.println("Print all the appropriate words");
        Scanner scanner = new Scanner(System.in);
        String inputStr = scanner.nextLine();
        String[] inputWords = inputStr.split(" ");
        if (inputWords.length != words.length) {
            return false;
        } else {
            Arrays.sort(inputWords);
            for (int i = 0; i < words.length; i++) {
                if (words[i].equals(inputWords[i])) {
                    return false;
                }
            }
        }
        return true;
    }
}
