import java.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Created by Vishan on 11/18/17.
 * Class used to establish SimpleCalendar model
 */

public class Model {

    private int maximumDays;
    private int currentDate;
    private boolean changed = false;

    private HashMap<String, ArrayList<Event>> events = new HashMap<>();
    private ArrayList<ChangeListener> listeners = new ArrayList<>();
    private GregorianCalendar cal = new GregorianCalendar();

    /**
     * Constructor for calendar model
     */
    public Model(){
        maximumDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        currentDate = cal.get(Calendar.DATE);
        //load();

    }

    public int getMaximumDays() {
        return maximumDays;
    }

    public void setMaximumDays(int maximumDays) {
        this.maximumDays = maximumDays;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public HashMap<String, ArrayList<Event>> getEvents() {
        return events;
    }

    public void setEvents(HashMap<String, ArrayList<Event>> events) {
        this.events = events;
    }

    public ArrayList<ChangeListener> getListeners() {
        return listeners;
    }

    public void setListeners(ArrayList<ChangeListener> listeners) {
        this.listeners = listeners;
    }

    public GregorianCalendar getCal() {
        return cal;
    }

    public void setCal(GregorianCalendar cal) {
        this.cal = cal;
    }

    /**
     * Adds listeners to an ArrayList
     * @param lis the listener to add
     */
    public void addListener(ChangeListener lis){
        listeners.add(lis);
    }

    /**
     * Updates ChangeListener array
     */
    public void updateListeners(){
        for(ChangeListener lis : listeners){
            lis.stateChanged(new ChangeEvent(this));
        }
    }


    /**
     * Setter method for current date
     */
    public void setCurrentDate(int date){ currentDate = date; }

    public int getCurrentDate(){ return currentDate; }

    /**
     * Getter method for current month
     * @return month the current month
     */
    public int getCurrentMonth(){
        return cal.get(Calendar.MONTH);
    }

    /**
     * Getter method for current year
     * @return year the current year
     */
    public int getCurrentYear(){
        return cal.get(Calendar.YEAR);
    }

    /**
     * Gets the max days in a month
     * @return the max days in a month
     */
    public int getMax(){
        return maximumDays;
    }

    public boolean changed(){
        return changed;
    }

    /**
     * Moves month to previous index
     */
    public void previousMonth(){
        cal.add(Calendar.MONTH, -1);
        maximumDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        changed = true;
        updateListeners();
    }

    /**
     * Moves month to next index
     */
    public void nextMonth(){
        cal.add(Calendar.MONTH, 1);
        maximumDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        changed = true;
        updateListeners();
    }

    /**
     * Returns int value of the day of the week
     * @param index the index of the day of the month
     * @return weekDay the int representing the day of the week
     */
    public int getWeekDay(int index){
        cal.set(Calendar.DAY_OF_MONTH, index);
        return cal.get(Calendar.DAY_OF_WEEK);

    }


    public boolean eventFlag(String date){
        return events.containsKey(date);
    }
    /**
     * Moves day to previous index, updates state
     */
    public void previousDay(){
        currentDate--;
        int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        if(currentDate < 1){
            previousMonth();;
            currentDate = max;
        }
        updateListeners();
    }

    /**
     * Moves day to next index, updates state
     */
    public void nextDay(){
        currentDate++;
        int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        if(currentDate > max){
            nextMonth();
            currentDate = 1;
        }
        /*
        cal.add(Calendar.MONTH, 1);
        maximumDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        changed = true;
        */
        updateListeners();
    }

    /**
     * Converts time format from hrs to mins
     * @param time
     * @return mins
     */
    private int convertTime(String time) {
        int hours = Integer.valueOf(time.substring(0, 2));
        return hours * 60 + Integer.valueOf(time.substring(3));
    }

    /**
     * Creates a new event on the specified date
     * @param eventTitle
     * @param eventStartTime
     * @param eventEndTime
     */
    public void create(String eventTitle, String eventStartTime, String eventEndTime){
        String date = (cal.get(Calendar.MONTH) + 1) + "/" + currentDate + "/" + cal.get(Calendar.YEAR);
        Event e = new Event(eventTitle, date, eventStartTime, eventEndTime);
        ArrayList<Event> eventsArr = new ArrayList<>();
        if (eventFlag(e.getEventDate())) {
            eventsArr = events.get(date);
        }
        eventsArr.add(e);
        events.put(date, eventsArr);
    }

    /**
     * Loads events from eventList.ser
     */
    public void load() {
        try {
            FileInputStream f = new FileInputStream("eventList.ser");
            ObjectInputStream o = new ObjectInputStream(f);
            //cast
            HashMap<String, ArrayList<Event>> ev = (HashMap<String, ArrayList<Event>>) o.readObject();
            for (String d : ev.keySet()) {
                if (eventFlag(d)){
                    ArrayList<Event> eventArray = events.get(d);
                    eventArray.addAll(ev.get(d));
                }
                else {
                    events.put(d, ev.get(d));
                }
            }
            o.close();
            f.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
            catch(ClassNotFoundException ev){
                System.out.println("Error: Class does not exist");
                ev.printStackTrace();
            }

    }

    /**
     * Saves events to .ser file
     */
    public void save() {
        if (events.isEmpty()) {
            return;
        }

        try {
            FileOutputStream f = new FileOutputStream("events.ser");
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(events);
            o.close();
            f.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets event objects as strings
     * @param eventDate
     * @return events a string representation of an event
     */
    public String getEvents(String eventDate) {
        ArrayList<Event> eventArray = events.get(eventDate);
        Collections.sort(eventArray, eventComparator());
        String events = "N/A";
        for (Event e : eventArray) {
            events += e.toString() + "\n";
        }
        return events;
    }

    /**
     * Checks conflicts between scheduled events
     * @param timeStart
     * @param timeEnd
     * @return boolean
     */
    public Boolean hasEventConflict(String timeStart, String timeEnd) {
        String date = (getCurrentMonth() + 1) + "/" + currentDate + "/" + getCurrentYear();
        if (!eventFlag(date)) {
            return false;
        }

        ArrayList<Event> eventArray = events.get(date);
        Collections.sort(eventArray, eventComparator());

        int eventStartTimeInMins = convertTime(timeStart);
        int eventEndTimeInMins = convertTime(timeEnd);

        for (Event e : eventArray) {
            int eventStartTime = convertTime(e.getEventStartTime()), eventEndTime = convertTime(e.getEventEndTime());
            if (eventStartTimeInMins >= eventStartTime && eventStartTimeInMins < eventEndTime) {
                return true;
            } else if (eventStartTimeInMins <= eventStartTime && eventEndTimeInMins > eventStartTime) {
                return true;
            }
        }
        return false;
    }

        /**
         * Compares two events and checks for time conflicts
         * @return the comparator value
         */
        public static Comparator<Event> eventComparator () {
            return new Comparator<Event>() {
                @Override
                public int compare(Event o1, Event o2) {
                    if (o1.getEventStartTime().substring(0, 2).equals(o2.getEventStartTime().substring(0, 2))) {
                        return Integer.parseInt(o1.getEventStartTime().substring(3, 5)) - Integer.parseInt(o2.getEventStartTime().substring(0, 2));
                    }
                    return Integer.parseInt(o1.getEventStartTime().substring(0, 2)) - Integer.parseInt(o1.getEventStartTime().substring(0, 2));
                }
            };
        }

    /**
     * Flag to check if current month has changed
     * @return changed
     */
    public boolean monthFlag(){
        return changed;
    }

    /**
     * Resets boolean flag changed
     */
    public void resetFlag(){
        changed = false;
    }
}
