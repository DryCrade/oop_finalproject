import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;

public class RegistrationForm extends JDialog { //Declaring instance variables
    private JTextField txtUsername;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JPasswordField txtConfPassword;
    private JButton registerButton;
    private JButton cancelButton;
    private JPanel registerPanel;
    private JButton iAlreadyHaveAnButton;

    public RegistrationForm(JFrame parent) { //Creating a new JFrame
        super(parent);
        setTitle("Create a new account");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(450, 474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        //ActionListener for the buttons
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        txtConfPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    registerUser();
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.out.println("Registration cancelled");
            }
        });


        iAlreadyHaveAnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                LoginForm loginForm = new LoginForm(null);
            }
        });
        setVisible(true);
    }


    private void registerUser() {
        String username = txtUsername.getText();
        String email = txtEmail.getText();
        String password = String.valueOf(txtPassword.getPassword());
        String confirmPassword = String.valueOf(txtConfPassword.getPassword());

        //Error Handling
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter all fields",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!(email.contains("@") && email.contains("."))) {
            JOptionPane.showMessageDialog(this,
                    "Email must contain '@' and '.'",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Confirm Password does not match",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = addUserToDatabase(username, email, password);
        if (user != null) {
            System.out.println("Successfully registered the user: " + user.username);
            dispose();

            LoginForm loginForm = new LoginForm(null);
        } else {
            System.out.println("Registration cancelled");
        }
    }

    // Registering the username and password to the database
    private User addUserToDatabase(String username, String email, String password) {
        User user = null;

        final String DB_URL = "jdbc:mysql://localhost/animelistusers";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try (Connection con = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO usercreds (username, email, password) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, password);

                int addedRows = preparedStatement.executeUpdate();
                if (addedRows > 0) {
                    user = new User();
                    user.username = username;
                    user.email = email;
                    user.password = password;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RegistrationForm myForm = new RegistrationForm(null);
        });
    }
    private static class User {
        String username;
        String email;
        String password;
    }
}
