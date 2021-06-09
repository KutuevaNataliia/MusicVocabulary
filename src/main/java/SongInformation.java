import java.util.Objects;

public class SongInformation {
    public String spotifyId;
    public String name;
    public String[] artists = new String[5];
    public String text;
    public int artistsNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SongInformation that = (SongInformation) o;
        return spotifyId.equals(that.spotifyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spotifyId);
    }
}

