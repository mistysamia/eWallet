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
public class HomepageAdmin extends javax.swing.JFrame {

    /**
     * Creates new form HomepagePersonal
     */
    public HomepageAdmin() {
        initComponents();
        setCalendar();
        this.setLocationRelativeTo(null);
        //show_transaction();
        show_pending();
        show_solved();
        show_tab1();
        show_tab2();
    }

    public HomepageAdmin(String name, String phoneNo) {
        initComponents();
        setCalendar();
        this.setLocationRelativeTo(null);
        username.setText(name);
        user_contact.setText(phoneNo);
        //show_transaction();
        show_pending();
        show_solved();
        show_tab1();
        show_tab2();
    }

    //Transaction
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

    //Admin Pending Issues
    public ArrayList<admin_issues> pendingList() {
        ArrayList<admin_issues> pendingList = new ArrayList<>();
        String phoneNo = user_contact.getText();
        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
            System.out.println("1. DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());

            String query = "SELECT * from ISSUE where Status = 'PENDING'";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            admin_issues pending;

            while (rs.next()) {
                int issue_id = rs.getInt("IssueID");
                int txn_id = rs.getInt("TransactionID");
                int sender_id = rs.getInt("SenderID");
                int receiver_id = rs.getInt("ReceiverID");
                String description = rs.getString("Description");
                String status = rs.getString("Status");

                pending = new admin_issues(issue_id, txn_id, sender_id, receiver_id, description, status);
                pendingList.add(pending);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pendingList;
    }

    public void show_pending() {
        ArrayList<admin_issues> list = pendingList();
        DefaultTableModel model = (DefaultTableModel) table_pending_issues.getModel();
        model.setNumRows(0);
        for (int i = list.size() - 1; i >= 0; i--) {
            Object[] row = new Object[6];
            row[0] = list.get(i).getIssueID();
            row[1] = list.get(i).getTxnID();
            row[2] = list.get(i).getSenderID();
            row[3] = list.get(i).getReceiverID();
            row[4] = list.get(i).getDescription();
            row[5] = list.get(i).getStatus();

            model.addRow(row);
        }

    }

    //Admin Pending Issues
    public ArrayList<admin_issues> solvedList() {
        ArrayList<admin_issues> solvedList = new ArrayList<>();
        String phoneNo = user_contact.getText();
        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
            System.out.println("1. DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());

            String query = "SELECT * from ISSUE where Status <> 'PENDING'";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            admin_issues pending;

            while (rs.next()) {
                int issue_id = rs.getInt("IssueID");
                int txn_id = rs.getInt("TransactionID");
                int sender_id = rs.getInt("SenderID");
                int receiver_id = rs.getInt("ReceiverID");
                String description = rs.getString("Description");
                String status = rs.getString("Status");

                pending = new admin_issues(issue_id, txn_id, sender_id, receiver_id, description, status);
                solvedList.add(pending);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return solvedList;
    }

    public void show_solved() {
        ArrayList<admin_issues> list = solvedList();
        DefaultTableModel model = (DefaultTableModel) table_solved_issues.getModel();
        model.setNumRows(0);
        for (int i = list.size() - 1; i >= 0; i--) {
            Object[] row = new Object[6];
            row[0] = list.get(i).getIssueID();
            row[1] = list.get(i).getTxnID();
            row[2] = list.get(i).getSenderID();
            row[3] = list.get(i).getReceiverID();
            row[4] = list.get(i).getDescription();
            row[5] = list.get(i).getStatus();

            model.addRow(row);
        }

    }
    
    //table 1
    public ArrayList<stat> tab1List() {
        ArrayList<stat> tab1List = new ArrayList<>();
        String phoneNo = user_contact.getText();
        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            String date_from = sdf.format(DateFrom.getDate());
//            String date_to = sdf.format(DateTo.getDate());
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
            System.out.println("1. DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());

            String query = "select top 5 InfoID, BillerID, BILLER.Contact as BContact, CompanyName, Branch, CNT from\n" +
            "(\n" +
            "	(select InfoID, CNT, Contact from\n" +
            "	(\n" +
            "		(select top 5 ReceiverID as InfoID2, COUNT(distinct SenderID) as CNT from TXN group by ReceiverID order by CNT desc) as T1\n" +
            "		inner join INFORMATION on T1.InfoID2 = INFORMATION.InfoID\n" +
            "	)) as T2\n" +
            "	inner join BILLER on T2.Contact = BILLER.Contact\n" +
            ")";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

//            String receiver_contact = "01676336205";
            stat pt;

            while (rs.next()) {
                int info_id = rs.getInt("InfoID");
                int biller_id = rs.getInt("BillerID");
                String contact = rs.getString("BContact");
                String company = rs.getString("CompanyName");
                String branch = rs.getString("Branch");
                int cnt= rs.getInt("CNT");

                
                pt = new stat(info_id, biller_id, contact, company, branch, cnt);
                tab1List.add(pt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tab1List;
    }
    
    public void show_tab1() {
        ArrayList<stat> list = tab1List();
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        model.setNumRows(0);
        for (int i = list.size() - 1; i >= 0; i--) {
            Object[] row = new Object[6];
            row[0] = list.get(i).getInfo_id();
            row[1] = list.get(i).getbillerID();
            row[2] = list.get(i).getContact();
            row[3] = list.get(i).getCompany();
            row[4] = list.get(i).getBranch();
            row[5] = list.get(i).getCNT();

            model.addRow(row);
        }

    }
    
    //table 2
    public ArrayList<stat> tab2List() {
        ArrayList<stat> tab2List = new ArrayList<>();
        String phoneNo = user_contact.getText();
        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            String date_from = sdf.format(DateFrom.getDate());
//            String date_to = sdf.format(DateTo.getDate());
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
            System.out.println("1. DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());

            String query = "select top 5 InfoID, BillerID, BILLER.Contact as BContact, CompanyName, Branch, Total from\n" +
            "(\n" +
            "	(select InfoID, Total, Contact from\n" +
            "	(\n" +
            "		(select top 5 ReceiverID as InfoID2, SUM(Amount) as Total from TXN group by ReceiverID order by Total desc) as T1\n" +
            "		inner join INFORMATION on T1.InfoID2 = INFORMATION.InfoID\n" +
            "	)) as T2\n" +
            "	inner join BILLER on T2.Contact = BILLER.Contact\n" +
            ")";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

//            String receiver_contact = "01676336205";
            stat pt;

            while (rs.next()) {
                int info_id = rs.getInt("InfoID");
                int biller_id = rs.getInt("BillerID");
                String contact = rs.getString("BContact");
                String company = rs.getString("CompanyName");
                String branch = rs.getString("Branch");
                int cnt= rs.getInt("Total");

                pt = new stat(info_id, biller_id, contact, company, branch, cnt);
                tab2List.add(pt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tab2List;
    }
    
    public void show_tab2() {
        ArrayList<stat> list = tab2List();
        DefaultTableModel model = (DefaultTableModel) table2.getModel();
        model.setNumRows(0);
        for (int i = list.size() - 1; i >= 0; i--) {
            Object[] row = new Object[6];
            row[0] = list.get(i).getInfo_id();
            row[1] = list.get(i).getbillerID();
            row[2] = list.get(i).getContact();
            row[3] = list.get(i).getCompany();
            row[4] = list.get(i).getBranch();
            row[5] = list.get(i).getCNT();

            model.addRow(row);
        }

    }
    
    
    
    
//    
//    
//    //Personal Subscription
//    public ArrayList<personal_subscription> subscriptionList() {
//        ArrayList<personal_subscription> subscriptionList = new ArrayList<>();
//        String phoneNo = user_contact.getText();
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
////            String date_from = sdf.format(DateFrom.getDate());
////            String date_to = sdf.format(DateTo.getDate());
//            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//            Connection connection = DriverManager.getConnection(
//                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
//            System.out.println("1. DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());
//
//            String query = "select BILLER.BillerID as BillerID, CompanyName, Branch, INFORMATION.Address as Address, Biller.Contact as Contact from BILLER INNER JOIN INFORMATION on INFORMATION.Contact = BILLER.CONTACT where INFORMATION.InfoID  IN (Select distinct ReceiverID From TXN where SenderID = (SELECT InfoID from INFORMATION where Contact = '"+ phoneNo+"'))" ;
//            Statement st = connection.createStatement();
//            ResultSet rs = st.executeQuery(query);
//
////            String receiver_contact = "01676336205";
//
//            personal_subscription ps;
//
//            while (rs.next()) {
//                int biller_id = rs.getInt("BillerID");
//                String company_name = rs.getString("CompanyName");
//                String branch = rs.getString("Branch");
//                String address = rs.getString("Address");
//                String contact = rs.getString("Contact");
//                
//                ps = new personal_subscription(biller_id, company_name, branch, address, contact);
//                subscriptionList.add(ps);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return subscriptionList;
//    }
//
//    public void show_subscription() {
//        ArrayList<personal_subscription> list = subscriptionList();
//        DefaultTableModel model = (DefaultTableModel) subscription_table.getModel();
//
//        for (int i = list.size()-1; i >=0; i--) {
//            Object[] row = new Object[8];
//            row[0] = list.get(i).getBillerID();
//            row[1] = list.get(i).getCompanyName();
//            row[2] = list.get(i).getBranch();
//            row[3] = list.get(i).getAddress();
//            row[4] = list.get(i).getContact();
//            model.addRow(row);
//        }
//
//    }

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
        Panel_SendMoney = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        Panel_Transactions = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        Panel_SearchAccount = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        Panel_Statistics = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        Panel_ReportedIssues = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
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
        Admin_Dashboard = new javax.swing.JPanel();
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
        Admin_SendMoney = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        billerID = new javax.swing.JTextField();
        Reference = new javax.swing.JTextField();
        AgentID = new javax.swing.JTextField();
        MobileNo = new javax.swing.JTextField();
        SendAmount = new javax.swing.JTextField();
        Password = new javax.swing.JPasswordField();
        bg_paybill = new javax.swing.JLabel();
        Admin_Transactions = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        transaction_table = new javax.swing.JTable();
        jLabel23 = new javax.swing.JLabel();
        DateFrom = new com.toedter.calendar.JDateChooser();
        DateTo = new com.toedter.calendar.JDateChooser();
        jButton6 = new javax.swing.JButton();
        jLabel60 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        Admin_SearchAccount = new javax.swing.JPanel();
        jLabel66 = new javax.swing.JLabel();
        Name = new javax.swing.JTextField();
        jLabel67 = new javax.swing.JLabel();
        Contact = new javax.swing.JTextField();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        CurrentBalance = new javax.swing.JTextField();
        jLabel64 = new javax.swing.JLabel();
        SearchBy = new javax.swing.JComboBox<>();
        jLabel65 = new javax.swing.JLabel();
        SecurityAnswer = new javax.swing.JTextField();
        jLabel62 = new javax.swing.JLabel();
        ACCopening = new javax.swing.JTextField();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        id_search = new javax.swing.JTextField();
        Branch = new javax.swing.JTextField();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        logo = new javax.swing.JTextField();
        jLabel74 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        SecurityQuestion = new javax.swing.JComboBox<>();
        ID_no = new javax.swing.JTextField();
        jButton11 = new javax.swing.JButton();
        NID_no = new javax.swing.JTextField();
        District = new javax.swing.JTextField();
        Address = new javax.swing.JTextField();
        bg_paybill1 = new javax.swing.JLabel();
        Admin_Statistics = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        table1 = new javax.swing.JTable();
        jScrollPane7 = new javax.swing.JScrollPane();
        table2 = new javax.swing.JTable();
        jLabel77 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        Admin_ReportedIssues = new javax.swing.JPanel();
        jLabel49 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        table_pending_issues = new javax.swing.JTable();
        jLabel61 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        table_solved_issues = new javax.swing.JTable();
        jButton4 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        Admin_Logout = new javax.swing.JPanel();
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

        Panel_SendMoney.setBackground(new java.awt.Color(4, 35, 63));
        Panel_SendMoney.setForeground(new java.awt.Color(255, 255, 255));
        Panel_SendMoney.setPreferredSize(new java.awt.Dimension(200, 50));
        Panel_SendMoney.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Panel_SendMoneyMouseClicked(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Send Money");

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icon_paybill.png"))); // NOI18N

        javax.swing.GroupLayout Panel_SendMoneyLayout = new javax.swing.GroupLayout(Panel_SendMoney);
        Panel_SendMoney.setLayout(Panel_SendMoneyLayout);
        Panel_SendMoneyLayout.setHorizontalGroup(
            Panel_SendMoneyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_SendMoneyLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        Panel_SendMoneyLayout.setVerticalGroup(
            Panel_SendMoneyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(Panel_SendMoneyLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(Panel_SendMoney, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 130, 200, 50));

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

        Panel_SearchAccount.setBackground(new java.awt.Color(4, 35, 63));
        Panel_SearchAccount.setForeground(new java.awt.Color(255, 255, 255));
        Panel_SearchAccount.setPreferredSize(new java.awt.Dimension(200, 50));
        Panel_SearchAccount.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Panel_SearchAccountMouseClicked(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Search Account");

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icon_addbalance.png"))); // NOI18N

        javax.swing.GroupLayout Panel_SearchAccountLayout = new javax.swing.GroupLayout(Panel_SearchAccount);
        Panel_SearchAccount.setLayout(Panel_SearchAccountLayout);
        Panel_SearchAccountLayout.setHorizontalGroup(
            Panel_SearchAccountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_SearchAccountLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        Panel_SearchAccountLayout.setVerticalGroup(
            Panel_SearchAccountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(Panel_SearchAccountLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(Panel_SearchAccount, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 200, 50));

        Panel_Statistics.setBackground(new java.awt.Color(4, 35, 63));
        Panel_Statistics.setForeground(new java.awt.Color(255, 255, 255));
        Panel_Statistics.setPreferredSize(new java.awt.Dimension(200, 50));
        Panel_Statistics.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Panel_StatisticsMouseClicked(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Statistics");

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icon_subscriptions.png"))); // NOI18N

        javax.swing.GroupLayout Panel_StatisticsLayout = new javax.swing.GroupLayout(Panel_Statistics);
        Panel_Statistics.setLayout(Panel_StatisticsLayout);
        Panel_StatisticsLayout.setHorizontalGroup(
            Panel_StatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_StatisticsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        Panel_StatisticsLayout.setVerticalGroup(
            Panel_StatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(Panel_StatisticsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(Panel_Statistics, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 280, 200, 50));

        Panel_ReportedIssues.setBackground(new java.awt.Color(4, 35, 63));
        Panel_ReportedIssues.setForeground(new java.awt.Color(255, 255, 255));
        Panel_ReportedIssues.setPreferredSize(new java.awt.Dimension(200, 50));
        Panel_ReportedIssues.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Panel_ReportedIssuesMouseClicked(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Reported Issues");

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icon_offers.png"))); // NOI18N

        javax.swing.GroupLayout Panel_ReportedIssuesLayout = new javax.swing.GroupLayout(Panel_ReportedIssues);
        Panel_ReportedIssues.setLayout(Panel_ReportedIssuesLayout);
        Panel_ReportedIssuesLayout.setHorizontalGroup(
            Panel_ReportedIssuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_ReportedIssuesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        Panel_ReportedIssuesLayout.setVerticalGroup(
            Panel_ReportedIssuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
            .addGroup(Panel_ReportedIssuesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(Panel_ReportedIssues, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 330, 200, 50));

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

        jPanel2.add(Panel_Logout, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 380, 200, 50));

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

        Admin_Dashboard.setBackground(new java.awt.Color(255, 255, 255));
        Admin_Dashboard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(201, 0, 50));
        jLabel28.setText("Account Information");
        Admin_Dashboard.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, -1, -1));

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(201, 0, 50));
        jLabel29.setText("Offers ");
        Admin_Dashboard.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 310, -1, -1));

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(4, 35, 63));
        jLabel30.setText("Total No of Agents");
        Admin_Dashboard.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 160, -1, -1));

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(5, 120, 60));
        jLabel31.setText("0");
        Admin_Dashboard.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 110, -1, -1));

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(4, 35, 63));
        jLabel32.setText("Total No of Billers");
        Admin_Dashboard.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 160, -1, -1));

        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(4, 35, 63));
        jLabel33.setText("Total Bill Paid Today");
        Admin_Dashboard.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, -1, -1));

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(4, 35, 63));
        jLabel34.setText("Total Cashout Today");
        Admin_Dashboard.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 260, -1, -1));

        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(4, 35, 63));
        jLabel35.setText("Total Send Money Today");
        Admin_Dashboard.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 260, -1, -1));

        jLabel36.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(4, 35, 63));
        jLabel36.setText("Live Offers");
        Admin_Dashboard.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 420, -1, -1));

        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(4, 35, 63));
        jLabel37.setText("All offers");
        Admin_Dashboard.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 420, -1, -1));

        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(4, 35, 63));
        jLabel38.setText("Total No of Personal Users");
        Admin_Dashboard.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 160, -1, -1));

        jLabel39.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(5, 120, 60));
        jLabel39.setText("0");
        Admin_Dashboard.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 110, -1, -1));

        jLabel40.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(5, 120, 60));
        jLabel40.setText("0");
        Admin_Dashboard.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 110, -1, -1));

        jLabel41.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(5, 120, 60));
        jLabel41.setText("TK. 0");
        Admin_Dashboard.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, -1, -1));

        jLabel42.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(5, 120, 60));
        jLabel42.setText("TK. 0");
        Admin_Dashboard.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 210, -1, -1));

        jLabel43.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(5, 120, 60));
        jLabel43.setText("TK. 0");
        Admin_Dashboard.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 210, -1, -1));

        jLabel44.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(5, 120, 60));
        jLabel44.setText("0");
        Admin_Dashboard.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 380, -1, -1));

        jLabel45.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(5, 120, 60));
        jLabel45.setText("0");
        Admin_Dashboard.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 380, -1, -1));

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_searchAccount.png"))); // NOI18N
        jButton1.setBorder(null);
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });
        Admin_Dashboard.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 40, -1, -1));

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_sendMoney.png"))); // NOI18N
        jButton2.setBorder(null);
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });
        Admin_Dashboard.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 40, -1, -1));

        bg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/dashboard_bg.png"))); // NOI18N
        Admin_Dashboard.add(bg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(Admin_Dashboard, "card2");

        Admin_SendMoney.setBackground(new java.awt.Color(255, 255, 255));
        Admin_SendMoney.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel20.setText("Mobile No :");
        Admin_SendMoney.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 170, -1, -1));

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel21.setText("Agent ID :");
        Admin_SendMoney.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 110, -1, -1));

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel22.setText("Amount :");
        Admin_SendMoney.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 228, -1, -1));

        jLabel46.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel46.setText("Reference:");
        Admin_SendMoney.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 284, -1, -1));

        jLabel48.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel48.setText("Password:");
        Admin_SendMoney.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 340, -1, -1));

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_sendMoney.png"))); // NOI18N
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
        Admin_SendMoney.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 390, -1, -1));

        billerID.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        billerID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        billerID.setBorder(null);
        Admin_SendMoney.add(billerID, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 80, 270, 30));

        Reference.setBackground(new java.awt.Color(245, 245, 245));
        Reference.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        Reference.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Reference.setBorder(null);
        Admin_SendMoney.add(Reference, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 286, 250, 28));

        AgentID.setBackground(new java.awt.Color(245, 245, 245));
        AgentID.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        AgentID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        AgentID.setBorder(null);
        Admin_SendMoney.add(AgentID, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 115, 250, 28));

        MobileNo.setBackground(new java.awt.Color(245, 245, 245));
        MobileNo.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        MobileNo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        MobileNo.setBorder(null);
        Admin_SendMoney.add(MobileNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 172, 250, 28));

        SendAmount.setBackground(new java.awt.Color(245, 245, 245));
        SendAmount.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        SendAmount.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        SendAmount.setBorder(null);
        Admin_SendMoney.add(SendAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 229, 250, 28));

        Password.setBackground(new java.awt.Color(245, 245, 245));
        Password.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Password.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Password.setBorder(null);
        Admin_SendMoney.add(Password, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 342, 250, 30));

        bg_paybill.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        bg_paybill.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/bg_adminSendMoney.png"))); // NOI18N
        Admin_SendMoney.add(bg_paybill, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(Admin_SendMoney, "card2");

        Admin_Transactions.setBackground(new java.awt.Color(255, 255, 255));
        Admin_Transactions.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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

        Admin_Transactions.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 760, 410));

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(201, 0, 50));
        jLabel23.setText("From:");
        Admin_Transactions.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, -1));

        DateFrom.setDateFormatString("yyyy-MM-dd");
        Admin_Transactions.add(DateFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 30, 150, -1));

        DateTo.setDateFormatString("yyyy-MM-dd");
        Admin_Transactions.add(DateTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 30, 150, -1));

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_search.png"))); // NOI18N
        jButton6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton6MouseClicked(evt);
            }
        });
        Admin_Transactions.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 30, 140, 30));

        jLabel60.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel60.setForeground(new java.awt.Color(201, 0, 50));
        jLabel60.setText("To:");
        Admin_Transactions.add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 30, -1, -1));

        jLabel59.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/bg_transactions.png"))); // NOI18N
        Admin_Transactions.add(jLabel59, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(Admin_Transactions, "card2");

        Admin_SearchAccount.setBackground(new java.awt.Color(255, 255, 255));
        Admin_SearchAccount.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel66.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel66.setText("Name:");
        Admin_SearchAccount.add(jLabel66, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 154, 150, -1));

        Name.setEditable(false);
        Name.setBackground(new java.awt.Color(245, 245, 245));
        Name.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Name.setBorder(null);
        Admin_SearchAccount.add(Name, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 155, 185, 25));

        jLabel67.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel67.setText("Contact No:");
        Admin_SearchAccount.add(jLabel67, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 204, 150, -1));

        Contact.setEditable(false);
        Contact.setBackground(new java.awt.Color(245, 245, 245));
        Contact.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Contact.setBorder(null);
        Admin_SearchAccount.add(Contact, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 200, 185, 25));

        jLabel68.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel68.setText("National ID No:");
        Admin_SearchAccount.add(jLabel68, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 252, 150, -1));

        jLabel69.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel69.setText("Address:");
        Admin_SearchAccount.add(jLabel69, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 298, 150, -1));

        jLabel63.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel63.setText("Current Balance:");
        Admin_SearchAccount.add(jLabel63, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 154, 150, -1));

        CurrentBalance.setEditable(false);
        CurrentBalance.setBackground(new java.awt.Color(245, 245, 245));
        CurrentBalance.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        CurrentBalance.setBorder(null);
        Admin_SearchAccount.add(CurrentBalance, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 154, 183, 25));

        jLabel64.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel64.setText("Security Question:");
        Admin_SearchAccount.add(jLabel64, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 202, 150, -1));

        SearchBy.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PersonalID", "BillerID", "AgentID" }));
        Admin_SearchAccount.add(SearchBy, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 46, 150, -1));

        jLabel65.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel65.setText("Security Answer:");
        Admin_SearchAccount.add(jLabel65, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 250, 150, -1));

        SecurityAnswer.setEditable(false);
        SecurityAnswer.setBackground(new java.awt.Color(245, 245, 245));
        SecurityAnswer.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SecurityAnswer.setBorder(null);
        Admin_SearchAccount.add(SecurityAnswer, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 250, 185, 25));

        jLabel62.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel62.setText("Acc. Opening Date:");
        Admin_SearchAccount.add(jLabel62, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 300, 160, -1));

        ACCopening.setEditable(false);
        ACCopening.setBackground(new java.awt.Color(245, 245, 245));
        ACCopening.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        ACCopening.setBorder(null);
        Admin_SearchAccount.add(ACCopening, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 298, 185, 25));

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_searchAgian.png"))); // NOI18N
        jButton9.setActionCommand("efsdf");
        jButton9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton9MouseClicked(evt);
            }
        });
        Admin_SearchAccount.add(jButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 430, 146, 30));

        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_editInfo.png"))); // NOI18N
        jButton10.setActionCommand("efsdf");
        jButton10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton10MouseClicked(evt);
            }
        });
        Admin_SearchAccount.add(jButton10, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 430, 146, 30));

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_search.png"))); // NOI18N
        jButton8.setActionCommand("efsdf");
        jButton8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton8MouseClicked(evt);
            }
        });
        Admin_SearchAccount.add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 40, 146, 30));

        id_search.setBackground(new java.awt.Color(245, 245, 245));
        id_search.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        id_search.setBorder(null);
        Admin_SearchAccount.add(id_search, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 46, 150, -1));

        Branch.setEditable(false);
        Branch.setBackground(new java.awt.Color(245, 245, 245));
        Branch.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Branch.setBorder(null);
        Admin_SearchAccount.add(Branch, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 106, 185, 25));

        jLabel70.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel70.setText("ID No:");
        Admin_SearchAccount.add(jLabel70, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 150, -1));

        jLabel71.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel71.setText("Branch:");
        Admin_SearchAccount.add(jLabel71, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 106, 150, -1));

        jLabel72.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel72.setText("ID No:");
        Admin_SearchAccount.add(jLabel72, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 46, 90, -1));

        jLabel73.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel73.setText("Search By:");
        Admin_SearchAccount.add(jLabel73, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 46, 100, -1));

        logo.setEditable(false);
        logo.setBackground(new java.awt.Color(245, 245, 245));
        logo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        logo.setBorder(null);
        Admin_SearchAccount.add(logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 346, 185, 25));

        jLabel74.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel74.setText("District:");
        Admin_SearchAccount.add(jLabel74, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 348, 150, -1));

        jLabel75.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel75.setText("Logo:");
        Admin_SearchAccount.add(jLabel75, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 350, 150, -1));

        SecurityQuestion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "What is your favourite appetizer?", "What is your favourite dessert?", "What is your favourite drink?" }));
        Admin_SearchAccount.add(SecurityQuestion, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 203, 190, -1));

        ID_no.setEditable(false);
        ID_no.setBackground(new java.awt.Color(245, 245, 245));
        ID_no.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        ID_no.setBorder(null);
        Admin_SearchAccount.add(ID_no, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 106, 185, 25));

        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_confirm_1.png"))); // NOI18N
        jButton11.setActionCommand("efsdf");
        jButton11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton11MouseClicked(evt);
            }
        });
        Admin_SearchAccount.add(jButton11, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 430, 146, 30));

        NID_no.setEditable(false);
        NID_no.setBackground(new java.awt.Color(245, 245, 245));
        NID_no.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        NID_no.setBorder(null);
        Admin_SearchAccount.add(NID_no, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 250, 185, 25));

        District.setEditable(false);
        District.setBackground(new java.awt.Color(245, 245, 245));
        District.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        District.setBorder(null);
        Admin_SearchAccount.add(District, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 346, 185, 25));

        Address.setEditable(false);
        Address.setBackground(new java.awt.Color(245, 245, 245));
        Address.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Address.setBorder(null);
        Admin_SearchAccount.add(Address, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 298, 185, 25));

        bg_paybill1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        bg_paybill1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/bg_searchAccount.png"))); // NOI18N
        Admin_SearchAccount.add(bg_paybill1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(Admin_SearchAccount, "card2");

        Admin_Statistics.setBackground(new java.awt.Color(255, 255, 255));
        Admin_Statistics.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel76.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel76.setForeground(new java.awt.Color(201, 0, 50));
        jLabel76.setText("Top 5 billers (based on the total number of subscribers):");
        Admin_Statistics.add(jLabel76, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));

        table1.setBackground(new java.awt.Color(200, 0, 50));
        table1.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        table1.setForeground(new java.awt.Color(255, 255, 255));
        table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Info ID", "Biller ID", "Biller Contact", "company Name", "Branch"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(table1);

        Admin_Statistics.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 760, 140));

        table2.setBackground(new java.awt.Color(200, 0, 50));
        table2.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        table2.setForeground(new java.awt.Color(255, 255, 255));
        table2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Info ID", "Biller ID", "Biller Contact", "Company Name", "Branch"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane7.setViewportView(table2);

        Admin_Statistics.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 300, 760, 200));

        jLabel77.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel77.setForeground(new java.awt.Color(201, 0, 50));
        jLabel77.setText("Top 5 billers (based on the total amount of incoming TXN):");
        Admin_Statistics.add(jLabel77, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 260, -1, -1));

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/bg_subscriptions.png"))); // NOI18N
        Admin_Statistics.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(Admin_Statistics, "card2");

        Admin_ReportedIssues.setBackground(new java.awt.Color(255, 255, 255));
        Admin_ReportedIssues.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel49.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel49.setForeground(new java.awt.Color(201, 0, 50));
        jLabel49.setText("Pending Issues");
        Admin_ReportedIssues.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, 40));

        table_pending_issues.setBackground(new java.awt.Color(200, 0, 50));
        table_pending_issues.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        table_pending_issues.setForeground(new java.awt.Color(255, 255, 255));
        table_pending_issues.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "IssueID", "TransactionID", "SenderID", "ReceiverID", "Description", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_pending_issues.setColumnSelectionAllowed(true);
        table_pending_issues.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_pending_issuesMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(table_pending_issues);
        table_pending_issues.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        Admin_ReportedIssues.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 760, 150));

        jLabel61.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel61.setForeground(new java.awt.Color(201, 0, 50));
        jLabel61.setText("Other Issues");
        Admin_ReportedIssues.add(jLabel61, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 250, -1, -1));

        table_solved_issues.setBackground(new java.awt.Color(200, 0, 50));
        table_solved_issues.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        table_solved_issues.setForeground(new java.awt.Color(255, 255, 255));
        table_solved_issues.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "IssueID", "TransactionID", "SenderID", "ReceiverID", "Description", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_solved_issues.setColumnSelectionAllowed(true);
        jScrollPane5.setViewportView(table_solved_issues);
        table_solved_issues.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        Admin_ReportedIssues.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, 760, 200));

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_ignored.png"))); // NOI18N
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton4MouseClicked(evt);
            }
        });
        Admin_ReportedIssues.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 20, 140, 30));

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_solved.png"))); // NOI18N
        jButton7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton7MouseClicked(evt);
            }
        });
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        Admin_ReportedIssues.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 20, 140, 30));

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/bg_offers.png"))); // NOI18N
        Admin_ReportedIssues.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(Admin_ReportedIssues, "card2");

        Admin_Logout.setBackground(new java.awt.Color(255, 255, 255));
        Admin_Logout.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        TxnID.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        TxnID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        TxnID.setBorder(null);
        TxnID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxnIDActionPerformed(evt);
            }
        });
        Admin_Logout.add(TxnID, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 80, 230, 30));

        ReportedID.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        ReportedID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        ReportedID.setBorder(null);
        ReportedID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReportedIDActionPerformed(evt);
            }
        });
        Admin_Logout.add(ReportedID, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 135, 230, 30));

        Amount.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        Amount.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Amount.setBorder(null);
        Amount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AmountActionPerformed(evt);
            }
        });
        Admin_Logout.add(Amount, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 245, 230, 30));

        Description.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        Description.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Description.setBorder(null);
        Description.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DescriptionActionPerformed(evt);
            }
        });
        Admin_Logout.add(Description, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 356, 230, 30));

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel24.setText("Transaction ID :");
        Admin_Logout.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 80, -1, -1));

        jLabel55.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel55.setText("Agent/ Biller ID :");
        Admin_Logout.add(jLabel55, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 135, -1, -1));

        jLabel56.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel56.setText("Amount :");
        Admin_Logout.add(jLabel56, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 250, -1, -1));

        jLabel57.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel57.setText("Date:");
        Admin_Logout.add(jLabel57, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 300, -1, -1));

        jLabel58.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel58.setText("Description:");
        Admin_Logout.add(jLabel58, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 350, -1, -1));

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/btn_submit.png"))); // NOI18N
        jButton5.setBorder(null);
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton5MouseClicked(evt);
            }
        });
        Admin_Logout.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 420, -1, -1));

        radio_biller.setBackground(new java.awt.Color(255, 255, 255));
        Acc_Type.add(radio_biller);
        radio_biller.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        radio_biller.setText("Biller");
        radio_biller.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_billerActionPerformed(evt);
            }
        });
        Admin_Logout.add(radio_biller, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 190, -1, -1));

        radio_agent.setBackground(new java.awt.Color(255, 255, 255));
        Acc_Type.add(radio_agent);
        radio_agent.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        radio_agent.setText("Agent");
        radio_agent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_agentActionPerformed(evt);
            }
        });
        Admin_Logout.add(radio_agent, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 190, -1, -1));

        Txn_Date.setDateFormatString("yyyy-MM-dd");
        Admin_Logout.add(Txn_Date, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 302, 250, 30));

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/bg_reportIssues.png"))); // NOI18N
        Admin_Logout.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.add(Admin_Logout, "card2");

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
        resetPanelColor(Panel_SendMoney);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_SearchAccount);
        resetPanelColor(Panel_Statistics);
        resetPanelColor(Panel_ReportedIssues);

        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Admin_Dashboard);
        jPanel3.repaint();
        jPanel3.revalidate();

        try {

            String contact = user_contact.getText();
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "select 0 as X, count(*) as cnt  from PERSONAL" +
                    " UNION " +
                    "select 1 as X, count(*) as cnt from AGENT" +
                    " UNION " +
                    "select 2 as X, count(*) as cnt  from BILLER"
            );
            //Total Personal Users
            resultSet.next();
            int x = resultSet.getInt("cnt");
            jLabel39.setText(Integer.toString(x));
            
            //Total Biller Users
            resultSet.next();
            x = resultSet.getInt("cnt");
            jLabel40.setText(Integer.toString(x));
            
            //Total Agent Users
            resultSet.next();
            x = resultSet.getInt("cnt");
            jLabel31.setText(Integer.toString(x));

          
            //PERSONAL - BILLER
            resultSet = statement.executeQuery(
                    "select sum(Amount) as SUM from TXN where SenderID LIKE '3%'"
                    + " and DATEDIFF(DAY, Date, GETDATE()) <= 1"
            );
            if (resultSet.next()) {
                double x2 = resultSet.getDouble("SUM");
                jLabel41.setText("TK. " + Double.toString(x2));
            }
            
            //BILLER - AGENT
            resultSet = statement.executeQuery(
                    "select sum(Amount) as SUM from TXN where SenderID LIKE '4%'"
                    + " and DATEDIFF(DAY, Date, GETDATE()) <= 1"
            );
            if (resultSet.next()) {
                double x2 = resultSet.getDouble("SUM");
                jLabel41.setText("TK. " + Double.toString(x2));
            }
            
            // AGENT - PERSONAL
            resultSet = statement.executeQuery(
                    "select sum(Amount) as SUM from TXN where SenderID LIKE '2%'"
                    + " and DATEDIFF(DAY, Date, GETDATE()) <= 1"
            );
            if (resultSet.next()) {
                double x2 = resultSet.getDouble("SUM");
                jLabel41.setText("TK. " + Double.toString(x2));
            }
            
            

            //Live Offers
            resultSet = statement.executeQuery(
                    "select count(OfferID) as noOfOffer from OFFER where DATEDIFF(DAY, DeadLine, GETDATE()) <= 0"
            );
            if (resultSet.next()) {
                int x2 = resultSet.getInt("noOfOffer");
                jLabel44.setText(Integer.toString(x2));
            }

            //All Offers
            resultSet = statement.executeQuery(
                    "select count(OfferID) as noOfOffer from OFFER"
            );
            if (resultSet.next()) {
                int x2 = resultSet.getInt("noOfOffer");
                jLabel45.setText(Integer.toString(x2));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_Panel_DashboardMouseClicked

    private void Panel_SendMoneyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_SendMoneyMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_SendMoney);
        Title_text.setText("Send Money");

        //Drak Blue
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_SearchAccount);
        resetPanelColor(Panel_Statistics);
        resetPanelColor(Panel_ReportedIssues);

        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Admin_SendMoney);
        jPanel3.repaint();
        jPanel3.revalidate();
    }//GEN-LAST:event_Panel_SendMoneyMouseClicked

    private void Panel_TransactionsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_TransactionsMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_Transactions);
        Title_text.setText("Transactions");

        //Drak Blue
        resetPanelColor(Panel_SendMoney);
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_SearchAccount);
        resetPanelColor(Panel_Statistics);
        resetPanelColor(Panel_ReportedIssues);

        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Admin_Transactions);
        jPanel3.repaint();
        jPanel3.revalidate();
        //show_transaction();
    }//GEN-LAST:event_Panel_TransactionsMouseClicked

    private void Panel_SearchAccountMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_SearchAccountMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_SearchAccount);
        Title_text.setText("Search Account");
        //Drak Blue
        resetPanelColor(Panel_SendMoney);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_Statistics);
        resetPanelColor(Panel_ReportedIssues);

        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Admin_SearchAccount);
        jPanel3.repaint();
        jPanel3.revalidate();
    }//GEN-LAST:event_Panel_SearchAccountMouseClicked

    private void Panel_StatisticsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_StatisticsMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_Statistics);
        Title_text.setText("Statistics");

        //Drak Blue
        resetPanelColor(Panel_SendMoney);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_SearchAccount);
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_ReportedIssues);

        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Admin_Statistics);
        jPanel3.repaint();
        jPanel3.revalidate();
    }//GEN-LAST:event_Panel_StatisticsMouseClicked

    private void Panel_ReportedIssuesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_ReportedIssuesMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_ReportedIssues);
        Title_text.setText("Reported Issues");

        //Drak Blue
        resetPanelColor(Panel_SendMoney);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_SearchAccount);
        resetPanelColor(Panel_Statistics);
        resetPanelColor(Panel_Dashboard);

        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Admin_ReportedIssues);
        jPanel3.repaint();
        jPanel3.revalidate();

        //show_offers();

    }//GEN-LAST:event_Panel_ReportedIssuesMouseClicked

    private void Panel_LogoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel_LogoutMouseClicked
        // TODO add your handling code here:
        //Magenda
        panelColor(Panel_Logout);
        Title_text.setText("Logout");

        //Drak Blue
        resetPanelColor(Panel_SendMoney);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_SearchAccount);
        resetPanelColor(Panel_Statistics);
        resetPanelColor(Panel_ReportedIssues);

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
            resetPanelColor(Panel_SendMoney);
            resetPanelColor(Panel_Transactions);
            resetPanelColor(Panel_SearchAccount);
            resetPanelColor(Panel_Statistics);
            resetPanelColor(Panel_ReportedIssues);

            resetPanelColor(Panel_Logout);
            //remove panel
            jPanel3.removeAll();
            jPanel3.repaint();
            jPanel3.revalidate();

            // add panel
            jPanel3.add(Admin_Dashboard);
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

            String a_id = AgentID.getText();
            int agent_id = Integer.parseInt(a_id);
            String agent_contact = MobileNo.getText();
            String s_amount = SendAmount.getText();
            double send_amount = Double.parseDouble(s_amount);
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
                    DB_curr_balance = DB_curr_balance + send_amount;
                    
                    query = "UPDATE INFORMATION SET CurrBalance = ? where Contact = " + agent_contact;
                    PreparedStatement st2 = connection.prepareStatement(query);
                    st2.setDouble(1, DB_curr_balance);
                    st2.executeUpdate();
                    
                    query = "Insert into TXN (Date, Amount, SenderID, ReceiverID, Reference)values (getdate(), ?,(SELECT InfoID From INFORMATION where Contact = "+sender_contact+"),?,?)";
                    PreparedStatement st3 = connection.prepareStatement(query);
                    st3.setDouble(1, send_amount);
                    st3.setInt(2, agent_id);
                    st3.setString(3, reference);
                    st3.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Transaction Successful");
                    
                    AgentID.setText("");
                    MobileNo.setText("");
                    SendAmount.setText("");
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
                if (radio_agent.isSelected()) {
                    if (reported_id == DB_senderID && amount == DB_amount && txn_date.equals(DB_date)) {
                        if (status == 1) {
                            JOptionPane.showMessageDialog(null, "The issue has already been submitted.\nHave Patience Dude!");
                        } else if (status == 0) {
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
                        if (status == 1) {
                            JOptionPane.showMessageDialog(null, "The issue has already been submitted.\nHave Ptience Dude!");
                        } else if (status == 0) {
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

    private void jButton9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton9MouseClicked
        // TODO add your handling code here:
        ID_no.setText("");
        Name.setText("");
        Contact.setText("");
        NID_no.setText("");
        Address.setText("");
        District.setText("");
        CurrentBalance.setText("");
        SecurityAnswer.setText("");
        ACCopening.setText("");
        logo.setText("");
        
    }//GEN-LAST:event_jButton9MouseClicked

    private void jButton10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton10MouseClicked
        // TODO add your handling code here:
        Name.setEditable(true);
        Contact.setEditable(true);
        NID_no.setEditable(true);
        Address.setEditable(true);
        District.setEditable(true);
        Branch.setEditable(true);
        SecurityAnswer.setEditable(true);
        logo.setEditable(true);
       
    }//GEN-LAST:event_jButton10MouseClicked

    private void jButton8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton8MouseClicked
        // TODO add your handling code here:

        String s_id = id_search.getText();
        int search_id = Integer.parseInt(s_id);
        String acc_type = SearchBy.getSelectedItem().toString();
        //PersonalID, BillerID, AgentID
        if (acc_type.equals("PersonalID")) {
          
            try {

                //String contact = user_contact.getText();

                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                Connection connection = DriverManager.getConnection(
                        "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
                System.out.println("DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());

                String query = "SELECT Name, INFORMATION.Contact as Contact, NationalID, Address, District, SecurityQue, SecurityAns, CurrBalance, AccOpening, PhotoPath FROM PERSONAL INNER JOIN INFORMATION ON INFORMATION.Contact = PERSONAL.Contact where PERSONAL.PersonalID= " + search_id;
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(query);

                //personal_transaction pt;

                while (rs.next()) {

                    String DB_name = rs.getString("Name");
                    String DB_Contact = rs.getString("Contact");
                    int nid= rs.getInt("NationalID");
                    String DB_nid = Integer.toString(nid);
                    String DB_address = rs.getString("Address");
                    String DB_district = rs.getString("District");
                    String DB_SecurityQ = rs.getString("SecurityQue");
                    String DB_SecurityA = rs.getString("SecurityAns");
                    double Current_balance = rs.getDouble("CurrBalance");
                    String DB_current_balance = Double.toString(Current_balance);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String acc_opening = sdf.format(rs.getDate("AccOpening"));
                    String DB_logo = rs.getString("PhotoPath");

                    ID_no.setText(s_id);
                    Name.setText(DB_name);
                    Contact.setText(DB_Contact);
                    NID_no.setText(DB_nid);
                    Address.setText(DB_address);
                    District.setText(DB_district);
                    CurrentBalance.setText("TK."+DB_current_balance);
                    SecurityAnswer.setText(DB_SecurityA);
                    ACCopening.setText(acc_opening);
                    logo.setText(DB_logo);
                    
                    

                    if (DB_SecurityQ.equals("What is your favourite appetizer?")) {
                        SecurityQuestion.setSelectedItem("What is your favourite appetizer?");
                    } else if (DB_SecurityQ.equals("What is your favourite dessert?")) {
                        SecurityQuestion.setSelectedItem("What is your favourite dessert?");
                    } else if (DB_SecurityQ.equals("What is your favourite drink?")) {
                        SecurityQuestion.setSelectedItem("What is your favourite drink?");
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else if (acc_type.equals("BillerID")) {
            
            try {

                //String contact = user_contact.getText();

                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                Connection connection = DriverManager.getConnection(
                        "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
                System.out.println("DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());

                String query = "SELECT CompanyName, Branch, INFORMATION.Contact as Contact, NationalID, Address, District, SecurityQue, SecurityAns, CurrBalance, AccOpening, PhotoPath FROM BILLER INNER JOIN INFORMATION ON INFORMATION.Contact = BILLER.Contact where BILLER.BillerID= " + search_id;
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(query);

                //personal_transaction pt;

                while (rs.next()) {

                    String DB_name = rs.getString("CompanyName");
                    String DB_branch = rs.getString("Branch");
                    String DB_Contact = rs.getString("Contact");
                    int nid= rs.getInt("NationalID");
                    String DB_nid = Integer.toString(nid);
                    String DB_address = rs.getString("Address");
                    String DB_district = rs.getString("District");
                    String DB_SecurityQ = rs.getString("SecurityQue");
                    String DB_SecurityA = rs.getString("SecurityAns");
                    double Current_balance = rs.getDouble("CurrBalance");
                    String DB_current_balance = Double.toString(Current_balance);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String acc_opening = sdf.format(rs.getDate("AccOpening"));
                    String DB_logo = rs.getString("PhotoPath");

                    ID_no.setText(s_id);
                    Name.setText(DB_name);
                    Contact.setText(DB_Contact);
                    NID_no.setText(DB_nid);
                    Address.setText(DB_address);
                    District.setText(DB_district);
                    Branch.setText(DB_branch);
                    CurrentBalance.setText("TK."+DB_current_balance);
                    SecurityAnswer.setText(DB_SecurityA);
                    ACCopening.setText(acc_opening);
                    logo.setText(DB_logo);
                    
                    

                    if (DB_SecurityQ.equals("What is your favourite appetizer?")) {
                        SecurityQuestion.setSelectedItem("What is your favourite appetizer?");
                    } else if (DB_SecurityQ.equals("What is your favourite dessert?")) {
                        SecurityQuestion.setSelectedItem("What is your favourite dessert?");
                    } else if (DB_SecurityQ.equals("What is your favourite drink?")) {
                        SecurityQuestion.setSelectedItem("What is your favourite drink?");
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else if (acc_type.equals("AgentID")) {
           
            try {

                //String contact = user_contact.getText();

                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                Connection connection = DriverManager.getConnection(
                        "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
                System.out.println("DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());

                String query = "SELECT Name, INFORMATION.Contact as Contact, NationalID, Address, District, SecurityQue, SecurityAns, CurrBalance, AccOpening, PhotoPath FROM AGENT INNER JOIN INFORMATION ON INFORMATION.Contact = AGENT.Contact where AGENT.AgentID= " + search_id;
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(query);

                //personal_transaction pt;

                while (rs.next()) {

                    String DB_name = rs.getString("Name");
                    String DB_Contact = rs.getString("Contact");
                    int nid= rs.getInt("NationalID");
                    String DB_nid = Integer.toString(nid);
                    String DB_address = rs.getString("Address");
                    String DB_district = rs.getString("District");
                    String DB_SecurityQ = rs.getString("SecurityQue");
                    String DB_SecurityA = rs.getString("SecurityAns");
                    double Current_balance = rs.getDouble("CurrBalance");
                    String DB_current_balance = Double.toString(Current_balance);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String acc_opening = sdf.format(rs.getDate("AccOpening"));
                    String DB_logo = rs.getString("PhotoPath");

                    ID_no.setText(s_id);
                    Name.setText(DB_name);
                    Contact.setText(DB_Contact);
                    NID_no.setText(DB_nid);
                    Address.setText(DB_address);
                    District.setText(DB_district);
                    CurrentBalance.setText("TK."+DB_current_balance);
                    SecurityAnswer.setText(DB_SecurityA);
                    ACCopening.setText(acc_opening);
                    logo.setText(DB_logo);
                    
                    

                    if (DB_SecurityQ.equals("What is your favourite appetizer?")) {
                        SecurityQuestion.setSelectedItem("What is your favourite appetizer?");
                    } else if (DB_SecurityQ.equals("What is your favourite dessert?")) {
                        SecurityQuestion.setSelectedItem("What is your favourite dessert?");
                    } else if (DB_SecurityQ.equals("What is your favourite drink?")) {
                        SecurityQuestion.setSelectedItem("What is your favourite drink?");
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else{
            JOptionPane.showMessageDialog(null, "Incorrect Account Search By Option");
        }

    }//GEN-LAST:event_jButton8MouseClicked

    private void jButton11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton11MouseClicked
        // TODO add your handling code here:
        
        
//         Name.setEditable(true);
//        Contact.setEditable(true);
//        NID_no.setEditable(true);
//        Address.setEditable(true);
//        District.setEditable(true);
//        Branch.setEditable(true);
//        SecurityAnswer.setEditable(true);
//        logo.setEditable(true);
        
//        try {
//            String contact = user_contact.getText();
//
//            String new_address = address.getText();
//            String new_district = district.getText();
//            String new_branch = branch.getText();
//            String new_security_ans = security_answer.getText();
//            String new_pass = String.valueOf(password.getText());
//            String new_confirm_pass = String.valueOf(confirm_password.getText());
//            String new_security_que = security_question.getSelectedItem().toString();
//            String new_logo = logo.getText();
//
//            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//            Connection connection = DriverManager.getConnection(
//                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
//
//            System.out.println("DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());
//
//            if (new_pass.equals(new_confirm_pass)) {
//
//                String query = "UPDATE INFORMATION SET Password = ?, Address = ?,District = ?,SecurityQue = ?,SecurityAns = ?,PhotoPath = ? where Contact = " + contact;
//                PreparedStatement st = connection.prepareStatement(query);
//                st.setString(1, new_pass);
//                st.setString(2, new_address);
//                st.setString(3, new_district);
//                st.setString(4, new_security_que);
//                st.setString(5, new_security_ans);
//                st.setString(6, new_logo);
//                st.executeUpdate();
//
//                String query1 = "UPDATE BILLER SET Branch = ? where Contact = " + contact;
//                PreparedStatement st1 = connection.prepareStatement(query1);
//                st1.setString(1, new_branch);
//                st1.executeUpdate();
//
//                JOptionPane.showMessageDialog(null, "Information updated successfully.");
//
//                address.setEditable(false);
//                district.setEditable(false);
//                password.setEditable(false);
//                confirm_password.setEditable(false);
//                branch.setEditable(false);
//                security_answer.setEditable(false);
//                logo.setEditable(false);
//
//                //Magenda
//                panelColor(Panel_Dashboard);
//                Title_text.setText("Dashboard");
//
//                //Drak Blue
//                resetPanelColor(Panel_Cashout);
//                resetPanelColor(Panel_Transactions);
//                resetPanelColor(Panel_AddOffers);
//                resetPanelColor(Panel_OffersList);
//                resetPanelColor(Panel_EditInformation);
//                resetPanelColor(Panel_Reportissues);
//                resetPanelColor(Panel_Logout);
//
//                //remove panel
//                jPanel3.removeAll();
//                jPanel3.repaint();
//                jPanel3.revalidate();
//
//                // add panel
//                jPanel3.add(Biller_Dashboard);
//                jPanel3.repaint();
//                jPanel3.revalidate();
//
//            } else {
//                JOptionPane.showMessageDialog(null, "Password doesn't match.");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        
    }//GEN-LAST:event_jButton11MouseClicked

    private void table_pending_issuesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_pending_issuesMouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_table_pending_issuesMouseClicked

    private void jButton7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton7MouseClicked
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_pending_issues.getModel();

        String issue_id = model.getValueAt(0, 0).toString();
        String txn_id = model.getValueAt(0, 1).toString();
        String sender_id = model.getValueAt(0, 2).toString();
        String receiver_id = model.getValueAt(0, 3).toString();
        
        String description = model.getValueAt(0, 4).toString();
        String status = model.getValueAt(0, 5).toString();
        

        DefaultTableModel model2 = (DefaultTableModel) table_solved_issues.getModel();

        Object[] row = new Object[6];
        row[0] = issue_id;
        row[1] = txn_id;
        row[2] = sender_id;
        row[3] = receiver_id;
        row[4] = description;
        row[5] = "SOLVED";
        model2.addRow(row);
        try {
            
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
            System.out.println("2. DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());
            

            
            
            String query = "UPDATE ISSUE SET Status = 'SOLVED' where IssueID = "+ issue_id;
            PreparedStatement st5 = connection.prepareStatement(query);
            st5.executeUpdate();
            model.removeRow(0);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton7MouseClicked

    private void jButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseClicked
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_pending_issues.getModel();

        String issue_id = model.getValueAt(0, 0).toString();
        String txn_id = model.getValueAt(0, 1).toString();
        String sender_id = model.getValueAt(0, 2).toString();
        String receiver_id = model.getValueAt(0, 3).toString();
        
        String description = model.getValueAt(0, 4).toString();
        String status = model.getValueAt(0, 5).toString();
        

        DefaultTableModel model2 = (DefaultTableModel) table_solved_issues.getModel();

        Object[] row = new Object[6];
        row[0] = issue_id;
        row[1] = txn_id;
        row[2] = sender_id;
        row[3] = receiver_id;
        row[4] = description;
        row[5] = "IGNORED";
        model2.addRow(row);
        try {
            
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=eWallet;selectMethod=cursor", "sa", "123456");
            System.out.println("2. DATABASE NAME IS:" + connection.getMetaData().getDatabaseProductName());
            

            
            
            String query = "UPDATE ISSUE SET Status = 'IGNORED' where IssueID = "+ issue_id;
            PreparedStatement st5 = connection.prepareStatement(query);
            st5.executeUpdate();
            model.removeRow(0);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton4MouseClicked

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        // TODO add your handling code here:
         //Magenda
        panelColor(Panel_SendMoney);
        Title_text.setText("Send Money");

        //Drak Blue
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_SearchAccount);
        resetPanelColor(Panel_Statistics);
        resetPanelColor(Panel_ReportedIssues);

        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Admin_SendMoney);
        jPanel3.repaint();
        jPanel3.revalidate();
    }//GEN-LAST:event_jButton2MouseClicked

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        // TODO add your handling code here:
          //Magenda
        panelColor(Panel_SearchAccount);
        Title_text.setText("Search Account");
        //Drak Blue
        resetPanelColor(Panel_SendMoney);
        resetPanelColor(Panel_Transactions);
        resetPanelColor(Panel_Dashboard);
        resetPanelColor(Panel_Statistics);
        resetPanelColor(Panel_ReportedIssues);

        resetPanelColor(Panel_Logout);

        //remove panel
        jPanel3.removeAll();
        jPanel3.repaint();
        jPanel3.revalidate();

        // add panel
        jPanel3.add(Admin_SearchAccount);
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
            java.util.logging.Logger.getLogger(HomepageAdmin.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomepageAdmin.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomepageAdmin.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomepageAdmin.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HomepageAdmin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ACCopening;
    private javax.swing.ButtonGroup Acc_Type;
    private javax.swing.JTextField Address;
    private javax.swing.JPanel Admin_Dashboard;
    private javax.swing.JPanel Admin_Logout;
    private javax.swing.JPanel Admin_ReportedIssues;
    private javax.swing.JPanel Admin_SearchAccount;
    private javax.swing.JPanel Admin_SendMoney;
    private javax.swing.JPanel Admin_Statistics;
    private javax.swing.JPanel Admin_Transactions;
    private javax.swing.JTextField AgentID;
    private javax.swing.JTextField Amount;
    private javax.swing.JTextField Branch;
    private javax.swing.JTextField Contact;
    private javax.swing.JTextField CurrentBalance;
    private com.toedter.calendar.JDateChooser DateFrom;
    private com.toedter.calendar.JDateChooser DateTo;
    private javax.swing.JTextField Description;
    private javax.swing.JTextField District;
    private javax.swing.JTextField ID_no;
    private javax.swing.JTextField MobileNo;
    private javax.swing.JTextField NID_no;
    private javax.swing.JTextField Name;
    private javax.swing.JPanel Panel_Dashboard;
    private javax.swing.JPanel Panel_Logout;
    private javax.swing.JPanel Panel_ReportedIssues;
    private javax.swing.JPanel Panel_SearchAccount;
    private javax.swing.JPanel Panel_SendMoney;
    private javax.swing.JPanel Panel_Statistics;
    private javax.swing.JPanel Panel_Transactions;
    private javax.swing.JPasswordField Password;
    private javax.swing.JTextField Reference;
    private javax.swing.JTextField ReportedID;
    private javax.swing.JComboBox<String> SearchBy;
    private javax.swing.JTextField SecurityAnswer;
    private javax.swing.JComboBox<String> SecurityQuestion;
    private javax.swing.JTextField SendAmount;
    private javax.swing.JLabel Title_text;
    private javax.swing.JTextField TxnID;
    private com.toedter.calendar.JDateChooser Txn_Date;
    private javax.swing.JLabel bg;
    private javax.swing.JLabel bg_paybill;
    private javax.swing.JLabel bg_paybill1;
    private javax.swing.JTextField billerID;
    private javax.swing.JLabel dateField;
    private javax.swing.JTextField id_search;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
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
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
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
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField logo;
    private javax.swing.JRadioButton radio_agent;
    private javax.swing.JRadioButton radio_biller;
    private javax.swing.JTable table1;
    private javax.swing.JTable table2;
    private javax.swing.JTable table_pending_issues;
    private javax.swing.JTable table_solved_issues;
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
