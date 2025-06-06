package controller;

import util.VnPayUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "VnPayIpnServlet", urlPatterns = {"/vnpay_ipn"})
public class VnPayIpnServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set response content type
        response.setContentType("application/json;charset=UTF-8");

        // Create a map to store all parameters
        Map<String, String> vnp_Params = new HashMap<>();

        // Get all parameters from the request
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            if (paramValue != null && !paramValue.isEmpty()) {
                vnp_Params.put(paramName, paramValue);
            }
        }

        // Remove vnp_SecureHash from the map to verify the checksum
        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (vnp_SecureHash != null) {
            vnp_Params.remove("vnp_SecureHash");

            // Remove vnp_SecureHashType if present
            if (vnp_Params.containsKey("vnp_SecureHashType")) {
                vnp_Params.remove("vnp_SecureHashType");
            }

            // Verify the checksum
            String signValue = VnPayUtil.hashAllFields(vnp_Params);

            PrintWriter out = response.getWriter();

            if (signValue.equals(vnp_SecureHash)) {
                // Valid signature
                String vnp_ResponseCode = vnp_Params.get("vnp_ResponseCode");
                String vnp_TxnRef = vnp_Params.get("vnp_TxnRef");
                String vnp_TransactionNo = vnp_Params.get("vnp_TransactionNo");

                // Check if payment is successful
                if ("00".equals(vnp_ResponseCode)) {
                    // Payment successful
                    // Check if this is a temporary reference (TEMP_timestamp)
                    if (vnp_TxnRef.startsWith("TEMP_")) {
                        // This is a temporary reference, the order will be created by VnPayReturnServlet
                        // No need to update anything here
                    } else {
                        // This is a regular order ID reference
                        String[] txnRefParts = vnp_TxnRef.split("_");
                        int orderId = -1;
                        if (txnRefParts.length > 0) {
                            try {
                                orderId = Integer.parseInt(txnRefParts[0]);
                                // Update order payment status (1 = paid)
                                newdao.OrderDAO orderDAO = new newdao.OrderDAO();
                                orderDAO.updatePaymentStatus(orderId, 1);
                            } catch (NumberFormatException e) {
                                // Invalid order ID format
                            }
                        }
                    }

                    // Return success response to VnPay
                    String jsonResponse = "{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}";
                    out.print(jsonResponse);
                } else {
                    // Payment failed
                    // Check if this is a temporary reference (TEMP_timestamp)
                    if (vnp_TxnRef.startsWith("TEMP_")) {
                        // This is a temporary reference, no order was created yet
                        // No need to update anything here
                    } else {
                        // This is a regular order ID reference
                        String[] txnRefParts = vnp_TxnRef.split("_");
                        int orderId = -1;
                        if (txnRefParts.length > 0) {
                            try {
                                orderId = Integer.parseInt(txnRefParts[0]);
                                // Update order payment status (-1 = failed)
                                newdao.OrderDAO orderDAO = new newdao.OrderDAO();
                                orderDAO.updatePaymentStatus(orderId, -1);
                            } catch (NumberFormatException e) {
                                // Invalid order ID format
                            }
                        }
                    }

                    // Return success response to VnPay (we still acknowledge receipt)
                    String jsonResponse = "{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}";
                    out.print(jsonResponse);
                }
            } else {
                // Invalid signature
                String jsonResponse = "{\"RspCode\":\"97\",\"Message\":\"Invalid Signature\"}";
                out.print(jsonResponse);
            }

            out.flush();
        } else {
            // No secure hash found
            PrintWriter out = response.getWriter();
            String jsonResponse = "{\"RspCode\":\"99\",\"Message\":\"Invalid Request\"}";
            out.print(jsonResponse);
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
