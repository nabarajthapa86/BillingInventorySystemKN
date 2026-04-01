package com.inventory.ui;

import com.inventory.dao.AdminDAO;
import com.inventory.dao.CashierDAO;
import com.inventory.model.Admin;
import com.inventory.model.Cashier;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("Inventory & Billing System — Login");
        setSize(420, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Inventory & Billing System", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        main.add(title, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1; main.add(new JLabel("Role:"), gbc);
        cmbRole = new JComboBox<>(new String[]{"Admin", "Cashier"});
        gbc.gridx = 1; main.add(cmbRole, gbc);

        gbc.gridx = 0; gbc.gridy = 2; main.add(new JLabel("Username:"), gbc);
        txtUsername = new JTextField(15);
        gbc.gridx = 1; main.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 3; main.add(new JLabel("Password:"), gbc);
        txtPassword = new JPasswordField(15);
        gbc.gridx = 1; main.add(txtPassword, gbc);

        btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(59, 130, 246));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        main.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> handleLogin());
        txtPassword.addActionListener(e -> handleLogin());

        add(main);
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = (String) cmbRole.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("Admin".equals(role)) {
            AdminDAO dao = new AdminDAO();
            Admin admin = dao.login(username, password);
            if (admin != null) {
                dispose();
                new AdminDashboard(admin).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid admin credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            CashierDAO dao = new CashierDAO();
            Cashier cashier = dao.login(username, password);
            if (cashier != null) {
                dispose();
                new CashierDashboard(cashier).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid cashier credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
