package com.example.tgcompliment_bot.service;


import com.example.tgcompliment_bot.config.BotProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotProperties botProperties;

    public TelegramBot(BotProperties botProperties) {
        this.botProperties = botProperties;
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Проверяем, есть ли сообщение и текст
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String userName = update.getMessage().getFrom().getFirstName();
            long chatId = update.getMessage().getChatId();

            String responseText = switch (messageText.toLowerCase()) {
                case "/start" -> "Привет, "+ userName+ "! Я бот, который будет радовать тебя комплиментами. Напиши /compliment чтобы начать или /help, чтобы получить список команд).";
                case "/help" -> "Доступные команды:\n/start - Начать\n/compliment - Получить комплимент\n/help - Помощь\n/info - Информация";
                case "/compliment" -> "Я демонстрационный бот на Spring Boot!";
                default -> "Вы написали: " + messageText +" " + userName;
            };

            sendMessage(chatId, responseText);
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

//package com.example.tgcompliment_bot.service;
//
//import com.example.tgcompliment_bot.config.BotProperties;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//@Component
//public class TelegramBot extends TelegramLongPollingBot {
//
//    private final BotProperties botProperties;
//
//    public TelegramBot(BotProperties botProperties) {
//        this.botProperties = botProperties;
//    }
//
//    @Override
//    public String getBotUsername() {
//        return botProperties.getUsername();
//    }
//
//    @Override
//    public String getBotToken() {
//        return botProperties.getToken();
//    }
//
//    @Override
//    public void onUpdateReceived(Update update) {
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            String messageText = update.getMessage().getText();
//            long chatId = update.getMessage().getChatId();
//
//            String responseText;
//
//            if (messageText.equals("/start")) {
//                responseText = "Привет! Я бот для комплиментов! Напиши /compliment чтобы получить комплимент.";
//            } else if (messageText.equals("/compliment")) {
//                responseText = getRandomCompliment();
//            } else if (messageText.equals("/help")) {
//                responseText = "Доступные команды:\n/start - Начать работу\n/compliment - Получить комплимент\n/help - Показать помощь";
//            } else {
//                responseText = "Извините, я понимаю только команды. Напишите /help для списка команд.";
//            }
//
//            sendMessage(chatId, responseText);
//        }
//    }
//
//    private String getRandomCompliment() {
//        String[] compliments = {
//                "Ты сегодня прекрасно выглядишь!",
//                "У тебя отличное чувство юмора!",
//                "Ты очень умный и интересный человек!",
//                "С тобой приятно общаться!",
//                "Ты делаешь этот мир лучше!",
//                "У тебя замечательная улыбка!",
//                "Ты полон энергии и энтузиазма!",
//                "Твой творческий подход вдохновляет!"
//        };
//
//        int randomIndex = (int) (Math.random() * compliments.length);
//        return compliments[randomIndex];
//    }
//
//    private void sendMessage(long chatId, String text) {
//        SendMessage message = new SendMessage();
//        message.setChatId(chatId);
//        message.setText(text);
//
//        try {
//            execute(message);
//        } catch (TelegramApiException e) {
//            System.err.println("Ошибка при отправке сообщения: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}
