public class WordToGuess extends WordInformation implements Guessable<SongTitle>{
    SongTitle[] songs;

    //Переделать конструктор и соответствующие методы !!!
    public WordToGuess(WordInformation wordInf, SongInformation[] songs) {
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
    public WordByOptions guess(SongTitle[] options, SongTitle[] rightAnswers) {
        WordByOptions completeTask = new WordByOptions();
        completeTask.taskExplanation = "Guess what songs contain the word";
        completeTask.answerExplanation = "Select all the appropriate songs";
        completeTask.mainForm = mainForm;
        completeTask.songs = options;
        completeTask.rightAnswers = rightAnswers;
        return completeTask;
    }
}
