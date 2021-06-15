import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.SavedTrack;
import com.wrapper.spotify.requests.data.library.GetUsersSavedTracksRequest;
import core.GLA;
import genius.SongSearch;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class GetUsersSavedTracks {
    private GetUsersSavedTracksRequest getUsersSavedTracksRequest;

    private final SpotifyApi spotifyApi = Authorization.spotifyApi;

    private final DbConnection dbConnection = new DbConnection();

    public void updateSongs() {
        dbConnection.open();
        MyPair<String, Long> accessTokenPair = dbConnection.getAccessToken();
        if (System.currentTimeMillis() >= accessTokenPair.getSecond()) {
            String refreshToken = dbConnection.getRefreshToken();
            Authorization.authorizationCodeRefresh_Sync(refreshToken, dbConnection);
        } else {
            spotifyApi.setAccessToken(accessTokenPair.getFirst());
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

    private void processText(String text, String spotifyID) {
        String[] words = text.split("\\s+");
        Set<String> set = new HashSet<>();
        for (String word: words) {
            String word1 = word.replaceAll("[\\p{P}&&[^\u0027\u2010]]", "");
            if (!word1.isEmpty() && !word1.matches("\\d+") && !word1.matches("\\W+")) {
                word = word1.substring(0, 1).toUpperCase() + word1.substring(1);
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



    private void processTracks(Paging<SavedTrack> savedTrackPaging) {
        SavedTrack[] tracks = savedTrackPaging.getItems();

        for (SavedTrack track: tracks) {
            String id = track.getTrack().getId();
            if (!General.songIDs.contains(id)) {
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
                        System.out.println(hit.getTitle());
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
            int total = savedTrackPaging.getTotal();
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
}
