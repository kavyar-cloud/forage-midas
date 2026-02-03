package com.jpmc.midascore.services;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IncentiveService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String incentiveUrl = "http://localhost:8080/incentive";

    public double getIncentive(Transaction transaction) {
        // Send the transaction to the API and get the response
        Incentive incentive = restTemplate.postForObject(incentiveUrl, transaction, Incentive.class);
        return incentive != null ? incentive.getAmount() : 0.0;
    }
}
