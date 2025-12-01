package javaproject1.BLL.Service.implementation;

import javaproject1.BLL.Service.abstraction.IPaymentService;
import javaproject1.DAL.Entity.Payment;
import javaproject1.DAL.Repo.Implementation.PaymentRepoImpl;
import javaproject1.DAL.Repo.abstraction.IPaymentRepo;

import java.util.Date;
import java.util.List;

public class PaymentServiceImpl implements IPaymentService {

    private final IPaymentRepo paymentRepo;

    public PaymentServiceImpl() {
        this(new PaymentRepoImpl());
    }

    public PaymentServiceImpl(IPaymentRepo paymentRepo) {
        this.paymentRepo = paymentRepo;
    }

    @Override
    public boolean processPayment(Payment payment) {
        if (payment == null) {
            System.out.println("Payment is null!");
            return false;
        }

        if (payment.getAmount() <= 0) {
            System.out.println("Invalid payment amount!");
            payment.setStatus("Failed");
            return false;
        }

        System.out.println("Processing payment of $" + payment.getAmount() +
                           " via " + payment.getPaymentMethod());
        payment.setStatus("Completed");
        payment.setTransactionDate(new Date());
        paymentRepo.updatePayment(payment);
        System.out.println("Payment successful.");
        return true;
    }

    @Override
    public void addPayment(Payment payment) {
        paymentRepo.addPayment(payment);
        System.out.println("Payment added with ID: " + payment.getPaymentId());
    }

    @Override
    public Payment getPaymentById(int id) {
        Payment payment = paymentRepo.getPaymentById(id);
        if (payment == null) {
            System.out.println("Payment not found with ID: " + id);
        }
        return payment;
    }

    @Override
    public void updatePayment(Payment payment) {
        paymentRepo.updatePayment(payment);
        System.out.println("Payment updated with ID: " + payment.getPaymentId());
    }

    @Override
    public void deletePayment(int id) {
        paymentRepo.deletePayment(id);
        System.out.println("Payment deleted with ID: " + id);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepo.getAllPayments();
    }
}
