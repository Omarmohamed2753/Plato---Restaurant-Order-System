package javaproject1.BLL.Service.abstraction;

import javaproject1.DAL.Entity.Payment;
import java.util.List;

public interface IPaymentService {

    boolean processPayment(Payment payment); 

    void addPayment(Payment payment);
    Payment getPaymentById(int id);
    void updatePayment(Payment payment);
    void deletePayment(int id);
    List<Payment> getAllPayments();
}
