package yukhlin.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User entity. Annotated for storing in xml file. Implements {@link Comparable} interface
 * for sorting and displaying in needed order
 * @author Yukhlin
 */
@XmlRootElement(name = "user")
public class User implements Comparable<User> {
    private String login;
    private String password;
    /**
     * Contains all needed statistics of this user
     */
    private Statistics statistics;

    public User() {
    }

    public User(String login, String password, Statistics statistics) {
        this.login = login;
        this.password = password;
        this.statistics = statistics;
    }


    /**
     * Consolidates comparing functionality to {@link Statistics} class
     * @param o
     * @return int
     */
    @Override
    public int compareTo(User o) {
        return getStatistics().compareTo(o.getStatistics());
    }

    @XmlElement
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @XmlElement
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @XmlElement
    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    /**
     * Increments number of the games
     */
    public void incrementGamesValue() {
        this.statistics.incrementGamesValue();
    }

    /**
     * Increments number of the attempts
     */
    public void incrementAttemptsValue() {
        this.statistics.incrementAttemptsValue();
    }
}