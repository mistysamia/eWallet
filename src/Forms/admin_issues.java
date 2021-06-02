/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms;

/**
 *
 * @author ASUS
 */
class admin_issues {
    private int issue_id,txn_id, sender_id, receiver_id;
    
    private String description, status;
    
    public admin_issues(int issue_id, int txn_id, int sender_id, int receiver_id, String description, String status)
    {
        this.issue_id = issue_id; 
        this.txn_id = txn_id;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.description = description;
        this.status = status; 
       
    }
    
    public int getIssueID(){
        return issue_id;
    }
    public int getTxnID(){
        return txn_id;
    }
    public int getSenderID(){
        return sender_id;
    }
    public int getReceiverID(){
        return receiver_id;
    }
    public String getDescription(){
        return description;
    }
    public String getStatus(){
        return status;
    }
}
