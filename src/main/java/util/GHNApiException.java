package util;

/**
 * Custom exception class for GHN API errors
 * Contains information about HTTP status code, error message, code, and code_message
 */
public class GHNApiException extends Exception {
    private final int statusCode;
    private final String errorResponse;
    private final String code;
    private final String codeMessage;
    private final String apiMessage;

    /**
     * Constructor for GHNApiException
     * 
     * @param statusCode The HTTP status code returned by the API
     * @param errorResponse The error response returned by the API
     * @param message The exception message
     * @param code The error code returned by the API
     * @param codeMessage The error code message returned by the API
     * @param apiMessage The message field from the API response
     */
    public GHNApiException(int statusCode, String errorResponse, String message, String code, String codeMessage, String apiMessage) {
        super(message);
        this.statusCode = statusCode;
        this.errorResponse = errorResponse;
        this.code = code;
        this.codeMessage = codeMessage;
        this.apiMessage = apiMessage;
    }

    /**
     * Gets the HTTP status code
     * 
     * @return The HTTP status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Gets the error response
     * 
     * @return The error response
     */
    public String getErrorResponse() {
        return errorResponse;
    }

    /**
     * Gets the error code
     * 
     * @return The error code
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the error code message
     * 
     * @return The error code message
     */
    public String getCodeMessage() {
        return codeMessage;
    }

    /**
     * Gets the API message
     * 
     * @return The message field from the API response
     */
    public String getApiMessage() {
        return apiMessage;
    }
}
