import java.util.Scanner;

public class WordToGuess extends WordInformation implements Guessable{
    SongInformation[] songs;

    public WordToGuess(WordInformation wordInf, SongInformation[] songs) {
        this.mainForm = wordInf.mainForm;
        this.translation = wordInf.translation;
        this.additionalForms = wordInf.additionalForms;
        this.additionalFormsNumber = wordInf.additionalFormsNumber;
        this.songs = songs;
    }
    //Угадать слово по переводу и песням, в которых оно есть
    @Override
    public boolean guess() {
        System.out.println("Guess the word by song and translation\n");
        System.out.println("Translation: " + translation);
        System.out.println("Songs:");
        for(SongInformation songInf: songs) {
            System.out.println("\nArtists:");
            for(int i = 0; i < songInf.artistsNumber; i++) {
                System.out.print(" " + songInf.artists[i]);
            }
            System.out.println("\nName: " + songInf.name);
        }
        Scanner scanner = new Scanner(System.in);
        String inputWord = scanner.nextLine();
        if (inputWord == mainForm) {
            return true;
        } else {
            for(String additionalForm: additionalForms) {
                if (additionalForm == inputWord) {
                    return true;
                }
            }
        }
        return false;
    }

    //Угадать, в каких песнях встречается слово
    @Override
    public boolean guess(Object[] options) {
        System.out.println("Guess what songs contain the word " + mainForm);
        int counter = 1;
        for(Object option: options) {
            if (option instanceof SongInformation) {
                SongInformation songInf = (SongInformation)option;
                System.out.println("\n" + counter + " - Artists:");
                for(int i = 0; i < songInf.artistsNumber; i++) {
                    System.out.print(" " + songInf.artists[i]);
                }
                System.out.println(" Name: " + songInf.name);
                counter++;
            }
        }
        Scanner scanner = new Scanner(System.in);
        String inputStr = scanner.nextLine();
        String[] rawNumbers = inputStr.split(" ");
        int[] numbers = new int[rawNumbers.length];
        for (int i = 0; i < rawNumbers.length; i++) {
            numbers[i] = Integer.parseInt(rawNumbers[i]);
        }
        if (numbers.length != songs.length) {
            return false;
        } else {
            boolean equal = false;
            for (int songNumber: numbers) {
                for(SongInformation songInformation: songs) {
                    if (songInformation.equals((SongInformation)options[songNumber])) {
                        equal = true;
                        break;
                    }
                }
                if (!equal) {
                    return false;
                } else {
                    equal = false;
                }
            }
        }
        return true;
    }
}
