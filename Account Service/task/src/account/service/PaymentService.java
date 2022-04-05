package account.service;

import account.model.Payment;
import account.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public void saveAll(List<Payment> toSave) {
        paymentRepository.saveAll(toSave);
    }

    public Payment findByEmployeeIgnoreCaseAndPeriod(String employee, String period) {
        return paymentRepository.findByEmployeeIgnoreCaseAndPeriod(employee, period).orElse(null);
    }

    public List<Payment> findByEmployeeIgnoreCaseOrderByPeriodDesc(String employee) {
        return paymentRepository.findByEmployeeIgnoreCaseOrderByPeriodDesc(employee);
    }

    public Payment save(Payment toSave) {
        return paymentRepository.save(toSave);
    }
}