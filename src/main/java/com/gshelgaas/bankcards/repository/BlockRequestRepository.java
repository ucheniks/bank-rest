package com.gshelgaas.bankcards.repository;

import com.gshelgaas.bankcards.entity.BlockRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с запросами на блокировку карт.
 * Предоставляет методы для управления запросами на блокировку.
 *
 * @author Георгий Шельгаас
 */
@Repository
public interface BlockRequestRepository extends JpaRepository<BlockRequest, Long> {

    /**
     * Находит все запросы на блокировку для карт указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список запросов на блокировку карт пользователя
     */
    List<BlockRequest> findByCardUserId(Long userId);

    /**
     * Находит pending запрос на блокировку для указанной карты.
     *
     * @param cardId идентификатор карты
     * @param status статус запроса (обычно PENDING)
     * @return запрос на блокировку если найден
     */
    Optional<BlockRequest> findByCardIdAndStatus(Long cardId, BlockRequest.BlockStatus status);

    /**
     * Находит все запросы на блокировку с указанным статусом.
     *
     * @param status статус запросов для фильтрации
     * @return список запросов с указанным статусом
     */
    List<BlockRequest> findByStatus(BlockRequest.BlockStatus status);
}