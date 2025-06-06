package util;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import newmodel.Order;
import newmodel.OrderDetail;
import newmodel.Shipping;

/**
 * Service class for creating GHN shipping orders
 * Integrates with the GHN API to create shipping orders for the application
 */
public class GHNOrderService {
    private static final Logger LOGGER = Logger.getLogger(GHNOrderService.class.getName());

    // Default values for shipping parameters
    private static final int DEFAULT_WEIGHT = 500; // 500g
    private static final int DEFAULT_LENGTH = 20; // 20cm
    private static final int DEFAULT_WIDTH = 20; // 20cm
    private static final int DEFAULT_HEIGHT = 10; // 10cm
    private static final int DEFAULT_SERVICE_TYPE_ID = 2; // Standard service

    /**
     * Creates a GHN shipping order for the given order
     * 
     * @param order The order to create a shipping order for
     * @param shipping The shipping information
     * @param recipientName The recipient's name
     * @param recipientPhone The recipient's phone number
     * @param recipientAddress The recipient's address
     * @param wardCode The recipient's ward code
     * @param districtId The recipient's district ID
     * @return The GHN order code if successful, null otherwise
     * @throws GHNApiException if the API returns an error response (4xx or 5xx)
     */
    public static String createGHNOrder(
            Order order, 
            Shipping shipping, 
            String recipientName, 
            String recipientPhone, 
            String recipientAddress, 
            String wardCode, 
            int districtId) throws GHNApiException {

        try {
            // Calculate total weight and value for insurance
            int totalWeight = calculateTotalWeight(order);
            int insuranceValue = calculateInsuranceValue(order.getTotal());
            int codAmount = calculateCODAmount(order.getTotal());

            // Prepare order data
            Map<String, Object> orderData = GHNApiUtil.prepareOrderData(
                    order.getId(),
                    recipientName,
                    recipientPhone,
                    recipientAddress,
                    wardCode,
                    districtId,
                    totalWeight,
                    DEFAULT_LENGTH,
                    DEFAULT_WIDTH,
                    DEFAULT_HEIGHT,
                    insuranceValue,
                    codAmount,
                    DEFAULT_SERVICE_TYPE_ID,
                    order.getOrderDetails()
            );

            // Create order with GHN API
            String response = GHNApiUtil.createOrder(orderData);

            // Parse order code from response
            String orderCode = parseOrderCodeFromResponse(response);

            // Update shipping status if order was created successfully
            if (orderCode != null) {
                // Here you would update the shipping record with the GHN order code
                // This would require adding a field to the Shipping model and database
                LOGGER.info("GHN order created successfully with code: " + orderCode);
            }

            return orderCode;
        } catch (GHNApiException e) {
            // Log the error and rethrow it to be handled by the servlet
            LOGGER.log(Level.SEVERE, "GHN API error: " + e.getMessage() + ", Status code: " + e.getStatusCode(), e);
            throw e;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error creating GHN order", e);
            return null;
        }
    }

    /**
     * Calculates the total weight of an order
     * 
     * @param order The order to calculate weight for
     * @return The total weight in grams
     */
    private static int calculateTotalWeight(Order order) {
        // In a real implementation, you would calculate based on products
        // For now, we'll use a default weight per item
        int itemCount = 0;
        for (OrderDetail detail : order.getOrderDetails()) {
            itemCount += detail.getQuantity();
        }

        return Math.max(DEFAULT_WEIGHT, itemCount * 100); // At least 500g, 100g per item
    }

    /**
     * Calculates the insurance value for an order
     * 
     * @param total The order total
     * @return The insurance value in VND
     */
    private static int calculateInsuranceValue(BigDecimal total) {
        // Convert BigDecimal to int, rounding down
        return total.intValue();
    }

    /**
     * Calculates the COD amount for an order
     * 
     * @param total The order total
     * @return The COD amount in VND
     */
    private static int calculateCODAmount(BigDecimal total) {
        // For COD payments, the COD amount is the order total
        // For prepaid orders, it would be 0
        return total.intValue();
    }

    /**
     * Parses the order code from the GHN API response
     * 
     * @param response The API response as a JSON string
     * @return The order code if found, null otherwise
     */
    private static String parseOrderCodeFromResponse(String response) {
        // Simple parsing of the response to extract the order code
        // In a real implementation, you would use a proper JSON parser
        if (response != null && response.contains("\"order_code\":")) {
            int startIndex = response.indexOf("\"order_code\":") + 13;
            int endIndex = response.indexOf("\"", startIndex);
            if (startIndex > 0 && endIndex > startIndex) {
                return response.substring(startIndex, endIndex);
            }
        }
        return null;
    }
}
