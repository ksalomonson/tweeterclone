package edu.byu.cs.tweeter.server.dao.dao_interfaces;

import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.dto.AuthTokenDTO;

public interface AuthTokenDAO extends StringPartitionDAO<AuthTokenDTO> {
    /**
     * Takes in a token and updates it in the database
     * @param token
     * @throws DataAccessException
     */
    void update(AuthTokenDTO token) throws DataAccessException;
}
