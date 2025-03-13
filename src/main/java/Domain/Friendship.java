package Domain;

import java.time.LocalDateTime;

public class Friendship {
    private Long Id1, Id2;
    private LocalDateTime friendFrom;
    private Boolean accepted = false;

    public Friendship(Long usr1, Long usr2, LocalDateTime friendFrom) {
        this.Id1 = usr1;
        this.Id2 = usr2;
        this.friendFrom = friendFrom;
    }

    public Long getId1() {
        return Id1;
    }

    public Long getId2() {
        return Id2;
    }

    public LocalDateTime getFriendFrom() {return friendFrom;}

    public Boolean getAccepted() {return accepted;}

    public void setId1(Long id1) {
        this.Id1 = id1;
    }

    public void setId2(Long id2) {
        this.Id2 = id2;
    }

    public void setFriendFrom(LocalDateTime friendFrom) {this.friendFrom = friendFrom;}

    public void setAccepted(Boolean accepted) {this.accepted = accepted;}

    @Override
    public String toString() {
        return "Friendship{" +
                "id2=" + Id1 +
                ", id1=" + Id2 +
                ", friendFrom=" + friendFrom +
                '}';
    }
}
