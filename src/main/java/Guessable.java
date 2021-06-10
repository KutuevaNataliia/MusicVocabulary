public interface Guessable<T> {
    Task guess();
    Task guess(T[] options, T[] rightAnswers);
}
