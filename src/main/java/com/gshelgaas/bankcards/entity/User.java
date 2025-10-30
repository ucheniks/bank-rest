package com.gshelgaas.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность пользователя системы.
 * Хранит учетные данные и основную информацию о пользователе.
 * Таблица: users
 *
 * @author Георгий Шельгаас
 */
@Getter
@Setter
@ToString(exclude = {"password", "cards"})
@EqualsAndHashCode(exclude = {"cards"})
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    /**
     * Уникальный идентификатор пользователя.
     * Автоинкрементный первичный ключ.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Электронная почта пользователя.
     * Уникальный идентификатор для входа в систему.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Зашифрованный пароль пользователя.
     * Хранится в виде BCrypt хеша.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Имя пользователя.
     */
    @Column(nullable = false)
    private String firstName;

    /**
     * Фамилия пользователя.
     */
    @Column(nullable = false)
    private String lastName;

    /**
     * Роль пользователя в системе.
     * Определяет уровень доступа к функционалу.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Дата и время регистрации пользователя.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Список банковских карт пользователя.
     * Однонаправленная связь One-to-Many.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Card> cards = new ArrayList<>();

    /**
     * Роли пользователей в системе.
     */
    public enum Role {
        /**
         * Обычный пользователь с базовыми правами
         */
        ROLE_USER,

        /**
         * Администратор с полными правами доступа
         */
        ROLE_ADMIN
    }
}