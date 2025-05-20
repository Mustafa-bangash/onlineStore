import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class OrderViewer extends JFrame {

    JTable table;
    DefaultTableModel model;

    public OrderViewer() {
        setTitle("Order List");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        model = new DefaultTableModel();
        table = new JTable(model);

        model.addColumn("Order ID");
        model.addColumn("Order Date");
        model.addColumn("Customer");
        model.addColumn("Product");
        model.addColumn("Quantity");

        loadData();

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        setVisible(true);
    }

    private void loadData() {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, " Database connection failed!");
            return;
        }

        try {
            Statement stmt = conn.createStatement();
            String sql = """
                SELECT 
                    o.OrderID,
                    o.OrderDate,
                    c.Name AS CustomerName,
                    p.ProductName,
                    od.Quantity
                FROM Orders o
                JOIN Customer c ON o.CustomerID = c.CustomerID
                JOIN OrderDetails od ON o.OrderID = od.OrderID
                JOIN Product p ON od.ProductID = p.ProductID
                ORDER BY o.OrderID
            """;

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("OrderID"),
                        rs.getDate("OrderDate"),
                        rs.getString("CustomerName"),
                        rs.getString("ProductName"),
                        rs.getInt("Quantity")
                });
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new OrderViewer();
    }
}
