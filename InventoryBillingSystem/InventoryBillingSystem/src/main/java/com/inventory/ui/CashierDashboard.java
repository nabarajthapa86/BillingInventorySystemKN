package com.inventory.ui;

import com.inventory.dao.BillDAO;
import com.inventory.dao.ProductDAO;
import com.inventory.model.Bill;
import com.inventory.model.BillItem;
import com.inventory.model.Cashier;
import com.inventory.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CashierDashboard extends JFrame {

    private final Cashier cashier;
    private final ProductDAO productDAO = new ProductDAO();
    private final BillDAO billDAO = new BillDAO();

    private JComboBox<Product> cmbProduct;
    private JTextField txtQty;
    private DefaultTableModel billModel;
    private JLabel lblTotal;
    private List<BillItem> currentItems = new ArrayList<>();
    private int tempItemID = 1;

    public CashierDashboard(Cashier cashier) {
        this.cashier = cashier;
        setTitle("Cashier Dashboard — " + cashier.getUsername());
        setSize(780, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuAccount = new JMenu("Account");
        JMenuItem itemLogout = new JMenuItem("Logout");
        itemLogout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        menuAccount.add(itemLogout);
        JMenu menuBills = new JMenu("My Bills");
        JMenuItem itemViewBills = new JMenuItem("View Past Bills");
        itemViewBills.addActionListener(e -> showPastBills());
        menuBills.add(itemViewBills);
        menuBar.add(menuAccount);
        menuBar.add(menuBills);
        setJMenuBar(menuBar);

        setLayout(new BorderLayout(8, 8));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Top: product selector ───────────────────────────────────────
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        topPanel.setBorder(BorderFactory.createTitledBorder("Add Item"));

        cmbProduct = new JComboBox<>();
        cmbProduct.setPreferredSize(new Dimension(240, 28));
        loadProducts();

        txtQty = new JTextField("1", 5);
        JButton btnAdd = new JButton("Add to Bill");
        btnAdd.addActionListener(e -> addItemToBill());

        topPanel.add(new JLabel("Product:"));
        topPanel.add(cmbProduct);
        topPanel.add(new JLabel("Qty:"));
        topPanel.add(txtQty);
        topPanel.add(btnAdd);
        add(topPanel, BorderLayout.NORTH);

        // ── Centre: bill items table ────────────────────────────────────
        billModel = new DefaultTableModel(new String[]{"#", "Product", "Qty", "Unit Price", "Subtotal"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable billTable = new JTable(billModel);
        billTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(billTable), BorderLayout.CENTER);

        // ── Bottom: remove, total, save ─────────────────────────────────
        JPanel bottomPanel = new JPanel(new BorderLayout(8, 4));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnRemove = new JButton("Remove Selected");
        JButton btnClear  = new JButton("Clear Bill");
        JButton btnSave   = new JButton("Save & Print Bill");
        btnSave.setBackground(new Color(34, 197, 94));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);

        btnRemove.addActionListener(e -> {
            int row = billTable.getSelectedRow();
            if (row >= 0) {
                currentItems.remove(row);
                refreshBillTable();
            }
        });

        btnClear.addActionListener(e -> {
            currentItems.clear();
            refreshBillTable();
        });

        btnSave.addActionListener(e -> saveBill());

        actionPanel.add(btnRemove);
        actionPanel.add(btnClear);
        actionPanel.add(btnSave);

        lblTotal = new JLabel("Total: Rs. 0.00", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTotal.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        bottomPanel.add(actionPanel, BorderLayout.WEST);
        bottomPanel.add(lblTotal, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadProducts() {
        cmbProduct.removeAllItems();
        for (Product p : productDAO.getAll()) {
            if (p.isAvailable()) cmbProduct.addItem(p);
        }
    }

    private void addItemToBill() {
        Product selected = (Product) cmbProduct.getSelectedItem();
        if (selected == null) { warn("No products available."); return; }
        int qty;
        try {
            qty = Integer.parseInt(txtQty.getText().trim());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            warn("Enter a valid quantity (positive number).");
            return;
        }
        if (qty > selected.getStockQty()) {
            warn("Not enough stock. Available: " + selected.getStockQty());
            return;
        }
        for (BillItem existing : currentItems) {
            if (existing.getProductID() == selected.getProductID()) {
                int newQty = existing.getQuantitySold() + qty;
                if (newQty > selected.getStockQty()) { warn("Total qty exceeds stock."); return; }
                existing.setQuantitySold(newQty);
                refreshBillTable();
                return;
            }
        }
        BillItem item = new BillItem(tempItemID++, 0, selected.getProductID(),
                selected.getName(), qty, selected.getPrice());
        currentItems.add(item);
        refreshBillTable();
    }

    private void refreshBillTable() {
        billModel.setRowCount(0);
        double total = 0;
        int i = 1;
        for (BillItem item : currentItems) {
            billModel.addRow(new Object[]{
                    i++,
                    item.getProductName(),
                    item.getQuantitySold(),
                    String.format("Rs. %.2f", item.getUnitPriceAtSale()),
                    String.format("Rs. %.2f", item.getSubtotal())
            });
            total += item.getSubtotal();
        }
        lblTotal.setText(String.format("Total: Rs. %.2f", total));
    }

    private void saveBill() {
        if (currentItems.isEmpty()) { warn("Add at least one item to the bill."); return; }

        Bill bill = new Bill(0, cashier.getCashierID());
        for (BillItem item : currentItems) {
            item.setBillID(0);
            bill.addItem(item);
        }

        int savedID = billDAO.saveBill(bill);
        if (savedID < 0) { warn("Failed to save bill."); return; }

        for (BillItem item : currentItems) {
            Product p = productDAO.getByID(item.getProductID());
            if (p != null) {
                productDAO.updateStock(p.getProductID(), p.getStockQty() - item.getQuantitySold());
            }
        }

        showBillReceipt(bill);
        currentItems.clear();
        refreshBillTable();
        loadProducts();
    }

    private void showBillReceipt(Bill bill) {
        StringBuilder sb = new StringBuilder();
        sb.append("==============================\n");
        sb.append("     INVENTORY & BILLING\n");
        sb.append("==============================\n");
        sb.append(String.format("Bill No : %d\n", bill.getBillID()));
        sb.append(String.format("Cashier : %s\n", cashier.getUsername()));
        sb.append(String.format("Date    : %s\n", bill.getBillDate()));
        sb.append("------------------------------\n");
        sb.append(String.format("%-18s %4s %8s\n", "Product", "Qty", "Amount"));
        sb.append("------------------------------\n");
        for (BillItem item : bill.getItems()) {
            sb.append(String.format("%-18s %4d %8.2f\n",
                    item.getProductName(), item.getQuantitySold(), item.getSubtotal()));
        }
        sb.append("------------------------------\n");
        sb.append(String.format("TOTAL           : Rs. %.2f\n", bill.getTotalAmount()));
        sb.append("==============================\n");
        sb.append("     Thank you!\n");

        JTextArea ta = new JTextArea(sb.toString());
        ta.setFont(new Font("Monospaced", Font.PLAIN, 13));
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(360, 360));

        JPanel receiptPanel = new JPanel(new BorderLayout(4, 8));
        receiptPanel.add(sp, BorderLayout.CENTER);
        JButton btnPrint = new JButton("Print");
        btnPrint.addActionListener(e -> {
            try { ta.print(); } catch (Exception ex) { ex.printStackTrace(); }
        });
        receiptPanel.add(btnPrint, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, receiptPanel, "Bill Receipt — #" + bill.getBillID(), JOptionPane.PLAIN_MESSAGE);
    }

    private void showPastBills() {
        List<Bill> bills = billDAO.getBillsByCashier(cashier.getCashierID());
        if (bills.isEmpty()) { JOptionPane.showMessageDialog(this, "No bills found."); return; }

        DefaultTableModel m = new DefaultTableModel(new String[]{"Bill ID", "Date", "Total (Rs.)"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Bill b : bills) {
            m.addRow(new Object[]{b.getBillID(), b.getBillDate(), String.format("%.2f", b.getTotalAmount())});
        }
        JTable t = new JTable(m);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton btnView = new JButton("View Selected");
        btnView.addActionListener(e -> {
            int row = t.getSelectedRow();
            if (row >= 0) {
                Bill selected = bills.get(row);
                showBillReceipt(selected);
            }
        });

        JPanel p = new JPanel(new BorderLayout(4, 8));
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        p.add(btnView, BorderLayout.SOUTH);
        p.setPreferredSize(new Dimension(420, 300));
        JOptionPane.showMessageDialog(this, p, "Past Bills", JOptionPane.PLAIN_MESSAGE);
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}
