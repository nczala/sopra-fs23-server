package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LobbyService {

    private final LobbyRepository lobbyRepository;


    @Autowired
    public LobbyService(@Qualifier("lobbyRepository") LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    public List<Lobby> getLobbies() {
        return this.lobbyRepository.findAll();
    }

    public Lobby createLobby(Lobby newLobby) {
        newLobby.setLobbyName(newLobby.getLobbyName());

        newLobby = lobbyRepository.save(newLobby);
        lobbyRepository.flush();

        return newLobby;
    }



}
