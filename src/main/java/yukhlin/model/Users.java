package yukhlin.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Entity for storing and managing the list of users. This entity is marshalled in xml file
 * Annotated for xml parser
 * @author Yukhlin
 */
@XmlRootElement(name = "users")
public class Users {
    /**
     * Contains the list of users
     */
    private List<User> users;

    public Users() {
    }

    public Users(List<User> users) {
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }

    @XmlElement(name = "user")
    public void setUsers(List<User> users) {
        this.users = users;
    }
}
