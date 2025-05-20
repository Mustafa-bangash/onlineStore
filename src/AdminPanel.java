import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JFrame {

    public AdminPanel() {
        setTitle("Admin Dashboard");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        JLabel title = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));


        JButton manageProductsBtn = new JButton(" Manage Products");
        JButton viewCustomersBtn = new JButton(" View Customers");
        JButton viewOrdersBtn = new JButton(" View Orders");
        JButton viewFeedbackBtn = new JButton(" View Feedback");

        manageProductsBtn.setPreferredSize(new Dimension(200, 40));
        viewCustomersBtn.setPreferredSize(new Dimension(200, 40));
        viewOrdersBtn.setPreferredSize(new Dimension(200, 40));
        viewFeedbackBtn.setPreferredSize(new Dimension(200, 40));


        manageProductsBtn.addActionListener(e -> new ProductViewer());
        viewCustomersBtn.addActionListener(e -> new CustomerViewer());
        viewOrdersBtn.addActionListener(e -> new OrderViewer());
        viewFeedbackBtn.addActionListener(e -> new FeedbackViewer());


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 10, 10));
        buttonPanel.add(manageProductsBtn);
        buttonPanel.add(viewCustomersBtn);
        buttonPanel.add(viewOrdersBtn);
        buttonPanel.add(viewFeedbackBtn);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 100));


        setLayout(new BorderLayout());
        add(title, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        new AdminPanel();
    }
}
