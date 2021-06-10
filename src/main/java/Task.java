public class Task {
    public String taskExplanation;
    public String answerExplanation;
}

class WordByItself extends Task {
    public String translation;
    public SongTitle[] songs;
    public String[] rightAnswers;
}

class WordByOptions extends Task {
    public String mainForm;
    public SongTitle[] songs;
    public SongTitle[] rightAnswers;
}

class SongByItself extends Task {
    public String[] words;
    public SongTitle rightAnswer;
}

class SongByOptions extends Task {
    public SongTitle song;
    public String[] words;
    public String[] rightAnswers;
}
