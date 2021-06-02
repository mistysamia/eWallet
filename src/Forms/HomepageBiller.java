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
public class HomepageBiller extends javax.swing.JFrame {

    /**
     * Creates new form HomepagePersonal
     */
    public HomepageBiller() {
        initComponents();
        this.setLocationRelativeTo(null);
        setCalendar();
        show_liveOffers();
        show_allOffers();
    }

    public HomepageBiller(String CompanyName, String phoneNo) {
        initComponents();
        this.setLocationRelativeTo(null);
        setCalendar();
        username.setText(CompanyName);
        user_contact.setText(phoneNo);
        show_liveOffers();
        show_allOffers();
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
    
    
    //Biller Live Offers
    public ArrayList<biller_OffersList> liveOfferList() {
        ArrayList<biller_OffersList> liveOfferList = new ArrayList<>();
        String phoneNo = user_contact.getText();
        try {
//            
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
            System.out.println("1. DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());

            String query = "SELECT OfferId, Rate, MaxDiscount, MinPurchase, Deadline, Voucher from OFFER where Deadline >= getDate() and BillerID = (SELECT BillerID from Biller where Contact = "+phoneNo+") " ;
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            biller_OffersList liveOffers;

            while (rs.next()) {
                int offer_id = rs.getInt("OfferID");
                int rate = rs.getInt("Rate");
                double max_discount = rs.getDouble("MaxDiscount");
                double min_purchase = rs.getDouble("MinPurchase");
                Date strDate = rs.getDate("Deadline");
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String deadline = dateFormat.format(strDate);
                String voucher = rs.getString("Voucher");
                
                
                liveOffers = new biller_OffersList(offer_id, rate, max_discount, min_purchase, deadline, voucher);
                liveOfferList.add(liveOffers);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return liveOfferList;
    }

    public void show_liveOffers() {
        ArrayList<biller_OffersList> list = liveOfferList();
        DefaultTableModel model = (DefaultTableModel) table_live_offers.getModel();
        model.setNumRows(0);
        for (int i = list.size()-1; i >=0; i--) {
            Object[] row = new Object[6];
            row[0] = list.get(i).getOfferID();
            row[1] = list.get(i).getRate();
            row[2] = list.get(i).getMaxDiscount();
            row[3] = list.get(i).getMinPurchase();
            row[4] = list.get(i).getDeadline();
            row[5] = list.get(i).getVoucher();
            model.addRow(row);
        }

    }
    
    
    //Biller All Offers
    public ArrayList<biller_OffersList> allOfferList() {
        ArrayList<biller_OffersList> allOfferList = new ArrayList<>();
        String phoneNo = user_contact.getText();
        try {
//            
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
            System.out.println("1. DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());

            String query = "SELECT OfferId, Rate, MaxDiscount, MinPurchase, Deadline, Voucher from OFFER where BillerID = (SELECT BillerID from Biller where Contact = "+phoneNo+")" ;
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            biller_OffersList allOffers;

            while (rs.next()) {
                int offer_id = rs.getInt("OfferID");
                int rate = rs.getInt("Rate");
                double max_discount = rs.getDouble("MaxDiscount");
                double min_purchase = rs.getDouble("MinPurchase");
                Date strDate = rs.getDate("Deadline");
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String deadline = dateFormat.format(strDate);
                String voucher = rs.getString("Voucher");
                
                
                allOffers = new biller_OffersList(offer_id, rate, max_discount, min_purchase, deadline, voucher);
                allOfferList.add(allOffers);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allOfferList;
    }

    public void show_allOffers() {
        ArrayList<biller_OffersList> list = allOfferList();
        DefaultTableModel model = (DefaultTableModel) table_all_offers.getModel();

        for (int i = list.size()-1; i >=0; i--) {
            Object[] row = new Object[6];
            row[0] = list.get(i).getOfferID();
            row[1] = list.get(i).getRate();
            row[2] = list.get(i).getMaxDiscount();
            row[3] = list.get(i).getMinPurchase();
            row[4] = list.get(i).getDeadline();
            row[5] = list.get(i).getVoucher();
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
        Panel_Cashout = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        Panel_Transactions = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        Panel_AddOffers = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        Panel_OffersList = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        Panel_EditInformation = new javax.swing.JPanel();
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
        username = new javax.swing.JLabel();
        user_contact = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        Biller_Dashboard = new javax.swing.JPanel();
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
        Biller_Cashout = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        Reference = new javax.swing.JTextField();
        AgentID = new javax.swing.JTextField();
        MobileNo = new javax.swing.JTextField();
        CashoutAmount = new javax.swing.JTextField();
        Password = new javax.swing.JPasswordField();
        bg_paybill = new javax.swing.JLabel();
        Biller_Transactions = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        DateFrom = new com.toedter.calendar.JDateChooser();
        jLabel60 = new javax.swing.JLabel();
        DateTo = new com.toedter.calendar.JDateChooser();
        jButton7 = new javax.swing.JButton();
        biller_transaction_table = new javax.swing.JScrollPane();
        transaction_table = new javax.swing.JTable();
        jLabel59 = new javax.swing.JLabel();
        Biller_AddOffers = new javax.swing.JPanel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        OfferTitle = new javax.swing.JTextField();
        Voucher = new javax.swing.JTextField();
        OfferRate = new javax.swing.JTextField();
        MaxDisc = new javax.swing.JTextField();
        MinPur = new javax.swing.JTextField();
        Deadline = new com.toedter.calendar.JDateChooser();
        bg_paybill1 = new javax.swing.JLabel();
        Biller_OffersList = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        table_all_offers = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        table_live_offers = new javax.swing.JTable();
        jLabel47 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        Biller_EditInformation = new javax.swing.JPanel();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        address = new javax.swing.JTextField();
        district = new javax.swing.JTextField();
        branch = new javax.swing.JTextField();
        security_answer = new javax.swing.JTextField();
        logo = new javax.swing.JTextField();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        security_question = new javax.swing.JComboBox<>();
        confirm_password = new javax.swing.JPasswordField();
        password = new javax.swing.JPasswordField();
        jLabel26 = new javax.swing.JLabel();
        Biller_Reportissues = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        TxnID = new javax.swing.JTextField();
        ReportedID = new javax.swing.JTextField();
        radio_agent = new javax.swing.JRadioButton();
        radio_user = new javax.swing.JRadioButton();
        Amount = new javax.swing.JTextField();
        Description = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
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

        Panel_Cashout.setBackground(new java.awt.Color(4, 35, 63));
        Panel_Cashout.setForeground(new java.awt.Color(255, 255, 255));
        Panel_Cashout.setPreferredSize(new java.awt.Dimension(200, 50));
        Panel_Cashout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Panel_CashoutMouseClicked(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Cashout");

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icon_paybill.png"))); // NOI18N

        javax.swing.GroupLayout Panel_CashoutLayout = new javax.swing.GroupLayout(Panel_Cashout);
        Panel_Cashout.setLayout(Panel_CashoutLayout);
        Panel_CashoutLayout.setHorizontalGroup(
            Panel_CashoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_CashoutLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        Panel_CashoutLayout.setVerticalGroup(
            Panel_CashoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(Panel_CashoutLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(Panel_Cashout, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 130, 200, 50));

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

        Panel_AddOffers.setBackground(new java.awt.Color(4, 35, 63));
        Panel_AddOffers.setForeground(new java.awt.Color(255, 255, 255));
        Panel_AddOffers.setPreferredSize(new java.awt.Dimension(200, 50));
        Panel_AddOffers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Panel_AddOffersMouseClicked(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Add Offers");

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icon_addbalance.png"))); // NOI18N

        javax.swing.GroupLayout Panel_AddOffersLayout = new javax.swing.GroupLayout(Panel_AddOffers);
        Panel_AddOffers.setLayout(Panel_AddOffersLayout);
        Panel_AddOffersLayout.setHorizontalGroup(
            Panel_AddOffersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_AddOffersLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        Panel_AddOffersLayout.setVerticalGroup(
            Panel_AddOffersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(Panel_AddOffersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(Panel_AddOffers, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 200, 50));

        Panel_OffersList.setBackground(new java.awt.Color(4, 35, 63));
        Panel_OffersList.setForeground(new java.awt.Color(255, 255, 255));
        Panel_OffersList.setPreferredSize(new java.awt.Dimension(200, 50));
        Panel_OffersList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Panel_OffersListMouseClicked(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Offers List");

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icon_subscriptions.png"))); // NOI18N

        javax.swing.GroupLayout Panel_OffersListLayout = new javax.swing.GroupLayout(Panel_OffersList);
        Panel_OffersList.setLayout(Panel_OffersListLayout);
        Panel_OffersListLayout.setHorizontalGroup(
            Panel_OffersListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_OffersListLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        Panel_OffersListLayout.setVerticalGroup(
            Panel_OffersListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(Panel_OffersListLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(Panel_OffersList, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 280, 200, 50));

        Panel_EditInformation.setBackground(new java.awt.Color(4, 35, 63));
        Panel_EditInformation.setForeground(new java.awt.Color(255, 255, 255));
        Panel_EditInformation.setPreferredSize(new java.awt.Dimension(200, 50));
        Panel_EditInformation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Panel_EditInformationMouseClicked(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Edit Informations");

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icon_offers.png"))); // NOI18N

        javax.swing.GroupLayout Panel_EditInformationLayout = new javax.swing.GroupLayout(Panel_EditInformation);
        Panel_EditInformation.setLayout(Panel_EditInformationLayout);
        Panel_EditInformationLayout.setHorizontalGroup(
            Panel_EditInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_EditInformationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        Panel_EditInformationLayout.setVerticalGroup(
            Panel_EditInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
            .addGroup(Panel_EditInformationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(Panel_EditInformation, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 330, 200, 50));

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
        jPanel1.add(Title_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 20, 420, -1));

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

        Biller_Dashboard.setBackground(new java.awt.Color(255, 255, 255));
        Biller_Dashboard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(201, 0, 50));
        jLabel28.setText("Account Information");
        Biller_Dashboard.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, -1, -1));

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(201, 0, 50));
        jLabel29.setText("Offers and Subscriptions");
        Biller_Dashboard.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 310, -1, -1));

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(4, 35, 63));
        jLabel30.setText("Total Cashout");
        Biller_Dashboard.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 160, -1, -1));

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(5, 120, 60));
        jLabel31.setText("TK. 0");
        Biller_Dashboard.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 110, -1, -1));

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(4, 35, 63));
        jLabel32.setText("Total Bill Received");
        Biller_Dashboard.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 160, -1, -1));

        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(4, 35, 63));
        jLabel33.setText("Last Transaction");
        Biller_Dashboard.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, -1, -1));

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(4, 35, 63));
        jLabel34.setText("Total Bill Received Today");
        Biller_Dashboard.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 260, -1, -1));

        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(4, 35, 63));
        jLabel35.setText("No of Bills Received Today");
        Biller_Dashboard.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 260, -1, -1));

        jLabel36.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(4, 35, 63));
        jLabel36.setText("Live Offers");
        Biller_Dashboard.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 420, -1, -1));

        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(4, 35, 63));
        jLabel37.setText("Subscribed Customers");
        Biller_Dashboard.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 420, -1, -1));

        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(4, 35, 63));
        jLabel38.setText("Current Balance");
        Biller_Dashboard.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, -1, -1));

        jLabel39.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(5, 120, 60));
        jLabel39.setText("TK. 0");
        Biller_Dashboard.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, -1, -1));

        jLabel40.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(5, 120, 60));
        jLabel40.setText("TK. 0");
        Biller_Dashboard.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 110, -1, -1));

        jLabel41.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(5, 120, 60));
        jLabel41.setText("TK. 0");
        Biller_Dashboard.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, -1, -1));

        jLabel42.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(5, 120, 60));
        jLabel42.setText("TK. 0");
        Biller_Dashboard.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 210, -1, -1));

        jLabel43.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(5, 120, 60));
        jLabel43.setText("0");
        Biller_Dashboard.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 210, -1, -1));

        jLabel44.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(5, 120, 60));
        jLabel44.setText("0");
        Biller_Dashboard.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 380, -1, -1));

        jLabel45.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(5, 120, 60));
        jLabel45.setText("0");
        Biller_Dashboard.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 380, -1, -1));

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_addOffers.png"))); // NOI18N
        jButton1.setBorder(null);
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });
        Biller_Dashboard.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 40, -1, -1));

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_cashout.png"))); // NOI18N
        jButton2.setBorder(null);
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });
        Biller_Dashboard.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 40, -1, -1));

        bg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/dashboard_bg.png"))); // NOI18N
        Biller_Dashboard.add(bg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(Biller_Dashboard, "card2");

        Biller_Cashout.setBackground(new java.awt.Color(255, 255, 255));
        Biller_Cashout.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel20.setText("Mobile No :");
        Biller_Cashout.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 170, -1, -1));

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel21.setText("Agent ID :");
        Biller_Cashout.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 120, -1, -1));

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel22.setText("Amount :");
        Biller_Cashout.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 220, -1, -1));

        jLabel46.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel46.setText("Reference:");
        Biller_Cashout.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 270, -1, -1));

        jLabel48.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel48.setText("Password:");
        Biller_Cashout.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 320, -1, -1));

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_cashout.png"))); // NOI18N
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
        Biller_Cashout.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 390, -1, -1));

        Reference.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        Reference.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Reference.setBorder(null);
        Biller_Cashout.add(Reference, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 266, 270, 30));

        AgentID.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        AgentID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        AgentID.setBorder(null);
        Biller_Cashout.add(AgentID, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 118, 270, 30));

        MobileNo.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        MobileNo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        MobileNo.setBorder(null);
        Biller_Cashout.add(MobileNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 168, 270, 30));

        CashoutAmount.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        CashoutAmount.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        CashoutAmount.setBorder(null);
        Biller_Cashout.add(CashoutAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 215, 270, 30));

        Password.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        Password.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Password.setBorder(null);
        Biller_Cashout.add(Password, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 315, 270, 30));

        bg_paybill.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        bg_paybill.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/bg_cashout.png"))); // NOI18N
        Biller_Cashout.add(bg_paybill, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(Biller_Cashout, "card2");

        Biller_Transactions.setBackground(new java.awt.Color(255, 255, 255));
        Biller_Transactions.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(201, 0, 50));
        jLabel23.setText("From:");
        Biller_Transactions.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, -1));

        DateFrom.setDateFormatString("yyyy-MM-dd");
        Biller_Transactions.add(DateFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 30, 150, -1));

        jLabel60.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel60.setForeground(new java.awt.Color(201, 0, 50));
        jLabel60.setText("To:");
        Biller_Transactions.add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 30, -1, -1));

        DateTo.setDateFormatString("yyyy-MM-dd");
        Biller_Transactions.add(DateTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 30, 150, -1));

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_search.png"))); // NOI18N
        jButton7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton7MouseClicked(evt);
            }
        });
        Biller_Transactions.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 30, 140, 30));

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

        Biller_Transactions.add(biller_transaction_table, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 760, 410));

        jLabel59.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/bg_transactions.png"))); // NOI18N
        Biller_Transactions.add(jLabel59, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(Biller_Transactions, "card2");

        Biller_AddOffers.setBackground(new java.awt.Color(255, 255, 255));
        Biller_AddOffers.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel49.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel49.setText("Offer Title :");
        Biller_AddOffers.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 80, -1, -1));

        jLabel50.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel50.setText("Offer Rate:");
        Biller_AddOffers.add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 134, -1, -1));

        jLabel51.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel51.setText("Max. Discount :");
        Biller_AddOffers.add(jLabel51, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 190, -1, -1));

        jLabel52.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel52.setText("Min. Purchase:");
        Biller_AddOffers.add(jLabel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 242, -1, -1));

        jLabel53.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel53.setText("Deadline:");
        Biller_AddOffers.add(jLabel53, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 300, -1, -1));

        jLabel54.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel54.setText("Voucher:");
        Biller_AddOffers.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 355, -1, -1));

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_addOffers.png"))); // NOI18N
        jButton4.setBorder(null);
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
        Biller_AddOffers.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 410, -1, -1));

        OfferTitle.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        OfferTitle.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        OfferTitle.setBorder(null);
        Biller_AddOffers.add(OfferTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 80, 230, 30));

        Voucher.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        Voucher.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Voucher.setBorder(null);
        Biller_AddOffers.add(Voucher, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 354, 230, 30));

        OfferRate.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        OfferRate.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        OfferRate.setBorder(null);
        Biller_AddOffers.add(OfferRate, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 134, 230, 30));

        MaxDisc.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        MaxDisc.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        MaxDisc.setBorder(null);
        Biller_AddOffers.add(MaxDisc, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 188, 230, 30));

        MinPur.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        MinPur.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        MinPur.setBorder(null);
        Biller_AddOffers.add(MinPur, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 244, 230, 30));

        Deadline.setDateFormatString("yyyy-MM-dd");
        Biller_AddOffers.add(Deadline, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 302, 250, 30));

        bg_paybill1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        bg_paybill1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/bg_AddOffers.png"))); // NOI18N
        Biller_AddOffers.add(bg_paybill1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(Biller_AddOffers, "card2");

        Biller_OffersList.setBackground(new java.awt.Color(255, 255, 255));
        Biller_OffersList.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        table_all_offers.setBackground(new java.awt.Color(200, 0, 50));
        table_all_offers.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        table_all_offers.setForeground(new java.awt.Color(255, 255, 255));
        table_all_offers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Offer ID", "Rate", "MaxDiscount", "MinPurchase", "Deadline", "Voucher"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_all_offers.setColumnSelectionAllowed(true);
        jScrollPane3.setViewportView(table_all_offers);
        table_all_offers.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        Biller_OffersList.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, 760, 200));

        table_live_offers.setBackground(new java.awt.Color(200, 0, 50));
        table_live_offers.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        table_live_offers.setForeground(new java.awt.Color(255, 255, 255));
        table_live_offers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Offer ID", "Rate", "MaxDiscount", "MinPurchase", "Deadline", "Voucher"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_live_offers.setColumnSelectionAllowed(true);
        jScrollPane4.setViewportView(table_live_offers);
        table_live_offers.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        Biller_OffersList.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 760, 150));

        jLabel47.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel47.setForeground(new java.awt.Color(201, 0, 50));
        jLabel47.setText("Live Offers");
        Biller_OffersList.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, 40));

        jLabel61.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel61.setForeground(new java.awt.Color(201, 0, 50));
        jLabel61.setText("All Offers");
        Biller_OffersList.add(jLabel61, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 250, -1, -1));

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/bg_transactions.png"))); // NOI18N
        Biller_OffersList.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(Biller_OffersList, "card2");

        Biller_EditInformation.setBackground(new java.awt.Color(255, 255, 255));
        Biller_EditInformation.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel62.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel62.setText("Logo:");
        Biller_EditInformation.add(jLabel62, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 294, 150, -1));

        jLabel63.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel63.setText("Branch:");
        Biller_EditInformation.add(jLabel63, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 154, 150, -1));

        jLabel64.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel64.setText("Security Question:");
        Biller_EditInformation.add(jLabel64, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 202, 150, -1));

        jLabel65.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel65.setText("Security Answer:");
        Biller_EditInformation.add(jLabel65, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 246, 150, -1));

        jLabel66.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel66.setText("Address:");
        Biller_EditInformation.add(jLabel66, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 152, 150, -1));

        jLabel67.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel67.setText("District:");
        Biller_EditInformation.add(jLabel67, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 204, 150, -1));

        jLabel68.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel68.setText("Password:");
        Biller_EditInformation.add(jLabel68, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 150, -1));

        jLabel69.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel69.setText("Confirm Password:");
        Biller_EditInformation.add(jLabel69, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 292, 150, -1));

        address.setEditable(false);
        address.setBackground(new java.awt.Color(245, 245, 245));
        address.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        address.setBorder(null);
        Biller_EditInformation.add(address, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 155, 185, -1));

        district.setEditable(false);
        district.setBackground(new java.awt.Color(245, 245, 245));
        district.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        district.setBorder(null);
        Biller_EditInformation.add(district, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 204, 185, -1));

        branch.setEditable(false);
        branch.setBackground(new java.awt.Color(245, 245, 245));
        branch.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        branch.setBorder(null);
        Biller_EditInformation.add(branch, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 156, 185, -1));

        security_answer.setEditable(false);
        security_answer.setBackground(new java.awt.Color(245, 245, 245));
        security_answer.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        security_answer.setBorder(null);
        Biller_EditInformation.add(security_answer, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 248, 185, -1));

        logo.setEditable(false);
        logo.setBackground(new java.awt.Color(245, 245, 245));
        logo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        logo.setBorder(null);
        Biller_EditInformation.add(logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 294, 185, -1));

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_confirm_1.png"))); // NOI18N
        jButton8.setActionCommand("efsdf");
        jButton8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton8MouseClicked(evt);
            }
        });
        Biller_EditInformation.add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 370, 146, 30));

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_cancel.png"))); // NOI18N
        jButton9.setActionCommand("efsdf");
        jButton9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton9MouseClicked(evt);
            }
        });
        Biller_EditInformation.add(jButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 370, 146, 30));

        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_editInfo.png"))); // NOI18N
        jButton10.setActionCommand("efsdf");
        jButton10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton10MouseClicked(evt);
            }
        });
        Biller_EditInformation.add(jButton10, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 370, 146, 30));

        security_question.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "What is your favourite appetizer?", "What is your favourite dessert?", "What is your favourite drink?" }));
        Biller_EditInformation.add(security_question, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 203, 190, -1));

        confirm_password.setEditable(false);
        confirm_password.setBackground(new java.awt.Color(245, 245, 245));
        confirm_password.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        confirm_password.setBorder(null);
        Biller_EditInformation.add(confirm_password, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 292, 185, 26));

        password.setEditable(false);
        password.setBackground(new java.awt.Color(245, 245, 245));
        password.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        password.setBorder(null);
        Biller_EditInformation.add(password, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 247, 185, 26));

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/bg_editProfile.png"))); // NOI18N
        Biller_EditInformation.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(Biller_EditInformation, "card2");

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

        TxnID.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        TxnID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        TxnID.setBorder(null);
        TxnID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxnIDActionPerformed(evt);
            }
        });
        Biller_Reportissues.add(TxnID, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 80, 230, 30));

        ReportedID.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        ReportedID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        ReportedID.setBorder(null);
        ReportedID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReportedIDActionPerformed(evt);
            }
        });
        Biller_Reportissues.add(ReportedID, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 135, 230, 30));

        radio_agent.setBackground(new java.awt.Color(255, 255, 255));
        acc_type.add(radio_agent);
        radio_agent.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        radio_agent.setText("Agent");
        radio_agent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_agentActionPerformed(evt);
            }
        });
        Biller_Reportissues.add(radio_agent, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 190, -1, -1));

        radio_user.setBackground(new java.awt.Color(255, 255, 255));
        acc_type.add(radio_user);
        radio_user.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        radio_user.setText("User");
        radio_user.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_userActionPerformed(evt);
            }
        });
        Biller_Reportissues.add(radio_user, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 190, -1, -1));

        Amount.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        Amount.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Amount.setBorder(null);
        Amount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AmountActionPerformed(evt);
            }
        });
        Biller_Reportissues.add(Amount, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 245, 230, 30));

        Description.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        Description.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Description.setBorder(null);
        Description.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DescriptionActionPerformed(evt);
            }
        });
        Biller_Reportissues.add(Description, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 356, 230, 30));

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_submit.png"))); // NOI18N
        jButton6.setBorder(null);
        Biller_Reportissues.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 420, -1, -1));

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

        Txn_Date.setDateFormatString("yyyy-MM-dd");
        Biller_Reportissues.add(Txn_Date, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 302, 250, 30));

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/bg_reportIssues.png"))); // NOI18N
        Biller_Reportissues.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

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
        resetPanelColor(Panel_Cashout);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_AddOffers);
        resetPanelColor(Panel_OffersList);
        resetPanelColor(Panel_EditInformation);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Biller_Dashboard);
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
                    //AD-4
                    "select sum(Amount) as SUM from TXN where ReceiverID in (select infoID from INFORMATION where Contact = "
                    + contact + ")"
            );
            //Total Bill Received, Cashout
            if (resultSet.next()) {
                double x2 = resultSet.getDouble("SUM");
                jLabel40.setText("TK. " + Double.toString(x2));
                jLabel31.setText("TK. " + Double.toString((x2 - x)));
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

            //Bill Received today
            resultSet = statement.executeQuery(
                    //AD-3
                    "select sum(Amount) as SUM from TXN where ReceiverID = " + infoID
                    + " and DATEDIFF(DAY, Date, GETDATE()) = 0"
            );
            if (resultSet.next()) {
                double x2 = resultSet.getDouble("SUM");
                jLabel42.setText("TK. " + Double.toString(x2));
            }
            //No of bill received today
            resultSet = statement.executeQuery(
                    "select count(Amount) as SUM from TXN where ReceiverID = " + infoID
                    + " and DATEDIFF(MONTH, Date, GETDATE()) <= 1"
            );
            if (resultSet.next()) {
                int x2 = resultSet.getInt("SUM");
                jLabel43.setText(Integer.toString(x2));
            }
            //AD-5
            //Live Offers
            resultSet = statement.executeQuery(
                    "select count(OfferID) as liveOffers from OFFER where DATEDIFF(DAY, DeadLine, GETDATE()) <= 0 and "
                    + "BillerID IN (SELECT Contact from BILLER where Contact = " + contact + ")"
            );
            if (resultSet.next()) {
                int x2 = resultSet.getInt("liveOffers");
                jLabel44.setText(Integer.toString(x2));
            }

            //Subscribed Customers
            resultSet = statement.executeQuery(
                    "select count(distinct SenderID) as noOfSubscribers from TXN where ReceiverID = " + infoID
            );
            if (resultSet.next()) {
                int x2 = resultSet.getInt("noOfSubscribers");
                jLabel45.setText(Integer.toString(x2));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }//GEN-LAST:event_Panel_DashboardMouseClicked

    private void Panel_CashoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_CashoutMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_Cashout);
        Title_text.setText("Cashout");

        //Drak Blue
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_AddOffers);
        resetPanelColor(Panel_OffersList);
        resetPanelColor(Panel_EditInformation);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Biller_Cashout);
        jPanel3.repaint();
        jPanel3.revalidate();
    }//GEN-LAST:event_Panel_CashoutMouseClicked

    private void Panel_TransactionsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_TransactionsMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_Transactions);
        Title_text.setText("Transactions");

        //Drak Blue
        resetPanelColor(Panel_Cashout);
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_AddOffers);
        resetPanelColor(Panel_OffersList);
        resetPanelColor(Panel_EditInformation);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Biller_Transactions);
        jPanel3.repaint();
        jPanel3.revalidate();
    }//GEN-LAST:event_Panel_TransactionsMouseClicked

    private void Panel_AddOffersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_AddOffersMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_AddOffers);
        Title_text.setText("Add Offers");
        //Drak Blue
        resetPanelColor(Panel_Cashout);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_OffersList);
        resetPanelColor(Panel_EditInformation);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Biller_AddOffers);
        jPanel3.repaint();
        jPanel3.revalidate();
    }//GEN-LAST:event_Panel_AddOffersMouseClicked

    private void Panel_OffersListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_OffersListMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_OffersList);
        Title_text.setText("Offers List");

        //Drak Blue
        resetPanelColor(Panel_Cashout);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_AddOffers);
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_EditInformation);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Biller_OffersList);
        jPanel3.repaint();
        jPanel3.revalidate();
    }//GEN-LAST:event_Panel_OffersListMouseClicked

    private void Panel_EditInformationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_EditInformationMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_EditInformation);
        Title_text.setText("Edit Information");

        //Drak Blue
        resetPanelColor(Panel_Cashout);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_AddOffers);
        resetPanelColor(Panel_OffersList);
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Biller_EditInformation);
        jPanel3.repaint();
        jPanel3.revalidate();

        try {

            String contact = user_contact.getText();

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
            System.out.println("DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());

            String query = "SELECT Address, District, Password, Branch, SecurityQue, SecurityAns, PhotoPath FROM INFORMATION INNER JOIN BILLER ON INFORMATION.Contact = BILLER.Contact where INFORMATION.Contact = " + contact;
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            personal_transaction pt;

            while (rs.next()) {

                String DB_address = rs.getString("Address");
                String DB_district = rs.getString("District");
                String DB_pass = rs.getString("Password");
                String DB_branch = rs.getString("Branch");
                String DB_SecurityQ = rs.getString("SecurityQue");
                String DB_SecurityA = rs.getString("SecurityAns");
                String DB_logo = rs.getString("PhotoPath");

                address.setText(DB_address);
                district.setText(DB_district);
                password.setText(DB_pass);
                branch.setText(DB_branch);
                security_answer.setText(DB_SecurityA);
                logo.setText(DB_logo);

                if (DB_SecurityQ.equals("What is your favourite appetizer?")) {
                    security_question.setSelectedItem("What is your favourite appetizer?");
                } else if (DB_SecurityQ.equals("What is your favourite dessert?")) {
                    security_question.setSelectedItem("What is your favourite dessert?");
                } else if (DB_SecurityQ.equals("What is your favourite drink?")) {
                    security_question.setSelectedItem("What is your favourite drink?");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }//GEN-LAST:event_Panel_EditInformationMouseClicked

    private void Panel_ReportissuesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_ReportissuesMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_Reportissues);
        Title_text.setText("Report Issues");

        //Drak Blue
        resetPanelColor(Panel_Cashout);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_AddOffers);
        resetPanelColor(Panel_OffersList);
        resetPanelColor(Panel_EditInformation);
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
        resetPanelColor(Panel_Cashout);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_AddOffers);
        resetPanelColor(Panel_OffersList);
        resetPanelColor(Panel_EditInformation);
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
            resetPanelColor(Panel_Cashout);
            resetPanelColor(Panel_Transactions);
            resetPanelColor(Panel_AddOffers);
            resetPanelColor(Panel_OffersList);
            resetPanelColor(Panel_EditInformation);
            resetPanelColor(Panel_Reportissues);
            resetPanelColor(Panel_Logout);
            //remove panel
            jPanel3.removeAll();
            jPanel3.repaint();
            jPanel3.revalidate();

            // add panel
            jPanel3.add(Biller_Dashboard);
            jPanel3.repaint();
            jPanel3.revalidate();
        }
    }//GEN-LAST:event_Panel_LogoutMouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void TxnIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxnIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxnIDActionPerformed

    private void ReportedIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReportedIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ReportedIDActionPerformed

    private void radio_agentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio_agentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radio_agentActionPerformed

    private void radio_userActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio_userActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radio_userActionPerformed

    private void AmountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AmountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AmountActionPerformed

    private void DescriptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DescriptionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DescriptionActionPerformed

    private void jButton10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton10MouseClicked
        // TODO add your handling code here:
        address.setEditable(true);
        district.setEditable(true);
        password.setEditable(true);
        confirm_password.setEditable(true);
        branch.setEditable(true);
        security_answer.setEditable(true);
        logo.setEditable(true);
    }//GEN-LAST:event_jButton10MouseClicked

    private void jButton8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton8MouseClicked
        // TODO add your handling code here:
        try {
            String contact = user_contact.getText();

            String new_address = address.getText();
            String new_district = district.getText();
            String new_branch = branch.getText();
            String new_security_ans = security_answer.getText();
            String new_pass = String.valueOf(password.getText());
            String new_confirm_pass = String.valueOf(confirm_password.getText());
            String new_security_que = security_question.getSelectedItem().toString();
            String new_logo = logo.getText();

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");

            System.out.println("DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());

            if (new_pass.equals(new_confirm_pass)) {

                String query = "UPDATE INFORMATION SET Password = ?, Address = ?,District = ?,SecurityQue = ?,SecurityAns = ?,PhotoPath = ? where Contact = " + contact;
                PreparedStatement st = connection.prepareStatement(query);
                st.setString(1, new_pass);
                st.setString(2, new_address);
                st.setString(3, new_district);
                st.setString(4, new_security_que);
                st.setString(5, new_security_ans);
                st.setString(6, new_logo);
                st.executeUpdate();

                String query1 = "UPDATE BILLER SET Branch = ? where Contact = " + contact;
                PreparedStatement st1 = connection.prepareStatement(query1);
                st1.setString(1, new_branch);
                st1.executeUpdate();

                JOptionPane.showMessageDialog(null, "Information updated successfully.");

                address.setEditable(false);
                district.setEditable(false);
                password.setEditable(false);
                confirm_password.setEditable(false);
                branch.setEditable(false);
                security_answer.setEditable(false);
                logo.setEditable(false);

                //Magenda
                panelColor(Panel_Dashboard);
                Title_text.setText("Dashboard");

                //Drak Blue
                resetPanelColor(Panel_Cashout);
                resetPanelColor(Panel_Transactions);
                resetPanelColor(Panel_AddOffers);
                resetPanelColor(Panel_OffersList);
                resetPanelColor(Panel_EditInformation);
                resetPanelColor(Panel_Reportissues);
                resetPanelColor(Panel_Logout);

                //remove panel
                jPanel3.removeAll();
                jPanel3.repaint();
                jPanel3.revalidate();

                // add panel
                jPanel3.add(Biller_Dashboard);
                jPanel3.repaint();
                jPanel3.revalidate();

            } else {
                JOptionPane.showMessageDialog(null, "Password doesn't match.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }//GEN-LAST:event_jButton8MouseClicked

    private void jButton9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton9MouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_Dashboard);
        Title_text.setText("Dashboard");

        //Drak Blue
        resetPanelColor(Panel_Cashout);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_AddOffers);
        resetPanelColor(Panel_OffersList);
        resetPanelColor(Panel_EditInformation);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Biller_Dashboard);
        jPanel3.repaint();
        jPanel3.revalidate();
    }//GEN-LAST:event_jButton9MouseClicked

    private void jButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseClicked
        // TODO add your handling code here:

        String contact = user_contact.getText();

        String offer_title = OfferTitle.getText();
        String rate_st = OfferRate.getText();
        int rate = Integer.parseInt(rate_st.trim());
        String max_discount_st = MaxDisc.getText();
        double max_discount = Double.parseDouble(max_discount_st.trim());
        String min_purchase_st = MinPur.getText();
        double min_purchase = Double.parseDouble(min_purchase_st.trim());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String deadline = sdf.format(Deadline.getDate());
        String voucher = Voucher.getText();
        System.out.println(deadline);
        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");

            System.out.println("DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());
            
            String query = "SELECT voucher from OFFER where BillerID =(Select BillerID FROM BILLER where Contact = "+contact+") and DeadLine>=GETDATE()";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            String DB_voucher;
            // flag is to be used
            int flag =1;
            while (rs.next()) {
                flag = 0;
                DB_voucher = rs.getString("voucher");
       
            }
            if (flag ==1){
                String query1 = "Insert into OFFER (billerID, Rate, MaxDiscount, MinPurchase, Deadline, Voucher)values ((Select BillerID FROM BILLER where Contact = "+contact+"),?,?,?,?,?)";
                PreparedStatement st1 = connection.prepareStatement(query1);
                st1.setInt(1, rate );
                st1.setDouble(2, max_discount);
                st1.setDouble(3, min_purchase);
                st1.setString(4, deadline);
                st1.setString(5, voucher);
                st1.executeUpdate();
                JOptionPane.showMessageDialog(null, "Offer added successfully");
                OfferTitle.setText("");
                OfferRate.setText("");
                MaxDisc.setText("");
                MinPur.setText("");
                Voucher.setText("");
                
            }else{
                JOptionPane.showMessageDialog(null, "An offer already exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }//GEN-LAST:event_jButton4MouseClicked

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
                if (radio_user.isSelected()) {
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
                    } else if (radio_agent.isSelected()) {
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

    private void jButton7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton7MouseClicked
        // TODO add your handling code here:
        show_transaction();

    }//GEN-LAST:event_jButton7MouseClicked

    private void jButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseClicked
        // TODO add your handling code here:
        try {

            String a_id = AgentID.getText();
            int agent_id = Integer.parseInt(a_id);
            String agent_contact = MobileNo.getText();
            String a_amount = CashoutAmount.getText();
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
                    
                    query = "Insert into REQUEST (SenderID, ReceiverID, Amount, Reference, Flag)values ((SELECT InfoID From INFORMATION where Contact = "+sender_contact+"),?,?,?,'P')";
                    PreparedStatement st3 = connection.prepareStatement(query);
                    st3.setInt(1, agent_id);
                    st3.setDouble(2, add_amount);
                    st3.setString(3, reference);
                    st3.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Your request is waiting for approval");
                    
                    AgentID.setText("");
                    MobileNo.setText("");
                    CashoutAmount.setText("");
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
    }//GEN-LAST:event_jButton3MouseClicked

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_Cashout);
        Title_text.setText("Cashout");

        //Drak Blue
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_AddOffers);
        resetPanelColor(Panel_OffersList);
        resetPanelColor(Panel_EditInformation);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Biller_Cashout);
        jPanel3.repaint();
        jPanel3.revalidate();
    }//GEN-LAST:event_jButton2MouseClicked

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_AddOffers);
        Title_text.setText("Add Offers");
        //Drak Blue
        resetPanelColor(Panel_Cashout);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_OffersList);
        resetPanelColor(Panel_EditInformation);
        resetPanelColor(Panel_Reportissues);
        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Biller_AddOffers);
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
            java.util.logging.Logger.getLogger(HomepageBiller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomepageBiller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomepageBiller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomepageBiller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HomepageBiller().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField AgentID;
    private javax.swing.JTextField Amount;
    private javax.swing.JPanel Biller_AddOffers;
    private javax.swing.JPanel Biller_Cashout;
    private javax.swing.JPanel Biller_Dashboard;
    private javax.swing.JPanel Biller_EditInformation;
    private javax.swing.JPanel Biller_OffersList;
    private javax.swing.JPanel Biller_Reportissues;
    private javax.swing.JPanel Biller_Transactions;
    private javax.swing.JTextField CashoutAmount;
    private com.toedter.calendar.JDateChooser DateFrom;
    private com.toedter.calendar.JDateChooser DateTo;
    private com.toedter.calendar.JDateChooser Deadline;
    private javax.swing.JTextField Description;
    private javax.swing.JTextField MaxDisc;
    private javax.swing.JTextField MinPur;
    private javax.swing.JTextField MobileNo;
    private javax.swing.JTextField OfferRate;
    private javax.swing.JTextField OfferTitle;
    private javax.swing.JPanel Panel_AddOffers;
    private javax.swing.JPanel Panel_Cashout;
    private javax.swing.JPanel Panel_Dashboard;
    private javax.swing.JPanel Panel_EditInformation;
    private javax.swing.JPanel Panel_Logout;
    private javax.swing.JPanel Panel_OffersList;
    private javax.swing.JPanel Panel_Reportissues;
    private javax.swing.JPanel Panel_Transactions;
    private javax.swing.JPasswordField Password;
    private javax.swing.JTextField Reference;
    private javax.swing.JTextField ReportedID;
    private javax.swing.JLabel Title_text;
    private javax.swing.JTextField TxnID;
    private com.toedter.calendar.JDateChooser Txn_Date;
    private javax.swing.JTextField Voucher;
    private javax.swing.ButtonGroup acc_type;
    private javax.swing.JTextField address;
    private javax.swing.JLabel bg;
    private javax.swing.JLabel bg_paybill;
    private javax.swing.JLabel bg_paybill1;
    private javax.swing.JScrollPane biller_transaction_table;
    private javax.swing.JTextField branch;
    private javax.swing.JPasswordField confirm_password;
    private javax.swing.JLabel dateField;
    private javax.swing.JTextField district;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
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
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField logo;
    private javax.swing.JPasswordField password;
    private javax.swing.JRadioButton radio_agent;
    private javax.swing.JRadioButton radio_user;
    private javax.swing.JTextField security_answer;
    private javax.swing.JComboBox<String> security_question;
    private javax.swing.JTable table_all_offers;
    private javax.swing.JTable table_live_offers;
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
