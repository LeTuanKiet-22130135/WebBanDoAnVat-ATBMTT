package util;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.GenericData;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service class for Facebook OAuth operations
 * Handles OAuth flow, token exchange, and user information retrieval
 */
public class FacebookOAuthService {
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
     * @param code The authorization code from Facebook
     * @return The access token
     * @throws IOException If an error occurs during the exchange
     */
    public static String exchangeCodeForToken(String code) throws IOException {
        // Build the token request URL
        String tokenUrl = FacebookOAuthConfig.TOKEN_URI + 
                "?client_id=" + FacebookOAuthConfig.APP_ID +
                "&redirect_uri=" + URLEncoder.encode(FacebookOAuthConfig.REDIRECT_URI, StandardCharsets.UTF_8) +
                "&client_secret=" + FacebookOAuthConfig.APP_SECRET +
                "&code=" + code;
        
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(tokenUrl));
        
        // Use Google's JSON parser
        JsonObjectParser parser = new GsonFactory().createJsonObjectParser();
        request.setParser(parser);
        
        // Execute the request and parse the response
        HttpResponse response = request.execute();
        GenericData responseData = response.parseAs(GenericData.class);
        
        // Extract the access token
        String accessToken = (String) responseData.get("access_token");
        if (accessToken == null || accessToken.isEmpty()) {
            throw new IOException("Access token not received from Facebook");
        }
        
        return accessToken;
    }

    /**
     * Retrieves user information using an access token
     * 
     * @param accessToken The access token
     * @return A map containing user information (email, first_name, last_name)
     * @throws IOException If an error occurs during the retrieval
     */
    public static Map<String, String> getUserInfo(String accessToken) throws IOException {
        // Build the user info request URL with fields parameter to specify what data to retrieve
        String userInfoUrl = FacebookOAuthConfig.USER_INFO_URI + 
                "?fields=id,email,first_name,last_name" +
                "&access_token=" + accessToken;
        
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(userInfoUrl));
        
        // Use Google's JSON parser
        JsonObjectParser parser = new GsonFactory().createJsonObjectParser();
        request.setParser(parser);
        
        // Execute the request and parse the response
        HttpResponse response = request.execute();
        GenericData responseData = response.parseAs(GenericData.class);
        
        Map<String, String> userInfo = new HashMap<>();
        
        // Extract user information safely
        if (responseData.containsKey("email")) {
            userInfo.put("email", (String) responseData.get("email"));
        }
        
        if (responseData.containsKey("first_name")) {
            userInfo.put("given_name", (String) responseData.get("first_name"));
        }
        
        if (responseData.containsKey("last_name")) {
            userInfo.put("family_name", (String) responseData.get("last_name"));
        }
        
        // Debug: Print the entire response for troubleshooting
        System.out.println("Facebook OAuth Response: " + responseData);
        
        return userInfo;
    }
}