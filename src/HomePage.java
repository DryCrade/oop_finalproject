import net.proteanit.sql.DbUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Objects;


public class HomePage extends DatabaseGUI implements SortPopup.SortListener {
    // Declaration of instance variables
    public JPanel homePagePanel;
    private JTextField txtTitle;
    private JTextField txtNotes;
    private JButton saveButton;
    private JTable table1;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton searchButton;
    private JTextField txtid;
    private JScrollPane jsPane;
    private JButton btnLogOut;
    private JButton btnDeleteAll;
    private JLabel txtMadeBy;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JButton btnSort;
    private JButton btnFlashlight;
    private JLabel profilePFP;
    private JLabel labelTitle;
    private JLabel labelScore;
    private JLabel labelStatus;
    private JLabel labelNotes;
    private JLabel headline;
    Connection con;
    PreparedStatement pst;
    private SortPopup sortPopup;

    public static void main(String[] args) { // Main method, JFrame is initialized here
        if (new HomePage().checkIfDatabaseHaveUsers()) {
            JFrame frame = new JFrame("Welcome to the anime list manager!");
            frame.setContentPane(new HomePage().homePagePanel);
            frame.getContentPane().setBackground(new Color(255,248,231));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        } else {
            RegistrationForm registrationForm = new RegistrationForm(null);
        }
    }

    private boolean checkIfDatabaseHaveUsers() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/animelistusers", "root", "");
            PreparedStatement statement = con.prepareStatement("SELECT COUNT(*) FROM usercreds");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void connect() { // Attempts to connect to a mySQL database
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/animelist", "root", "");
            System.out.println("Success");


        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public HomePage() {

        connect(); // Connects with the database, quits if database can't be reached
        table_load(); // If database is detected and there is a registered user, home page will load

        jsPane.setOpaque(false); //Transparent JScrollPane
        jsPane.getViewport().setOpaque(false);

        // ComboBox options
        String[] scores = {"-", "10", "9", "8", "7", "6", "5", "4", "3", "2", "1"};
        for (String score : scores) {
            comboBox1.addItem(score);
        }

        String[] statuses = {"-", "Plan to watch", "Watching", "Finished watching"};
        for (String status : statuses) {
            comboBox2.addItem(status);
        }

        table1.getTableHeader().setReorderingAllowed(false);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title, score, status, notes;

                title = txtTitle.getText();
                score = Objects.requireNonNull(comboBox1.getSelectedItem()).toString();
                status = Objects.requireNonNull(comboBox2.getSelectedItem()).toString();
                notes = txtNotes.getText();

                if (notes.isEmpty()) {
                    notes = "-";
                }

                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Title required.");
                    return;
                }
                if (status.equals("-")) {
                    JOptionPane.showMessageDialog(null, "You need to select a status.");
                    return;
                }

                try {
                    pst = con.prepareStatement("insert into tablelist(title,score,status,notes)values(?,?,?,?)");
                    pst.setString(1, title);
                    pst.setString(2, score);
                    pst.setString(3, status);
                    pst.setString(4, notes);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Entry added!");
                    table_load();
                    txtTitle.setText("");
                    comboBox1.setSelectedItem("-");
                    comboBox2.setSelectedItem("-");
                    txtNotes.setText("");
                    txtTitle.requestFocus();
                } catch (SQLException e1) {

                    e1.printStackTrace();
                }

            }
        });
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    String id = txtid.getText();

                    pst = con.prepareStatement("select title,score,status,notes from tablelist where id = ?");
                    pst.setString(1, id);
                    ResultSet rs = pst.executeQuery();

                    if (rs.next()) {
                        String title = rs.getString(1);
                        String score = rs.getString(2);
                        String status = rs.getString(3);
                        String notes = rs.getString(4);

                        txtTitle.setText(title);
                        comboBox1.setSelectedItem(score);
                        comboBox2.setSelectedItem(status);
                        txtNotes.setText(notes);

                    } else {
                        txtTitle.setText("");
                        comboBox1.setSelectedItem("-");
                        comboBox2.setSelectedItem("-");
                        txtNotes.setText("");
                        JOptionPane.showMessageDialog(null, "Invalid Anime No.");

                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title, score, status, notes, id;

                title = txtTitle.getText();
                score = comboBox1.getSelectedItem().toString();
                status = comboBox2.getSelectedItem().toString();
                notes = txtNotes.getText();
                id = txtid.getText();

                if (notes.isEmpty()) {
                    notes = "-";
                }

                if (status.equals("-")) {
                    JOptionPane.showMessageDialog(null, "You need to select a status.");
                    return;
                }

                try {
                    pst = con.prepareStatement("update tablelist set title = ?,score = ?,status = ?,notes = ? where id = ?");
                    pst.setString(1, title);
                    pst.setString(2, score);
                    pst.setString(3, status);
                    pst.setString(4, notes);
                    pst.setString(5, id);

                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Entry Updated!");
                    table_load();
                    txtTitle.setText("");
                    comboBox1.setSelectedItem("-");
                    comboBox2.setSelectedItem("-");
                    txtNotes.setText("");
                    txtTitle.requestFocus();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id;
                id = txtid.getText();

                try {
                    pst = con.prepareStatement("delete from tablelist where id = ?");

                    pst.setString(1, id);

                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Entry deleted!");
                    table_load();
                    txtTitle.setText("");
                    comboBox1.setSelectedItem("-");
                    comboBox2.setSelectedItem("-");
                    txtNotes.setText("");
                    txtTitle.requestFocus();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        // Placeholder for txtid
        txtid.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtid.getText().equals("Enter ID..")) {
                    txtid.setText("");
                    txtid.setForeground(new Color(0, 0, 0));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtid.getText().equals("")) {
                    txtid.setText("Enter ID..");
                    txtid.setForeground(new Color(153, 153, 153));
                }
            }
        });
        btnLogOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Log out button clicked
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(homePagePanel);
                frame.dispose(); // Close the current frame

                // Open the login form
                new LoginForm(null);
            }
        });
        btnDeleteAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(homePagePanel, "Are you sure you want to delete all entries?");
                if (option == JOptionPane.YES_OPTION) {
                    deleteAllEntries();
                    table_load();
                }
            }
        });
        btnSort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        sortPopup = new SortPopup();
        sortPopup.setSortListener(this); // Set sort listener

        btnSort.addActionListener(e -> {
            JFrame frame = new JFrame("Sort");
            frame.setContentPane(sortPopup.sortPanel);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });

        Component[] components = {headline, txtMadeBy, labelTitle, labelScore, labelStatus, labelNotes};

        //Dark and Bright Mode
        btnFlashlight.addActionListener(new ActionListener() {
            boolean isDarkMode = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                Color backgroundColor = isDarkMode? new Color(255, 248, 231) : new Color(34, 32, 33);
                Color foregroundColor = isDarkMode? Color.BLACK : Color.WHITE;

                homePagePanel.setBackground(backgroundColor);
                for (Component component : components) {
                    component.setForeground(foregroundColor);
                }

                isDarkMode =!isDarkMode;
                homePagePanel.repaint();
            }
        });
    }
    void table_load() {
        try {
            pst = con.prepareStatement("select * from tablelist");
            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
            table1.setDefaultEditor(Object.class, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void deleteAllEntries() {
        try {
            pst = con.prepareStatement("DELETE FROM tablelist");
            pst.executeUpdate();
            JOptionPane.showMessageDialog(homePagePanel, "All entries are deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void onSort(String scoreSortOption, String statusSortOption) { //Option to sort the table
        try {
            String query = "SELECT * FROM tablelist";

            if (!scoreSortOption.equals("-")) {
                if (scoreSortOption.equals("Descending")) {
                    query += " ORDER BY score ASC";
                } else if (scoreSortOption.equals("Ascending")) {
                    query += " ORDER BY score DESC";
                }
            }

            if (!statusSortOption.equals("-")) {
                if (!scoreSortOption.equals("-")) {
                    query += ", ";
                } else {
                    query += " ORDER BY ";
                }

                query += "CASE " +
                        "WHEN status = '" + statusSortOption + "' THEN 1 ELSE 2 END";
            }

            pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
            table1.setDefaultEditor(Object.class, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
