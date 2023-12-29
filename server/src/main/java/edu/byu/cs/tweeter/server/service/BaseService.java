package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.dao_interfaces.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.dao_interfaces.DAOFactory;
import edu.byu.cs.tweeter.server.dao.dto.AuthTokenDTO;
import edu.byu.cs.tweeter.server.dao.dto.UserDTO;

public class BaseService {

    /**
     * converts a UserDTO to a User so that it can be returned via a JSON object
     * @param userDTO
     * @return
     */
    protected User convertUserDTO(UserDTO userDTO) {
        User user = new User(userDTO.getFirstName(),userDTO.getLastName(),
                userDTO.getUserAlias(),userDTO.getImageUrl());
        return user;
    }

    /**
     * authenticates a user can perform the requested task, also renews authtoken.
     * @param authToken
     * @param daoFactory
     * @return
     */
    protected AuthToken authenticate(AuthToken authToken,DAOFactory daoFactory){
        AuthTokenDAO authTokenDAO = daoFactory.makeAuthTokenDao();
        try{
            AuthTokenDTO authTokenDTO = authTokenDAO.getItem(authToken.token);
            long difference = System.currentTimeMillis() - authTokenDTO.datetime;

            if(difference > 3600000){
                throw new RuntimeException("[Bad Request] Please login");
            }
            authTokenDTO.setDatetime(System.currentTimeMillis());
            System.out.println("About to update token");
            System.out.println("UserAlias that is getting queried "+ authTokenDTO.getUserAlias());
            authTokenDAO.update(authTokenDTO);
            System.out.println("Token updated");
        }catch (DataAccessException ex){
            System.out.println(ex.getMessage());
            throw new RuntimeException("Problem with authToken");
        }

        return authToken;
    }
}
