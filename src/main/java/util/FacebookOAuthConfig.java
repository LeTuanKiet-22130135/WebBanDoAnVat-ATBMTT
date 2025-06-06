package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration class for Facebook OAuth
 * Contains constants and settings for Facebook OAuth integration
 * Configuration is loaded from oauth.properties file
 */
public class FacebookOAuthConfig {
    // Facebook OAuth credentials loaded from properties file
    public static final String APP_ID;
    public static final String APP_SECRET;
    public static final String REDIRECT_URI;

    // OAuth endpoints
    public static final String AUTH_URI = "https://www.facebook.com/v18.0/dialog/oauth";
    public static final String TOKEN_URI = "https://graph.facebook.com/v18.0/oauth/access_token";

    // OAuth scopes
    public static final String SCOPE_EMAIL = "email";
    public static final String SCOPE_PUBLIC_PROFILE = "public_profile";

    // Full scope string for authorization request
    public static final String SCOPES = SCOPE_EMAIL + "," + SCOPE_PUBLIC_PROFILE;

    // User info endpoint
    public static final String USER_INFO_URI = "https://graph.facebook.com/v18.0/me";

    // Static initializer to load properties from file
    static {
        Properties properties = new Properties();
        try (InputStream input = FacebookOAuthConfig.class.getClassLoader().getResourceAsStream("oauth.properties")) {
            if (input == null) {
                throw new IOException("Unable to find oauth.properties file");
            }
            properties.load(input);

            APP_ID = properties.getProperty("facebook.app.id");
            APP_SECRET = properties.getProperty("facebook.app.secret");
            REDIRECT_URI = properties.getProperty("facebook.redirect.uri");

            if (APP_ID == null || APP_SECRET == null || REDIRECT_URI == null) {
                throw new IOException("Required Facebook OAuth properties are missing");
            }
        } catch (IOException e) {
            System.err.println("Error loading Facebook OAuth properties: " + e.getMessage());
            // Fallback to default values in case of error
            throw new RuntimeException("Failed to load Facebook OAuth configuration", e);
        }
    }

    /**
     * Builds the authorization URL for Facebook OAuth
     * 
     * @param state A unique state parameter to prevent CSRF attacks
     * @return The authorization URL
     */
    public static String buildAuthorizationUrl(String state) {
        return AUTH_URI + "?" +
                "client_id=" + APP_ID + "&" +
                "redirect_uri=" + REDIRECT_URI + "&" +
                "response_type=code&" +
                "scope=" + SCOPES + "&" +
                "state=" + state;
    }
}