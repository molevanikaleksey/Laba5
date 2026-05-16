package domain;

import java.time.Instant;

public class User {
    private Long UserId;
    private String login;
    private String password;
    private Instant createdAt;

    public User(String login, String password, Long UserId, Instant createdAt){
        this.login = login;
        this.password = password;
        this.UserId = UserId;
        this.createdAt = Instant.now();

    }

    public String getLogin() {
        return login;
    }
    public Long getUserId(){
        return UserId;
    }

    public void setUserId(Long userId) {
        UserId = userId;
    }

    public String getPassword() {
        return password;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
