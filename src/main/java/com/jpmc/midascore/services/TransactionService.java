package com.jpmc.midascore.services;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.User;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.services.IncentiveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final UserRepository userRepo;
    private final TransactionRecordRepository txRepo;
    private final IncentiveService incentiveService; // Add this

    public TransactionService(UserRepository userRepo,
                              TransactionRecordRepository txRepo,
                              IncentiveService incentiveService) { // inject here
        this.userRepo = userRepo;
        this.txRepo = txRepo;
        this.incentiveService = incentiveService;
    }

    @Transactional
    public void processTransaction(Transaction tx) {

        User sender = userRepo.findById(tx.getSenderId()).orElse(null);
        User recipient = userRepo.findById(tx.getRecipientId()).orElse(null);

        if (sender == null || recipient == null) {
            return;
        }

        if (sender.getBalance() < tx.getAmount()) {
            return;
        }

        // ✅ CALL INCENTIVE API BEFORE UPDATING BALANCES
        double incentiveAmount = incentiveService.getIncentive(tx);
        tx.setIncentive(incentiveAmount); // if Transaction has this field

        // Adjust balances including incentive
        sender.setBalance(sender.getBalance() - tx.getAmount());
        recipient.setBalance(recipient.getBalance() + tx.getAmount() + incentiveAmount);

        // Save updated users
        userRepo.save(sender);
        userRepo.save(recipient);

        // Save transaction record including incentive
        TransactionRecord record =
                new TransactionRecord(sender, recipient, tx.getAmount(), incentiveAmount);
        txRepo.save(record);

    }
}
