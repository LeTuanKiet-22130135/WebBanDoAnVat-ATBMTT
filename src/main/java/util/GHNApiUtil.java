package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Utility class for interacting with the GHN API
 * Handles API requests for creating shipping orders
 */
public class GHNApiUtil {
    private static final String PROPERTIES_FILE = "order.properties";
    private static String apiToken;
    private static String shopId;
    private static String createOrderUrl;
    private static String baseUrl;

    static {
        loadProperties();
    }

    /**
     * Loads GHN API properties from the configuration file
     */
    private static void loadProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = GHNApiUtil.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (inputStream != null) {
                properties.load(inputStream);
                apiToken = properties.getProperty("ghn.api.token");
                shopId = properties.getProperty("ghn.shop.id");
                createOrderUrl = properties.getProperty("ghn.api.create_order_url");
                baseUrl = properties.getProperty("ghn.api.base_url");
            } else {
                System.err.println("Unable to find " + PROPERTIES_FILE);
            }
        } catch (IOException e) {
            System.err.println("Error loading properties: " + e.getMessage());
        }
    }

    /**
     * Creates a shipping order with GHN
     * 
     * @param orderData Map containing the order data
     * @return String containing the API response
     * @throws IOException if there's an error communicating with the API
     * @throws GHNApiException if the API returns an error response
     */
    public static String createOrder(Map<String, Object> orderData) throws IOException, GHNApiException {
        // Add shop_id to the order data
        orderData.put("shop_id", Integer.parseInt(shopId));

        // Make the API request
        return makeApiRequest(createOrderUrl, mapToJson(orderData));
    }

    /**
     * Makes a request to the GHN API
     * 
     * @param apiUrl The API endpoint URL
     * @param jsonData The request data as a JSON string
     * @return String containing the API response
     * @throws IOException if there's an error communicating with the API
     * @throws GHNApiException if the API returns an error response (4xx or 5xx)
     */
    private static String makeApiRequest(String apiUrl, String jsonData) throws IOException, GHNApiException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Token", apiToken);
        connection.setDoOutput(true);

        // Write request data
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int statusCode = connection.getResponseCode();

        // Check if the response code indicates an error (4xx or 5xx)
        if (statusCode >= 400) {
            // Read error response
            StringBuilder errorResponse = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    errorResponse.append(responseLine.trim());
                }
            }

            String errorResponseStr = errorResponse.toString();

            // Extract code, code_message, and message from the error response JSON
            String code = extractJsonValue(errorResponseStr, "code");
            String codeMessage = extractJsonValue(errorResponseStr, "code_message");
            String apiMessage = extractJsonValue(errorResponseStr, "message");

            // If code, code_message, or apiMessage couldn't be extracted, use default values
            if (code == null) code = "UNKNOWN_ERROR";
            if (codeMessage == null) codeMessage = "Unknown error occurred";
            if (apiMessage == null) apiMessage = "No message provided";

            throw new GHNApiException(statusCode, errorResponseStr, 
                    "GHN API returned error code: " + statusCode,
                    code, codeMessage, apiMessage);
        }

        // Read successful response
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        return response.toString();
    }

    /**
     * Converts a Map to a JSON string
     * 
     * @param map The Map to convert
     * @return A JSON string representation of the Map
     */
    private static String mapToJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;

            json.append("\"").append(entry.getKey()).append("\":");

            Object value = entry.getValue();
            appendJsonValue(json, value);
        }

        json.append("}");
        return json.toString();
    }

    private static void appendJsonValue(StringBuilder json, Object value) {
        if (value instanceof String) {
            json.append("\"").append(value).append("\"");
        } else if (value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double) {
            json.append(value);
        } else if (value instanceof Boolean) {
            json.append(value);
        } else if (value == null) {
            json.append("null");
        } else if (value instanceof Map) {
            json.append(mapToJson((Map<String, Object>) value));
        } else if (value instanceof java.util.List) {
            json.append("[");
            boolean firstItem = true;
            for (Object item : (java.util.List<?>) value) {
                if (!firstItem) {
                    json.append(",");
                }
                firstItem = false;
                appendJsonValue(json, item);
            }
            json.append("]");
        } else {
            json.append("\"").append(value.toString()).append("\"");
        }
    }

    /**
     * Prepares order data for the GHN API
     * 
     * @param orderId The internal order ID
     * @param toName Recipient's name
     * @param toPhone Recipient's phone number
     * @param toAddress Recipient's address
     * @param toWardCode Recipient's ward code
     * @param toDistrictId Recipient's district ID
     * @param weight Total weight in grams
     * @param length Package length in cm
     * @param width Package width in cm
     * @param height Package height in cm
     * @param insuranceValue Insurance value
     * @param codAmount COD amount
     * @param serviceTypeId Service type ID
     * @return Map containing the prepared order data
     */

    /**
     * Extracts a value from a JSON string based on a key
     * 
     * @param json The JSON string to parse
     * @param key The key to extract the value for
     * @return The value for the key, or null if not found
     */
    private static String extractJsonValue(String json, String key) {
        // Simple JSON parsing to extract a value for a key
        String keyPattern = "\"" + key + "\":";
        int keyIndex = json.indexOf(keyPattern);

        if (keyIndex == -1) {
            return null;
        }

        int valueStartIndex = keyIndex + keyPattern.length();

        // Skip whitespace
        while (valueStartIndex < json.length() && Character.isWhitespace(json.charAt(valueStartIndex))) {
            valueStartIndex++;
        }

        if (valueStartIndex >= json.length()) {
            return null;
        }

        char firstChar = json.charAt(valueStartIndex);

        // Handle string values (enclosed in quotes)
        if (firstChar == '"') {
            int valueEndIndex = json.indexOf('"', valueStartIndex + 1);
            if (valueEndIndex == -1) {
                return null;
            }
            return json.substring(valueStartIndex + 1, valueEndIndex);
        }

        // Handle numeric, boolean, or null values (not enclosed in quotes)
        int valueEndIndex = valueStartIndex;
        while (valueEndIndex < json.length()) {
            char c = json.charAt(valueEndIndex);
            if (c == ',' || c == '}' || Character.isWhitespace(c)) {
                break;
            }
            valueEndIndex++;
        }

        return json.substring(valueStartIndex, valueEndIndex);
    }

    public static Map<String, Object> prepareOrderData(
            int orderId, 
            String toName, 
            String toPhone, 
            String toAddress, 
            String toWardCode, 
            int toDistrictId,
            int weight,
            int length,
            int width,
            int height,
            int insuranceValue,
            int codAmount,
            int serviceTypeId,
            java.util.List<newmodel.OrderDetail> orderDetails) {

        Map<String, Object> orderData = new HashMap<>();

        // Required fields
        orderData.put("payment_type_id", 2); // 2 for COD
        orderData.put("note", "");
        orderData.put("required_note", "KHONGCHOXEMHANG"); // Do not allow inspection
        orderData.put("client_order_code", String.valueOf(orderId));

        // Sender information (from)
        orderData.put("from_name", "TinTest124");
        orderData.put("from_phone", "0987654321");
        orderData.put("from_address", "72 Thành Thái, Phường 14, Quận 10, Hồ Chí Minh, Vietnam");
        orderData.put("from_ward_name", "Phường 14");
        orderData.put("from_district_name", "Quận 10");
        orderData.put("from_province_name", "HCM");

        // Return information
        orderData.put("return_phone", "0332190444");
        orderData.put("return_address", "39 NTT");
        orderData.put("return_district_id", null);
        orderData.put("return_ward_code", "");

        // Recipient information (to)
        orderData.put("to_name", toName);
        orderData.put("to_phone", toPhone);
        orderData.put("to_address", toAddress);
        orderData.put("to_ward_code", toWardCode);
        orderData.put("to_district_id", toDistrictId);

        // Package information
        orderData.put("cod_amount", codAmount);
        orderData.put("weight", weight);
        orderData.put("length", length);
        orderData.put("width", width);
        orderData.put("height", height);
        orderData.put("insurance_value", insuranceValue);
        orderData.put("service_type_id", serviceTypeId);

        // Create items array
        java.util.List<Map<String, Object>> itemsList = new java.util.ArrayList<>();

        // If no order details, add a default item
        if (orderDetails == null || orderDetails.isEmpty()) {
            Map<String, Object> defaultItem = new HashMap<>();
            defaultItem.put("name", "Order Items");
            defaultItem.put("code", "ITEM" + orderId);
            defaultItem.put("quantity", 1);
            defaultItem.put("price", codAmount);
            defaultItem.put("length", 10);
            defaultItem.put("width", 10);
            defaultItem.put("height", 10);
            defaultItem.put("weight", 500);

            Map<String, String> category = new HashMap<>();
            category.put("level1", "Goods");
            defaultItem.put("category", category);

            itemsList.add(defaultItem);
        } else {
            // Add each order detail as an item
            for (newmodel.OrderDetail detail : orderDetails) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", detail.getProductName());
                item.put("code", "ITEM" + detail.getId());
                item.put("quantity", detail.getQuantity());
                item.put("price", detail.getPrice().intValue());
                item.put("length", 10); // Default values
                item.put("width", 10);
                item.put("height", 10);
                item.put("weight", 100 * detail.getQuantity()); // Estimate weight based on quantity

                Map<String, String> category = new HashMap<>();
                category.put("level1", "Goods");
                item.put("category", category);

                itemsList.add(item);
            }
        }

        orderData.put("items", itemsList);

        return orderData;
    }
}
