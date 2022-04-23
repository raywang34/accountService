package account.repository;

import account.model.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Ray
 */
@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {
    Optional<Payment> findByEmployeeIgnoreCaseAndPeriod(String employee, String period);
    List<Payment> findByEmployeeIgnoreCaseOrderByPeriodDesc(String employee);
}