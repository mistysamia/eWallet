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
class personal_offer {
    private int biller_id, rate;
    private double max_discount, min_purchase;
    private String biller_name, deadline, voucher;
    
    public personal_offer(int biller_id, String biller_name, int rate, double max_discount, double min_purchase, String deadline, String voucher)
    {
        this.biller_id = biller_id;    
        this.biller_name = biller_name;
        this.rate = rate;
        this.max_discount = max_discount;
        this.min_purchase = min_purchase; 
        this.deadline = deadline;
        this.voucher = voucher;
    }
    
    public int getBillerID(){
        return biller_id;
    }
    public String getBillerName(){
        return biller_name;
    }
    public int getRate(){
        return rate;
    }
    public double getMaxDiscount(){
        return max_discount;
    }
    public double getMinPurchase(){
        return min_purchase;
    }
    public String getDeadline(){
        return deadline;
    }
    public String getVoucher(){
        return voucher;
    }
    
}
