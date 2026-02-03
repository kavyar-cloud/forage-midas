package com.jpmc.midascore;

import com.jpmc.midascore.FileLoader;
import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserPopulator {

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private DatabaseConduit databaseConduit;

    public void populate() {
        String[] userLines = fileLoader.loadStrings("/test_data/lkjhgfdsa.hjkl");

        // ✅ REQUIRED null + empty check
        if (userLines == null || userLines.length == 0) {
            return;
        }

        for (String userLine : userLines) {
            if (userLine == null || userLine.isBlank()) {
                continue;
            }

            String[] userData = userLine.split(", ");
            User user = new User(
                    userData[0],
                    Float.parseFloat(userData[1])
            );

            databaseConduit.save(user);
        }
    }
}
