import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ProductViewer extends JFrame {

    JTable table;
    DefaultTableModel model;

    public ProductViewer() {
        setTitle("Product List");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        model = new DefaultTableModel();
        table = new JTable(model);

        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Price");
        model.addColumn("Quantity");
        model.addColumn("Category Name");
        model.addColumn("Supplier Name");


        loadData();

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        setVisible(true);
        JButton addButton = new JButton("Add Product");

        addButton.addActionListener(e -> addProduct());
        JButton deleteButton = new JButton("Delete Product");
        deleteButton.addActionListener(e -> deleteProduct());

        JButton updateButton = new JButton("Update Product");
        updateButton.addActionListener(e -> updateProduct());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(addButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(updateButton);
        add(bottomPanel, "South");

    }
    private void deleteProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠ Please select a product to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this product?");
        if (confirm != JOptionPane.YES_OPTION) return;

        int productId = (int) model.getValueAt(selectedRow, 0);

        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null) {
                JOptionPane.showMessageDialog(this, " Database connection failed!");
                return;
            }

            String sql = "DELETE FROM product WHERE ProductID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, productId);

            stmt.executeUpdate();
            conn.close();

            model.setRowCount(0);
            loadData();

            JOptionPane.showMessageDialog(this, " Product deleted!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, " Please select a product to update.");
            return;
        }

        int productId = (int) model.getValueAt(selectedRow, 0);
        String currentName = String.valueOf(model.getValueAt(selectedRow, 1));
        String currentPrice = String.valueOf(model.getValueAt(selectedRow, 2));
        String currentQuantity = String.valueOf(model.getValueAt(selectedRow, 3));
        String currentCategory = String.valueOf(model.getValueAt(selectedRow, 4));
        String currentSupplier = String.valueOf(model.getValueAt(selectedRow, 5));

        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null) {
                JOptionPane.showMessageDialog(this, " Database connection failed!");
                return;
            }


            JComboBox<Category> categoryBox = new JComboBox<>();
            Statement catStmt = conn.createStatement();
            ResultSet catRs = catStmt.executeQuery("SELECT * FROM Category");
            Category selectedCat = null;
            while (catRs.next()) {
                Category cat = new Category(catRs.getInt("CategoryID"), catRs.getString("Name"));
                categoryBox.addItem(cat);
                if (cat.toString().equalsIgnoreCase(currentCategory)) {
                    selectedCat = cat;
                }
            }
            categoryBox.setSelectedItem(selectedCat);


            JComboBox<Supplier> supplierBox = new JComboBox<>();
            Statement supStmt = conn.createStatement();
            ResultSet supRs = supStmt.executeQuery("SELECT * FROM Supplier");
            Supplier selectedSup = null;
            while (supRs.next()) {
                Supplier sup = new Supplier(supRs.getInt("SupplierID"), supRs.getString("SupplierName"));
                supplierBox.addItem(sup);
                if (sup.toString().equalsIgnoreCase(currentSupplier)) {
                    selectedSup = sup;
                }
            }
            supplierBox.setSelectedItem(selectedSup);


            JTextField nameField = new JTextField(currentName);
            JTextField priceField = new JTextField(currentPrice);
            JTextField quantityField = new JTextField(currentQuantity);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(new JLabel("Product Name:")); panel.add(nameField);
            panel.add(new JLabel("Price:")); panel.add(priceField);
            panel.add(new JLabel("Quantity:")); panel.add(quantityField);
            panel.add(new JLabel("Category:")); panel.add(categoryBox);
            panel.add(new JLabel("Supplier:")); panel.add(supplierBox);

            int result = JOptionPane.showConfirmDialog(this, panel, "Update Product", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;


            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            Category selectedCategory = (Category) categoryBox.getSelectedItem();
            Supplier selectedSupplier = (Supplier) supplierBox.getSelectedItem();


            String sql = "UPDATE Product SET ProductName=?, Price=?, Quantity=?, CategoryID=?, SupplierID=? WHERE ProductID=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setInt(3, quantity);
            stmt.setInt(4, selectedCategory.getId());
            stmt.setInt(5, selectedSupplier.getId());
            stmt.setInt(6, productId);

            stmt.executeUpdate();
            conn.close();

            model.setRowCount(0);
            loadData();

            JOptionPane.showMessageDialog(this, " Product updated!");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, " Invalid number input.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addProduct() {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null) {
                JOptionPane.showMessageDialog(this, " Database connection failed!");
                return;
            }


            JComboBox<Category> categoryBox = new JComboBox<>();
            Statement catStmt = conn.createStatement();
            ResultSet catRs = catStmt.executeQuery("SELECT * FROM Category");
            while (catRs.next()) {
                categoryBox.addItem(new Category(catRs.getInt("CategoryID"), catRs.getString("Name")));
            }


            JComboBox<Supplier> supplierBox = new JComboBox<>();
            Statement supStmt = conn.createStatement();
            ResultSet supRs = supStmt.executeQuery("SELECT * FROM Supplier");
            while (supRs.next()) {
                supplierBox.addItem(new Supplier(supRs.getInt("SupplierID"), supRs.getString("SupplierName")));
            }


            JTextField nameField = new JTextField();
            JTextField priceField = new JTextField();
            JTextField quantityField = new JTextField();

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(new JLabel("Product Name:")); panel.add(nameField);
            panel.add(new JLabel("Price:")); panel.add(priceField);
            panel.add(new JLabel("Quantity:")); panel.add(quantityField);
            panel.add(new JLabel("Category:")); panel.add(categoryBox);
            panel.add(new JLabel("Supplier:")); panel.add(supplierBox);

            int result = JOptionPane.showConfirmDialog(this, panel, "Add New Product", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;


            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            Category selectedCategory = (Category) categoryBox.getSelectedItem();
            Supplier selectedSupplier = (Supplier) supplierBox.getSelectedItem();


            String sql = "INSERT INTO Product (ProductName, Price, Quantity, CategoryID, SupplierID) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setInt(3, quantity);
            stmt.setInt(4, selectedCategory.getId());
            stmt.setInt(5, selectedSupplier.getId());

            stmt.executeUpdate();
            conn.close();

            model.setRowCount(0);
            loadData();
            JOptionPane.showMessageDialog(this, " Product added!");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, " Invalid number input.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                p.ProductID,
                p.ProductName,
                p.Price,
                p.Quantity,
                c.Name AS CategoryName,
                s.SupplierName AS SupplierName
            FROM Product p
            JOIN Category c ON p.CategoryID = c.CategoryID
            JOIN Supplier s ON p.SupplierID = s.SupplierID
        """;

            ResultSet rs = stmt.executeQuery(sql);
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("ProductID"),
                        rs.getString("ProductName"),
                        rs.getDouble("Price"),
                        rs.getInt("Quantity"),
                        rs.getString("CategoryName"),
                        rs.getString("SupplierName")
                });
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new ProductViewer();
    }
}
