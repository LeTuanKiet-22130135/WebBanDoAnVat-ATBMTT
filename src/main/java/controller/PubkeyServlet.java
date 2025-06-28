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
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
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

                // Check if user already has a public key
                if (pubkeyDAO.hasPubkey(userId)) {
                    // Update existing public key
                    Pubkey pubkey = pubkeyDAO.getPubkeyByUserId(userId);
                    pubkey.setPubkey(pubkeyBytes);
                    pubkey.setAvailable(true);
                    pubkeyDAO.updatePubkey(pubkey);
                } else {
                    // Create new public key
                    pubkeyDAO.createNewPubkey(userId, pubkeyBytes);
                }

                request.setAttribute("success", "Public key uploaded successfully");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error uploading public key", e);
                request.setAttribute("error", "Error uploading public key: " + e.getMessage());
            }
        } else if ("delete".equals(action)) {
            // Handle public key deletion
            try {
                pubkeyDAO.deletePubkey(userId);
                request.setAttribute("success", "Public key deleted successfully");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error deleting public key", e);
                request.setAttribute("error", "Error deleting public key: " + e.getMessage());
            }
        } else if ("toggle".equals(action)) {
            // Handle toggling public key availability
            try {
                Pubkey pubkey = pubkeyDAO.getPubkeyByUserId(userId);
                if (pubkey != null) {
                    pubkey.setAvailable(!pubkey.isAvailable());
                    pubkeyDAO.updatePubkey(pubkey);
                    String status = pubkey.isAvailable() ? "enabled" : "disabled";
                    request.setAttribute("success", "Public key " + status + " successfully");
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error toggling public key availability", e);
                request.setAttribute("error", "Error toggling public key availability: " + e.getMessage());
            }
        }

        // Redirect back to the pubkey management page
        doGet(request, response);
    }

    /**
     * Validates that the provided bytes represent a valid RSA public key.
     * 
     * @param keyBytes The bytes to validate
     * @return true if the bytes represent a valid RSA public key, false otherwise
     */
    private boolean isValidRSAPublicKey(byte[] keyBytes) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            // Check if the key is RSA and has the correct length (1024 bits)
            return "RSA".equals(publicKey.getAlgorithm()) && 
                   publicKey.getEncoded().length * 8 >= 1024;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Invalid public key", e);
            return false;
        }
    }
}
