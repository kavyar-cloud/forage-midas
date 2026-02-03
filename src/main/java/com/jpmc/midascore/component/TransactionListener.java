package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.User;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TransactionListener {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final RestTemplate restTemplate;

    public TransactionListener(UserRepository userRepository,
                               TransactionRecordRepository transactionRecordRepository,
                               RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.restTemplate = restTemplate;
    }

    @KafkaListener(
            topics = "${general.kafka-topic}",
            groupId = "midas-core"
    )
    @Transactional
    public void receive(Transaction transaction) {

        // 1️⃣ Validate users
        User sender = userRepository.findById(transaction.getSenderId()).orElse(null);
        User recipient = userRepository.findById(transaction.getRecipientId()).orElse(null);

        if (sender == null || recipient == null) return;
        if (sender.getBalance() < transaction.getAmount()) return;

        // 2️⃣ Call Incentive API
        Incentive incentive = restTemplate.postForObject(
                "http://localhost:8080/incentive",
                transaction,
                Incentive.class
        );

        double incentiveAmount = incentive != null ? incentive.getAmount() : 0.0;

        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(
                recipient.getBalance() + transaction.getAmount() + incentiveAmount
        );



        userRepository.save(sender);
        userRepository.save(recipient);

        // 4️⃣ Save transaction + incentive
        TransactionRecord record = new TransactionRecord(
                sender,
                recipient,
                transaction.getAmount(),
                incentiveAmount
        );

        transactionRecordRepository.save(record);
    }
}
