package ui;

import model.Restaurant;
import model.RestaurantList;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Modified from:
 *  https://docs.oracle.com/javase/tutorial/uiswing/examples/components
 *    /ListDemoProject/src/components/ListDemo.java
 */

public class MainPanel extends JPanel
        implements ListSelectionListener {

    private static final String ADD_RESTAURANT = "Add Restaurant";
    private static final String ADD_LIST = "Add New List";
    private static final String MERGE_LISTS = "Merge Lists";
    private static final String SAVE = "Save Profile";
    private static final int WIDTH = 500;

    private JList leftList;
    private JList rightList;
    private DefaultListModel<RestaurantList> leftListModel;
    private DefaultListModel<Restaurant> rightListModel;
    private JButton addRestaurantBtn;
    private JButton mergeListBtn;
    private JButton saveBtn;

    // secondary frames:
    private RestrInputFrame restrInputFrame;
    private RestrListInputFrame listInputFrame;
    private MergeFrame mergeFrame;

    // Setup a panel with two JLists one at LINE_START, one at CENTER
    public MainPanel() {
        super(new BorderLayout());

        // add data into list models
        setUpListModels();
        // set up lists to be displayed
        setUpLists();

        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,
                BoxLayout.LINE_AXIS));
        // set up buttons in buttonPane
        setUpButtons(buttonPane);

        JScrollPane leftScrollPane = new JScrollPane(leftList);
        JScrollPane rightScrollPane = new JScrollPane(rightList);

        add(leftScrollPane, BorderLayout.LINE_START);
        add(rightScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
        setPreferredSize(new Dimension(WIDTH, 400));

        setUpHiddenFrames();
    }

    /**
     * EFFECTS: set up the secondary (pop-up) frames
     */
    private void setUpHiddenFrames() {
        restrInputFrame = new RestrInputFrame();
        restrInputFrame.setVisible(false);
        restrInputFrame.addListener(new SubmitButtonListener());

        listInputFrame = new RestrListInputFrame();
        listInputFrame.setVisible(false);
        listInputFrame.addListener(new SubmitButtonListener());
    }

    /**
     * EFFECTS: read data into list models
     */
    void setUpListModels() {
        Restaurant r1 = new Restaurant("McDonald's");
        Restaurant r2 = new Restaurant("Starbucks");
        Restaurant r3 = new Restaurant("Green Dragon");
        Restaurant r4 = new Restaurant("Cactus");
        RestaurantList rl1 = new RestaurantList("La1");
        RestaurantList rl2 = new RestaurantList("La2");

        rl1.add(r1);
        rl1.add(r2);
        rl2.add(r3);
        rl2.add(r4);

        rightListModel = new DefaultListModel<Restaurant>();
        leftListModel = new DefaultListModel<RestaurantList>();
        leftListModel.addElement(rl1);
        leftListModel.addElement(rl2);
    }

    /**
     * EFFECTS: add listModels to corresponding lists, and set up display settings
     */
    private void setUpLists() {
        leftList = new JList(leftListModel);
        leftList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leftList.setSelectedIndex(-1);
        leftList.addListSelectionListener(this);
        leftList.setVisibleRowCount(5);
        leftList.setPreferredSize(new Dimension(200, 100));

        rightList = new JList(rightListModel);
        rightList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rightList.setSelectedIndex(-1);
        rightList.setVisibleRowCount(5);
    }

    /**
     * Set up JButton fields of this class
     */
    private void setUpButtons(JPanel panel) {
        addRestaurantBtn = new JButton(ADD_RESTAURANT);
        addRestaurantBtn.setActionCommand(ADD_RESTAURANT);
        addRestaurantBtn.addActionListener(new MainButtonsListener());
        addRestaurantBtn.setEnabled(false);

        JButton addListBtn = new JButton(ADD_LIST);
        addListBtn.setActionCommand(ADD_LIST);
        addListBtn.addActionListener(new MainButtonsListener());

        mergeListBtn = new JButton(MERGE_LISTS);
        mergeListBtn.setActionCommand(MERGE_LISTS);
        mergeListBtn.addActionListener(new MainButtonsListener());
        mergeListBtn.setEnabled(false);

        saveBtn = new JButton(SAVE);
        saveBtn.setActionCommand(SAVE);
        //saveBtn.addActionListener();

        panel.add(addRestaurantBtn);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(addListBtn);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(mergeListBtn);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(saveBtn);
    }

    /**
     * EFFECTS: add elements of the currently selected restaurantList into the rightList column
     */
    void updateRightList() {
        rightListModel.clear();
        RestaurantList selected = getSelectedRestaurantList();
        for (int i = 0; i < selected.size(); i++) {
            rightListModel.addElement(selected.get(i));
        }
    }

    /**
     * Event listener for when left list selection changes
     *
     * EFFECTS: When the selection in leftList changes,
     *          (1) refresh the content of right list to the appropriate collection
     *          (2) activate/deactivate addRestaurantBtn and mergeListBtn
     * REQUIRES: Members of leftList are Objects of RestaurantList
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!leftList.getValueIsAdjusting()) {
            // When there is no selection
            if (leftList.getSelectedIndex() == -1 || leftListModel.size() <= 1) {
                addRestaurantBtn.setEnabled(false);
                mergeListBtn.setEnabled(false);
            } else {
                updateRightList();
                addRestaurantBtn.setEnabled(true);
                mergeListBtn.setEnabled(true);
            }
        }
    }

    /**
     * EFFECTS: returns the currently selected restaurant list
     */
    public RestaurantList getSelectedRestaurantList() {
        int idx = leftList.getSelectedIndex();
        if (idx == -1) {
            return null;
        } else {
            return leftListModel.getElementAt(idx);
        }
    }

    /**
     * EFFECTS: triggers correct windows depending on which button is pressed
     */
    class MainButtonsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals(ADD_RESTAURANT)) {
                // Reset and open the inputFrame window
                restrInputFrame.resetText();
                restrInputFrame.setVisible(true);
            }
            if (e.getActionCommand().equals(ADD_LIST)) {
                listInputFrame.resetText();
                listInputFrame.setVisible(true);
            }
            if (e.getActionCommand().equals(MERGE_LISTS)) {
                mergeFrame = new MergeFrame(leftListModel, leftList.getSelectedIndex());
                mergeFrame.addListener(new MergeButtonListener());
                mergeFrame.setVisible(true);
            }
            if (e.getActionCommand().equals(SAVE)) {
                // TODO: confirm saving profile
            }
        }
    }

    /**
     * EFFECTS: when 'submit' button is pressed on restrInputFrame,
     *          add the new restaurant object to the current RestaurantList
     *          when pressed in listInputFrame,
     *          add the new RestaurantList to leftList and update rightList
     */
    class SubmitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Submit")) {
                Restaurant restaurant;
                try {
                    restaurant = restrInputFrame.getRestaurant();
                    // if not successfully added to list
                    if (!getSelectedRestaurantList().add(restaurant)) {
                        // TODO: notification window saying restaurant already exists
                    } else {
                        updateRightList();
                    }
                    restrInputFrame.setVisible(false);
                } catch (NumberFormatException exception) {
                    // TODO: notification window saying rating is invalid
                }
            }

            if (e.getActionCommand().equals("Submit List")) {
                RestaurantList list = listInputFrame.getRestrList();
                leftListModel.addElement(list);
                leftList.setSelectedIndex(leftListModel.size() - 1);
                updateRightList();
                listInputFrame.setVisible(false);
            }
        }
    }

    /**
     * EFFECTS: when merge button is pressed in mergeFrame,
     *          merge the 'selected' restaurant list into the 'current' one
     *          and remove the 'selected' restaurant list from 'leftList'
     */
    class MergeButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Merge")) {
                RestaurantList current = getSelectedRestaurantList();
                int selectedIdx = mergeFrame.getSelectedIndex();
                if (selectedIdx != -1) {
                    current.add(leftListModel.getElementAt(selectedIdx).getRestaurants());
                    leftListModel.remove(selectedIdx);
                    updateRightList();
                }
                mergeFrame.setVisible(false);
            }
        }
    }


    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("MainPanel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new MainPanel();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
