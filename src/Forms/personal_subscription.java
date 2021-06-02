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
class personal_subscription {
    private int biller_id;
    private String company_name, branch, address, contact;
    
    public personal_subscription(int biller_id, String company_name, String branch, String address, String contact)
    {
        this.biller_id = biller_id;    
        this.company_name = company_name;
        this.branch = branch;
        this.address = address;
        this.contact = contact; 
       
    }
    
    public int getBillerID(){
        return biller_id;
    }
    public String getCompanyName(){
        return company_name;
    }
    public String getBranch(){
        return branch;
    }
    public String getAddress(){
        return address;
    }
    public String getContact(){
        return contact;
    }
}
