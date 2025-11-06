package javaproject1.DAL.Repo.abstraction;

import java.util.List;
import javaproject1.DAL.Entity.Payment;

public interface IPaymentRepo {
    
    public void addPayment(Payment payment);
    public Payment getPaymentById(int id);
    public void updatePayment(Payment payment);
    public void deletePayment(int id);
    List<Payment> getAllPayments();
    
}