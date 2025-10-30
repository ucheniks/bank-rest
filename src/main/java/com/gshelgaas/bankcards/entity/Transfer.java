package com.gshelgaas.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Сущность перевода между картами.
 * Хранит историю всех финансовых операций в системе.
 * Таблица: transfers
 *
 * @author Георгий Шельгаас
 */
@Getter
@Setter
@ToString(exclude = {"fromCard", "toCard"})
@EqualsAndHashCode(exclude = {"fromCard", "toCard"})
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transfers")
public class Transfer {

    /**
     * Уникальный идентификатор перевода.
     * Автоинкрементный первичный ключ.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Карта-отправитель перевода.
     * Связь Many-to-One с сущностью Card.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_card_id", nullable = false)
    private Card fromCard;

    /**
     * Карта-получатель перевода.
     * Связь Many-to-One с сущностью Card.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_card_id", nullable = false)
    private Card toCard;

    /**
     * Сумма перевода.
     * Точность: 15 цифр, 2 знака после запятой.
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    /**
     * Дата и время выполнения перевода.
     */
    @Column(nullable = false)
    private LocalDateTime transferDate;

    /**
     * Статус выполнения перевода.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status;

    /**
     * Описание или комментарий к переводу.
     * Необязательное поле.
     */
    private String description;

    /**
     * Статусы выполнения перевода.
     */
    public enum TransferStatus {
        /**
         * Перевод успешно выполнен
         */
        SUCCESS,

        /**
         * Перевод не выполнен (ошибка)
         */
        FAILED,

        /**
         * Перевод в процессе обработки
         */
        PENDING
    }
}