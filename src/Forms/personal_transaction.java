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
class personal_transaction {
    private int txn_id, sender_id, receiver_id;
    private float amount;
    private String date, reference, sender_contact, receiver_contact;
    
    public personal_transaction(int txn_id, String date, float amount,  int sender_id, int receiver_id, String sender_contact, String receiver_contact, String reference )
    {
        this.txn_id = txn_id;    
        this.amount = amount;
        this.date = date;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id; 
        this.sender_contact = sender_contact;
        this.receiver_contact = receiver_contact;
        this.reference = reference;
    }
    
    public int getTxn_id(){
        return txn_id;
    }
    public float getAmount(){
        return amount;
    }
    public String getDate(){
        return date;
    }
    public int getSenderID(){
        return sender_id;
    }
    public int getReceiverID(){
        return receiver_id;
    }
    public String getSenderContact(){
        return sender_contact;
    }
    public String getReceiverContact(){
        return receiver_contact;
    }
    public String getReference(){
        return reference;
    }
    
    
}
