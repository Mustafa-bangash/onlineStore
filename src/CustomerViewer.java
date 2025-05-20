import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class CustomerViewer extends JFrame {

    JTable table;
    DefaultTableModel model;

    public CustomerViewer() {
        setTitle("Customer List");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        model = new DefaultTableModel();
        table = new JTable(model);

        model.addColumn("Customer ID");
        model.addColumn("Name");
        model.addColumn("Email");
        model.addColumn("Address");

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
            ResultSet rs = stmt.executeQuery("SELECT * FROM Customer");

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("CustomerID"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Address")
                });
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new CustomerViewer();
    }
}
