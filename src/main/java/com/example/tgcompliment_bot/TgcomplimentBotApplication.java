package com.example.tgcompliment_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TgcomplimentBotApplication {

    public static void main(String[] args) {

        SpringApplication.run(TgcomplimentBotApplication.class, args);
        System.out.println("Telegram бот запущен!");
    }

}
