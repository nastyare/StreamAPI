package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
class Book {
    private String name;
    private String author;
    private int publishingYear;
    private String isbn;
    private String publisher;
}

@Data
@AllArgsConstructor
class Visitor {
    private String name;
    private String surname;
    private String phone;
    private boolean subscribed;
    private List<Book> favoriteBooks;
}

@Data
@AllArgsConstructor
class SmsMessage {
    private String phone;
    private String message;
}

public class Library {
    public static void main(String[] args) {
        String filePath = "src/main/java/json/books.json";

        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type visitorListType = new TypeToken<List<Visitor>>() {}.getType();
            List<Visitor> visitors = gson.fromJson(reader, visitorListType);

            // 1 задание: Вывести список посетителей и их количество
            System.out.println("Список посетителей:");
            visitors.forEach(visitor -> System.out.println(visitor.getName() + " " + visitor.getSurname()));
            System.out.println("Количество посетителей: " + visitors.size());

            // 2 задание: вывести список и количество книг, добавленных посетителями в избранное, без повторений
            Set<Book> uniqueFavoriteBooks = visitors.stream()
                    .flatMap(visitor -> visitor.getFavoriteBooks().stream())
                    .collect(Collectors.toSet());

            System.out.println("\nСписок уникальных книг в избранном:");
            uniqueFavoriteBooks.forEach(book -> System.out.println(book.getName() + " - " + book.getAuthor()));
            System.out.println("Количество уникальных книг: " + uniqueFavoriteBooks.size());

            // 3 задание: отсортировать по году издания и вывести список книг
            List<Book> sortedBooks = uniqueFavoriteBooks.stream()
                    .sorted(Comparator.comparingInt(Book::getPublishingYear))
                    .collect(Collectors.toList());

            System.out.println("\nСписок книг, отсортированных по году издания:");
            sortedBooks.forEach(book -> System.out.println(book.getName() + " - " + book.getPublishingYear()));

            // 4 задание: проверить, есть ли у кого-то в избранном книга автора "Jane Austen"
            boolean hasJaneAustenBook = visitors.stream()
                    .flatMap(visitor -> visitor.getFavoriteBooks().stream())
                    .anyMatch(book -> book.getAuthor().equalsIgnoreCase("Jane Austen"));
            System.out.println("\nЕсть ли у кого-то в избранном книга автора 'Jane Austen': " + hasJaneAustenBook);

            // 5 задание: вывести максимальное число добавленных в избранное книг
            int maxFavoriteBooks = visitors.stream()
                    .mapToInt(visitor -> visitor.getFavoriteBooks().size())
                    .max()
                    .orElse(0);
            System.out.println("Максимальное число добавленных в избранное книг: " + maxFavoriteBooks);

            // 6 задание: создать класс sms-сообщения и сгруппировать посетителей
            List<SmsMessage> smsMessages = new ArrayList<>();
            double averageFavorites = visitors.stream()
                    .mapToInt(visitor -> visitor.getFavoriteBooks().size())
                    .average()
                    .orElse(0);

            visitors.forEach(visitor -> {
                int favoriteCount = visitor.getFavoriteBooks().size();
                String message;
                if (favoriteCount > averageFavorites) {
                    message = "you are a bookworm";
                } else if (favoriteCount < averageFavorites) {
                    message = "read more";
                } else {
                    message = "fine";
                }
                smsMessages.add(new SmsMessage(visitor.getPhone(), message));
            });

            System.out.println("\nSMS сообщения:");
            smsMessages.forEach(sms -> System.out.println("Телефон: " + sms.getPhone() + ", Сообщение: " + sms.getMessage()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
