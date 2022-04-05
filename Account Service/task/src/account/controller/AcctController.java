package account.controller;

import account.model.Payment;
import account.service.EventService;
import account.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.*;

@RestController
public class AcctController {

    @Autowired
    PaymentService paymentService;

    @Autowired
    EventService eventService;

    @PostMapping("/api/acct/payments")
    public Map<String, String> postPayments(@Valid @RequestBody List<Payment> payments) {

        checkPayments("POST", payments);

        try {
            paymentService.saveAll(payments);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }

        return Map.of("status", "Added successfully!");
    }

    @PutMapping("/api/acct/payments")
    public Map<String, String> putPayments(@Valid @RequestBody Payment payment) {

        checkPayments("PUT", List.of(payment));

        try {
            Payment existingPayment = paymentService.findByEmployeeIgnoreCaseAndPeriod(
                    payment.getEmployee(), payment.getPeriod());

            if (existingPayment != null) {
                payment.setId(existingPayment.getId());
            }

            paymentService.save(payment);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }

        return Map.of("status", "Updated successfully!");
    }

    private void checkPayments(String type, List<Payment> payments) {

        List<String> errMsg = new ArrayList<>();
        Set<String> hashSet = new HashSet<>();

        for (int i = 0; i < payments.size(); i++) {
            if (type.equals("POST") && (!hashSet.add(payments.get(i).getEmployee() + payments.get(i).getPeriod()) ||
                    paymentService.findByEmployeeIgnoreCaseAndPeriod(payments.get(i).getEmployee(),
                            payments.get(i).getPeriod()) != null)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("payments[%d]: Payment exist!", i));
            }

            if (payments.get(i).getSalary() < 0) {
                errMsg.add(String.format("payments[%d].salary: Salary must be non negative!", i));
            }

            if (!payments.get(i).getPeriod().matches("(0\\d|1[0-2])-\\d{4}")) {
                errMsg.add(String.format("payments[%d].period: Wrong date!", i));
            }
        }

        if (errMsg.size() != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.join(", ", errMsg));
        }
    }
}