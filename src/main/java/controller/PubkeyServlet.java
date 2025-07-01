package controller;

import newdao.PubkeyDAO;
import newdao.UserDAO;
import newmodel.Pubkey;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet for handling public key registration and management.
 */
@WebServlet("/pubkey")
@MultipartConfig
public class PubkeyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(PubkeyServlet.class.getName());
    private final UserDAO userDAO = new UserDAO();
    private final PubkeyDAO pubkeyDAO = new PubkeyDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get user ID from session
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        if (username == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = userDAO.getUserIdByUsername(username);

        // Fetch user object to check if user is logged in through Google
        newmodel.User user = userDAO.getUserByUsername(username);
        request.setAttribute("user", user);

        // Check if user has a public key
        Pubkey pubkey = pubkeyDAO.getPubkeyByUserId(userId);
        request.setAttribute("pubkey", pubkey);

        // Forward to the pubkey management page
        request.getRequestDispatcher("pubkey.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get user ID from session
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        if (username == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = userDAO.getUserIdByUsername(username);

        // Get the action parameter
        String action = request.getParameter("action");

        if ("upload".equals(action)) {
            // Check if user already has an available key
            Pubkey existingPubkey = pubkeyDAO.getPubkeyByUserId(userId);
            if (existingPubkey != null && existingPubkey.isAvailable()) {
                request.setAttribute("error", "You already have an active public key. Please mark it as lost before uploading a new one.");
                doGet(request, response);
                return;
            }

            // Handle public key upload
            Part filePart = request.getPart("pubkeyFile");

            if (filePart == null) {
                request.setAttribute("error", "No file uploaded");
                doGet(request, response);
                return;
            }

            try (InputStream fileContent = filePart.getInputStream()) {
                // Read the file content
                byte[] pubkeyBytes = fileContent.readAllBytes();

                // Validate the public key
                if (!isValidRSAPublicKey(pubkeyBytes)) {
                    request.setAttribute("error", "Invalid RSA public key file");
                    doGet(request, response);
                    return;
                }

                // Extract and decode the Base64 content
                byte[] decodedKey = extractAndDecodePublicKey(pubkeyBytes);
                if (decodedKey == null) {
                    request.setAttribute("error", "Failed to extract public key data");
                    doGet(request, response);
                    return;
                }

                // Check if this key was previously used and marked as unavailable
                if (existingPubkey != null && !existingPubkey.isAvailable() && 
                    java.util.Arrays.equals(existingPubkey.getPubkey(), decodedKey)) {
                    request.setAttribute("error", "This key has been marked as lost. Please generate a new key pair.");
                    doGet(request, response);
                    return;
                }

                // Always create a new public key row instead of updating existing one
                pubkeyDAO.createNewPubkey(userId, decodedKey);

                request.setAttribute("success", "Public key uploaded successfully");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error uploading public key", e);
                request.setAttribute("error", "Error uploading public key: " + e.getMessage());
            }
        } else if ("lost".equals(action)) {
            // Handle marking a key as lost
            try {
                Pubkey pubkey = pubkeyDAO.getPubkeyByUserId(userId);
                if (pubkey != null && pubkey.isAvailable()) {
                    pubkey.setAvailable(false);
                    pubkeyDAO.updatePubkey(pubkey);
                    request.setAttribute("success", "Your key has been marked as lost. Please upload a new key.");
                } else if (pubkey != null && !pubkey.isAvailable()) {
                    request.setAttribute("error", "This key is already marked as lost.");
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error marking key as lost", e);
                request.setAttribute("error", "Error marking key as lost: " + e.getMessage());
            }
        }

        // Redirect back to the pubkey management page
        doGet(request, response);
    }

    /**
     * Validates that the provided bytes represent a valid RSA public key in PEM format.
     * 
     * @param keyBytes The bytes to validate
     * @return true if the bytes represent a valid RSA public key, false otherwise
     */
    private boolean isValidRSAPublicKey(byte[] keyBytes) {
        try {
            // Convert bytes to string
            String pemContent = new String(keyBytes, StandardCharsets.UTF_8);

            // Check if it's a PEM format public key
            if (!pemContent.contains("-----BEGIN PUBLIC KEY-----")) {
                LOGGER.warning("Not a PEM format public key");
                return false;
            }

            // Check if it's a private key
            if (pemContent.contains("-----BEGIN PRIVATE KEY-----") || 
                pemContent.contains("-----BEGIN RSA PRIVATE KEY-----")) {
                LOGGER.warning("Private key detected, not allowed");
                return false;
            }

            // Extract the Base64-encoded content
            String base64Content = pemContent
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

            // Decode the Base64 content
            byte[] decodedKey = Base64.getDecoder().decode(base64Content);

            // Validate the key
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            // Check if the key is RSA and has the correct length (1024 bits)
            return "RSA".equals(publicKey.getAlgorithm()) && 
                   publicKey.getEncoded().length * 8 >= 1024;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Invalid public key", e);
            return false;
        }
    }

    /**
     * Extracts and decodes the Base64 content from a PEM format public key.
     * 
     * @param keyBytes The bytes of the PEM format public key
     * @return The decoded public key bytes, or null if extraction fails
     */
    private byte[] extractAndDecodePublicKey(byte[] keyBytes) {
        try {
            // Convert bytes to string
            String pemContent = new String(keyBytes, StandardCharsets.UTF_8);

            // Check if it's a PEM format public key
            if (!pemContent.contains("-----BEGIN PUBLIC KEY-----") || 
                !pemContent.contains("-----END PUBLIC KEY-----")) {
                return null;
            }

            // Extract the Base64-encoded content
            String base64Content = pemContent
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

            // Decode the Base64 content
            return Base64.getDecoder().decode(base64Content);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to extract public key", e);
            return null;
        }
    }
}
