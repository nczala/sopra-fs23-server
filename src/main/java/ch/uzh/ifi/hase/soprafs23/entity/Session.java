package ch.uzh.ifi.hase.soprafs23.entity;

public class Session {
    private String username;
    private String password;
    private long userid;
    private String token;

    public Session(String username, String password, long userid, String token) {
        this.username = username;
        this.password = password;
        this.userid = userid;
        this.token = token;
    }

    // Needed for mapper
    public Session() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
