import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;

public class LoginForm extends JDialog {
    private JButton btnOK;
    private JButton btnCancel;
    private JTextField txtEmail;
    private JPasswordField pfPassword;
    public JPanel loginPanel;
    private JButton btnCreateAcc;

    public LoginForm(JFrame parent) { //Creating a new JFrame
        super(parent);
        setTitle("Login to your account");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(450, 474/2));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        //ActionListeners for the buttons
        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.out.println("Login cancelled");
            }
        });


        btnCreateAcc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current frame

                // Open the login form
                RegistrationForm registrationForm = new RegistrationForm(null);
            }
        });

        txtEmail.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });

        pfPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });

        setVisible(true);
    }

    private void performLogin() {
        String email = txtEmail.getText();
        String password = String.valueOf(pfPassword.getPassword());
        User user = getAuthenticatedUser(email, password); // Checking if login matches with the existing database

        if (user != null) {
            dispose(); // Close the login form
            openHomePage(); // Open the home page
        } else {
            JOptionPane.showMessageDialog(LoginForm.this,
                    "Email or Password invalid",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private User getAuthenticatedUser(String email, String password) {
        User user = null;
        final String DB_URL = "jdbc:mysql://localhost/animelistusers";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection con = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM usercreds WHERE email=? AND password=?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = new User();
                user.username = resultSet.getString("username");
                user.email = resultSet.getString("email");
                user.password = resultSet.getString("password");
            }

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    private void openHomePage() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("HomePage");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setContentPane(new HomePage().homePagePanel);
                frame.pack();
                frame.setLocationRelativeTo(null); // Center the frame
                frame.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginForm loginForm = new LoginForm(null);
            }
        });
    }
}
