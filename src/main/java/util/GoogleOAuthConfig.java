package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration class for Google OAuth
 * Contains constants and settings for Google OAuth integration
 * Configuration is loaded from oauth.properties file
 */
public class GoogleOAuthConfig {
    // Google OAuth credentials loaded from properties file
    public static final String CLIENT_ID;
    public static final String CLIENT_SECRET;
    public static final String REDIRECT_URI;

    // OAuth endpoints
    public static final String AUTH_URI = "https://accounts.google.com/o/oauth2/auth";
    public static final String TOKEN_URI = "https://oauth2.googleapis.com/token";

    // OAuth scopes
    public static final String SCOPE_EMAIL = "email";
    public static final String SCOPE_PROFILE = "profile";

    // Full scope string for authorization request
    public static final String SCOPES = SCOPE_EMAIL + " " + SCOPE_PROFILE;

    // User info endpoint
    public static final String USER_INFO_URI = "https://www.googleapis.com/oauth2/v3/userinfo";

    // Static initializer to load properties from file
    static {
        Properties properties = new Properties();
        try (InputStream input = GoogleOAuthConfig.class.getClassLoader().getResourceAsStream("oauth.properties")) {
            if (input == null) {
                throw new IOException("Unable to find oauth.properties file");
            }
            properties.load(input);

            CLIENT_ID = properties.getProperty("client.id");
            CLIENT_SECRET = properties.getProperty("client.secret");
            REDIRECT_URI = properties.getProperty("redirect.uri");

            if (CLIENT_ID == null || CLIENT_SECRET == null || REDIRECT_URI == null) {
                throw new IOException("Required OAuth properties are missing");
            }
        } catch (IOException e) {
            System.err.println("Error loading OAuth properties: " + e.getMessage());
            // Fallback to default values in case of error
            throw new RuntimeException("Failed to load OAuth configuration", e);
        }
    }

    /**
     * Builds the authorization URL for Google OAuth
     * 
     * @param state A unique state parameter to prevent CSRF attacks
     * @return The authorization URL
     */
    public static String buildAuthorizationUrl(String state) {
        return AUTH_URI + "?" +
                "client_id=" + CLIENT_ID + "&" +
                "redirect_uri=" + REDIRECT_URI + "&" +
                "response_type=code&" +
                "scope=" + SCOPES + "&" +
                "access_type=offline&" +
                "state=" + state;
    }
}
