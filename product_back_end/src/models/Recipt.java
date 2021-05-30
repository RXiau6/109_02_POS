/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author RXiau6
 */
public class Recipt {
    private String account;
    private String recipt_num;
    private double transaction_amount;
    private String transaction_date;
    private String currency;

    public Recipt(String account, String recipt_num, double transaction_amount, String currency) {
        this.account = account;
        this.recipt_num = recipt_num;
        this.transaction_amount = transaction_amount;
        this.currency = currency;
    }
    
    public Recipt(){
        
    }
    public String getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public double getTransaction_amount() {
        return transaction_amount;
    }

    public void setTransaction_amount(double transaction_amount) {
        this.transaction_amount = transaction_amount;
    }

    public String getRecipt_num() {
        return recipt_num;
    }

    public void setRecipt_num(String recipt_num) {
        this.recipt_num = recipt_num;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
