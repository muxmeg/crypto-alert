package com.cryptoalert.server.alerts;

import com.cryptoalert.server.alerts.model.Alert;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertsRepository extends
    JpaRepository<Alert, Long> {
  List<Alert> findByCurrencyPairAndLimit(String currencyPair, String limit);
  List<Alert> findByCurrencyPairAndCompletedFalse(String currencyPair);
}
