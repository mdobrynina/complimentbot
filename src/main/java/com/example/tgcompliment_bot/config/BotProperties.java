//package com.example.tgcompliment_bot.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//@Component
//public class BotProperties {
//
//    @Value("${telegram.bot.token}")
//    private String token;
//
//    @Value("${telegram.bot.username}")
//    private String username;
//
//    public String getToken() {
//        return token;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//}

package com.example.tgcompliment_bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BotProperties {

    @Value("${telegram.bot.token}")
    private String token;

    @Value("${telegram.bot.username}")
    private String username;

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }
}