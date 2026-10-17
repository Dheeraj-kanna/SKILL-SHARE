
public class Review {
    private int reviewId;
    private int exchangeId;
    private int reviewerId;
    private int rating;
    private String comment;

    public Review(int exchangeId, int reviewerId, int rating, String comment) {
        this.exchangeId = exchangeId;
        this.reviewerId = reviewerId;
        this.rating = rating;
        this.comment = comment;
    }

    public int getReviewId() {
        return reviewId;
    }

    public int getExchangeId() {
        return exchangeId;
    }

    public int getReviewerId() {
        return reviewerId;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }
}
