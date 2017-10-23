package yukhlin.service;

import org.springframework.stereotype.Component;
import yukhlin.model.User;
import yukhlin.model.Users;

import javax.xml.bind.*;
import java.io.*;
import java.util.*;

/**
 * This web app stores users in xml file.
 * Implementation of {@link UserService}, used for storing and managing data (users) in Users.xml file
 * @author Yukhlin
 */
@Component("UserServiceXMLImpl")
public class UserServiceXMLImpl implements UserService {
    /**
     * Xml file with users. It is located in the directory of the project (resources folder).
     * If the file doesn't exist, it will be created in the project directory (resources folder).
     */
    public static final File USERS_FILE = new File("src\\main\\resources\\users\\Users.xml");
    private JAXBContext context;

    /**
     * As initialization of {@link UserServiceXMLImpl#context} can throw the exception, the setter has been implemented
     * for throwing the exception further and handling it in the controller
     * @throws JAXBException
     */
    public void setContext() throws JAXBException {
        if (context == null) {
            context = JAXBContext.newInstance(Users.class);
        }
    }

    /**
     * If the file doesn't exist it is considered that the user also doesn't exist.
     * Returns the user if the login was found in the file and the password matches this login in the file.
     * Otherwise, returns null. Checks login ignoring case.
     * @param login login of the user
     * @param password password of the user
     * @return {@link User} instance
     * @throws JAXBException
     */
    @Override
    public User getUser(String login, String password) throws JAXBException {
        if (!USERS_FILE.exists())
            return null;
        for (User user : getUsers()) {
            if (user.getLogin().equalsIgnoreCase(login) && user.getPassword().equals(password))
                return user;
        }
        return null;
    }

    /**
     * Checks if the user exists. If the file doesn't exist - user also doesn't exist.
     * If user wasn't found in the file, returns false. Checks login ignoring case.
     * @param login User login
     * @return boolean value
     * @throws JAXBException
     */
    @Override
    public boolean isExistingUser(String login) throws JAXBException {
        if (!USERS_FILE.exists())
            return false;
        for (User user : getUsers()) {
            if (user.getLogin().equalsIgnoreCase(login))
                return true;
        }
        return false;
    }

    /**
     * Adds new user to xml file. If the file doesn't exist, create the new file and add the user into this file.
     * If the file exists, method gets the list of all users from this file and adds new user to the list,
     * sets the list in {@link Users} instance and marshal it in xml file
     * @param user instance of {@link User}
     * @throws JAXBException
     * @throws IOException
     */
    @Override
    public void addNewUser(User user) throws JAXBException, IOException {
        Users users = new Users();
        if (!USERS_FILE.exists()) {
            USERS_FILE.getParentFile().mkdirs();
            List<User> userList = new ArrayList<>();
            userList.add(user);
            users.setUsers(userList);
        } else {
            List<User> userList = getUsers();
            userList.add(user);
            users.setUsers(userList);
        }
        marshal(users);
    }

    /**
     * Returns all users from xml file. Otherwise, returns mutable implementation of List {@link ArrayList}
     * @return {@link List} of users from the file
     * @throws JAXBException
     */
    @Override
    public List<User> getUsers() throws JAXBException {
        if (!USERS_FILE.exists())
            return new ArrayList<>();
        Users users = unmarshal();
        return users.getUsers();
    }

    /**
     * Accepts instance of the user and updates corresponding object in xml file according to actual state of this user.
     * It is considered that file already exists and there is no corresponding check.
     * @param user accepts instance of {@link User}
     * @throws JAXBException
     */
    @Override
    public void updateUser(User user) throws JAXBException {
        Users users = new Users();
        List<User> userList = getUsers();
        for (User fileUser : userList) {
            if (fileUser.getLogin().equalsIgnoreCase(user.getLogin()))
                fileUser.setStatistics(user.getStatistics());
        }
        users.setUsers(userList);
        marshal(users);
    }

    /**
     * Service method. Here were consolidated repetitive actions of marshalling
     * @param users {@link Users}
     * @throws JAXBException
     */
    private void marshal(Users users) throws JAXBException {
        setContext();
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(users, USERS_FILE);
    }

    /**
     * Service method. Here were consolidated repetitive actions of unmarshalling
     * @return {@link Users} instance
     * @throws JAXBException
     */
    private Users unmarshal() throws JAXBException {
        setContext();
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (Users) unmarshaller.unmarshal(USERS_FILE);
    }
}