import java.util.Arrays;
import java.util.Objects;

public class SongTitle {
    public String spotifyId;
    public String name;
    public String[] artists = new String[5];
    public int artistsNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SongTitle songTitle = (SongTitle) o;
        return spotifyId.equals(songTitle.spotifyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spotifyId);
    }
}
