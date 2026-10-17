
public class SkillExchange {
    private int exchangeId;
    private int requesterId;
    private int receiverId;
    private String skillName;
    private String status;

    public SkillExchange(int requesterId, int receiverId, String skillName) {
        this.requesterId = requesterId;
        this.receiverId = receiverId;
        this.skillName = skillName;
        this.status = "Pending";
    }

    public int getExchangeId() {
        return exchangeId;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public String getSkillName() {
        return skillName;
    }

    public String getStatus() {
        return status;
    }

    public void setExchangeId(int exchangeId) {
        this.exchangeId = exchangeId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
