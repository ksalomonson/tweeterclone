package edu.byu.cs.tweeter.server.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.dao_interfaces.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.dao_interfaces.DAOFactory;
import edu.byu.cs.tweeter.server.dao.dao_interfaces.ImageDAO;
import edu.byu.cs.tweeter.server.dao.dao_interfaces.UserDAO;
import edu.byu.cs.tweeter.server.dao.dto.AuthTokenDTO;
import edu.byu.cs.tweeter.server.dao.dto.UserDTO;



public class UserService extends BaseService {


    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe

    private static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    /**
     * Logs in user, sets valid authtoken. Calls hash password
     * @param request
     * @param daoFactory
     * @return
     */
    public LoginResponse login(LoginRequest request, DAOFactory daoFactory) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        UserDAO userDAO = daoFactory.makeUserDao();
        AuthTokenDAO authTokenDAO = daoFactory.makeAuthTokenDao();
        if(!userDAO.isInDatabase(request.getUsername())){
            return new LoginResponse("\nincorrect password");
        }
        User user;
        AuthToken authToken = new AuthToken(generateNewToken(),System.currentTimeMillis());
        AuthTokenDTO authTokenDTO = new AuthTokenDTO(request.getUsername(),authToken.token
                ,authToken.timestamp);
        try {
            String givenPasswordHashed = hashPassword(request.getPassword());
            String actualPasswordHashed = userDAO.getPassword(request.getUsername());
            if(givenPasswordHashed.equals(actualPasswordHashed)){
                user = convertUserDTO(userDAO.getItem(request.getUsername()));
                authTokenDAO.addItem(authTokenDTO,request.getUsername());
            } else {
                return new LoginResponse("\nincorrect password");
            }
        }catch (DataAccessException ex){
            System.out.println(ex.getMessage());
            return new LoginResponse("\n sorry there is an internal issue");
        }


        // put authToken in database when login is ready
        return new LoginResponse(user, authToken);
    }

    /**
     * Registers a new user. Hashes password, sets valid authtoken
     * @param request
     * @param daoFactory
     * @return
     */
    public RegisterResponse register(RegisterRequest request, DAOFactory daoFactory) {
        if (request.getUsername() == null) {
            throw new RuntimeException("[Bad Request] Missing username attribute");
        }
        if (request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing password attribute");
        }
        if (request.getFirstName() == null) {
            throw new RuntimeException("[Bad Request] Missing first name attribute");
        }
        if (request.getLastName() == null) {
            throw new RuntimeException("[Bad Request] Missing last name attribute");
        }
        if (request.getImage() == null) {
            throw new RuntimeException("[Bad Request] Missing image attribute");
        }

        UserDAO userDAO = daoFactory.makeUserDao();
        ImageDAO imageDAO = daoFactory.makeImageDao();
        AuthTokenDAO authTokenDAO = daoFactory.makeAuthTokenDao();

        String hashedPassword = hashPassword(request.getPassword());

        UserDTO userDTO = new UserDTO(request.getFirstName(),request.getLastName(),
                request.getUsername(), imageDAO.uploadImage(request.getImage(),request.getUsername()),
                hashedPassword,0,0);
        User user = convertUserDTO(userDTO);
        AuthToken authToken = new AuthToken(generateNewToken(),System.currentTimeMillis());
        try{
            userDAO.addItem(userDTO,userDTO.getUserAlias());
            AuthTokenDTO authTokenDTO = new AuthTokenDTO(request.getUsername(),authToken.token
                    ,authToken.timestamp);
            authTokenDAO.addItem(authTokenDTO,authTokenDTO.getUserAlias());
        } catch (DataAccessException ex){
            System.out.println(ex.getMessage());
            return new RegisterResponse("\n" + ex.getMessage() + "\nPlease try again");
        }

        return new RegisterResponse(user, authToken);
    }

    /**
     * Gets a user for the purpose of changing the displayed user in the user activity
     * @param request
     * @param daoFactory
     * @return
     */
    public UserResponse getUser(UserRequest request, DAOFactory daoFactory){
        if(request.getTargetUserAlias() == null){
            throw new RuntimeException("Missing user alias");
        }
        AuthToken updatedAuthToken = authenticate(request.getAuthToken(),daoFactory);
        UserDAO userDAO = daoFactory.makeUserDao();
        UserDTO userDTO = null;
        try{
            userDTO = userDAO.getItem(request.getTargetUserAlias());
        }catch (DataAccessException ex){
            System.out.println(ex.getMessage());
            throw new RuntimeException("There is a problem with getting user's profile");
        }

        return new UserResponse(convertUserDTO(userDTO),updatedAuthToken);
    }

    /**
     * Deletes the authtoken
     * @param request
     * @param daoFactory
     * @return
     */
    public LogoutResponse logout(LogoutRequest request,DAOFactory daoFactory){

        try {
            AuthTokenDAO authTokenDAO = daoFactory.makeAuthTokenDao();
            authTokenDAO.deleteItem(request.getAuthToken().token);
        }catch (DataAccessException ex){
            System.out.println(ex.getMessage());
            System.out.println("Problem with deleting authtoken on logout");
            throw new RuntimeException(ex.getMessage());
        }

        return new LogoutResponse();
    }

    private static String hashPassword(String passwordToHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "FAILED TO HASH";
    }


}
