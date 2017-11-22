import java.io.Serializable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vishan on 11/20/17.
 */

/**
 * Class to create a serializable event object with correpsonding event methods
 */
public class Event implements Serializable {

    private static final long serialVersionUID = -517246911231232341L;
    private String eventTitle;
    private String eventDate;
    private String eventStartTime;
    private String eventEndTime;

    /**
     * Constructor for serializable event object
     *
     * @param eventTitle
     * @param eventDate
     * @param eventStartTime
     * @param eventEndTime
     */
    public Event(String eventTitle, String eventDate, String eventStartTime, String eventEndTime) {
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;

    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(String eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public String getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(String eventEndTime) {
        this.eventEndTime = eventEndTime;
    }

        /**
         * Returns event object as a string
         * @return
         */
    public String toString() {
        String eventString;
        if (eventEndTime.equals("")) {
            eventString = eventStartTime + ": " + eventTitle;
            return eventString;
        }
        eventString = eventStartTime + "-" + eventEndTime + ": " + eventTitle;
        return eventString;
    }

}

