package account.controller;

import account.model.Payment;
import account.model.User;
import account.service.EventService;
import account.service.PaymentService;
import account.service.UserService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
public class EmplController {

    @Autowired
    UserService userService;

    @Autowired
    PaymentService paymentService;

    private final List<String> months = List.of("January", "February", "March", "April", "May",
            "June", "July", "August", "September", "October", "November", "December");

    @GetMapping("/api/empl/payment")
    public String getUser(@AuthenticationPrincipal UserDetails details, @RequestParam(required = false) String period) {

        User user = userService.findByEmailIgnoreCase(details.getUsername());
        String result = "";

        if (period != null) {
            Payment payment = paymentService.findByEmployeeIgnoreCaseAndPeriod(details.getUsername(), period);

            if (payment == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No payment!");
            } else {
                JSONObject jsonObject = new JSONObject(Map.of(
                        "name", user.getName(),
                        "lastname", user.getLastname(),
                        "period", adjustPeriod(period),
                        "salary", adjustSalary(payment.getSalary())));

                result = jsonObject.toString();
            }
        } else {
            JSONArray jsonArray = new JSONArray();

            List<Payment> payments = paymentService.findByEmployeeIgnoreCaseOrderByPeriodDesc(details.getUsername());

            for (int i = 0; i < payments.size(); i++) {
                jsonArray.put(Map.of(
                        "name", user.getName(),
                        "lastname", user.getLastname(),
                        "period", adjustPeriod(payments.get(i).getPeriod()),
                        "salary", adjustSalary(payments.get(i).getSalary())));
            }

            if (payments.size() > 0) {
                result = jsonArray.toString();
            }
        }

        return result;
    }

    private String adjustPeriod(String period) {
        if (!period.matches("(0\\d|1[0-2])-\\d{4}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "payments[0].period: Wrong date!");
        }

        int i = Integer.parseInt(period.substring(0, 2)) - 1;

        return months.get(i) + period.substring(2);
    }

    private String adjustSalary(long salary) {
        return String.format("%d dollar(s) %d cent(s)", salary / 100, salary % 100);
    }
}