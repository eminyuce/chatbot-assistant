package com.yuce.chat.assistant.util;

import com.yuce.chat.assistant.model.StockResponse;
import com.yuce.chat.assistant.model.WeatherResponse;
import com.yuce.chat.assistant.persistence.entity.Book;
import com.yuce.chat.assistant.persistence.entity.Drug;
import com.yuce.chat.assistant.persistence.entity.UserEntity;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class FormatTextUtil {
    private static FormatTextUtil instance = new FormatTextUtil();

    public static FormatTextUtil getInstance() {
        return instance;
    }


    public static String formatBookResponse(List<Book> books, String subIntent) {
        if (CollectionUtils.isEmpty(books)) {
            return "No book found or affected.";
        }
        List<String> result = new ArrayList<>();
        for (var book : books) {
            result.add(getBookString(book, subIntent));
        }
        return result.toString();
    }

    private static String getBookString(Book book, String subIntent) {
        switch (subIntent) {
            case Constants.ADD_BOOK:
                return String.format("Book added successfully: '%s' by %s (%d)",
                        book.getTitle(), book.getAuthor(), book.getYear());

            case Constants.DELETE_BOOK:
                return String.format("Book deleted successfully: '%s'", book.getTitle());

            case Constants.UPDATE_BOOK:
                return String.format("Book updated successfully: '%s' by %s (%d)",
                        book.getTitle(), book.getAuthor(), book.getYear());

            case Constants.FIND_BOOK:
                return String.format("Book deleted successfully: '%s'", book.getTitle());

            default:
                return "Unknown operation performed on the book.";
        }
    }


    public static String formatUsersResponse(List<UserEntity> users) {
        if (users == null || users.isEmpty()) {
            return "No users found.";
        }

        long totalUsers = users.size();

        Map<String, Long> roleCounts = users.stream()
                .flatMap(user -> Arrays.stream(user.getRoles().split(",")))
                .map(String::trim)
                .collect(Collectors.groupingBy(String::toString, Collectors.counting()));

        Set<String> allRoles = roleCounts.keySet();
        long differentRoleCount = allRoles.size();

        String roleCountDetails = roleCounts.entrySet().stream()
                .map(entry -> String.format("%d user(s) is/are %s", entry.getValue(), entry.getKey()))
                .collect(Collectors.joining("\n"));

        String userDetails = users.stream()
                .map(user -> String.format("Username: %s, Roles: %s", user.getUsername(), user.getRoles()))
                .collect(Collectors.joining("\n"));

        return String.format("Total users: %d\nDifferent roles: %d\n\nRole Counts:\n%s\n\nUser Details:\n%s",
                totalUsers, differentRoleCount, roleCountDetails, userDetails);
    }


    public String formatWeatherResponse(WeatherResponse weather) {
        return String.format(
                "The weather in %s is %.1f°C with %d%% humidity.",
                weather.getName(),
                weather.getMain().getTemp(),
                weather.getMain().getHumidity()
        );
    }

    public String formatStockResponse(StockResponse stock) {
        return String.format(
                "The stock price of %s is $%s.",
                stock.getGlobalQuote().getSymbol(),
                stock.getGlobalQuote().getPrice()
        );
    }

    public String formatDrugResponse(Drug findDrug) {
        return findDrug.toString();
    }
}
