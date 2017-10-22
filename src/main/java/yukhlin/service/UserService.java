package yukhlin.service;

import yukhlin.model.User;

import java.util.List;

/**
 * The interface which allows distinguishing main methods for storing and updating data.
 * Created for making the app more flexible
 * (e.g. for adding another mechanism of storing data - database(JDBC, Hibernate, etc.))
 * Methods are described in {@link UserServiceXMLImpl} implementation.
 * @author Yukhlin
 */
public interface UserService {
    User getUser(String login, String password) throws Exception;
    boolean isExistingUser(String login) throws Exception;
    void addNewUser(User user) throws Exception;
    List<User> getUsers() throws Exception;
    void updateUser(User user) throws Exception;
}
