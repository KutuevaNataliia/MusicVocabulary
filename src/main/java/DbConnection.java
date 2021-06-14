import java.io.Closeable;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

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

    public  ArrayList<SongInformation> getSongInformationsWithWord(String word) {
        ArrayList<SongInformation> songInformations = new ArrayList<>();
        String query1 = "SELECT * FROM my_songs WHERE spotify_song_id IN "
        + "(SELECT spotify_song_id FROM words_in_songs WHERE word = ?);";
        String query2 = "SELECT name FROM artists WHERE spotify_song_id = ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query1);
            preparedStatement.setString(1, word);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                SongInformation songInf = new SongInformation();
                songInf.spotifyId = resultSet.getString("spotify_song_id");
                songInf.name = resultSet.getString("name");
                songInf.text = resultSet.getString("text");
                PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
                preparedStatement2.setString(1, songInf.spotifyId);
                ResultSet resultSet2 = preparedStatement2.executeQuery();
                int counter2 = 0;
                while (resultSet2.next()) {
                    songInf.artists[counter2] = resultSet2.getString("name");
                    counter2++;
                }
                songInf.artistsNumber = counter2;
                songInformations.add(songInf);
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return songInformations;
    }

    public ArrayList<SongTitle> getSongTitlesWithWord(String word) {
        ArrayList<SongTitle> songTitles = new ArrayList<>();
        String query1 = "SELECT spotify_song_id, name  FROM my_songs WHERE spotify_song_id IN "
                + "(SELECT spotify_song_id FROM words_in_songs WHERE word = ?);";
        String query2 = "SELECT name FROM artists WHERE spotify_song_id = ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query1);
            preparedStatement.setString(1, word);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                SongTitle songTitle = new SongTitle();
                songTitle.spotifyId = resultSet.getString("spotify_song_id");
                songTitle.name = resultSet.getString("name");
                PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
                preparedStatement2.setString(1, songTitle.spotifyId);
                ResultSet resultSet2 = preparedStatement2.executeQuery();
                int counter2 = 0;
                while (resultSet2.next()) {
                    songTitle.artists[counter2] = resultSet2.getString("name");
                    counter2++;
                }
                songTitle.artistsNumber = counter2;
                songTitles.add(songTitle);
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return songTitles;
    }

    public Vector<String> getAllSongIDs() {
        String query = "SELECT spotify_song_id FROM my_songs;";
        Vector<String> songIDs = new Vector<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                songIDs.add(resultSet.getString("spotify_song_id"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return  songIDs;
    }


    public SongInformation getSongInformationByID(String spotifyID) {
        String query1 = "SELECT * FROM my_songs WHERE spotify_song_id = ?;";
        String query2 = "SELECT name FROM artists WHERE spotify_song_id = ?;";
        SongInformation songInf = new SongInformation();
        songInf.spotifyId = spotifyID;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query1);
            preparedStatement.setString(1, spotifyID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                songInf.name = resultSet.getString("name");
                songInf.text = resultSet.getString("text");
                PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
                preparedStatement2.setString(1, spotifyID);
                ResultSet resultSet2 = preparedStatement2.executeQuery();
                int counter = 0;
                while (resultSet2.next()) {
                    songInf.artists[counter] = resultSet2.getString("name");
                    counter++;
                }
                songInf.artistsNumber = counter;
            }
        }
        catch  (SQLException e) {
            System.out.println(e.getMessage());
        }
        return songInf;
    }

    public SongTitle getSongTitleByID(String spotifyID) {
        String query1 = "SELECT name FROM my_songs WHERE spotify_song_id = ?;";
        String query2 = "SELECT name FROM artists WHERE spotify_song_id = ?;";
        SongTitle songTitle = new SongTitle();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query1);
            preparedStatement.setString(1, spotifyID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                songTitle.spotifyId = spotifyID;
                songTitle.name = resultSet.getString("name");
                PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
                preparedStatement2.setString(1, spotifyID);
                ResultSet resultSet2 = preparedStatement2.executeQuery();
                int counter = 0;
                while (resultSet2.next()) {
                    songTitle.artists[counter] = resultSet2.getString("name");
                    counter++;
                }
                songTitle.artistsNumber = counter;
            }
        }
        catch  (SQLException e) {
            System.out.println(e.getMessage());
        }
       return songTitle;
    }

    public Vector<WordInformation> getAllWordInformation() {
        String query = "SELECT * FROM words_information;";
        Vector<WordInformation> wordInfs = new Vector<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                WordInformation wordInf = new WordInformation();
                wordInf.mainForm = resultSet.getString("main");
                wordInf.translation = resultSet.getString("translation");
                int counter = 0;
                for (int i = 0; i < 5; i++) {
                    String additionalForm = resultSet.getString(i + 4);
                    if (additionalForm != null) {
                        wordInf.additionalForms[i] = additionalForm;
                        counter++;
                    }
                }
                wordInf.additionalFormsNumber = counter;
                wordInfs.add(wordInf);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return wordInfs;
    }

    public String[] getWordsInSong(String songID) {
        String query = "SELECT word FROM words_in_songs WHERE spotify_song_id = ?;";
        ArrayList<String> rareWords = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, songID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                rareWords.add(resultSet.getString("word"));
            }
        }
        catch  (SQLException e) {
            System.out.println(e.getMessage());
        }
        String[] words = new String[rareWords.size()];
        words = rareWords.toArray(words);
        return words;
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

    public MyPair<WordInformation, Integer> getWordInformation(String word) {
        WordInformation wordInf = null;
        int key = 0;
        String query = "SELECT * FROM words_information WHERE main = ? OR additional1 = ? " +
                "OR additional2 = ? OR additional3 = ? OR additional4 = ? OR additional5 = ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            for (int i = 1; i <= 6; i++) {
                preparedStatement.setString(i, word);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                key = resultSet.getInt(1);
                wordInf = new WordInformation();
                wordInf.mainForm = resultSet.getString("main");
                wordInf.translation = resultSet.getString("translation");
                wordInf.additionalFormsNumber = 0;
                for (int i = 0; i < 5; i++) {
                    String additionalForm = resultSet.getString(i + 4);
                    if (additionalForm != null) {
                        wordInf.additionalForms[wordInf.additionalFormsNumber] = additionalForm;
                        wordInf.additionalFormsNumber++;
                    }
                }
            }
        }
        catch  (SQLException e) {
            System.out.println(e.getMessage());
        }
        return  new MyPair<>(wordInf, key);
    }

    public void addWordInformation(WordInformation wordInf) {
        String query = "INSERT INTO words_information (main, translation, additional1, additional2, additional3, " +
                "additional4, additional5) VALUES (?, ?, ?, ?, ?, ?, ?);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, wordInf.mainForm);
            preparedStatement.setString(2, wordInf.translation);
            int counter = 0;
            for ( ;counter < wordInf.additionalFormsNumber; counter++) {
                preparedStatement.setString(counter + 3, wordInf.additionalForms[counter]);
            }
            for ( ;counter < 5; counter++) {
                preparedStatement.setNull(counter + 3, Types.NULL);
            }
            int rows = preparedStatement.executeUpdate();
        }
        catch  (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateWordInformation(WordInformation wordInf, int key) {
        if (wordInf == null) {
            return;
        }
        String query = "UPDATE words_information SET main = ?, translation = ?, additional1 = ?, " +
                "additional2 = ?, additional3 = ?, additional4 = ?, additional5 = ? WHERE id = ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, wordInf.mainForm);
            preparedStatement.setString(2, wordInf.translation);
            int counter = 0;
            for ( ;counter < wordInf.additionalFormsNumber; counter++) {
                preparedStatement.setString(counter + 3, wordInf.additionalForms[counter]);
            }
            for ( ;counter < 5; counter++) {
                preparedStatement.setNull(counter + 3, Types.NULL);
            }
            preparedStatement.setInt(8, key);
            int rows = preparedStatement.executeUpdate();
        }
        catch  (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Vector<String> getAllRareWords() {
        Vector<String> rareWords = new Vector<>();
        String query = "SELECT word FROM my_vocabulary WHERE frequency < 4 ORDER BY word;";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                rareWords.add(resultSet.getString("word"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rareWords;
    }

    public LinkedList<String> getFavourites() {
        LinkedList<String> favourites = new LinkedList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT word FROM favourites ORDER BY word;");
            while(resultSet.next()) {
                favourites.add(resultSet.getString("word"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return favourites;
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

    public int getSongsAmount() {
        int result = 0;
        String query = "SELECT COUNT(*) FROM my_songs;";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.next()) {
                result = resultSet.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public int getAllWordsAmount() {
        int result = 0;
        String query = "SELECT COUNT(*) FROM my_vocabulary;";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.next()) {
                result = resultSet.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public ArrayList<MyPair<String, Integer>> getAllVocabulary() {
        ArrayList<MyPair<String, Integer>> pairs= new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM my_vocabulary ORDER BY word;");
            while(resultSet.next()) {
                pairs.add(new MyPair<>(resultSet.getString("word"), resultSet.getInt("frequency")));
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

    public MyPair<String, Long> getAccessToken() {
        String query = "SELECT token, expires FROM tokens WHERE name = 'access';";
        MyPair<String, Long> pair = new MyPair<>("", (long)0);
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                pair.setFirst(resultSet.getString("token"));
                pair.setSecond(resultSet.getLong("expires"));
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
