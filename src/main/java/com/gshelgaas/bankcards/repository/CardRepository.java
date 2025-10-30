package com.gshelgaas.bankcards.repository;

import com.gshelgaas.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с банковскими картами.
 * Предоставляет методы для доступа к данным карт и их фильтрации.
 *
 * @author Георгий Шельгаас
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    /**
     * Проверяет существование карты с указанным номером.
     * Номер карты должен быть предварительно зашифрован.
     *
     * @param cardNumber зашифрованный номер карты
     * @return true если карта с таким номером существует, иначе false
     */
    boolean existsByCardNumber(String cardNumber);

    /**
     * Находит страницу карт пользователя с возможностью фильтрации по статусу.
     *
     * @param userId   идентификатор пользователя
     * @param status   статус карты для фильтрации (может быть null)
     * @param pageable параметры пагинации
     * @return страница карт пользователя
     */
    @Query("SELECT c FROM Card c WHERE " +
            "c.user.id = :userId AND " +
            "(:status IS NULL OR c.status = :status)")
    Page<Card> findByUserIdWithFilters(
            @Param("userId") Long userId,
            @Param("status") Card.CardStatus status,
            Pageable pageable);
}