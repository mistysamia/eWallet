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
public class HomepageAgent extends javax.swing.JFrame {

    /**
     * Creates new form HomepagePersonal
     */
    public HomepageAgent() {
        initComponents();
        this.setLocationRelativeTo(null);
        setCalendar();
        show_pendinReq();
        show_otherReq();
    }

    public HomepageAgent(String name, String phoneNo) {
        initComponents();
        this.setLocationRelativeTo(null);
        setCalendar();
        username.setText(name);
        user_contact.setText(phoneNo);
        show_pendinReq();
        show_otherReq();
    }

    //Biller Transaction
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
        for (int i = list.size() - 1; i >= 0; i--) {
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

    //Pending Requests
    public ArrayList<agent_request> pendingList() {
        ArrayList<agent_request> pendingList = new ArrayList<>();
        String phoneNo = user_contact.getText();
        try {
//            
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
            System.out.println("1. DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());

            String query = "SELECT * from REQUEST where Flag = 'P' and (SenderID = (SELECT InfoID FROM INFORMATION where Contact = " + phoneNo + ") OR ReceiverID = (SELECT InfoID FROM INFORMATION where Contact = " + phoneNo + "))";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            agent_request pendingReq;

            while (rs.next()) {
                int req_id = rs.getInt("ReqID");
                int sender_id = rs.getInt("SenderID");
                int receiver_id = rs.getInt("ReceiverID");
                double amount = rs.getDouble("Amount");
                String reference = rs.getString("Reference");
                String status = rs.getString("Flag");

                pendingReq = new agent_request(req_id, sender_id, receiver_id, amount, reference, status);
                pendingList.add(pendingReq);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pendingList;
    }

    public void show_pendinReq() {
        ArrayList<agent_request> list = pendingList();
        DefaultTableModel model = (DefaultTableModel) pending_table.getModel();
        model.setNumRows(0);
        for (int i = list.size() - 1; i >= 0; i--) {
            Object[] row = new Object[6];
            row[0] = list.get(i).getReqID();
            row[1] = list.get(i).getSenderID();
            row[2] = list.get(i).getReceiverID();
            row[3] = list.get(i).getAmount();
            row[4] = list.get(i).getReference();
            row[5] = list.get(i).getStatus();
            model.addRow(row);
        }

    }

    //Biller All Offers
    //Pending Requests
    public ArrayList<agent_request> otherList() {
        ArrayList<agent_request> otherList = new ArrayList<>();
        String phoneNo = user_contact.getText();
        try {
//            
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
            System.out.println("1. DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());

            String query = "SELECT * from REQUEST where Flag <> 'P' and (SenderID = (SELECT InfoID FROM INFORMATION where Contact = " + phoneNo + ") OR ReceiverID = (SELECT InfoID FROM INFORMATION where Contact = " + phoneNo + "))";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            agent_request pendingReq;

            while (rs.next()) {
                int req_id = rs.getInt("ReqID");
                int sender_id = rs.getInt("SenderID");
                int receiver_id = rs.getInt("ReceiverID");
                double amount = rs.getDouble("Amount");
                String reference = rs.getString("Reference");
                String status = rs.getString("Flag");

                pendingReq = new agent_request(req_id, sender_id, receiver_id, amount, reference, status);
                otherList.add(pendingReq);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return otherList;
    }

    public void show_otherReq() {
        ArrayList<agent_request> list = otherList();
        DefaultTableModel model = (DefaultTableModel) all_table.getModel();

        for (int i = list.size() - 1; i >= 0; i--) {
            Object[] row = new Object[6];
            row[0] = list.get(i).getReqID();
            row[1] = list.get(i).getSenderID();
            row[2] = list.get(i).getReceiverID();
            row[3] = list.get(i).getAmount();
            row[4] = list.get(i).getReference();
            row[5] = list.get(i).getStatus();
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
        acc_type = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        Panel_Dashboard = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        Panel_Requests = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        Panel_Transactions = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
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
        username = new javax.swing.JLabel();
        user_contact = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        Agent_Dashboard = new javax.swing.JPanel();
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
        Agent_Requests = new javax.swing.JPanel();
        jLabel62 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        pending_table = new javax.swing.JTable();
        jLabel63 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        all_table = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        bg_paybill = new javax.swing.JLabel();
        Agent_Transactions = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        DateFrom = new com.toedter.calendar.JDateChooser();
        jLabel60 = new javax.swing.JLabel();
        DateTo = new com.toedter.calendar.JDateChooser();
        jButton7 = new javax.swing.JButton();
        biller_transaction_table = new javax.swing.JScrollPane();
        transaction_table = new javax.swing.JTable();
        jLabel59 = new javax.swing.JLabel();
        Biller_Reportissues = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        Description = new javax.swing.JTextField();
        Txn_Date = new com.toedter.calendar.JDateChooser();
        Amount = new javax.swing.JTextField();
        radio_biller = new javax.swing.JRadioButton();
        radio_user = new javax.swing.JRadioButton();
        ReportedID = new javax.swing.JTextField();
        TxnID = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
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

        Panel_Requests.setBackground(new java.awt.Color(4, 35, 63));
        Panel_Requests.setForeground(new java.awt.Color(255, 255, 255));
        Panel_Requests.setPreferredSize(new java.awt.Dimension(200, 50));
        Panel_Requests.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Panel_RequestsMouseClicked(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Requests");

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icon_paybill.png"))); // NOI18N

        javax.swing.GroupLayout Panel_RequestsLayout = new javax.swing.GroupLayout(Panel_Requests);
        Panel_Requests.setLayout(Panel_RequestsLayout);
        Panel_RequestsLayout.setHorizontalGroup(
            Panel_RequestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_RequestsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        Panel_RequestsLayout.setVerticalGroup(
            Panel_RequestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(Panel_RequestsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(Panel_Requests, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 130, 200, 50));

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );
        Panel_ReportissuesLayout.setVerticalGroup(
            Panel_ReportissuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel_ReportissuesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Panel_ReportissuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(Panel_Reportissues, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 200, 50));

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

        jPanel2.add(Panel_Logout, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 280, 200, 50));

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

        username.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        username.setForeground(new java.awt.Color(255, 255, 255));
        username.setText("Name");
        jPanel1.add(username, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 10, 200, -1));

        user_contact.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        user_contact.setForeground(new java.awt.Color(255, 255, 255));
        user_contact.setText("Name");
        jPanel1.add(user_contact, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 40, 200, -1));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/header.png"))); // NOI18N
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.setLayout(new java.awt.CardLayout());

        Agent_Dashboard.setBackground(new java.awt.Color(255, 255, 255));
        Agent_Dashboard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(201, 0, 50));
        jLabel28.setText("Account Information");
        Agent_Dashboard.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, -1, -1));

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(201, 0, 50));
        jLabel29.setText("Customers");
        Agent_Dashboard.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 310, -1, -1));

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(4, 35, 63));
        jLabel30.setText("Total Received in Biller Acc.");
        Agent_Dashboard.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 160, -1, -1));

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(5, 120, 60));
        jLabel31.setText("TK. 0");
        Agent_Dashboard.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 110, -1, -1));

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(4, 35, 63));
        jLabel32.setText("Total Paid in Personal Acc.");
        Agent_Dashboard.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 160, -1, -1));

        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(4, 35, 63));
        jLabel33.setText("Last Transaction");
        Agent_Dashboard.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, -1, -1));

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(4, 35, 63));
        jLabel34.setText("Monthly Paid in Personal Acc.");
        Agent_Dashboard.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 260, -1, -1));

        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(4, 35, 63));
        jLabel35.setText("Monthly Received from Biller ");
        Agent_Dashboard.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 260, -1, -1));

        jLabel36.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(4, 35, 63));
        jLabel36.setText("Total No of Personal Customers");
        Agent_Dashboard.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 420, -1, -1));

        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(4, 35, 63));
        jLabel37.setText("Total No of Billers");
        Agent_Dashboard.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 420, -1, -1));

        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(4, 35, 63));
        jLabel38.setText("Current Balance");
        Agent_Dashboard.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, -1, -1));

        jLabel39.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(5, 120, 60));
        jLabel39.setText("TK. 0");
        Agent_Dashboard.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, -1, -1));

        jLabel40.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(5, 120, 60));
        jLabel40.setText("TK. 0");
        Agent_Dashboard.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 110, -1, -1));

        jLabel41.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(5, 120, 60));
        jLabel41.setText("TK. 0");
        Agent_Dashboard.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, -1, -1));

        jLabel42.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(5, 120, 60));
        jLabel42.setText("TK. 0");
        Agent_Dashboard.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 210, -1, -1));

        jLabel43.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(5, 120, 60));
        jLabel43.setText("TK. 0");
        Agent_Dashboard.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 210, -1, -1));

        jLabel44.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(5, 120, 60));
        jLabel44.setText("0");
        Agent_Dashboard.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 380, -1, -1));

        jLabel45.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(5, 120, 60));
        jLabel45.setText("0");
        Agent_Dashboard.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 380, -1, -1));

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_transactions.png"))); // NOI18N
        jButton1.setBorder(null);
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });
        Agent_Dashboard.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 40, -1, -1));

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_requests.png"))); // NOI18N
        jButton2.setBorder(null);
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });
        Agent_Dashboard.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 40, -1, -1));

        bg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/dashboard_bg.png"))); // NOI18N
        Agent_Dashboard.add(bg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(Agent_Dashboard, "card2");

        Agent_Requests.setBackground(new java.awt.Color(255, 255, 255));
        Agent_Requests.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel62.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel62.setForeground(new java.awt.Color(201, 0, 50));
        jLabel62.setText("Pending Requests");
        Agent_Requests.add(jLabel62, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));

        pending_table.setBackground(new java.awt.Color(200, 0, 50));
        pending_table.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        pending_table.setForeground(new java.awt.Color(255, 255, 255));
        pending_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Request ID", "Sender ID", "Receiver ID", "Amount", "Reference", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(pending_table);

        Agent_Requests.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 760, 140));

        jLabel63.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel63.setForeground(new java.awt.Color(201, 0, 50));
        jLabel63.setText("Other Requests");
        Agent_Requests.add(jLabel63, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 260, -1, -1));

        all_table.setBackground(new java.awt.Color(200, 0, 50));
        all_table.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        all_table.setForeground(new java.awt.Color(255, 255, 255));
        all_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Request ID", "Sender ID", "Receiver ID", "Amount", "Reference", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(all_table);

        Agent_Requests.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 300, 760, 200));

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_reject.png"))); // NOI18N
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
        Agent_Requests.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 50, 140, 30));

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_accept.png"))); // NOI18N
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton4MouseClicked(evt);
            }
        });
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        Agent_Requests.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 50, 140, 30));

        bg_paybill.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        bg_paybill.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/bg_transactions.png"))); // NOI18N
        Agent_Requests.add(bg_paybill, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(Agent_Requests, "card2");

        Agent_Transactions.setBackground(new java.awt.Color(255, 255, 255));
        Agent_Transactions.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(201, 0, 50));
        jLabel23.setText("From:");
        Agent_Transactions.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, -1));

        DateFrom.setDateFormatString("yyyy-MM-dd");
        Agent_Transactions.add(DateFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 30, 150, -1));

        jLabel60.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel60.setForeground(new java.awt.Color(201, 0, 50));
        jLabel60.setText("To:");
        Agent_Transactions.add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 30, -1, -1));

        DateTo.setDateFormatString("yyyy-MM-dd");
        Agent_Transactions.add(DateTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 30, 150, -1));

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_search.png"))); // NOI18N
        jButton7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton7MouseClicked(evt);
            }
        });
        Agent_Transactions.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 30, 140, 30));

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
        biller_transaction_table.setViewportView(transaction_table);

        Agent_Transactions.add(biller_transaction_table, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 760, 410));

        jLabel59.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/bg_transactions.png"))); // NOI18N
        Agent_Transactions.add(jLabel59, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(Agent_Transactions, "card2");

        Biller_Reportissues.setBackground(new java.awt.Color(255, 255, 255));
        Biller_Reportissues.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_submit.png"))); // NOI18N
        jButton5.setBorder(null);
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton5MouseClicked(evt);
            }
        });
        Biller_Reportissues.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 420, -1, -1));

        Description.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        Description.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Description.setBorder(null);
        Description.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DescriptionActionPerformed(evt);
            }
        });
        Biller_Reportissues.add(Description, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 356, 230, 30));

        Txn_Date.setDateFormatString("yyyy-MM-dd");
        Biller_Reportissues.add(Txn_Date, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 302, 250, 30));

        Amount.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        Amount.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Amount.setBorder(null);
        Amount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AmountActionPerformed(evt);
            }
        });
        Biller_Reportissues.add(Amount, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 245, 230, 30));

        radio_biller.setBackground(new java.awt.Color(255, 255, 255));
        acc_type.add(radio_biller);
        radio_biller.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        radio_biller.setText("Billler");
        radio_biller.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_billerActionPerformed(evt);
            }
        });
        Biller_Reportissues.add(radio_biller, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 190, -1, -1));

        radio_user.setBackground(new java.awt.Color(255, 255, 255));
        acc_type.add(radio_user);
        radio_user.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        radio_user.setText("User");
        radio_user.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_userActionPerformed(evt);
            }
        });
        Biller_Reportissues.add(radio_user, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 190, -1, -1));

        ReportedID.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        ReportedID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        ReportedID.setBorder(null);
        ReportedID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReportedIDActionPerformed(evt);
            }
        });
        Biller_Reportissues.add(ReportedID, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 135, 230, 30));

        TxnID.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        TxnID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        TxnID.setBorder(null);
        TxnID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxnIDActionPerformed(evt);
            }
        });
        Biller_Reportissues.add(TxnID, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 80, 230, 30));

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel24.setText("Transaction ID :");
        Biller_Reportissues.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 80, -1, -1));

        jLabel55.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel55.setText("Agent/ User ID :");
        Biller_Reportissues.add(jLabel55, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 135, -1, -1));

        jLabel56.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel56.setText("Amount :");
        Biller_Reportissues.add(jLabel56, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 250, -1, -1));

        jLabel57.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel57.setText("Date:");
        Biller_Reportissues.add(jLabel57, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 300, -1, -1));

        jLabel58.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel58.setText("Description:");
        Biller_Reportissues.add(jLabel58, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 350, -1, -1));

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/bg_reportIssues.png"))); // NOI18N
        Biller_Reportissues.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(Biller_Reportissues, "card2");

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
        resetPanelColor(Panel_Requests);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Agent_Dashboard);
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
            //Total Paid in Personal Account
            resultSet = statement.executeQuery(
                    //AD-2
                    "select sum(Amount) as SUM from TXN where SenderID in (select infoID from INFORMATION where Contact = "
                    + contact + ") and ReceiverID Like '3%'"
            );

            if (resultSet.next()) {
                double x2 = resultSet.getDouble("SUM");
                jLabel40.setText("TK. " + Double.toString(x2));

            }

            //Total received from Biller Account
            resultSet = statement.executeQuery(
                    //AD-2
                    "select sum(Amount) as SUM from TXN where ReceiverID in (select infoID from INFORMATION where Contact = "
                    + contact + ") and SenderID Like '4%'"
            );

            if (resultSet.next()) {
                double x2 = resultSet.getDouble("SUM");
                jLabel31.setText("TK. " + Double.toString(x2));

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

            //Balance Added to personal account in last month
            resultSet = statement.executeQuery(
                    //AD-3
                    "select sum(Amount) as SUM from TXN where SenderID = " + infoID
                    + " and ReceiverID Like '3%' and DATEDIFF(MONTH, Date, GETDATE()) <= 1"
            );
            if (resultSet.next()) {
                double x2 = resultSet.getDouble("SUM");
                jLabel42.setText("TK. " + Double.toString(x2));
            }
            //Monthly paid in biller account in Last Month
            resultSet = statement.executeQuery(
                    "select sum(Amount) as SUM from TXN where ReceiverID = " + infoID
                    + " and SenderID Like '4%' and DATEDIFF(MONTH, Date, GETDATE()) <= 1"
            );
            if (resultSet.next()) {
                double x2 = resultSet.getDouble("SUM");
                jLabel43.setText("TK. " + Double.toString(x2));
            }

            resultSet = statement.executeQuery(
                    "select 0 as X, count(distinct ReceiverID) as cnt  from TXN where SenderID = " + infoID
                    + " UNION "
                    + "select 1 as X, count(distinct SenderID) as cnt  from TXN where ReceiverID = " + infoID
            );
            //Total Personal users
            if (resultSet.next()) {
                int x2 = resultSet.getInt("cnt");
                jLabel44.setText(Integer.toString(x2));
            }
            //Total Personal users
            if (resultSet.next()) {
                int x2 = resultSet.getInt("cnt");
                jLabel45.setText(Integer.toString(x2));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }//GEN-LAST:event_Panel_DashboardMouseClicked

    private void Panel_RequestsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_RequestsMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_Requests);
        Title_text.setText("Pay Bill");

        //Drak Blue
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Agent_Requests);
        jPanel3.repaint();
        jPanel3.revalidate();
    }//GEN-LAST:event_Panel_RequestsMouseClicked

    private void Panel_TransactionsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_TransactionsMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_Transactions);
        Title_text.setText("Transactions");

        //Drak Blue
        resetPanelColor(Panel_Requests);
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Agent_Transactions);
        jPanel3.repaint();
        jPanel3.revalidate();
    }//GEN-LAST:event_Panel_TransactionsMouseClicked

    private void Panel_ReportissuesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_ReportissuesMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_Reportissues);
        Title_text.setText("Report Issues");

        //Drak Blue
        resetPanelColor(Panel_Requests);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Biller_Reportissues);
        jPanel3.repaint();
        jPanel3.revalidate();
    }//GEN-LAST:event_Panel_ReportissuesMouseClicked

    private void Panel_LogoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_LogoutMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_Logout);
        Title_text.setText("Logout");

        //Drak Blue
        resetPanelColor(Panel_Requests);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Dashboard);

        int choice = JOptionPane.showConfirmDialog(null, "Do you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            LoginFormAgent lf_agent = new LoginFormAgent();
            lf_agent.setVisible(true);
            lf_agent.pack();
            lf_agent.setLocationRelativeTo(null);
            this.dispose();
        } else {
            //Magenda
            panelColor(Panel_Dashboard);
            Title_text.setText("Dashboard");

            //Drak Blue
            resetPanelColor(Panel_Requests);
            resetPanelColor(Panel_Transactions);
            resetPanelColor(Panel_Reportissues);
            resetPanelColor(Panel_Logout);
            //remove panel
            jPanel3.removeAll();
            jPanel3.repaint();
            jPanel3.revalidate();

            // add panel
            jPanel3.add(Agent_Dashboard);
            jPanel3.repaint();
            jPanel3.revalidate();
        }
    }//GEN-LAST:event_Panel_LogoutMouseClicked

    private void DescriptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DescriptionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DescriptionActionPerformed

    private void AmountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AmountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AmountActionPerformed

    private void radio_billerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio_billerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radio_billerActionPerformed

    private void radio_userActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio_userActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radio_userActionPerformed

    private void ReportedIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReportedIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ReportedIDActionPerformed

    private void TxnIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxnIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxnIDActionPerformed

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
                    if (DB_status.equals("PENDING")) {
                        status = 1;
                    }
                }
                if (radio_user.isSelected() || radio_biller.isSelected()) {
                    if (reported_id == DB_receiverID && amount == DB_amount && txn_date.equals(DB_date)) {
                        if (status == 1) {
                            JOptionPane.showMessageDialog(null, "The issue has already been submitted.\nHave Patience Dude!");
                        } else if (status == 0) {
                            String query1 = "Insert into ISSUE (TransactionID, SenderID, ReceiverID, Description, Status)values (?,(SELECT InfoID from Information where Contact = " + phoneNo + "),?,?,'PENDING')";
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

    private void jButton7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton7MouseClicked
        // TODO add your handling code here:
        show_transaction();
    }//GEN-LAST:event_jButton7MouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseClicked
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) pending_table.getModel();

        String req_id = model.getValueAt(0, 0).toString();
        String sender_id = model.getValueAt(0, 1).toString();
        String receiver_id = model.getValueAt(0, 2).toString();
        String amount = model.getValueAt(0, 3).toString();
        
        String reference = model.getValueAt(0, 4).toString();
        String status = model.getValueAt(0, 5).toString();

        DefaultTableModel model2 = (DefaultTableModel) all_table.getModel();

        Object[] row = new Object[6];
        row[0] = req_id;
        row[1] = sender_id;
        row[2] = receiver_id;
        row[3] = amount;
        row[4] = reference;
        row[5] = "A";
        model2.addRow(row);
        try {
            
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
            System.out.println("2. DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());
            String query = "Select CurrBalance From INFORMATION where InfoID= "+sender_id;
            double sender_curr_balance=0.0, receiver_curr_balance=0.0;
            
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
                if(rs.next()){
                    sender_curr_balance = rs.getDouble("CurrBalance");
                }
            query = "Select CurrBalance From INFORMATION where InfoID= "+receiver_id;
            st = connection.createStatement();
            rs = st.executeQuery(query);
                if(rs.next()){
                    receiver_curr_balance = rs.getDouble("CurrBalance");
                }
            
            double amou = Double.parseDouble(amount);
            sender_curr_balance = sender_curr_balance - amou;
            receiver_curr_balance = receiver_curr_balance + amou;
            
            query = "UPDATE INFORMATION SET CurrBalance = ? where InfoID = " + sender_id;
            PreparedStatement st2 = connection.prepareStatement(query);
            st2.setDouble(1, sender_curr_balance);
            st2.executeUpdate();
            
            query = "UPDATE INFORMATION SET CurrBalance = ? where InfoID = " + receiver_id;
            PreparedStatement st3 = connection.prepareStatement(query);
            st3.setDouble(1, receiver_curr_balance);
            st3.executeUpdate();

            query = "Insert into TXN (Date, Amount, SenderID, ReceiverID, Reference)values (getdate(), ?,?,?,?)";
            PreparedStatement st4 = connection.prepareStatement(query);
            st4.setDouble(1, amou);
            st4.setInt(2, Integer.parseInt(sender_id));
            st4.setInt(3, Integer.parseInt(receiver_id));
            st4.setString(4, reference);
            st4.executeUpdate();
            model.removeRow(0);
            
            query = "UPDATE REQUEST SET Flag = 'A' where ReqID = "+ req_id;
            PreparedStatement st5 = connection.prepareStatement(query);
            st5.executeUpdate();
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_jButton4MouseClicked

    private void jButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseClicked
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) pending_table.getModel();

        String req_id = model.getValueAt(0, 0).toString();
        String sender_id = model.getValueAt(0, 1).toString();
        String receiver_id = model.getValueAt(0, 2).toString();
        String amount = model.getValueAt(0, 3).toString();
        
        String reference = model.getValueAt(0, 4).toString();
        String status = model.getValueAt(0, 5).toString();

        DefaultTableModel model2 = (DefaultTableModel) all_table.getModel();

        Object[] row = new Object[6];
        row[0] = req_id;
        row[1] = sender_id;
        row[2] = receiver_id;
        row[3] = amount;
        row[4] = reference;
        row[5] = "R";
        model2.addRow(row);
        try {
            
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
            System.out.println("2. DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());
            

            
            
            String query = "UPDATE REQUEST SET Flag = 'R' where ReqID = "+ req_id;
            PreparedStatement st5 = connection.prepareStatement(query);
            st5.executeUpdate();
            model.removeRow(0);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton3MouseClicked

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_Requests);
        Title_text.setText("Pay Bill");

        //Drak Blue
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Agent_Requests);
        jPanel3.repaint();
        jPanel3.revalidate();
    }//GEN-LAST:event_jButton2MouseClicked

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        // TODO add your handling code here:
          // TODO add your handling code here:
        //Magenda
        panelColor(Panel_Transactions);
        Title_text.setText("Transactions");

        //Drak Blue
        resetPanelColor(Panel_Requests);
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Agent_Transactions);
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
            java.util.logging.Logger.getLogger(HomepageAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomepageAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomepageAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomepageAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HomepageAgent().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Agent_Dashboard;
    private javax.swing.JPanel Agent_Requests;
    private javax.swing.JPanel Agent_Transactions;
    private javax.swing.JTextField Amount;
    private javax.swing.JPanel Biller_Reportissues;
    private com.toedter.calendar.JDateChooser DateFrom;
    private com.toedter.calendar.JDateChooser DateTo;
    private javax.swing.JTextField Description;
    private javax.swing.JPanel Panel_Dashboard;
    private javax.swing.JPanel Panel_Logout;
    private javax.swing.JPanel Panel_Reportissues;
    private javax.swing.JPanel Panel_Requests;
    private javax.swing.JPanel Panel_Transactions;
    private javax.swing.JTextField ReportedID;
    private javax.swing.JLabel Title_text;
    private javax.swing.JTextField TxnID;
    private com.toedter.calendar.JDateChooser Txn_Date;
    private javax.swing.ButtonGroup acc_type;
    private javax.swing.JTable all_table;
    private javax.swing.JLabel bg;
    private javax.swing.JLabel bg_paybill;
    private javax.swing.JScrollPane biller_transaction_table;
    private javax.swing.JLabel dateField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton7;
    private com.toedter.calendar.JCalendar jCalendar1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
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
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable pending_table;
    private javax.swing.JRadioButton radio_biller;
    private javax.swing.JRadioButton radio_user;
    private com.toedter.calendar.demo.TestDateEvaluator testDateEvaluator1;
    private com.toedter.calendar.demo.TestDateEvaluator testDateEvaluator2;
    private javax.swing.JTable transaction_table;
    private javax.swing.JLabel user_contact;
    private javax.swing.JLabel username;
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
