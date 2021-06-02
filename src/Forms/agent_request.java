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
class agent_request {
    private int req_id, sender_id, receiver_id;
    private double amount;
    private String reference, status;
    
    public agent_request(int req_id, int sender_id, int receiver_id, double amount, String reference, String status)
    {
        this.req_id = req_id; 
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.amount = amount;
        this.reference = reference;
        this.status = status;
       
    }
    
    public int getReqID(){
        return req_id;
    }
    public int getSenderID(){
        return sender_id;
    }
    public int getReceiverID(){
        return receiver_id;
    }
    public double getAmount(){
        return amount;
    }
    public String getReference(){
        return reference;
    }
    public String getStatus(){
        return status;
    }
}
