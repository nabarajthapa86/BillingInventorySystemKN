package com.inventory.ui;

import com.inventory.dao.CashierDAO;
import com.inventory.dao.ProductDAO;
import com.inventory.model.Admin;
import com.inventory.model.Cashier;
import com.inventory.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {

    private final Admin admin;
    private final CashierDAO cashierDAO = new CashierDAO();
    private final ProductDAO productDAO = new ProductDAO();

    private DefaultTableModel cashierModel;
    private DefaultTableModel productModel;

    public AdminDashboard(Admin admin) {
        this.admin = admin;
        setTitle("Admin Dashboard — " + admin.getUsername());
        setSize(800, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuAccount = new JMenu("Account");
        JMenuItem itemLogout = new JMenuItem("Logout");
        itemLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        menuAccount.add(itemLogout);
        menuBar.add(menuAccount);
        setJMenuBar(menuBar);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Manage Cashiers", buildCashierPanel());
        tabs.addTab("Manage Products", buildProductPanel());
        add(tabs);
    }

    // ─── Cashier Panel ───────────────────────────────────────────────────────────

    private JPanel buildCashierPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        cashierModel = new DefaultTableModel(new String[]{"ID", "Username"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(cashierModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        loadCashiers();

        JPanel form = new JPanel(new GridLayout(3, 2, 6, 6));
        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        form.add(new JLabel("Username:")); form.add(txtUser);
        form.add(new JLabel("Password:")); form.add(txtPass);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton btnAdd  = new JButton("Add");
        JButton btnEdit = new JButton("Edit Selected");
        JButton btnDel  = new JButton("Delete Selected");

        btnAdd.addActionListener(e -> {
            String u = txtUser.getText().trim();
            String p = new String(txtPass.getPassword()).trim();
            if (u.isEmpty() || p.isEmpty()) { warn("Fill username and password."); return; }
            if (cashierDAO.create(u, p)) { loadCashiers(); txtUser.setText(""); txtPass.setText(""); }
            else warn("Could not add cashier. Username may already exist.");
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { warn("Select a cashier to edit."); return; }
            int id = (int) cashierModel.getValueAt(row, 0);
            String u = txtUser.getText().trim();
            String p = new String(txtPass.getPassword()).trim();
            if (u.isEmpty() || p.isEmpty()) { warn("Fill new username and password."); return; }
            if (cashierDAO.update(id, u, p)) loadCashiers();
            else warn("Update failed.");
        });

        btnDel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { warn("Select a cashier to delete."); return; }
            int id = (int) cashierModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this, "Delete this cashier?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (cashierDAO.delete(id)) loadCashiers();
                else warn("Delete failed. Cashier may have existing bills.");
            }
        });

        btnPanel.add(btnAdd); btnPanel.add(btnEdit); btnPanel.add(btnDel);

        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.CENTER);
        south.add(btnPanel, BorderLayout.SOUTH);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private void loadCashiers() {
        cashierModel.setRowCount(0);
        for (Cashier c : cashierDAO.getAll()) {
            cashierModel.addRow(new Object[]{c.getCashierID(), c.getUsername()});
        }
    }

    // ─── Product Panel ────────────────────────────────────────────────────────────

    private JPanel buildProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        productModel = new DefaultTableModel(new String[]{"ID", "Name", "Price (Rs.)", "Stock"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(productModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        loadProducts();

        JPanel form = new JPanel(new GridLayout(4, 2, 6, 6));
        JTextField txtName  = new JTextField();
        JTextField txtPrice = new JTextField();
        JTextField txtStock = new JTextField();
        form.add(new JLabel("Name:"));     form.add(txtName);
        form.add(new JLabel("Price:"));    form.add(txtPrice);
        form.add(new JLabel("Stock Qty:")); form.add(txtStock);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton btnAdd  = new JButton("Add");
        JButton btnEdit = new JButton("Edit Selected");
        JButton btnDel  = new JButton("Delete Selected");

        btnAdd.addActionListener(e -> {
            try {
                String n = txtName.getText().trim();
                double pr = Double.parseDouble(txtPrice.getText().trim());
                int st = Integer.parseInt(txtStock.getText().trim());
                if (n.isEmpty()) { warn("Enter product name."); return; }
                if (productDAO.create(n, pr, st)) { loadProducts(); txtName.setText(""); txtPrice.setText(""); txtStock.setText(""); }
                else warn("Could not add product.");
            } catch (NumberFormatException ex) { warn("Price and Stock must be numbers."); }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { warn("Select a product to edit."); return; }
            try {
                int id = (int) productModel.getValueAt(row, 0);
                String n = txtName.getText().trim();
                double pr = Double.parseDouble(txtPrice.getText().trim());
                int st = Integer.parseInt(txtStock.getText().trim());
                if (productDAO.update(id, n, pr, st)) loadProducts();
                else warn("Update failed.");
            } catch (NumberFormatException ex) { warn("Price and Stock must be numbers."); }
        });

        btnDel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { warn("Select a product to delete."); return; }
            int id = (int) productModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this, "Delete this product?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (productDAO.delete(id)) loadProducts();
                else warn("Delete failed. Product may be linked to existing bills.");
            }
        });

        btnPanel.add(btnAdd); btnPanel.add(btnEdit); btnPanel.add(btnDel);

        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.CENTER);
        south.add(btnPanel, BorderLayout.SOUTH);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private void loadProducts() {
        productModel.setRowCount(0);
        for (Product p : productDAO.getAll()) {
            productModel.addRow(new Object[]{p.getProductID(), p.getName(), p.getPrice(), p.getStockQty()});
        }
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}
