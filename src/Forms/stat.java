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
class stat {
    
     private int info_id, biller_id, cnt;
    
    private String contact,company, branch ;
    
    public stat(int info_id, int biller_id,String contact, String company, String branch, int cnt )
    {
        this.info_id = info_id;    
        this.biller_id = biller_id;
        this.contact = contact;
        this.company = company;
        this.branch = branch; 
        this.cnt = cnt;
        
    }
    
    public int getInfo_id(){
        return info_id;
    }
    public int getbillerID(){
        return biller_id;
    }
    public String getContact(){
        return contact;
    }
    public String getCompany(){
        return company;
    }
    public String getBranch(){
        return branch;
    }
    public int getCNT(){
        return cnt;
    }
    
    
}
