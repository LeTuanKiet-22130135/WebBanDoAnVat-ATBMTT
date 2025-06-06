package util;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.GenericData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service class for Google OAuth operations
 * Handles OAuth flow, token exchange, and user information retrieval
 */
public class GoogleOAuthService {
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /**
     * Generates a unique state parameter for OAuth flow
     * Used to prevent CSRF attacks
     * 
     * @return A unique state string
     */
    public static String generateState() {
        return UUID.randomUUID().toString();
    }

    /**
     * Exchanges an authorization code for an access token
     * 
     * @param code The authorization code from Google
     * @return The access token
     * @throws IOException If an error occurs during the exchange
     */
    public static String exchangeCodeForToken(String code) throws IOException {
        TokenResponse response = new AuthorizationCodeTokenRequest(
                HTTP_TRANSPORT, 
                JSON_FACTORY, 
                new GenericUrl(GoogleOAuthConfig.TOKEN_URI),
                code)
                .setRedirectUri(GoogleOAuthConfig.REDIRECT_URI)
                .setClientAuthentication(
                        new com.google.api.client.auth.oauth2.ClientParametersAuthentication(
                                GoogleOAuthConfig.CLIENT_ID, 
                                GoogleOAuthConfig.CLIENT_SECRET))
                .execute();

        return response.getAccessToken();
    }

    /**
     * Retrieves user information using an access token
     * 
     * @param accessToken The access token
     * @return A map containing user information (email, given_name, family_name)
     * @throws IOException If an error occurs during the retrieval
     */
    public static Map<String, String> getUserInfo(String accessToken) throws IOException {
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(request -> 
                request.getHeaders().setAuthorization("Bearer " + accessToken));

        HttpRequest request = requestFactory.buildGetRequest(
                new GenericUrl(GoogleOAuthConfig.USER_INFO_URI));

        // Use Google's JSON parser instead of manual string parsing
        JsonObjectParser parser = new com.google.api.client.json.gson.GsonFactory().createJsonObjectParser();
        request.setParser(parser);

        // Parse the response into a map
        GenericData responseData = request.execute().parseAs(com.google.api.client.util.GenericData.class);

        Map<String, String> userInfo = new HashMap<>();

        // Extract user information safely
        if (responseData.containsKey("email")) {
            userInfo.put("email", (String) responseData.get("email"));
        }

        if (responseData.containsKey("given_name")) {
            userInfo.put("given_name", (String) responseData.get("given_name"));
        }

        if (responseData.containsKey("family_name")) {
            userInfo.put("family_name", (String) responseData.get("family_name"));
        }

        // Debug: Print the entire response for troubleshooting
        System.out.println("Google OAuth Response: " + responseData);

        return userInfo;
    }
}
