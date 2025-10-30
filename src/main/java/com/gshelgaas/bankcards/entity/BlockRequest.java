package com.gshelgaas.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Сущность запроса на блокировку карты.
 * Хранит запросы пользователей на блокировку карт и их статусы.
 * Таблица: block_requests
 *
 * @author Георгий Шельгаас
 */
@Getter
@Setter
@ToString(exclude = {"card"})
@EqualsAndHashCode(exclude = {"card"})
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "block_requests")
public class BlockRequest {

    /**
     * Уникальный идентификатор запроса.
     * Автоинкрементный первичный ключ.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Карта, для которой запрашивается блокировка.
     * Связь Many-to-One с сущностью Card.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    /**
     * Причина блокировки карты.
     * Обязательно заполняется пользователем.
     */
    @Column(nullable = false)
    private String reason;

    /**
     * Статус обработки запроса.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BlockStatus status;

    /**
     * Дата и время создания запроса.
     */
    @Column(nullable = false)
    private LocalDateTime requestedAt;

    /**
     * Дата и время обработки запроса администратором.
     * Заполняется при одобрении или отклонении запроса.
     */
    private LocalDateTime processedAt;

    /**
     * Статусы обработки запроса на блокировку.
     */
    public enum BlockStatus {
        /**
         * Запрос ожидает рассмотрения администратором
         */
        PENDING,

        /**
         * Запрос одобрен, карта заблокирована
         */
        APPROVED,

        /**
         * Запрос отклонен администратором
         */
        REJECTED
    }
}