package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.Session;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "birthday", target = "birthday", dateFormat = "dd.MM.yyyy")
    @Mapping(source = "creationDate", target = "creationDate", dateFormat = "dd.MM.yyyy")
    UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.birthday", target = "birthday", dateFormat = "dd.MM.yyyy")
    User convertUserPutDTOtoEntity(UserPutDTO user, long id);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    Session convertSessionPostDTOtoEntity(SessionPostDTO sessionPostDTO);

    @Mapping(source = "token", target = "token")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "userid", target = "userid")
    SessionGetDTO convertEntityToSessionGetDTO(Session session);
}
