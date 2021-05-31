import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.SavedTrack;
import com.wrapper.spotify.requests.data.library.GetUsersSavedTracksRequest;
import core.GLA;
import genius.SongSearch;
import org.apache.hc.core5.http.ParseException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class GetUsersSavedTracks {
    private GetUsersSavedTracksRequest getUsersSavedTracksRequest;

    private SpotifyApi spotifyApi = Authorization.spotifyApi;

    private final DbConnection dbConnection = new DbConnection();

    public void updateSongs() {
        dbConnection.open();
        TokenExpiresPair accessTokenPair = dbConnection.getAccessToken();
        if (System.currentTimeMillis() >= accessTokenPair.expires) {
            String refreshToken = dbConnection.getRefreshToken();
            Authorization.authorizationCodeRefresh_Sync(refreshToken);
        } else {
            spotifyApi.setAccessToken(accessTokenPair.token);
        }
        setGetUsersSavedTracksRequest(0);
        getUsersSavedTracks_Sync();
        dbConnection.close();
    }

    public void setGetUsersSavedTracksRequest (int offset) {
       getUsersSavedTracksRequest = spotifyApi.getUsersSavedTracks()
          .limit(50)
          .offset(offset)
//          .market(CountryCode.SE)
          .build();
    }

    private void printTracks(Paging<SavedTrack> savedTrackPaging, FileWriter writer) throws IOException{
        SavedTrack[] tracks = savedTrackPaging.getItems();
        for (SavedTrack track: tracks) {
            writer.write("Artists: ");
            ArtistSimplified[] artists = track.getTrack().getArtists();
            for (ArtistSimplified artist: artists) {
                writer.write(artist.getName() + " ");
            }
            writer.write("Track: " + track.getTrack().getName() + "\n");
        }
    }

    private void processText(String text, String spotifyID) {
        String[] words = text.split("\\s+");
        Set<String> set = new HashSet<>();
        for (String word: words) {
            String word1 = word.replaceAll("[\\p{P}&&[^\u0027]]", "");
            if (!word1.isEmpty()) {
                word = word1.substring(0, 1).toUpperCase() + word1.substring(1);
                if (!word.matches("\\d+") && !word.matches("\\W+")) {
                    int frequency = dbConnection.getWordFrequency(word);
                    if (frequency == 0) {
                        set.add(word);
                        dbConnection.addWordToVocabulary(word);
                        dbConnection.addWordSongConnection(word, spotifyID);
                    } else if (frequency < 20) {
                        dbConnection.increaseWordFrequency(word, frequency);
                        if (frequency < 3 && !set.contains(word)) {
                            set.add(word);
                            dbConnection.addWordSongConnection(word, spotifyID);
                        }
                    }
                }
            }
        }
    }



    private void processTracks(Paging<SavedTrack> savedTrackPaging) {
        SavedTrack[] tracks = savedTrackPaging.getItems();

        for (SavedTrack track: tracks) {
            String id = track.getTrack().getId();
            if (!dbConnection.checkSong(id)) {
                ArtistSimplified[] artists = track.getTrack().getArtists();
                StringBuilder glaSearch = new StringBuilder();
                String[] myArtists = new String[artists.length];
                int counter = 0;
                for (ArtistSimplified artist: artists) {
                    myArtists[counter] = artist.getName();
                    glaSearch.append(artist.getName());
                    glaSearch.append(' ');
                    counter++;
                }
                String name = track.getTrack().getName();
                glaSearch.append(name);
                GLA gla = new GLA();
                try {
                    SongSearch songSearch = gla.search(glaSearch.toString());
                    LinkedList<SongSearch.Hit> hits = songSearch.getHits();
                    for (SongSearch.Hit hit: hits) {
                        boolean sameTrack = true;
                        for (String myArtist: myArtists) {
                            if (!hit.getArtist().getName().contains(myArtist)){
                                sameTrack = false;
                                break;
                            }
                        }
                        if (!hit.getTitle().equalsIgnoreCase(name)) {
                            sameTrack = false;
                        }
                        if (sameTrack) {
                            String text = hit.fetchLyrics();
                            dbConnection.insertSong(id, myArtists, name, text);
                            processText(text, id);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getUsersSavedTracks_Sync() {
        try {
            final Paging<SavedTrack> savedTrackPaging = getUsersSavedTracksRequest.execute();

            //System.out.println("Total: " + savedTrackPaging.getTotal());
            int total = savedTrackPaging.getTotal();
            /*try (FileWriter writer = new FileWriter("FavouriteTracks.txt")) {
                writer.write("Total: " + total + "\n");
                printTracks(savedTrackPaging, writer);
                for (int i = 0; i < ((total - 1) / 50); i++) {
                    setGetUsersSavedTracksRequest((i + 1) * 50);
                    Paging<SavedTrack> savedTrackPaging2 = getUsersSavedTracksRequest.execute();
                    printTracks(savedTrackPaging2, writer);
                }
            }*/
            processTracks(savedTrackPaging);
            for (int i = 0; i < ((total - 1) / 50); i++) {
                setGetUsersSavedTracksRequest((i + 1) * 50);
                Paging<SavedTrack> savedTrackPaging2 = getUsersSavedTracksRequest.execute();
                processTracks(savedTrackPaging2);
            }
            System.out.println("All tracks added");
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void getUsersSavedTracks_Async() {
        try {
            final CompletableFuture<Paging<SavedTrack>> pagingFuture = getUsersSavedTracksRequest.executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.
            final Paging<SavedTrack> savedTrackPaging = pagingFuture.join();

            System.out.println("Total: " + savedTrackPaging.getTotal());
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
    }
}
