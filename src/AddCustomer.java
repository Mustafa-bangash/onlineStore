import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddCustomer extends JFrame {

    public AddCustomer() {
        setTitle("Add Customer");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField addressField = new JTextField();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Email:")); panel.add(emailField);
        panel.add(new JLabel("Address:")); panel.add(addressField);

        JButton submitButton = new JButton("Add Customer");
        submitButton.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String address = addressField.getText();

            try {
                Connection conn = DBConnection.getConnection();
                String sql = "INSERT INTO Customer (Name, Email, Address) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, address);
                stmt.executeUpdate();
                conn.close();
                JOptionPane.showMessageDialog(this, " Customer added!");
                dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, " Failed to add customer.");
            }
        });

        panel.add(submitButton);
        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new AddCustomer();
    }
}
