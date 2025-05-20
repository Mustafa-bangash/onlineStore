import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class FeedbackViewer extends JFrame {

    JTable table;
    DefaultTableModel model;

    public FeedbackViewer() {
        setTitle("Customer Feedback");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        model = new DefaultTableModel();
        table = new JTable(model);

        model.addColumn("Feedback ID");
        model.addColumn("Customer Name");
        model.addColumn("Product Name");
        model.addColumn("Rating");
        model.addColumn("Comment");

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
                    f.FeedbackID,
                    c.Name AS CustomerName,
                    p.ProductName,
                    f.Rating,
                    f.Comment
                FROM Feedback f
                JOIN Customer c ON f.CustomerID = c.CustomerID
                JOIN Product p ON f.ProductID = p.ProductID
            """;

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("FeedbackID"),
                        rs.getString("CustomerName"),
                        rs.getString("ProductName"),
                        rs.getInt("Rating"),
                        rs.getString("Comment")
                });
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new FeedbackViewer();
    }
}
