package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class LobbyGetDTO {

    private Long id;
    private String lobbyName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }
}
