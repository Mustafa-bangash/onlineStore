import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CustomerPanel extends JFrame {

    private JComboBox<Customer> orderCustomerBox;
    private JComboBox<Customer> feedbackCustomerBox;
    private JComboBox<Product> productBox;
    private JComboBox<Product> feedbackProductBox;

    public CustomerPanel() {
        setTitle("Customer Panel");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Register", registerTab());
        tabs.add("Place Order", orderTab());
        tabs.add("Give Feedback", feedbackTab());

        add(tabs);

        reloadCustomers();

        setVisible(true);
    }

    private JPanel registerTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField addressField = new JTextField();
        JButton registerBtn = new JButton("Register");

        registerBtn.addActionListener(e -> {
            try {
                Connection conn = DBConnection.getConnection();
                String sql = "INSERT INTO Customer (Name, Email, Address) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nameField.getText());
                stmt.setString(2, emailField.getText());
                stmt.setString(3, addressField.getText());
                stmt.executeUpdate();
                conn.close();
                JOptionPane.showMessageDialog(this, " Customer Registered!");
                reloadCustomers();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Email:")); panel.add(emailField);
        panel.add(new JLabel("Address:")); panel.add(addressField);
        panel.add(registerBtn);

        return panel;
    }

    private JPanel orderTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        orderCustomerBox = new JComboBox<>();
        productBox = new JComboBox<>();
        JTextField quantityField = new JTextField();
        JButton orderBtn = new JButton("Place Order");

        orderBtn.addActionListener(e -> {
            try {
                int qty = Integer.parseInt(quantityField.getText());
                Customer c = (Customer) orderCustomerBox.getSelectedItem();
                Product p = (Product) productBox.getSelectedItem();

                Connection conn = DBConnection.getConnection();

                String sql1 = "INSERT INTO Orders (CustomerID, OrderDate) VALUES (?, CURDATE())";
                PreparedStatement stmt1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
                stmt1.setInt(1, c.getId());
                stmt1.executeUpdate();

                ResultSet rs = stmt1.getGeneratedKeys();
                int orderId = 0;
                if (rs.next()) orderId = rs.getInt(1);

                String sql2 = "INSERT INTO OrderDetails (OrderID, ProductID, Quantity) VALUES (?, ?, ?)";
                PreparedStatement stmt2 = conn.prepareStatement(sql2);
                stmt2.setInt(1, orderId);
                stmt2.setInt(2, p.getId());
                stmt2.setInt(3, qty);
                stmt2.executeUpdate();

                conn.close();
                JOptionPane.showMessageDialog(this, " Order placed!");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        panel.add(new JLabel("Select Customer:")); panel.add(orderCustomerBox);
        panel.add(new JLabel("Select Product:")); panel.add(productBox);
        panel.add(new JLabel("Quantity:")); panel.add(quantityField);
        panel.add(orderBtn);

        return panel;
    }

    private JPanel feedbackTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        feedbackCustomerBox = new JComboBox<>();
        feedbackProductBox = new JComboBox<>();
        JTextField ratingField = new JTextField();
        JTextArea commentArea = new JTextArea(3, 20);
        JButton feedbackBtn = new JButton("Submit Feedback");

        feedbackBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        feedbackBtn.addActionListener(e -> {
            try {
                int rating = Integer.parseInt(ratingField.getText());
                String comment = commentArea.getText();
                Customer c = (Customer) feedbackCustomerBox.getSelectedItem();
                Product p = (Product) feedbackProductBox.getSelectedItem();

                Connection conn = DBConnection.getConnection();
                String sql = "INSERT INTO Feedback (CustomerID, ProductID, Rating, Comment) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, c.getId());
                stmt.setInt(2, p.getId());
                stmt.setInt(3, rating);
                stmt.setString(4, comment);
                stmt.executeUpdate();
                conn.close();
                JOptionPane.showMessageDialog(this, " Feedback submitted!");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        panel.add(new JLabel("Select Customer:"));
        panel.add(feedbackCustomerBox);
        panel.add(new JLabel("Select Product:"));
        panel.add(feedbackProductBox);
        panel.add(new JLabel("Rating (1 to 5):"));
        panel.add(ratingField);
        panel.add(new JLabel("Comment:"));
        panel.add(new JScrollPane(commentArea));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(feedbackBtn);

        return panel;
    }

    private void reloadCustomers() {
        try {
            if (orderCustomerBox != null) orderCustomerBox.removeAllItems();
            if (feedbackCustomerBox != null) feedbackCustomerBox.removeAllItems();
            if (productBox != null) productBox.removeAllItems();
            if (feedbackProductBox != null) feedbackProductBox.removeAllItems();

            Connection conn = DBConnection.getConnection();

            ResultSet crs = conn.createStatement().executeQuery("SELECT * FROM Customer");
            while (crs.next()) {
                Customer c = new Customer(crs.getInt("CustomerID"), crs.getString("Name"), crs.getString("Email"), crs.getString("Address"));
                if (orderCustomerBox != null) orderCustomerBox.addItem(c);
                if (feedbackCustomerBox != null) feedbackCustomerBox.addItem(c);
            }

            ResultSet prs = conn.createStatement().executeQuery("SELECT * FROM Product");
            while (prs.next()) {
                Product p = new Product(prs.getInt("ProductID"), prs.getString("ProductName"), prs.getDouble("Price"));
                if (productBox != null) productBox.addItem(p);
                if (feedbackProductBox != null) feedbackProductBox.addItem(p);
            }

            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new CustomerPanel();
    }
}
