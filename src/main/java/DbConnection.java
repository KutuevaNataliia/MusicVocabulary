import java.io.Closeable;
import java.sql.*;
import java.util.ArrayList;

public class DbConnection implements Closeable{
    private Connection connection;
    public void open() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:songs.db");
            System.out.println("Connected");
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertSong(String spotifyID, String[] artists, String name, String text) {
        String query1 = "INSERT INTO my_songs (spotify_song_id, name, text) " +
                "VALUES (?, ?, ?);";
        String query2 = "INSERT INTO artists (name, spotify_song_id) " +
                "VALUES (?, ?);";
        try {
            PreparedStatement preparedStatement1 = connection.prepareStatement(query1);
            preparedStatement1.setString(1, spotifyID);
            preparedStatement1.setString(2, name);
            preparedStatement1.setString(3, text);
            int rows = preparedStatement1.executeUpdate();

            for (String artist: artists) {
                PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
                preparedStatement2.setString(1, artist);
                preparedStatement2.setString(2, spotifyID);
                int rows2 = preparedStatement2.executeUpdate();
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public boolean checkSong(String spotifyID) {
        String query = "SELECT spotify_song_id FROM my_songs WHERE my_songs.spotify_song_id = ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, spotifyID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public String getSongText(String spotifyID) {
        String query = "SELECT text FROM my_songs WHERE my_songs.spotify_song_id = ?;";
        String result = "";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, spotifyID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getString(1);
            }
        }
        catch  (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public int getWordFrequency(String word) {
        int result = 0;
        String query = "SELECT frequency FROM my_vocabulary WHERE word = ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, word);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
        }
        catch  (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public void addWordToVocabulary(String word) {
        String query = "INSERT INTO my_vocabulary (word, frequency) " +
                "VALUES (?, 1);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, word);
            int rows = preparedStatement.executeUpdate();
        }
        catch  (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void increaseWordFrequency(String word, int frequency) {
        String query = "UPDATE my_vocabulary SET frequency = ? WHERE word = ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, frequency + 1);
            preparedStatement.setString(2, word);
            int rows = preparedStatement.executeUpdate();
        }
        catch  (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String[] getAllRareWords() {
        ArrayList<String> rareWords = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT word FROM my_vocabulary WHERE frequency < 4 ORDER BY word;");
            while(resultSet.next()) {
                rareWords.add(resultSet.getString("word"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        String allRareWords[] = new String[rareWords.size()];
        allRareWords = rareWords.toArray(allRareWords);
        return allRareWords;
    }

    public void addWordToFavourites(String word) {
        String query = "INSERT INTO favourites (word) VALUES(?);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, word);
            int rows = preparedStatement.executeUpdate();
        }
        catch  (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteWordFromFavourites(String word) {
        String query = "DELETE FROM favourites WHERE word = ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, word);
            int rows = preparedStatement.executeUpdate();
        }
        catch  (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<WordFrequencyPair> getAllVocabulary() {
        ArrayList<WordFrequencyPair> pairs= new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM my_vocabulary ORDER BY word;");
            while(resultSet.next()) {
                pairs.add(new WordFrequencyPair(resultSet.getString("word"), resultSet.getInt("frequency")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return pairs;
    }

    public void addWordSongConnection(String word, String spotifyID) {
        String query = "INSERT INTO words_in_songs (word, spotify_song_id) "
                + "VALUES (?, ?);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, word);
            preparedStatement.setString(2, spotifyID);
            int rows = preparedStatement.executeUpdate();
        }
        catch  (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addAccessToken(String accessToken, long expires) {
        String query = "INSERT INTO tokens (name, token, expires) VALUES ('access', ?, ?);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, accessToken);
            preparedStatement.setLong(2, expires);
            int rows = preparedStatement.executeUpdate();
        }
        catch  (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addRefreshToken(String refreshToken) {
        String query = "INSERT INTO tokens (name, token) VALUES ('refresh', ?);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, refreshToken);
            int rows = preparedStatement.executeUpdate();
        }
        catch  (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void changeAccessToken(String accessToken, long expires) {
        String query = "UPDATE tokens SET token = ?, expires = ? WHERE name = 'access';";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, accessToken);
            preparedStatement.setLong(2, expires);
            int rows = preparedStatement.executeUpdate();
        }
        catch  (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void changeRefreshToken(String refreshToken) {
        String query = "UPDATE tokens SET token = ? WHERE name = 'refresh';";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, refreshToken);
            int rows = preparedStatement.executeUpdate();
        }
        catch  (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public TokenExpiresPair getAccessToken() {
        String query = "SELECT token, expires FROM tokens WHERE name = 'access';";
        TokenExpiresPair pair = new TokenExpiresPair("", 0);
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                pair.token = resultSet.getString("token");
                pair.expires = resultSet.getLong("expires");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return pair;
    }

    public String getRefreshToken() {
        String query = "SELECT token FROM tokens WHERE name = 'refresh';";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getString("token");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return "";
    }

    public void close() {
        try {
            connection.close();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
