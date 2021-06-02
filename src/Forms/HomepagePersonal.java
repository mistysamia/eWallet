/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ASUS
 */
public class HomepagePersonal extends javax.swing.JFrame {

    /**
     * Creates new form HomepagePersonal
     */
    public HomepagePersonal() {
        initComponents();
        setCalendar();
        this.setLocationRelativeTo(null);
        //show_transaction();
        show_offers();
        show_subscription();
    }

    public HomepagePersonal(String name, String phoneNo) {
        initComponents();
        setCalendar();
        this.setLocationRelativeTo(null);
        username.setText(name);
        user_contact.setText(phoneNo);
        //show_transaction();
        show_offers();
        show_subscription();
    }
    
    
    //Personal Transaction
    public ArrayList<personal_transaction> transList() {
        ArrayList<personal_transaction> transList = new ArrayList<>();
        String phoneNo = user_contact.getText();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date_from = sdf.format(DateFrom.getDate());
            String date_to = sdf.format(DateTo.getDate());
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
            System.out.println("1. DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());

            String query = "select TransactionID,Date,Amount,Reference,ReceiverID ,SenderID ,\n" +
                            "(select Contact from INFORMATION where InfoID=ReceiverID) as ReceiverContact,\n" +
                            "(select Contact from INFORMATION where InfoID=SenderID) as SenderContact \n" +
                            "from  TXN where(date between '"+date_from+"' and '"+date_to+"') and (receiverID=(SELECT InfoID From INFORMATION where Contact = '"+phoneNo+"') or senderID=(SELECT InfoID From INFORMATION where Contact = '"+phoneNo+"'))";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

//            String receiver_contact = "01676336205";

            personal_transaction pt;

            while (rs.next()) {
                int txn_id = rs.getInt("TransactionID");

                Date strDate = rs.getDate("Date");
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = dateFormat.format(strDate);

                float amount = rs.getFloat("Amount");
                int sender_id = rs.getInt("SenderID");
                int receiver_id = rs.getInt("ReceiverID");
                String reference = rs.getString("Reference");
                String sender_contact = rs.getString("SenderContact");

                String receiver_contact = rs.getString("ReceiverContact");
                pt = new personal_transaction(txn_id, date, amount, sender_id, receiver_id, sender_contact, receiver_contact, reference);
                transList.add(pt);

                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transList;
    }

    public void show_transaction() {
        ArrayList<personal_transaction> list = transList();
        DefaultTableModel model = (DefaultTableModel) transaction_table.getModel();
        model.setNumRows(0);
        for (int i = list.size()-1; i >=0; i--) {
            Object[] row = new Object[8];
            row[0] = list.get(i).getTxn_id();
            row[1] = list.get(i).getDate();
            row[2] = list.get(i).getAmount();
            row[3] = list.get(i).getSenderID();
            row[4] = list.get(i).getSenderContact();
            row[5] = list.get(i).getReceiverID();
            row[6] = list.get(i).getReceiverContact();
            row[7] = list.get(i).getReference();
            model.addRow(row);
            
        }

    }
    
    
    
    
    
    //Personal Offers List
    public ArrayList<personal_offer> offerList() {
        ArrayList<personal_offer> offerList = new ArrayList<>();
        String phoneNo = user_contact.getText();
        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            String date_from = sdf.format(DateFrom.getDate());
//            String date_to = sdf.format(DateTo.getDate());
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
            System.out.println("1. DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());

            String query = "select OFFER.BillerID, BILLER.CompanyName, Rate, MaxDiscount, MinPurchase, Deadline, Voucher from OFFER INNER JOIN BILLER on OFFER.BillerID = Biller.BillerID where Deadline >= getDate()";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

//            String receiver_contact = "01676336205";

            personal_offer po;

            while (rs.next()) {
                int biller_id = rs.getInt("BillerID");
                String biller_name = rs.getString("CompanyName");
                int rate = rs.getInt("Rate");
                double max_discount = rs.getDouble("MaxDiscount");
                double min_purchase = rs.getDouble("MinPurchase");
                Date strDate = rs.getDate("Deadline");
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String deadline = dateFormat.format(strDate);
                String voucher = rs.getString("Voucher");
                
                
                po = new personal_offer(biller_id, biller_name, rate, max_discount, min_purchase, deadline, voucher);
                offerList.add(po);
                
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return offerList;
    }

    public void show_offers() {
        ArrayList<personal_offer> list = offerList();
        DefaultTableModel model = (DefaultTableModel) offer_table.getModel();
        model.setNumRows(0);
        for (int i = 0; i <list.size(); i++) {
            Object[] row = new Object[7];
            row[0] = list.get(i).getBillerID();
            row[1] = list.get(i).getBillerName();
            row[2] = list.get(i).getRate();
            row[3] = list.get(i).getMaxDiscount();
            row[4] = list.get(i).getMinPurchase();
            row[5] = list.get(i).getDeadline();
            row[6] = list.get(i).getVoucher();
            model.addRow(row);
        }

    }
    
    
    //Personal Subscription
    public ArrayList<personal_subscription> subscriptionList() {
        ArrayList<personal_subscription> subscriptionList = new ArrayList<>();
        String phoneNo = user_contact.getText();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            String date_from = sdf.format(DateFrom.getDate());
//            String date_to = sdf.format(DateTo.getDate());
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
            System.out.println("1. DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());

            String query = "select BILLER.BillerID as BillerID, CompanyName, Branch, INFORMATION.Address as Address, Biller.Contact as Contact from BILLER INNER JOIN INFORMATION on INFORMATION.Contact = BILLER.CONTACT where INFORMATION.InfoID  IN (Select distinct ReceiverID From TXN where SenderID = (SELECT InfoID from INFORMATION where Contact = '"+ phoneNo+"'))" ;
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

//            String receiver_contact = "01676336205";

            personal_subscription ps;

            while (rs.next()) {
                int biller_id = rs.getInt("BillerID");
                String company_name = rs.getString("CompanyName");
                String branch = rs.getString("Branch");
                String address = rs.getString("Address");
                String contact = rs.getString("Contact");
                
                ps = new personal_subscription(biller_id, company_name, branch, address, contact);
                subscriptionList.add(ps);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subscriptionList;
    }

    public void show_subscription() {
        ArrayList<personal_subscription> list = subscriptionList();
        DefaultTableModel model = (DefaultTableModel) subscription_table.getModel();
        model.setNumRows(0);
        for (int i = list.size()-1; i >=0; i--) {
            Object[] row = new Object[8];
            row[0] = list.get(i).getBillerID();
            row[1] = list.get(i).getCompanyName();
            row[2] = list.get(i).getBranch();
            row[3] = list.get(i).getAddress();
            row[4] = list.get(i).getContact();
            model.addRow(row);
        }

    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        testDateEvaluator1 = new com.toedter.calendar.demo.TestDateEvaluator();
        testDateEvaluator2 = new com.toedter.calendar.demo.TestDateEvaluator();
        jCalendar1 = new com.toedter.calendar.JCalendar();
        Acc_Type = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        Panel_Dashboard = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        Panel_Paybill = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        Panel_Transactions = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        Panel_Addbalance = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        Panel_Subscriptions = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        Panel_Availableoffers = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        Panel_Reportissues = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        Panel_Logout = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        dateField = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        Title_text = new javax.swing.JLabel();
        user_contact = new javax.swing.JLabel();
        username = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        User_Dashboard = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        bg = new javax.swing.JLabel();
        User_Paybill = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        billerID = new javax.swing.JTextField();
        voucher = new javax.swing.JTextField();
        billerContact = new javax.swing.JTextField();
        amount = new javax.swing.JTextField();
        reference = new javax.swing.JTextField();
        password = new javax.swing.JPasswordField();
        bg_paybill = new javax.swing.JLabel();
        User_Transactions = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        transaction_table = new javax.swing.JTable();
        jLabel23 = new javax.swing.JLabel();
        DateFrom = new com.toedter.calendar.JDateChooser();
        DateTo = new com.toedter.calendar.JDateChooser();
        jButton6 = new javax.swing.JButton();
        jLabel60 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        User_Addbalance = new javax.swing.JPanel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        AgentID = new javax.swing.JTextField();
        MobileNo = new javax.swing.JTextField();
        AddAmount = new javax.swing.JTextField();
        Reference = new javax.swing.JTextField();
        Password = new javax.swing.JPasswordField();
        bg_paybill1 = new javax.swing.JLabel();
        User_Subscriptions = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        subscription_table = new javax.swing.JTable();
        jLabel25 = new javax.swing.JLabel();
        User_Availableoffers = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        offer_table = new javax.swing.JTable();
        jLabel26 = new javax.swing.JLabel();
        User_Reportissues = new javax.swing.JPanel();
        TxnID = new javax.swing.JTextField();
        ReportedID = new javax.swing.JTextField();
        Amount = new javax.swing.JTextField();
        Description = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        radio_biller = new javax.swing.JRadioButton();
        radio_agent = new javax.swing.JRadioButton();
        Txn_Date = new com.toedter.calendar.JDateChooser();
        jLabel27 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1000, 600));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setPreferredSize(new java.awt.Dimension(600, 200));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Panel_Dashboard.setBackground(new java.awt.Color(4, 35, 63));
        Panel_Dashboard.setForeground(new java.awt.Color(255, 255, 255));
        Panel_Dashboard.setPreferredSize(new java.awt.Dimension(200, 50));
        Panel_Dashboard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Panel_DashboardMouseClicked(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Dashboard");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icon_dashboard.png"))); // NOI18N

        javax.swing.GroupLayout Panel_DashboardLayout = new javax.swing.GroupLayout(Panel_Dashboard);
        Panel_Dashboard.setLayout(Panel_DashboardLayout);
        Panel_DashboardLayout.setHorizontalGroup(
            Panel_DashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_DashboardLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        Panel_DashboardLayout.setVerticalGroup(
            Panel_DashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(Panel_DashboardLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.add(Panel_Dashboard, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 200, 50));

        Panel_Paybill.setBackground(new java.awt.Color(4, 35, 63));
        Panel_Paybill.setForeground(new java.awt.Color(255, 255, 255));
        Panel_Paybill.setPreferredSize(new java.awt.Dimension(200, 50));
        Panel_Paybill.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Panel_PaybillMouseClicked(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Pay Bill");

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icon_paybill.png"))); // NOI18N

        javax.swing.GroupLayout Panel_PaybillLayout = new javax.swing.GroupLayout(Panel_Paybill);
        Panel_Paybill.setLayout(Panel_PaybillLayout);
        Panel_PaybillLayout.setHorizontalGroup(
            Panel_PaybillLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_PaybillLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        Panel_PaybillLayout.setVerticalGroup(
            Panel_PaybillLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(Panel_PaybillLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(Panel_Paybill, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 130, 200, 50));

        Panel_Transactions.setBackground(new java.awt.Color(4, 35, 63));
        Panel_Transactions.setForeground(new java.awt.Color(255, 255, 255));
        Panel_Transactions.setPreferredSize(new java.awt.Dimension(200, 50));
        Panel_Transactions.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Panel_TransactionsMouseClicked(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Transactions");

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icon_transaction.png"))); // NOI18N

        javax.swing.GroupLayout Panel_TransactionsLayout = new javax.swing.GroupLayout(Panel_Transactions);
        Panel_Transactions.setLayout(Panel_TransactionsLayout);
        Panel_TransactionsLayout.setHorizontalGroup(
            Panel_TransactionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_TransactionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        Panel_TransactionsLayout.setVerticalGroup(
            Panel_TransactionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(Panel_TransactionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(Panel_Transactions, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 180, 200, 50));

        Panel_Addbalance.setBackground(new java.awt.Color(4, 35, 63));
        Panel_Addbalance.setForeground(new java.awt.Color(255, 255, 255));
        Panel_Addbalance.setPreferredSize(new java.awt.Dimension(200, 50));
        Panel_Addbalance.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Panel_AddbalanceMouseClicked(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Add Balance");

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icon_addbalance.png"))); // NOI18N

        javax.swing.GroupLayout Panel_AddbalanceLayout = new javax.swing.GroupLayout(Panel_Addbalance);
        Panel_Addbalance.setLayout(Panel_AddbalanceLayout);
        Panel_AddbalanceLayout.setHorizontalGroup(
            Panel_AddbalanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_AddbalanceLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        Panel_AddbalanceLayout.setVerticalGroup(
            Panel_AddbalanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(Panel_AddbalanceLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(Panel_Addbalance, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 200, 50));

        Panel_Subscriptions.setBackground(new java.awt.Color(4, 35, 63));
        Panel_Subscriptions.setForeground(new java.awt.Color(255, 255, 255));
        Panel_Subscriptions.setPreferredSize(new java.awt.Dimension(200, 50));
        Panel_Subscriptions.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Panel_SubscriptionsMouseClicked(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Subscriptions");

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icon_subscriptions.png"))); // NOI18N

        javax.swing.GroupLayout Panel_SubscriptionsLayout = new javax.swing.GroupLayout(Panel_Subscriptions);
        Panel_Subscriptions.setLayout(Panel_SubscriptionsLayout);
        Panel_SubscriptionsLayout.setHorizontalGroup(
            Panel_SubscriptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_SubscriptionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        Panel_SubscriptionsLayout.setVerticalGroup(
            Panel_SubscriptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(Panel_SubscriptionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(Panel_Subscriptions, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 280, 200, 50));

        Panel_Availableoffers.setBackground(new java.awt.Color(4, 35, 63));
        Panel_Availableoffers.setForeground(new java.awt.Color(255, 255, 255));
        Panel_Availableoffers.setPreferredSize(new java.awt.Dimension(200, 50));
        Panel_Availableoffers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Panel_AvailableoffersMouseClicked(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Available Offers");

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icon_offers.png"))); // NOI18N

        javax.swing.GroupLayout Panel_AvailableoffersLayout = new javax.swing.GroupLayout(Panel_Availableoffers);
        Panel_Availableoffers.setLayout(Panel_AvailableoffersLayout);
        Panel_AvailableoffersLayout.setHorizontalGroup(
            Panel_AvailableoffersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_AvailableoffersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        Panel_AvailableoffersLayout.setVerticalGroup(
            Panel_AvailableoffersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
            .addGroup(Panel_AvailableoffersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(Panel_Availableoffers, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 330, 200, 50));

        Panel_Reportissues.setBackground(new java.awt.Color(4, 35, 63));
        Panel_Reportissues.setForeground(new java.awt.Color(255, 255, 255));
        Panel_Reportissues.setPreferredSize(new java.awt.Dimension(200, 50));
        Panel_Reportissues.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Panel_ReportissuesMouseClicked(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Report Issues");

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icon_Reportissues.png"))); // NOI18N

        javax.swing.GroupLayout Panel_ReportissuesLayout = new javax.swing.GroupLayout(Panel_Reportissues);
        Panel_Reportissues.setLayout(Panel_ReportissuesLayout);
        Panel_ReportissuesLayout.setHorizontalGroup(
            Panel_ReportissuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_ReportissuesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel17)
                .addGap(18, 18, 18)
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        Panel_ReportissuesLayout.setVerticalGroup(
            Panel_ReportissuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(Panel_ReportissuesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(Panel_Reportissues, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 380, 200, 50));

        Panel_Logout.setBackground(new java.awt.Color(4, 35, 63));
        Panel_Logout.setForeground(new java.awt.Color(255, 255, 255));
        Panel_Logout.setPreferredSize(new java.awt.Dimension(200, 50));
        Panel_Logout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Panel_LogoutMouseClicked(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Logout");

        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icon_logout.png"))); // NOI18N

        javax.swing.GroupLayout Panel_LogoutLayout = new javax.swing.GroupLayout(Panel_Logout);
        Panel_Logout.setLayout(Panel_LogoutLayout);
        Panel_LogoutLayout.setHorizontalGroup(
            Panel_LogoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_LogoutLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        Panel_LogoutLayout.setVerticalGroup(
            Panel_LogoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
            .addGroup(Panel_LogoutLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(Panel_Logout, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 430, 200, 50));

        dateField.setBackground(new java.awt.Color(4, 35, 63));
        dateField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        dateField.setForeground(new java.awt.Color(255, 255, 255));
        dateField.setText("Date:");
        jPanel2.add(dateField, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 560, 140, -1));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/drawer.png"))); // NOI18N
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 600));

        jPanel1.setPreferredSize(new java.awt.Dimension(1000, 80));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Title_text.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        Title_text.setForeground(new java.awt.Color(255, 255, 255));
        Title_text.setText("Dashboard");
        jPanel1.add(Title_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 20, 250, -1));

        user_contact.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        user_contact.setForeground(new java.awt.Color(255, 255, 255));
        user_contact.setText("Name");
        jPanel1.add(user_contact, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 40, 200, -1));

        username.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        username.setForeground(new java.awt.Color(255, 255, 255));
        username.setText("Name");
        jPanel1.add(username, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 10, 200, -1));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/header.png"))); // NOI18N
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.setLayout(new java.awt.CardLayout());

        User_Dashboard.setBackground(new java.awt.Color(255, 255, 255));
        User_Dashboard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(201, 0, 50));
        jLabel28.setText("Account Information");
        User_Dashboard.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, -1, -1));

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(201, 0, 50));
        jLabel29.setText("Offers and Subscriptions");
        User_Dashboard.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 310, -1, -1));

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(4, 35, 63));
        jLabel30.setText("Total Balance Added");
        User_Dashboard.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 160, -1, -1));

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(5, 120, 60));
        jLabel31.setText("TK. 0");
        User_Dashboard.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 110, -1, -1));

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(4, 35, 63));
        jLabel32.setText("Total Bill Paid");
        User_Dashboard.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 160, -1, -1));

        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(4, 35, 63));
        jLabel33.setText("Last Transaction");
        User_Dashboard.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, -1, -1));

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(4, 35, 63));
        jLabel34.setText("Bill Paid in Last Month");
        User_Dashboard.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 260, -1, -1));

        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(4, 35, 63));
        jLabel35.setText("Balance Added in Last Month");
        User_Dashboard.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 260, -1, -1));

        jLabel36.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(4, 35, 63));
        jLabel36.setText("Available Offers");
        User_Dashboard.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 420, -1, -1));

        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(4, 35, 63));
        jLabel37.setText("Subscriptions");
        User_Dashboard.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 420, -1, -1));

        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(4, 35, 63));
        jLabel38.setText("Current Balance");
        User_Dashboard.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, -1, -1));

        jLabel39.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(5, 120, 60));
        jLabel39.setText("TK. 0");
        User_Dashboard.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, -1, -1));

        jLabel40.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(5, 120, 60));
        jLabel40.setText("TK. 0");
        User_Dashboard.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 110, -1, -1));

        jLabel41.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(5, 120, 60));
        jLabel41.setText("TK. 0");
        User_Dashboard.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, -1, -1));

        jLabel42.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(5, 120, 60));
        jLabel42.setText("TK. 0");
        User_Dashboard.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 210, -1, -1));

        jLabel43.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(5, 120, 60));
        jLabel43.setText("TK. 0");
        User_Dashboard.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 210, -1, -1));

        jLabel44.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(5, 120, 60));
        jLabel44.setText("0");
        User_Dashboard.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 380, -1, -1));

        jLabel45.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(5, 120, 60));
        jLabel45.setText("0");
        User_Dashboard.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 380, -1, -1));

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_addBalance.png"))); // NOI18N
        jButton1.setBorder(null);
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });
        User_Dashboard.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 40, -1, -1));

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_PayBill.png"))); // NOI18N
        jButton2.setBorder(null);
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });
        User_Dashboard.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 40, -1, -1));

        bg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/dashboard_bg.png"))); // NOI18N
        User_Dashboard.add(bg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(User_Dashboard, "card2");

        User_Paybill.setBackground(new java.awt.Color(255, 255, 255));
        User_Paybill.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel20.setText("Mobile No :");
        User_Paybill.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 130, -1, -1));

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel21.setText("Biller ID :");
        User_Paybill.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 80, -1, -1));

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel22.setText("Amount :");
        User_Paybill.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 180, -1, -1));

        jLabel46.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel46.setText("Reference:");
        User_Paybill.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 230, -1, -1));

        jLabel47.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel47.setText("Voucher:");
        User_Paybill.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 280, -1, -1));

        jLabel48.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel48.setText("Password:");
        User_Paybill.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 320, -1, -1));

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_PayBill.png"))); // NOI18N
        jButton3.setBorder(null);
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton3MouseClicked(evt);
            }
        });
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        User_Paybill.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 390, -1, -1));

        billerID.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        billerID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        billerID.setBorder(null);
        User_Paybill.add(billerID, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 80, 270, 30));

        voucher.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        voucher.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        voucher.setBorder(null);
        User_Paybill.add(voucher, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 276, 270, 30));

        billerContact.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        billerContact.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        billerContact.setBorder(null);
        User_Paybill.add(billerContact, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 130, 270, 30));

        amount.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        amount.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        amount.setBorder(null);
        User_Paybill.add(amount, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 178, 270, 30));

        reference.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        reference.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        reference.setBorder(null);
        User_Paybill.add(reference, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 228, 270, 30));

        password.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        password.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        password.setBorder(null);
        User_Paybill.add(password, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 322, 270, 30));

        bg_paybill.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        bg_paybill.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/bg_payBill.png"))); // NOI18N
        User_Paybill.add(bg_paybill, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(User_Paybill, "card2");

        User_Transactions.setBackground(new java.awt.Color(255, 255, 255));
        User_Transactions.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        transaction_table.setBackground(new java.awt.Color(200, 0, 50));
        transaction_table.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        transaction_table.setForeground(new java.awt.Color(255, 255, 255));
        transaction_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Transaction ID", "Date", "Amount", "Seneder ID", "Sender Contact", "Receiver ID", "Receiver Contact", "Reference"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        transaction_table.setColumnSelectionAllowed(true);
        jScrollPane2.setViewportView(transaction_table);
        transaction_table.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (transaction_table.getColumnModel().getColumnCount() > 0) {
            transaction_table.getColumnModel().getColumn(5).setResizable(false);
            transaction_table.getColumnModel().getColumn(5).setHeaderValue("Receiver ID");
            transaction_table.getColumnModel().getColumn(6).setHeaderValue("Receiver Contact");
            transaction_table.getColumnModel().getColumn(7).setHeaderValue("Reference");
        }

        User_Transactions.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 760, 410));

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(201, 0, 50));
        jLabel23.setText("From:");
        User_Transactions.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, -1));

        DateFrom.setDateFormatString("yyyy-MM-dd");
        User_Transactions.add(DateFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 30, 150, -1));

        DateTo.setDateFormatString("yyyy-MM-dd");
        User_Transactions.add(DateTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 30, 150, -1));

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_search.png"))); // NOI18N
        jButton6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton6MouseClicked(evt);
            }
        });
        User_Transactions.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 30, 140, 30));

        jLabel60.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel60.setForeground(new java.awt.Color(201, 0, 50));
        jLabel60.setText("To:");
        User_Transactions.add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 30, -1, -1));

        jLabel59.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/bg_transactions.png"))); // NOI18N
        User_Transactions.add(jLabel59, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(User_Transactions, "card2");

        User_Addbalance.setBackground(new java.awt.Color(255, 255, 255));
        User_Addbalance.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel49.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel49.setText("Agent ID :");
        User_Addbalance.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 110, -1, -1));

        jLabel50.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel50.setText("Mobile No :");
        User_Addbalance.add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 170, -1, -1));

        jLabel51.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel51.setText("Amount :");
        User_Addbalance.add(jLabel51, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 226, -1, -1));

        jLabel52.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel52.setText("Reference:");
        User_Addbalance.add(jLabel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 282, -1, -1));

        jLabel54.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel54.setText("Password:");
        User_Addbalance.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 340, -1, -1));

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_sendRequest.png"))); // NOI18N
        jButton4.setBorder(null);
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton4MouseClicked(evt);
            }
        });
        User_Addbalance.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 390, -1, -1));

        AgentID.setBackground(new java.awt.Color(245, 245, 245));
        AgentID.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        AgentID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        AgentID.setBorder(null);
        User_Addbalance.add(AgentID, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 116, 250, 25));

        MobileNo.setBackground(new java.awt.Color(245, 245, 245));
        MobileNo.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        MobileNo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        MobileNo.setBorder(null);
        User_Addbalance.add(MobileNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 174, 250, 25));

        AddAmount.setBackground(new java.awt.Color(245, 245, 245));
        AddAmount.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        AddAmount.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        AddAmount.setBorder(null);
        User_Addbalance.add(AddAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 230, 250, 25));

        Reference.setBackground(new java.awt.Color(245, 245, 245));
        Reference.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        Reference.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Reference.setBorder(null);
        Reference.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReferenceActionPerformed(evt);
            }
        });
        User_Addbalance.add(Reference, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 288, 250, 25));

        Password.setBackground(new java.awt.Color(245, 245, 245));
        Password.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        Password.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Password.setBorder(null);
        User_Addbalance.add(Password, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 344, 250, 25));

        bg_paybill1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        bg_paybill1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/bg_adminSendMoney.png"))); // NOI18N
        User_Addbalance.add(bg_paybill1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(User_Addbalance, "card2");

        User_Subscriptions.setBackground(new java.awt.Color(255, 255, 255));
        User_Subscriptions.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        subscription_table.setBackground(new java.awt.Color(200, 0, 50));
        subscription_table.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        subscription_table.setForeground(new java.awt.Color(255, 255, 255));
        subscription_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Biller ID", "Company Name", "Branch", "Address", "Contact No"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        subscription_table.setColumnSelectionAllowed(true);
        jScrollPane3.setViewportView(subscription_table);
        subscription_table.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        User_Subscriptions.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 760, 450));

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/bg_subscriptions.png"))); // NOI18N
        User_Subscriptions.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(User_Subscriptions, "card2");

        User_Availableoffers.setBackground(new java.awt.Color(255, 255, 255));
        User_Availableoffers.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        offer_table.setBackground(new java.awt.Color(200, 0, 50));
        offer_table.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        offer_table.setForeground(new java.awt.Color(255, 255, 255));
        offer_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Biller ID", "Biller Name", "Rate", "Max. Discount", "Min. Purchase", "Deadline", "Voucher"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(offer_table);
        offer_table.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        User_Availableoffers.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 760, 450));

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/bg_offers.png"))); // NOI18N
        User_Availableoffers.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(User_Availableoffers, "card2");

        User_Reportissues.setBackground(new java.awt.Color(255, 255, 255));
        User_Reportissues.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        TxnID.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        TxnID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        TxnID.setBorder(null);
        TxnID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxnIDActionPerformed(evt);
            }
        });
        User_Reportissues.add(TxnID, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 80, 230, 30));

        ReportedID.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        ReportedID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        ReportedID.setBorder(null);
        ReportedID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReportedIDActionPerformed(evt);
            }
        });
        User_Reportissues.add(ReportedID, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 135, 230, 30));

        Amount.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        Amount.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Amount.setBorder(null);
        Amount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AmountActionPerformed(evt);
            }
        });
        User_Reportissues.add(Amount, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 245, 230, 30));

        Description.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        Description.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Description.setBorder(null);
        Description.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DescriptionActionPerformed(evt);
            }
        });
        User_Reportissues.add(Description, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 356, 230, 30));

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel24.setText("Transaction ID :");
        User_Reportissues.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 80, -1, -1));

        jLabel55.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel55.setText("Agent/ Biller ID :");
        User_Reportissues.add(jLabel55, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 135, -1, -1));

        jLabel56.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel56.setText("Amount :");
        User_Reportissues.add(jLabel56, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 250, -1, -1));

        jLabel57.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel57.setText("Date:");
        User_Reportissues.add(jLabel57, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 300, -1, -1));

        jLabel58.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel58.setText("Description:");
        User_Reportissues.add(jLabel58, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 350, -1, -1));

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_submit.png"))); // NOI18N
        jButton5.setBorder(null);
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton5MouseClicked(evt);
            }
        });
        User_Reportissues.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 420, -1, -1));

        radio_biller.setBackground(new java.awt.Color(255, 255, 255));
        Acc_Type.add(radio_biller);
        radio_biller.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        radio_biller.setText("Biller");
        radio_biller.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_billerActionPerformed(evt);
            }
        });
        User_Reportissues.add(radio_biller, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 190, -1, -1));

        radio_agent.setBackground(new java.awt.Color(255, 255, 255));
        Acc_Type.add(radio_agent);
        radio_agent.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        radio_agent.setText("Agent");
        radio_agent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_agentActionPerformed(evt);
            }
        });
        User_Reportissues.add(radio_agent, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 190, -1, -1));

        Txn_Date.setDateFormatString("yyyy-MM-dd");
        User_Reportissues.add(Txn_Date, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 302, 250, 30));

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/bg_reportIssues.png"))); // NOI18N
        User_Reportissues.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(User_Reportissues, "card2");

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 80, 800, 520));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/white_bg.png"))); // NOI18N
        jLabel1.setText("dfghjkl");
        jLabel1.setPreferredSize(new java.awt.Dimension(1000, 600));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Panel_DashboardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_DashboardMouseClicked
        // TODO add your handling code here:

        //Magenda
        panelColor(Panel_Dashboard);
        Title_text.setText("Dashboard");

        //Drak Blue
        resetPanelColor(Panel_Paybill);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_Addbalance);
        resetPanelColor(Panel_Subscriptions);
        resetPanelColor(Panel_Availableoffers);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(User_Dashboard);
        jPanel3.repaint();
        jPanel3.revalidate();

        try {

            String contact = user_contact.getText();
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "select * from INFORMATION where Contact = " + contact
            );
            //Current Balance
            resultSet.next();
            double x = resultSet.getDouble("CurrBalance");
            jLabel39.setText("TK. " + Double.toString(x));

            resultSet = statement.executeQuery(
                    //AD-2
                    "select sum(Amount) as SUM from TXN where SenderID in (select infoID from INFORMATION where Contact = "
                    + contact + ")"
            );
            //Total Bill Paid, Total balance Added
            if (resultSet.next()) {
                double x2 = resultSet.getDouble("SUM");
                jLabel40.setText("TK. " + Double.toString(x2));
                jLabel31.setText("TK. " + Double.toString((x2 + x)));
            }

            resultSet = statement.executeQuery(
                    "select infoID from INFORMATION where Contact = " + contact
            );
            resultSet.next();
            String infoID = Long.toString(resultSet.getLong("infoID"));

            //Last Transaction
            resultSet = statement.executeQuery(
                    "select top 1 Amount from TXN where SenderID = " + infoID
                    + " or ReceiverID = " + infoID + " order by Date desc"
            );
            if (resultSet.next()) {
                float x2 = resultSet.getFloat("Amount");
                jLabel41.setText("TK. " + Float.toString(x2));

            }

            //Bill Paid in last month
            resultSet = statement.executeQuery(
                    //AD-3
                    "select sum(Amount) as SUM from TXN where SenderID = " + infoID
                    + " and DATEDIFF(MONTH, Date, GETDATE()) <= 1"
            );
            if (resultSet.next()) {
                double x2 = resultSet.getDouble("SUM");
                jLabel42.setText("TK. " + Double.toString(x2));
            }
            //Balance Added in Last Month
            resultSet = statement.executeQuery(
                    "select sum(Amount) as SUM from TXN where ReceiverID = " + infoID
                    + " and DATEDIFF(MONTH, Date, GETDATE()) <= 1"
            );
            if (resultSet.next()) {
                double x2 = resultSet.getDouble("SUM");
                jLabel43.setText("TK. " + Double.toString(x2));
            }

            //Available Offers
            resultSet = statement.executeQuery(
                    "select count(OfferID) as noOfOffer from OFFER where DATEDIFF(DAY, DeadLine, GETDATE()) <= 0"
            );
            if (resultSet.next()) {
                int x2 = resultSet.getInt("noOfOffer");
                jLabel44.setText(Integer.toString(x2));
            }

            //Subscriptions
            resultSet = statement.executeQuery(
                    "select count(distinct ReceiverID) as noOfSubscriptions from TXN where SenderID = " + infoID
            );
            if (resultSet.next()) {
                int x2 = resultSet.getInt("noOfSubscriptions");
                jLabel45.setText(Integer.toString(x2));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_Panel_DashboardMouseClicked

    private void Panel_PaybillMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_PaybillMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_Paybill);
        Title_text.setText("Pay Bill");

        //Drak Blue
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_Addbalance);
        resetPanelColor(Panel_Subscriptions);
        resetPanelColor(Panel_Availableoffers);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(User_Paybill);
        jPanel3.repaint();
        jPanel3.revalidate();
    }//GEN-LAST:event_Panel_PaybillMouseClicked

    private void Panel_TransactionsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_TransactionsMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_Transactions);
        Title_text.setText("Transactions");

        //Drak Blue
        resetPanelColor(Panel_Paybill);
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_Addbalance);
        resetPanelColor(Panel_Subscriptions);
        resetPanelColor(Panel_Availableoffers);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(User_Transactions);
        jPanel3.repaint();
        jPanel3.revalidate();
        //show_transaction();
    }//GEN-LAST:event_Panel_TransactionsMouseClicked

    private void Panel_AddbalanceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_AddbalanceMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_Addbalance);
        Title_text.setText("Add Balance");
        //Drak Blue
        resetPanelColor(Panel_Paybill);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_Subscriptions);
        resetPanelColor(Panel_Availableoffers);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(User_Addbalance);
        jPanel3.repaint();
        jPanel3.revalidate();
    }//GEN-LAST:event_Panel_AddbalanceMouseClicked

    private void Panel_SubscriptionsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_SubscriptionsMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_Subscriptions);
        Title_text.setText("Subscriptions");

        //Drak Blue
        resetPanelColor(Panel_Paybill);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_Addbalance);
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_Availableoffers);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(User_Subscriptions);
        jPanel3.repaint();
        jPanel3.revalidate();
    }//GEN-LAST:event_Panel_SubscriptionsMouseClicked

    private void Panel_AvailableoffersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_AvailableoffersMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_Availableoffers);
        Title_text.setText("Available Offers");

        //Drak Blue
        resetPanelColor(Panel_Paybill);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_Addbalance);
        resetPanelColor(Panel_Subscriptions);
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(User_Availableoffers);
        jPanel3.repaint();
        jPanel3.revalidate();
        
        //show_offers();
        
    }//GEN-LAST:event_Panel_AvailableoffersMouseClicked

    private void Panel_ReportissuesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_ReportissuesMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_Reportissues);
        Title_text.setText("Report Issues");

        //Drak Blue
        resetPanelColor(Panel_Paybill);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_Addbalance);
        resetPanelColor(Panel_Subscriptions);
        resetPanelColor(Panel_Availableoffers);
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(User_Reportissues);
        jPanel3.repaint();
        jPanel3.revalidate();
    }//GEN-LAST:event_Panel_ReportissuesMouseClicked

    private void Panel_LogoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_LogoutMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_Logout);
        Title_text.setText("Logout");

        //Drak Blue
        resetPanelColor(Panel_Paybill);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_Addbalance);
        resetPanelColor(Panel_Subscriptions);
        resetPanelColor(Panel_Availableoffers);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Dashboard);

        int choice = JOptionPane.showConfirmDialog(null, "Do you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            LoginForm lf = new LoginForm();
            lf.setVisible(true);
            lf.pack();
            lf.setLocationRelativeTo(null);
            this.dispose();
        } else {
            //Magenda
            panelColor(Panel_Dashboard);
            Title_text.setText("Dashboard");

            //Drak Blue
            resetPanelColor(Panel_Paybill);
            resetPanelColor(Panel_Transactions);
            resetPanelColor(Panel_Addbalance);
            resetPanelColor(Panel_Subscriptions);
            resetPanelColor(Panel_Availableoffers);
            resetPanelColor(Panel_Reportissues);
            resetPanelColor(Panel_Logout);
            //remove panel
            jPanel3.removeAll();
            jPanel3.repaint();
            jPanel3.revalidate();

            // add panel
            jPanel3.add(User_Dashboard);
            jPanel3.repaint();
            jPanel3.revalidate();
        }
    }//GEN-LAST:event_Panel_LogoutMouseClicked

    private void ReportedIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReportedIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ReportedIDActionPerformed

    private void DescriptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DescriptionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DescriptionActionPerformed

    private void TxnIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxnIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxnIDActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jButton3ActionPerformed

    private void AmountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AmountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AmountActionPerformed

    private void radio_agentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio_agentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radio_agentActionPerformed

    private void radio_billerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio_billerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radio_billerActionPerformed

    private void jButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseClicked
        // TODO add your handling code here:

        try {

            String biller_id = billerID.getText();
            String biller_contact = billerContact.getText();
            String paid_amount = amount.getText();
            double bill = Double.parseDouble(paid_amount);
            String user_reference = reference.getText();
            String available_voucher = voucher.getText();
            String pass = String.valueOf(password.getText());

            String sender_contact = user_contact.getText();

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
            System.out.println("2. DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());

            String query = "select Contact,(select CurrBalance from Information where InfoID=(select infoId from Information where Contact=" + sender_contact + ")) as SenderBalance, "
                    + "(select CurrBalance from Information where InfoID=(select infoId from Information where Contact=" + biller_contact + ")) as BillerBalance, "
                    + "(select InfoID from Information where Contact=" + sender_contact + ") as SenderID, "
                    + "(select InfoID from Information where Contact=" + biller_contact + ") as ReceiverID, "
                    + "(select Password from Information where InfoID=(select infoId from Information where Contact=" + sender_contact + ")) as Password,"
                    + "(select Voucher from OFFER where BillerID=" + biller_id + " and DeadLine>=GETDATE()) as Voucher, "
                    + "(select Rate from OFFER where BillerID=" + biller_id + " and DeadLine>=GETDATE()) as Rate, "
                    + "(select MaxDiscount from OFFER where BillerID=" + biller_id + " and DeadLine>=GETDATE()) as MaxDiscount, "
                    + "(select MinPurchase from OFFER where BillerID=" + biller_id + " and DeadLine>=GETDATE()) as MinPurchase "
                    + "from Biller where BillerID=" + biller_id;

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            String DB_contact, DB_user_pass, DB_voucher;
            double curr_balance_personal, curr_balance_biller;
            int rate, SenID, RecID;
            double max_disc, min_purchase, disc_gained = 0;

            while (rs.next()) {
                DB_contact = rs.getString("Contact");
                curr_balance_personal = rs.getDouble("SenderBalance");
                curr_balance_biller = rs.getDouble("BillerBalance");
                DB_user_pass = rs.getString("Password");
                DB_voucher = rs.getString("Voucher");
                rate = rs.getInt("Rate");
                SenID = rs.getInt("SenderID");
                RecID = rs.getInt("ReceiverID");
                max_disc = rs.getDouble("MaxDiscount");
                min_purchase = rs.getDouble("MinPurchase");
                System.out.println(DB_contact);
                System.out.println(curr_balance_personal);
                System.out.println(curr_balance_biller);
                System.out.println(DB_user_pass);
                System.out.println(DB_voucher);
                System.out.println(rate);
                System.out.println(max_disc);
                System.out.println(min_purchase);

                if (bill >= min_purchase && available_voucher.equals(DB_voucher)) {
                    disc_gained = rate * .01 * bill;
                    if (disc_gained > max_disc) {
                        disc_gained = max_disc;
                    }

                }

                bill = bill - disc_gained;

                try {

                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                    connection = DriverManager.getConnection(
                            "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");

                    System.out.println("DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());

                    if (curr_balance_personal >= bill && pass.equals(DB_user_pass)) {
                        curr_balance_personal = curr_balance_personal - bill;
                        curr_balance_biller = curr_balance_biller + bill;

                        String query1 = "UPDATE INFORMATION SET CurrBalance = ? where Contact = " + sender_contact;
                        PreparedStatement st1 = connection.prepareStatement(query1);
                        st1.setDouble(1, curr_balance_personal);
                        st1.executeUpdate();

                        System.out.println("Passed1");

                        String query2 = "UPDATE INFORMATION SET CurrBalance = ? where Contact = " + biller_contact;
                        PreparedStatement st2 = connection.prepareStatement(query2);
                        st2.setDouble(1, curr_balance_biller);
                        st2.executeUpdate();

                        System.out.println("Passed2");

                        String query3 = "Insert into TXN (Date, Amount, SenderID, ReceiverID, Reference)values (getdate(), ?,?,?,?)";
                        PreparedStatement st3 = connection.prepareStatement(query3);
                        st3.setDouble(1, bill);
                        st3.setInt(2, SenID);
                        st3.setInt(3, RecID);
                        st3.setString(4, user_reference);
                        st3.executeUpdate();

                        System.out.println("Passed3");
                        JOptionPane.showMessageDialog(null, "Bill paid successfully.");

                        billerID.setText("");
                        billerContact.setText("");
                        amount.setText("");
                        reference.setText("");
                        voucher.setText("");
                        password.setText("");

                    } else {
                        JOptionPane.showMessageDialog(null, "Incorrect Information or Insufficient Balance");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }//GEN-LAST:event_jButton3MouseClicked

    private void jButton5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton5MouseClicked
        // TODO add your handling code here:

        try {

            String phoneNo = user_contact.getText();

            String txn_id_sp = TxnID.getText();
            int txn_id = Integer.parseInt(txn_id_sp);
            String reported_id_sp = ReportedID.getText();
            int reported_id = Integer.parseInt(reported_id_sp);

            String amount_sp = Amount.getText();
            double amount = Double.parseDouble(amount_sp);
            String description = Description.getText();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String txn_date = sdf.format(Txn_Date.getDate());

            System.out.println(amount);

            String acc_type;
            System.out.println("Passed gegee");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");

            System.out.println("DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());

            String query = "SELECT Date, Amount, SenderID, ReceiverID from TXN  where TransactionID=" + txn_id;
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            Date DB_date_1;

            String DB_date, DB_status;
            double DB_amount;
            int DB_senderID, DB_receiverID;
            System.out.println("Passed");
            // flag is to be used
            int flag = 1;
            while (rs.next()) {
                flag = 0;
                DB_date_1 = rs.getDate("Date");
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                DB_date = sdf1.format(DB_date_1);
                DB_amount = rs.getDouble("Amount");
                DB_senderID = rs.getInt("SenderID");
                DB_receiverID = rs.getInt("ReceiverID");
                String query2 = "SELECT Status from ISSUE  where TransactionID=" + txn_id;
                Statement st2 = connection.createStatement();
                ResultSet rs2 = st2.executeQuery(query2);
                int status = 0;
                while (rs2.next()) {
                    
                    DB_status = rs2.getString("Status");
                    if(DB_status.equals("PENDING")){
                        status=1;
                    }
                }
                if (radio_agent.isSelected()) {
                        if (reported_id == DB_senderID && amount == DB_amount && txn_date.equals(DB_date)) {
                            if (status==1) {
                                JOptionPane.showMessageDialog(null, "The issue has already been submitted.\nHave Patience Dude!");
                            } else if(status==0) {
                                String query1 = "Insert into ISSUE (TransactionID, SenderID, ReceiverID, Description, Status)values (?,?,(SELECT InfoID from Information where Contact = " + phoneNo + "),?,'PENDING')";
                                PreparedStatement st1 = connection.prepareStatement(query1);
                                st1.setInt(1, txn_id);
                                st1.setInt(2, DB_senderID);
                                st1.setString(3, description);

                                st1.executeUpdate();
                                JOptionPane.showMessageDialog(null, "Issue submitted successfully");
                                TxnID.setText("");
                                ReportedID.setText("");
                                Amount.setText("");
                                Description.setText("");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Incorrect Information");
                        }
                    } else if (radio_biller.isSelected()) {
                        if (reported_id == DB_receiverID && amount == DB_amount && txn_date.equals(DB_date)) {
                            if (status==1) {
                                JOptionPane.showMessageDialog(null, "The issue has already been submitted.\nHave Ptience Dude!");
                            } else if(status==0) {
                                String query1 = "Insert into ISSUE (TransactionID, SenderID, ReceiverID, Description, Status)values (?,(SELECT InfoID from Information where Contact = " + phoneNo + "),?,?,'PENDING')";
                                PreparedStatement st1 = connection.prepareStatement(query1);
                                st1.setInt(1, txn_id);
                                st1.setInt(2, DB_receiverID);
                                st1.setString(3, description);

                                st1.executeUpdate();
                                JOptionPane.showMessageDialog(null, "Issue submitted successfully");
                                TxnID.setText("");
                                ReportedID.setText("");
                                Amount.setText("");
                                Description.setText("");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Incorrect Information");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Please choose an account type.");
                    }
                
            }

            System.out.println("Passed1");

            if (flag == 1) {
                JOptionPane.showMessageDialog(null, "Incorrect Transaction ID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }//GEN-LAST:event_jButton5MouseClicked

    private void jButton6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton6MouseClicked
        // TODO add your handling code here:
        show_transaction();
        
    }//GEN-LAST:event_jButton6MouseClicked

    private void ReferenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReferenceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ReferenceActionPerformed

    private void jButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseClicked
        // TODO add your handling code here:
        try {

            String a_id = AgentID.getText();
            int agent_id = Integer.parseInt(a_id);
            String agent_contact = MobileNo.getText();
            String a_amount = AddAmount.getText();
            double add_amount = Double.parseDouble(a_amount);
            String reference = Reference.getText();
            String pass = String.valueOf(Password.getText());

            String sender_contact = user_contact.getText();

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
            System.out.println("2. DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());

            String query = "SELECT Password FROM INFORMATION where Contact = "+sender_contact;
            
            String DB_pass ="", DB_Contact="";
            double DB_curr_balance=0.0;
            
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            
            if(rs.next()){
                DB_pass = rs.getString("Password");
            }
            if(pass.equals(DB_pass)){
                query = "SELECT Contact, CurrBalance FROM INFORMATION where InfoID ="+ agent_id;
                st = connection.createStatement();
                rs = st.executeQuery(query);
                if(rs.next()){
                    DB_Contact = rs.getString("Contact");
                    DB_curr_balance = rs.getDouble("CurrBalance");
                }
                
                if(agent_contact.equals(DB_Contact)){
//                    DB_curr_balance = DB_curr_balance + send_amount;
//                    
//                    query = "UPDATE INFORMATION SET CurrBalance = ? where Contact = " + agent_contact;
//                    PreparedStatement st2 = connection.prepareStatement(query);
//                    st2.setDouble(1, DB_curr_balance);
//                    st2.executeUpdate();
                    
                    query = "Insert into REQUEST (ReceiverID, SenderID, Amount, Reference, Flag)values ((SELECT InfoID From INFORMATION where Contact = "+sender_contact+"),?,?,?,'P')";
                    PreparedStatement st3 = connection.prepareStatement(query);
                    st3.setInt(1, agent_id);
                    st3.setDouble(2, add_amount);
                    st3.setString(3, reference);
                    st3.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Your request is waiting for approval");
                    
                    AgentID.setText("");
                    MobileNo.setText("");
                    AddAmount.setText("");
                    Reference.setText("");
                    Password.setText("");
                    
                }else{
                    
                    JOptionPane.showMessageDialog(null, "Incorrect Contact");  
                }
            }else{
                 JOptionPane.showMessageDialog(null, "Incorrect Password");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton4MouseClicked

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        // TODO add your handling code here:
         //Magenda
        panelColor(Panel_Paybill);
        Title_text.setText("Pay Bill");

        //Drak Blue
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_Addbalance);
        resetPanelColor(Panel_Subscriptions);
        resetPanelColor(Panel_Availableoffers);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(User_Paybill);
        jPanel3.repaint();
        jPanel3.revalidate();
    }//GEN-LAST:event_jButton2MouseClicked

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        // TODO add your handling code here:
         // TODO add your handling code here:
        //Magenda
        panelColor(Panel_Addbalance);
        Title_text.setText("Add Balance");
        //Drak Blue
        resetPanelColor(Panel_Paybill);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_Subscriptions);
        resetPanelColor(Panel_Availableoffers);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(User_Addbalance);
        jPanel3.repaint();
        jPanel3.revalidate();
    }//GEN-LAST:event_jButton1MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HomepagePersonal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomepagePersonal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomepagePersonal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomepagePersonal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HomepagePersonal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup Acc_Type;
    private javax.swing.JTextField AddAmount;
    private javax.swing.JTextField AgentID;
    private javax.swing.JTextField Amount;
    private com.toedter.calendar.JDateChooser DateFrom;
    private com.toedter.calendar.JDateChooser DateTo;
    private javax.swing.JTextField Description;
    private javax.swing.JTextField MobileNo;
    private javax.swing.JPanel Panel_Addbalance;
    private javax.swing.JPanel Panel_Availableoffers;
    private javax.swing.JPanel Panel_Dashboard;
    private javax.swing.JPanel Panel_Logout;
    private javax.swing.JPanel Panel_Paybill;
    private javax.swing.JPanel Panel_Reportissues;
    private javax.swing.JPanel Panel_Subscriptions;
    private javax.swing.JPanel Panel_Transactions;
    private javax.swing.JPasswordField Password;
    private javax.swing.JTextField Reference;
    private javax.swing.JTextField ReportedID;
    private javax.swing.JLabel Title_text;
    private javax.swing.JTextField TxnID;
    private com.toedter.calendar.JDateChooser Txn_Date;
    private javax.swing.JPanel User_Addbalance;
    private javax.swing.JPanel User_Availableoffers;
    private javax.swing.JPanel User_Dashboard;
    private javax.swing.JPanel User_Paybill;
    private javax.swing.JPanel User_Reportissues;
    private javax.swing.JPanel User_Subscriptions;
    private javax.swing.JPanel User_Transactions;
    private javax.swing.JTextField amount;
    private javax.swing.JLabel bg;
    private javax.swing.JLabel bg_paybill;
    private javax.swing.JLabel bg_paybill1;
    private javax.swing.JTextField billerContact;
    private javax.swing.JTextField billerID;
    private javax.swing.JLabel dateField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private com.toedter.calendar.JCalendar jCalendar1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable offer_table;
    private javax.swing.JPasswordField password;
    private javax.swing.JRadioButton radio_agent;
    private javax.swing.JRadioButton radio_biller;
    private javax.swing.JTextField reference;
    private javax.swing.JTable subscription_table;
    private com.toedter.calendar.demo.TestDateEvaluator testDateEvaluator1;
    private com.toedter.calendar.demo.TestDateEvaluator testDateEvaluator2;
    private javax.swing.JTable transaction_table;
    private javax.swing.JLabel user_contact;
    private javax.swing.JLabel username;
    private javax.swing.JTextField voucher;
    // End of variables declaration//GEN-END:variables

    // set and reset panel color functions
    private void panelColor(JPanel panel) {
        panel.setBackground(new java.awt.Color(201, 0, 50));
    }

    private void resetPanelColor(JPanel panel) {
        panel.setBackground(new java.awt.Color(4, 35, 63));
    }

    // date: Auto update function
    public void setCalendar() {
        Calendar cal = new GregorianCalendar();
        int dd = cal.get(Calendar.DAY_OF_MONTH);
        int mm = cal.get(Calendar.MONTH);
        int yy = cal.get(Calendar.YEAR);
        dateField.setText("Date: " + yy + "-" + (mm + 1) + "-" + dd);
    }

}
