import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


/**
 * Created by Vishan on 11/18/17.
 * Class used to establish SimpleCalendar view
 */

public class View implements ChangeListener {

    private Model model;
    private DAYS[] days = DAYS.values();
    private MONTHS[] months = MONTHS.values();
    private int max;
    private int highlighter = -1;

    private ArrayList<JButton> dayButtons = new ArrayList<JButton>();

    private JFrame frame = new JFrame("CalendarFrame");
    private JPanel monthPanel = new JPanel();
    private JLabel month = new JLabel();
    private JButton create = new JButton("Create");
    private JButton next = new JButton("Next");
    private JButton prev = new JButton("Previous");
    private JTextPane dayText = new JTextPane();

    /**
     * Constructs the calendar view
     *
     * @param model the specified model object
     */
    public View(Model model) {
        this.model = model;
        max = model.getMax();
        monthPanel.setLayout(new GridLayout(0, 7));
        dayText.setPreferredSize(new Dimension(300, 150));
        dayText.setEditable(false);

        newDayButtons();
        addEmptyButtons();
        addDayButtons();
        //highlightEvents();
        displayDate(model.getCurrentDate());
        //highlightSelectedDate(model.getSelectedDay() - 1);

        create.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newEventDialog();
            }
        });

        JButton previousMonth = new JButton("<<");
        previousMonth.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                model.previousMonth();
                create.setEnabled(false);
                next.setEnabled(false);
                prev.setEnabled(false);
                dayText.setText("");
            }
        });
        JButton nextMonth = new JButton(">>");
        nextMonth.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                model.nextMonth();
                create.setEnabled(false);
                next.setEnabled(false);
                prev.setEnabled(false);
                dayText.setText("");
            }
        });

        JPanel monthContainer = new JPanel();
        monthContainer.setLayout(new BorderLayout());
        month.setText(months[model.getCurrentMonth()] + " " + model.getCurrentYear());
        monthContainer.add(month, BorderLayout.NORTH);
        monthContainer.add(new JLabel("       S             M             T             W             T              F             S"), BorderLayout.CENTER);
        monthContainer.add(monthPanel, BorderLayout.SOUTH);

        JPanel dayViewPanel = new JPanel();
        dayViewPanel.setLayout(new BorderLayout());

        JScrollPane dayScrollPane = new JScrollPane(dayText);
        dayScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        dayViewPanel.add(dayScrollPane, BorderLayout.WEST);

        JPanel buttonsPanel = new JPanel();
        next.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                model.nextDay();
            }
        });
        prev.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                model.previousDay();
            }
        });
        buttonsPanel.add(prev);
        buttonsPanel.add(create);
        buttonsPanel.add(next);
        dayViewPanel.add(buttonsPanel, BorderLayout.EAST);

        JButton quit = new JButton("Quit");
        quit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                model.save();
                System.exit(0);
            }
        });

        frame.add(previousMonth);
        frame.add(monthContainer);
        frame.add(nextMonth);
        frame.add(dayViewPanel);
        frame.add(quit);
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }



    /**
     * Adds day buttons to the month panel ArrayList
     */
    public void addDayButtons() {
        for (JButton b : dayButtons) {
            monthPanel.add(b);
        }
    }

    /**
     * Adds an empty button to the month panel (for calendar formatting)
     */
    public void addEmptyButtons() {
        for (int j = 1; j < model.getWeekDay(1); j++) {
            JButton emptyButton = new JButton();
            emptyButton.setEnabled(false);
            monthPanel.add(emptyButton);
        }
    }

    /**
     * Creates a new diaglog to add an event
     */
    private void newEventDialog() {
        final JDialog eventDialog = new JDialog();
        eventDialog.setTitle("Create new event");
        eventDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        final JTextField eventText = new JTextField(40);
        final JTextField eventStartTime = new JTextField(15);
        final JTextField eventEndTime= new JTextField(15);

        JButton saveEvent = new JButton("Save event");

        saveEvent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (eventText.getText().isEmpty()) {
                    return;
                }
                if ((!eventText.getText().isEmpty() && (eventStartTime.getText().isEmpty() || eventEndTime.getText().isEmpty()))
                        || eventStartTime.getText().length() != 5
                        || eventEndTime.getText().length() != 5
                        || !eventStartTime.getText().matches("([01]?[0-9]|2[0-3]):[0-5][0-9]")
                        || !eventEndTime.getText().matches("([01]?[0-9]|2[0-3]):[0-5][0-9]")) {
                    JDialog errorDialog = new JDialog();
                    errorDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
                    errorDialog.setLayout(new GridLayout(2, 0));
                    errorDialog.add(new JLabel("Incorrect format! Enter time as XX:YY"));
                    JButton done = new JButton("DONE");
                    done.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            errorDialog.dispose();
                        }
                    });
                    errorDialog.add(done);
                    errorDialog.pack();
                    errorDialog.setVisible(true);
                }

                else if (!eventText.getText().equals("")) {
                    if (model.hasEventConflict(eventStartTime.getText(), eventEndTime.getText())) {
                        JDialog conflictDialog = new JDialog();
                        conflictDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
                        conflictDialog.setLayout(new GridLayout(2, 0));
                        conflictDialog.add(new JLabel("Time conflict."));
                        JButton ok = new JButton("Okay");
                        ok.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                conflictDialog.dispose();
                            }
                        });
                        conflictDialog.add(ok);
                        conflictDialog.pack();
                        conflictDialog.setVisible(true);
                    } else {
                        eventDialog.dispose();
                        model.create(eventText.getText(), eventStartTime.getText(), eventEndTime.getText());
                        displayDate(model.getCurrentDate());
                        //highlightEvents();
                    }
                }
            }
        });

        eventDialog.setLayout(new GridBagLayout());
        JLabel date = new JLabel();
        date.setText(model.getCurrentMonth() + 1 + "/" + model.getCurrentDate() + "/" + model.getCurrentYear());
        date.setBorder(BorderFactory.createEmptyBorder());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.gridx = 0;
        c.gridy = 0;
        eventDialog.add(date, c);
        c.gridy = 1;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.LINE_START;
        eventDialog.add(new JLabel("Event"), c);
        c.gridy = 2;
        eventDialog.add(eventText, c);
        c.gridy = 3;
        c.weightx = 0.0;
        c.anchor = GridBagConstraints.LINE_START;
        eventDialog.add(new JLabel("Time Start (00:00)"), c);
        c.anchor = GridBagConstraints.CENTER;
        eventDialog.add(new JLabel("Time End (24:00)"), c);
        c.gridy = 4;
        c.anchor = GridBagConstraints.LINE_START;
        eventDialog.add(eventStartTime, c);
        c.anchor = GridBagConstraints.CENTER;
        eventDialog.add(eventEndTime, c);
        c.anchor = GridBagConstraints.LINE_END;
        eventDialog.add(saveEvent, c);
        eventDialog.pack();
        eventDialog.setVisible(true);
    }

    /**
     * Creates new day buttons & respective action listeners and adds them to array list
     */

    public void newDayButtons(){
        for(int i = 1; i <= max; i++){
           final int foo = i;
           JButton dayButton = new JButton(Integer.toString(foo));
           dayButton.setBackground(Color.WHITE);
           //anonymous action listener class
            dayButton.addActionListener(
                    new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            displayDate(foo);
                            //highlightDate(foo - 1);
                            create.setEnabled(true);
                            next.setEnabled(true);
                            prev.setEnabled(true);
                        }
                    });
            dayButtons.add(dayButton);
        }

    }

    /**
     * Highlights the selected date
     *
     * @param foo the index of the date to highlight
     */
/*
    public void highlightDate(int foo){
        Border line = new LineBorder(Color.GREEN, 2);
        dayButtons.get(foo).setBorder(line);

        if(highlighter != -1){
            dayButtons.get(highlighter).setBorder(new JButton().getBorder());
        }
        highlighter = foo;
    }
*/

    /**
     * Displays date
     * @param date
     */
    public void displayDate(final int date) {
        model.setCurrentDate(date);
        String day = days[model.getWeekDay(date) - 1] + "";
        String currDate = (model.getCurrentMonth() + 1) + "/" + date + "/" + model.getCurrentYear();
        String events = "";
        if(model.eventFlag(currDate)){
            events += model.getEvents(currDate);
        }
        dayText.setText(day + " " + currDate);
        dayText.setCaretPosition(0);

    }

    @Override
    /**
     * Uses ChangeEvents to check state of model and repaint View accordingly
     */
    public void stateChanged(ChangeEvent e) {
        if (model.monthFlag()) {
            max = model.getMax();
            dayButtons.clear();
            monthPanel.removeAll();
            month.setText(months[model.getCurrentMonth()] + " " + model.getCurrentYear());
            newDayButtons();
            addEmptyButtons();
            addDayButtons();
            highlighter = -1;
            model.resetFlag();
            frame.pack();
            frame.repaint();
        } else {
            displayDate(model.getCurrentDate());

        }
    }

}