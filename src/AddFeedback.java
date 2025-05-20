import javax.swing.*;
import java.sql.*;

public class AddFeedback extends JFrame {

    public AddFeedback() {
        setTitle("Add Feedback");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JComboBox<Customer> customerBox = new JComboBox<>();
        JComboBox<Product> productBox = new JComboBox<>();
        JTextField ratingField = new JTextField();
        JTextArea commentArea = new JTextArea(4, 20);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        try {
            Connection conn = DBConnection.getConnection();


            Statement cs = conn.createStatement();
            ResultSet crs = cs.executeQuery("SELECT * FROM Customer");
            while (crs.next()) {
                customerBox.addItem(new Customer(
                        crs.getInt("CustomerID"),
                        crs.getString("Name"),
                        crs.getString("Email"),
                        crs.getString("Address")
                ));
            }


            Statement ps = conn.createStatement();
            ResultSet prs = ps.executeQuery("SELECT * FROM Product");
            while (prs.next()) {
                productBox.addItem(new Product(
                        prs.getInt("ProductID"),
                        prs.getString("ProductName"),
                        prs.getDouble("Price")
                ));
            }

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        panel.add(new JLabel("Select Customer:")); panel.add(customerBox);
        panel.add(new JLabel("Select Product:")); panel.add(productBox);
        panel.add(new JLabel("Rating (1–5):")); panel.add(ratingField);
        panel.add(new JLabel("Comment:")); panel.add(new JScrollPane(commentArea));

        JButton submitButton = new JButton("Submit Feedback");

        submitButton.addActionListener(e -> {
            try {
                int rating = Integer.parseInt(ratingField.getText());
                String comment = commentArea.getText();
                Customer customer = (Customer) customerBox.getSelectedItem();
                Product product = (Product) productBox.getSelectedItem();

                Connection conn = DBConnection.getConnection();
                String sql = "INSERT INTO Feedback (CustomerID, ProductID, Rating, Comment) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, customer.getId());
                stmt.setInt(2, product.getId());
                stmt.setInt(3, rating);
                stmt.setString(4, comment);
                stmt.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(this, " Feedback submitted!");
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, " Failed to submit feedback.");
            }
        });

        panel.add(submitButton);
        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new AddFeedback();
    }
}
