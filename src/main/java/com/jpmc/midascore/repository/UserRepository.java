package com.jpmc.midascore.repository;

import com.jpmc.midascore.entity.User;
import com.jpmc.midascore.entity.UserRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Optional: find a user by name
    Optional<User   > findByUsername(String name);
}
