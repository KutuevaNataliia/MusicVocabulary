import java.util.Random;
import java.util.Vector;

public interface Guessable {
    Task guess();
    Task guess(Vector<String> options, Random random);
}
