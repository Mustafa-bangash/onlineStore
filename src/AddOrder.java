import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;

public class AddOrder extends JFrame {

    public AddOrder() {
        setTitle("Add New Order");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JComboBox<Customer> customerBox = new JComboBox<>();
        JComboBox<Product> productBox = new JComboBox<>();
        JTextField quantityField = new JTextField();

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
        panel.add(new JLabel("Enter Quantity:")); panel.add(quantityField);

        JButton submitButton = new JButton("Place Order");

        submitButton.addActionListener(e -> {
            try {
                int quantity = Integer.parseInt(quantityField.getText());
                Customer customer = (Customer) customerBox.getSelectedItem();
                Product product = (Product) productBox.getSelectedItem();

                Connection conn = DBConnection.getConnection();


                String orderSQL = "INSERT INTO Orders (CustomerID, OrderDate) VALUES (?, ?)";
                PreparedStatement orderStmt = conn.prepareStatement(orderSQL, Statement.RETURN_GENERATED_KEYS);
                orderStmt.setInt(1, customer.getId());
                orderStmt.setDate(2, Date.valueOf(LocalDate.now()));
                orderStmt.executeUpdate();

                ResultSet generatedKeys = orderStmt.getGeneratedKeys();
                int orderId = 0;
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                }


                String detailSQL = "INSERT INTO OrderDetails (OrderID, ProductID, Quantity) VALUES (?, ?, ?)";
                PreparedStatement detailStmt = conn.prepareStatement(detailSQL);
                detailStmt.setInt(1, orderId);
                detailStmt.setInt(2, product.getId());
                detailStmt.setInt(3, quantity);
                detailStmt.executeUpdate();

                conn.close();

                JOptionPane.showMessageDialog(this, " Order placed!");
                dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, " Failed to place order.");
            }
        });

        panel.add(submitButton);
        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new AddOrder();
    }
}
