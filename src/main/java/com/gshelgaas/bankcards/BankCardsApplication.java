package com.gshelgaas.bankcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс Spring Boot приложения.
 * Запускает контекст Spring и инициализирует все компоненты приложения.
 *
 * @author Георгий Шельгаас
 */
@SpringBootApplication
public class BankCardsApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankCardsApplication.class, args);
    }
}