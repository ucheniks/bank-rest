package com.gshelgaas.bankcards.repository;

import com.gshelgaas.bankcards.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с переводами между картами.
 * Предоставляет методы для доступа к истории финансовых операций.
 *
 * @author Георгий Шельгаас
 */
@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    /**
     * Находит все переводы, связанные с пользователем.
     * Включает как исходящие (где пользователь - отправитель),
     * так и входящие (где пользователь - получатель) переводы.
     *
     * @param fromUserId идентификатор пользователя как отправителя
     * @param toUserId   идентификатор пользователя как получателя
     * @return список переводов пользователя
     */
    List<Transfer> findByFromCardUserIdOrToCardUserId(Long fromUserId, Long toUserId);
}