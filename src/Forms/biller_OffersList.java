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
class biller_OffersList {
    private int offer_id, rate;
    double max_discount, min_purchase;
    private String deadline, voucher;
    
    public biller_OffersList(int offer_id, int rate, double max_discount, double min_purchase, String deadline, String voucher)
    {
        this.offer_id = offer_id; 
        this.rate = rate;
        this.max_discount = max_discount;
        this.min_purchase = min_purchase;
        this.deadline = deadline;
        this.voucher = voucher; 
       
    }
    
    public int getOfferID(){
        return offer_id;
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
