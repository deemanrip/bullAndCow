package yukhlin.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entity for containing of user's statistics.
 * Annotated for storing in xml file. Implements {@link Comparable} interface
 * for sorting and displaying instances of {@link User} class
 * @author Yukhlin
 */
@XmlRootElement(name = "statistics")
public class Statistics implements Comparable<Statistics> {
    private int attempts;
    private int games;

    public Statistics() {
    }

    public Statistics(int attempts, int games) {
        this.attempts = attempts;
        this.games = games;
    }

    /**
     * Sorts by average number of attempts in ascending order.
     * Sorts users with 0 games in the bottom of the list.
     * @param o
     * @return int
     */
    @Override
    public int compareTo(Statistics o) {
        if (this.games == 0)
            return 1;
        if (o.games == 0)
            return -1;
        return getAverageNumberOfAttempts() - o.getAverageNumberOfAttempts();
    }

    public int getAttempts() {
        return attempts;
    }

    @XmlElement
    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public int getGames() {
        return games;
    }

    @XmlElement
    public void setGames(int games) {
        this.games = games;
    }

    /**
     * Calculates average numbers of attempts per one game and rounds this value.
     * @return int
     */
    public int getAverageNumberOfAttempts() {
        if (games == 0)
            return 0;
        double result = (double) attempts / games;
        return (int) Math.round(result);
    }

    /**
     * Increments number of games of the user
     */
    public void incrementGamesValue() {
        this.games++;
    }

    /**
     * Increments number of attempts of the user
     */
    public void incrementAttemptsValue() {
        this.attempts++;
    }
}