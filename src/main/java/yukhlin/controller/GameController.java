package yukhlin.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import yukhlin.model.Statistics;
import yukhlin.model.User;
import yukhlin.service.UserService;

import java.util.*;

/**
 * Controller of the web app which takes users input and returns corresponding views.
 * This class is responsible for logging of users, creating new accounts, setting up the game.
 * @author Yukhlin
 */
@Controller
public class GameController {
    /**
     * This field is used for managing users using xml.
     * Annotated for auto injecting (using Spring) by the corresponding instance of the service.
     * @see yukhlin.service.UserServiceXMLImpl
     */
    @Autowired
    @Qualifier("UserServiceXMLImpl")
    private UserService userService;
    private User currentUser;
    private int randomNumber;
    /**
     * Contains the list of all user's attempts in the current game.
     */
    private List<String> previousAttempts;

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Returns start page of the app which contains fields for logging in and reference
     * for registration of the new account {@link GameController#createNewUser()}
     * @return the view of the start page
     */
    @RequestMapping(value = "/login.html", method = RequestMethod.GET)
    public ModelAndView userLogin() {
        ModelAndView modelAndView = new ModelAndView("loginPage");
        return modelAndView;
    }

    /**
     * Takes user's input (login and password) while the attempt to sign in.
     * Checks if the user exists in the file
     * and password matches this login using {@link yukhlin.service.UserServiceXMLImpl} instance.
     * @param login
     * @param password
     * @return null if the user doesn't exist or password doesn't match the login.
     * If validation is successful, sets current user and redirects to start page {@link GameController#startGame()}.
     * Otherwise, returns view for failed authorization.
     * If any exception was thrown, returns to the user the page with error message.
     */
    @RequestMapping(value = "/validateCredentials", method = RequestMethod.POST)
    public ModelAndView validateCredentials(@RequestParam("login") String login,
                                            @RequestParam("password") String password) {
        try {
            User user;
            user = userService.getUser(login, password);
            if (user == null)
                return new ModelAndView("failedAuthorization");
            else {
                setCurrentUser(user);
                return new ModelAndView("redirect:/startPage");
            }
        } catch (Exception e) {
            return new ModelAndView("errorPage");
        }
    }

    /**
     * @return the form for creation of the new account
     */
    @RequestMapping(value = "/createNewUser")
    public ModelAndView createNewUser() {
        return new ModelAndView("newUserForm");
    }

    /**
     * Validate credentials while the attempt to create the new user from {@link GameController#createNewUser()}.
     * @param login
     * @param password
     * @param confirmPassword
     * @return the page with error if password doesn't match confirm password field
     * or if the entered login already exists in xml file (ignores case).
     * Otherwise, redirects to start page {@link GameController#startGame()}.
     * If any exception was thrown, returns to the user the page with error message.
     */
    @RequestMapping(value = "/createNewUser", method = RequestMethod.POST)
    public ModelAndView validateCredentialsAndCreate(@RequestParam("login") String login,
                                                     @RequestParam("password") String password,
                                                     @RequestParam("confirmPassword") String confirmPassword) {
        try {
            if (userService.isExistingUser(login)) {
                ModelAndView modelAndView = new ModelAndView("createAccountError");
                modelAndView.addObject("login", login);
                return modelAndView;
            }
            if (!password.equals(confirmPassword))
                return new ModelAndView("incorrectPassword");
            User user = new User(login, password, new Statistics(0,0));
            userService.addNewUser(user);
            setCurrentUser(user);
            return new ModelAndView("redirect:/startPage");
        } catch (Exception e) {
            return new ModelAndView("errorPage");
        }
    }

    /**
     * Forms and sorts (for more information regarding sorting see {@link Statistics}) the list of users for displaying
     * the rating.
     * Also, contains the reference for starting the game.
     * If {@link GameController#currentUser} is null, redirects to {@link GameController#userLogin()}
     * If any exception was thrown, returns to the user the page with error message.
     * @return start page with statistics and reference.
     */
    @RequestMapping(value = "/startPage")
    public ModelAndView startPage() {
        try {
            if (currentUser == null)
                return new ModelAndView("redirect:/login.html");
            ModelAndView modelAndView = new ModelAndView("startPage");
            List<User> userList = userService.getUsers();
            Collections.sort(userList);
            modelAndView.addObject("usersList", userList);
            return modelAndView;
        } catch (Exception e) {
            return new ModelAndView("errorPage");
        }
    }

    /**
     * Invokes {@link GameController#initialSetUp()} for initial setting the game.
     * If {@link GameController#currentUser} is null, redirects to {@link GameController#userLogin()}
     * Selecting of the number implemented as dropdown list.
     * If any exception was thrown, returns to the user the page with error message.
     * @return the page with form of selecting the number.
     */
    @RequestMapping(value = "/startGame")
    public ModelAndView startGame() {
        try {
            if (currentUser == null)
                return new ModelAndView("redirect:/login.html");
            initialSetUp();
            return new ModelAndView("startGame");
        } catch (Exception e) {
            return new ModelAndView("errorPage");
        }
    }

    /**
     * Validates the selected number of the user.
     * Calculates number of bulls {@link GameController#calculateBulls(Collection)} and
     * number of cows {@link GameController#calculateCows(Collection)}.
     * Passes values from Map (digits selected by the user)
     * of received parameters to {@link GameController#calculateCows(Collection)}
     * and {@link GameController#calculateBulls(Collection)}
     * @param digits {@link Map}, which contains four digits, which user has selected from dropdown list.
     * @return if the user didn't guess the number, returns the page with
     * updated information (current result, previous attempts).
     * Increments the number of user's attempts. If the user guessed the number (bulls == 4),
     * returns page with congratulations.
     * Adds current attempt (selected number and number of bulls and cows) to {@link GameController#previousAttempts}.
     * If any exception was thrown, returns to the user the page with error message.
     * If {@link GameController#currentUser} is null, redirects to {@link GameController#userLogin()}
     */
    @RequestMapping(value = "/validateNumber")
    public ModelAndView validateNumber(@RequestParam Map<String, String> digits) {
        try {
            if (currentUser == null)
                return new ModelAndView("redirect:/login.html");
            incrementUserAttempts();
            int bulls = calculateBulls(digits.values());
            if (bulls == 4) {
                ModelAndView modelAndView = new ModelAndView("congratulationsPage");
                modelAndView.addObject("number", randomNumber);
                return modelAndView;
            }
            ModelAndView modelAndView = new ModelAndView("resultOfAttempt");
            String number = concatResult(digits);
            int cows = calculateCows(digits.values());
            previousAttempts.add(number + " " + bulls + "B" + cows + "C");
            modelAndView.addObject("previousAttempts", previousAttempts);
            modelAndView.addObject("cows", cows);
            modelAndView.addObject("bulls", bulls);
            return modelAndView;
        } catch (Exception e) {
            return new ModelAndView("errorPage");
        }
    }

    /**
     * Game setup.
     * Generates random number {@link GameController#generateNumber()}
     * Initialize {@link GameController#previousAttempts}.
     * Increments amount of user's games {@link GameController#incrementUserGames()}.
     * @throws Exception
     */
    private void initialSetUp() throws Exception {
        generateNumber();
        initializePreviousAttemptsList();
        incrementUserGames();
    }

    /**
     * Service method which generates random number with four digits using {@link Math#random()}
     * and initialize {@link GameController#randomNumber} with this value.
     */
    private void generateNumber() {
        randomNumber = (int)(Math.random()*9000)+1000;
    }

    private void initializePreviousAttemptsList() {
        previousAttempts = new ArrayList<>();
    }

    /**
     * Increments number of games of current user and updates xml file with this information.
     * @throws Exception
     */
    private void incrementUserGames() throws Exception {
        currentUser.incrementGamesValue();
        userService.updateUser(currentUser);
    }

    /**
     * Increments number of attempts of current user and updates xml file with this information.
     * @throws Exception
     */
    private void incrementUserAttempts() throws Exception {
        currentUser.incrementAttemptsValue();
        userService.updateUser(currentUser);
    }

    /**
     * Service method which concatenates the number (selected by user) from passed {@link Map}
     * @param digits {@link Map} - four digits selected by the user (keys are names of variables).
     * @return String value of the number selected by the user.
     */
    private String concatResult(Map<String, String> digits) {
        StringBuilder builder = new StringBuilder();
        for (String digit : digits.values())
            builder.append(digit);
        return builder.toString();
    }

    /**
     * Calculates the number of cows (digits without considering the position).
     * Converts taken collection to array.
     * Converts int representation of generated number to String representation.
     * Compares each digit from users input (converted array) with generated number:
     * if digit from user's input doesn't equal to digit from generated number
     * but generated number contains this digit - count the cow.
     * @param digits values from Map (digits selected by user)
     * @return number of cows based on digits selected by the user
     */
    private int calculateCows(Collection<String> digits) {
        int result = 0;
        String[] arrayDigits = new String[4];
        digits.toArray(arrayDigits);
        String number = String.valueOf(randomNumber);
        for (int i = 0; i < number.length(); i++) {
            if (!arrayDigits[i].equals(Character.toString(number.charAt(i))) && number.contains(arrayDigits[i]))
                result++;
        }
        return result;
    }

    /**
     * Calculates the number of bulls (digits considering the position).
     * Converts taken collection to array.
     * Converts int representation of generated number to String representation.
     * Compares each digit from users input (converted array) with generated number:
     * if digit from user's input equals to digit from generated number - count the bull.
     * @param digits values from Map (digits selected by user)
     * @return number of bulls based on digits selected by the user
     */
    private int calculateBulls(Collection<String> digits) {
        int result = 0;
        String[] arrayDigits = new String[4];
        digits.toArray(arrayDigits);
        String number = String.valueOf(randomNumber);
        for (int i = 0; i < number.length(); i++) {
            if (arrayDigits[i].equals(Character.toString(number.charAt(i))))
                result++;
        }
        return result;
    }
}