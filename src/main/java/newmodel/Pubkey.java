package newmodel;

import java.util.Arrays;

/**
 * Model class representing a public key entry in the database.
 * This class corresponds to the 'pubkey' table.
 */
public class Pubkey {
    private int id;
    private int userId;
    private byte[] pubkey;
    private boolean available;

    // Default constructor
    public Pubkey() {
    }

    // Constructor with all fields
    public Pubkey(int id, int userId, byte[] pubkey, boolean available) {
        this.id = id;
        this.userId = userId;
        this.pubkey = pubkey;
        this.available = available;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public byte[] getPubkey() {
        return pubkey;
    }

    public void setPubkey(byte[] pubkey) {
        this.pubkey = pubkey;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Pubkey{" +
                "id=" + id +
                ", userId=" + userId +
                ", pubkey=" + (pubkey != null ? Arrays.toString(pubkey) : "null") +
                ", available=" + available +
                '}';
    }
}