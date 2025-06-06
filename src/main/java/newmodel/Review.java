package newmodel;

/**
 * Model class for product reviews
 * Represents a review in the reviews table
 */
public class Review {
    private int id;
    private int uId;
    private int pId;
    private int rating;
    private String comment;
    
    // Additional fields for display purposes
    private String username;
    private String userAvatar;
    private String date;
    
    /**
     * Default constructor
     */
    public Review() {
    }
    
    /**
     * Constructor with all required fields
     * 
     * @param id The review ID
     * @param uId The user ID
     * @param pId The product ID
     * @param rating The rating (1-5)
     * @param comment The review comment
     */
    public Review(int id, int uId, int pId, int rating, String comment) {
        this.id = id;
        this.uId = uId;
        this.pId = pId;
        this.rating = rating;
        this.comment = comment;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUId() {
        return uId;
    }
    
    public void setUId(int uId) {
        this.uId = uId;
    }
    
    public int getPId() {
        return pId;
    }
    
    public void setPId(int pId) {
        this.pId = pId;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getUserAvatar() {
        return userAvatar;
    }
    
    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
}