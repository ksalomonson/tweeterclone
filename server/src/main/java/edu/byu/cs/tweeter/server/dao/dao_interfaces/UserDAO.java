package edu.byu.cs.tweeter.server.dao.dao_interfaces;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.dto.UserDTO;

public interface UserDAO extends StringPartitionDAO<UserDTO> {

    void updateUser (UserDTO user) throws DataAccessException;

    String getPassword(String userAlias) throws DataAccessException;

    void addUserBatch(List<UserDTO> users);


}
