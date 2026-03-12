package com.example.tgcompliment_bot.service;

import com.example.tgcompliment_bot.config.BotProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotProperties botProperties;
    private final UserMoodTracker moodTracker = new UserMoodTracker();
    private final Random random = new Random();

    private final List<String> negativeRoots = Arrays.asList(
            "плохо","устал","груст","печаль","депресс","огорч","тяжел","скучн","страшн",
            "не хочу жить","хочу умереть","мне больно"
    );

    private final List<String> positiveRoots = Arrays.asList(
            "рад","счаст","весел","классн","супер","здоров","прекрасн","кайф","восхит","вдохновл"
    );

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
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String messageRaw = update.getMessage().getText();
        String messageText = messageRaw.toLowerCase();
        String userName = update.getMessage().getFrom().getFirstName();
        long chatId = update.getMessage().getChatId();

        // --- Сначала проверяем команды ---
        switch (messageText) {
            case "/start" -> {
                sendMessageWithMenu(chatId, startMessage(userName));
                return;
            }
            case "/help" -> {
                sendMessageWithMenu(chatId, helpMessage());
                return;
            }
            case "/info" -> {
                sendMessage(chatId, infoMessage());
                return;
            }
            case "/compliment" -> {
                sendMessage(chatId, generateMoodBasedCompliment(userName, chatId));
                return;
            }
        }

        // --- Кризисные сообщения ---
        boolean highAlert = Arrays.asList(
                "хочу умереть","не хочу жить","мне больно"
        ).stream().anyMatch(messageText::contains);

        if (highAlert) {
            moodTracker.setScore(chatId, -3);
            System.out.println("‼️ Кризисное сообщение от " + userName + ": " + messageRaw);
            System.out.println("Рейтинг пользователя " + userName + " установлен на -3");
            sendMessage(chatId, "Я вижу, что тебе очень тяжело, " + userName + ". 💖\n" +
                    "Пожалуйста, обратись за поддержкой к близким или специалисту. 🫂");
            return;
        }

        // --- Обычные сообщения: анализ эмоций ---
        boolean isNegative = negativeRoots.stream().anyMatch(messageText::contains);
        boolean isPositive = positiveRoots.stream().anyMatch(messageText::contains);

        if (isNegative) moodTracker.addNegative(chatId);
        else if (isPositive) moodTracker.addPositive(chatId);

        System.out.println("Новый рейтинг пользователя " + userName + " (chatId=" + chatId + "): " + moodTracker.getScore(chatId));

        // --- Ответ комплиментом ---
        sendMessage(chatId, generateMoodBasedCompliment(userName, chatId));
    }

    private String preprocessMessage(String text) {
        return text.toLowerCase()
                .replaceAll("[!?.)(]", " ")
                .replaceAll("[^\\p{L}\\p{Nd}\\s💖🌸✨🎉⚡💕🫂💛💌😄]", "");
    }

    private String startMessage(String userName) {
        return "Привет, " + userName + "! 🌸\n" +
                "Я бот для поддержки и комплиментов. Выбери команду кнопкой или напиши её:\n" +
                "/compliment - Получить комплимент и поддержку\n" +
                "/help       - Показать это меню команд\n" +
                "/info       - Узнать о боте 💖";
    }

    private String helpMessage() {
        return "Меню команд:\n" +
                "🔹 /start - Начать работу с ботом\n" +
                "🔹 /compliment - Получить комплимент и поддержку\n" +
                "🔹 /help - Показать это меню\n" +
                "🔹 /info - Информация о боте 🌸";
    }

    private String infoMessage() {
        return "Я — бот-комплиментатор и поддержка. 🌟\n" +
                "Слежу за твоим настроением и стараюсь поднимать его через комплименты и дружескую поддержку. 💕";
    }

    private String generateMoodBasedCompliment(String userName, long chatId) {
        int score = moodTracker.getScore(chatId);
        System.out.println("Рейтинг пользователя " + userName + " (chatId=" + chatId + "): " + score);

        String[] options = selectComplimentByScore(score);
        String compliment = options[random.nextInt(options.length)];
        compliment = String.format(compliment, userName);

        System.out.println("Отправлен комплимент пользователю " + userName + ": " + compliment);
        return compliment;
    }

    private String[] selectComplimentByScore(int score) {
        if (score <= -3) {
            return new String[]{
                    "Я рядом с тобой, %s, всё преодолимо! 💖",
                    "Ты не один/одна, %s, вместе справимся! 🫂",
                    "Сейчас трудно, %s, но я верю в тебя! 🌟"
            };
        } else if (score == -2) {
            return new String[]{
                    "С тобой всё будет хорошо, %s! 💖",
                    "Держись, %s! Всё постепенно наладится! 🌸",
                    "Я с тобой, %s! 💛"
            };
        } else if (score == -1 || score == 0) {
            return new String[]{
                    "Ты замечательный/замечательная, %s! 💛",
                    "С тобой приятно общаться, %s! 💕",
                    "Сегодня хороший день для улыбки, %s! 😄",
                    "Ты особенный/особенная, %s! 🌈"
            };
        } else if (score == 1 || score == 2) {
            return new String[]{
                    "Ура, %s! Всё супер! ✨",
                    "Как здорово, %s! Продолжай сиять! 🌸",
                    "Ты заряжаешь всех своей энергией, %s! ⚡"
            };
        } else {
            return new String[]{
                    "Восторг, %s! Ты сияешь! 💖",
                    "Ты невероятен/невероятна, %s! 🎉",
                    "Твоя радость вдохновляет всех, %s! 🌟"
            };
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

    private void sendMessageWithMenu(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("/start"));
        row1.add(new KeyboardButton("/help"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("/compliment"));
        row2.add(new KeyboardButton("/info"));

        keyboard.add(row1);
        keyboard.add(row2);

        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}