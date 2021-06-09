import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import org.apache.hc.core5.http.ParseException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class Authorization {
    final static Logger logger = Logger.getLogger(Authorization.class);

    private static final String clientId = "4c8bbd870d194e10af716b60a15e7cbe";
    private static final String clientSecret = "f66a67dab5834c8e8831511a5280351b";
    private static final URI redirectUri = SpotifyHttpManager.makeUri("https://localhost:8080");


    public static AuthorizationCodeRequest getAuthorizationCodeRequest(String retrievedCode) {
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(retrievedCode)
                .build();
        return authorizationCodeRequest;
    }

    public static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRedirectUri(redirectUri)
            .build();

    private static final AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
          //.state("x4xkmn9pu3j6ukrs8n")
          .scope("user-library-read")
          .show_dialog(true)
            .build();

    public static void authorizationCodeUri_Sync() {
        final URI uri = authorizationCodeUriRequest.execute();

        System.out.println("URI: " + uri.toString());
    }

    public static void authorizationCodeRefresh_Sync(String refreshToken, DbConnection dbConnection) {
        spotifyApi.setRefreshToken(refreshToken);
        AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh()
                .build();
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

            // Set access and refresh token for further "spotifyApi" object usage
            String accessToken = authorizationCodeCredentials.getAccessToken();
            spotifyApi.setAccessToken(accessToken);

            /*DbConnection dbConnection = new DbConnection();
            dbConnection.open();*/
            long expires = System.currentTimeMillis() + (authorizationCodeCredentials.getExpiresIn() - 1) * 1000;
            dbConnection.changeAccessToken(accessToken, expires);
            dbConnection.changeRefreshToken(authorizationCodeCredentials.getRefreshToken());
            //dbConnection.close();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

//    private static final AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
//            .build();

    public static SpotifyApi authorizationCode_Sync(AuthorizationCodeRequest authorizationCodeRequest) {

        System.out.println("Объект api создан");
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
            logger.info("Authorization success");
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
            logger.error("Authorization fails");
        }
        return spotifyApi;
    }

    public static void authorizationCode_Async(AuthorizationCodeRequest authorizationCodeRequest) {
        try {
            final CompletableFuture<AuthorizationCodeCredentials> authorizationCodeCredentialsFuture = authorizationCodeRequest.executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeCredentialsFuture.join();

            // Set access and refresh token for further "spotifyApi" object usage

            DbConnection dbConnection = new DbConnection();
            dbConnection.open();
            long expires = System.currentTimeMillis() + (authorizationCodeCredentials.getExpiresIn()- 1) * 1000;
            dbConnection.changeAccessToken(authorizationCodeCredentials.getAccessToken(), expires);
            dbConnection.changeRefreshToken(authorizationCodeCredentials.getRefreshToken());
            dbConnection.close();
            //spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            //spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
        //return spotifyApi;
    }

    public static void authorizationCodeUri_Async() {
        try {
            final CompletableFuture<URI> uriFuture = authorizationCodeUriRequest.executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.
            final URI uri = uriFuture.join();

            System.out.println("URI: " + uri.toString());
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
    }
}
