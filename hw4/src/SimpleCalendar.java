/**
 * Created by Vishan on 11/18/17.
 */
public class SimpleCalendar {

    /**
     * Main controller class method. Displays simple calendar.
     * @param args
     */
    public static void main(String[] args){
        Model model = new Model();
        View view = new View(model);
        model.addListener(view);
    }
}
