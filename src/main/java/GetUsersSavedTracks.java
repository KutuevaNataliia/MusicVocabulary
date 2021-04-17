import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.SavedTrack;
import com.wrapper.spotify.requests.data.library.GetUsersSavedTracksRequest;
import org.apache.hc.core5.http.ParseException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class GetUsersSavedTracks {
    private static GetUsersSavedTracksRequest getUsersSavedTracksRequest;

    public static void setGetUsersSavedTracksRequest (SpotifyApi spotifyApi) {
       getUsersSavedTracksRequest = spotifyApi.getUsersSavedTracks()
          .limit(50)
//          .offset(0)
//          .market(CountryCode.SE)
          .build();
    }


    public static void getUsersSavedTracks_Sync() {
        try {
            final Paging<SavedTrack> savedTrackPaging = getUsersSavedTracksRequest.execute();

            System.out.println("Total: " + savedTrackPaging.getTotal());
            SavedTrack[] tracks = savedTrackPaging.getItems();
            try (FileWriter writer = new FileWriter("FavouriteTracks.txt")) {
                for (SavedTrack track: tracks) {
                    ArtistSimplified[] artists = track.getTrack().getArtists();
                    for (ArtistSimplified artist: artists) {
                        writer.write(artist.getName() + " ");
                    }
                    writer.write(" " + track.getTrack().getName() + "\n");
                }
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void getUsersSavedTracks_Async() {
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
