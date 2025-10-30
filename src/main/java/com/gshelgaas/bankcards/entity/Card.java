package com.gshelgaas.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность банковской карты.
 * Хранит информацию о карте, включая зашифрованный номер, баланс и историю операций.
 * Связана с пользователем, переводами и запросами на блокировку.
 * <p>
 * Таблица: cards
 *
 * @author Георгий Шельгаас
 */
@Getter
@Setter
@ToString(exclude = {"cardNumber", "user", "outgoingTransfers", "incomingTransfers", "blockRequests"})
@EqualsAndHashCode(exclude = {"user", "outgoingTransfers", "incomingTransfers", "blockRequests"})
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cards")
public class Card {

    /**
     * Уникальный идентификатор карты.
     * Автоинкрементный первичный ключ.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Номер карты в зашифрованном виде.
     * Шифруется с помощью AES перед сохранением в базу данных.
     * В ответах API возвращается в замаскированном виде.
     */
    @Column(nullable = false, unique = true)
    private String cardNumber;

    /**
     * Имя владельца карты.
     * Соответствует имени на физической карте.
     */
    @Column(nullable = false)
    private String cardHolder;

    /**
     * Дата истечения срока действия карты.
     * Используется для автоматической проверки актуальности карты.
     * При просрочке карта автоматически помечается как EXPIRED.
     */
    @Column(nullable = false)
    private LocalDate expiryDate;

    /**
     * Текущий статус карты.
     * Определяет возможности использования карты для операций.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status;

    /**
     * Текущий баланс карты.
     * Точность: 15 цифр, 2 знака после запятой.
     * Используется для проверки достаточности средств при переводах.
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    /**
     * Владелец карты.
     * Связь Many-to-One с сущностью User.
     * Каждая карта принадлежит ровно одному пользователю.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Дата и время создания карты в системе.
     * Заполняется автоматически при создании карты.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Исходящие переводы с данной карты.
     * Связь One-to-Many с сущностью Transfer.
     * Каскадное удаление и orphan removal включены.
     */
    @OneToMany(mappedBy = "fromCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transfer> outgoingTransfers = new ArrayList<>();

    /**
     * Входящие переводы на данную карту.
     * Связь One-to-Many с сущностью Transfer.
     * Каскадное удаление и orphan removal включены.
     */
    @OneToMany(mappedBy = "toCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transfer> incomingTransfers = new ArrayList<>();

    /**
     * Запросы на блокировку данной карты.
     * Связь One-to-Many с сущностью BlockRequest.
     * Каскадное удаление и orphan removal включены.
     */
    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BlockRequest> blockRequests = new ArrayList<>();

    /**
     * Статусы банковской карты.
     */
    public enum CardStatus {
        /**
         * Карта активна и может использоваться для операций.
         * Может выполнять переводы и принимать платежи.
         */
        ACTIVE,

        /**
         * Карта заблокирована администратором или по запросу пользователя.
         * Не может использоваться для операций.
         */
        BLOCKED,

        /**
         * Срок действия карты истек.
         * Вычисляется автоматически на основе expiryDate.
         * Не может использоваться для операций.
         */
        EXPIRED,

        /**
         * Ожидает одобрения блокировки администратором.
         * Пользователь запросил блокировку, администратор еще не обработал.
         * Не может использоваться для операций.
         */
        PENDING_BLOCK
    }
}