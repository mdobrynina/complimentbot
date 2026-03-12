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
                case "/start" -> "Привет, " + userName + "! Я бот, который будет радовать тебя комплиментами. Напиши /compliment чтобы начать или /help, чтобы получить список команд).";
                case "/help" -> "Доступные команды:\n/start - Начать\n/compliment - Получить комплимент\n/help - Помощь\n/info - Информация";
                case "/compliment" -> userName + ", " + getRandomCompliment();
                default -> "Вы написали: " + messageText + " " + userName;
            };
            if (messageText.equalsIgnoreCase("/compliment")) {
                System.out.println("Отправлен комплимент пользователю " + userName + ": " + responseText);
            }

            sendMessage(chatId, responseText);
        }
    }

    // Добавляем метод для случайного комплимента
    private String getRandomCompliment() {
        String[] compliments = {
                "Ты сегодня сияешь! ✨💖",
                "Твоя улыбка делает мир ярче! 😄🌸",
                "Как здорово, что ты здесь! 💛",
                "Ты невероятно вдохновляющий человек! 🌟💫",
                "С тобой приятно общаться, ты даришь тепло! 💕",
                "Твоя энергия заряжает окружающих! ⚡😊",
                "Ты делаешь этот мир добрее! 🌈💖",
                "Твой творческий подход восхищает! 🎨🌟",
                "Ты особенный/особенная, просто знай это! 💝",
                "Сегодня твоя улыбка особенно яркая! 😍🌸"
        };

        int randomIndex = (int) (Math.random() * compliments.length);
        return compliments[randomIndex];
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
