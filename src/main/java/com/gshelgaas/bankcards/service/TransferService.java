package com.gshelgaas.bankcards.service;

import com.gshelgaas.bankcards.dto.TransferRequestDto;
import com.gshelgaas.bankcards.dto.TransferResponseDto;
import com.gshelgaas.bankcards.exception.NotFoundException;
import com.gshelgaas.bankcards.exception.ForbiddenException;
import com.gshelgaas.bankcards.exception.ConflictException;

import java.util.List;

/**
 * Сервис для управления переводами между банковскими картами.
 * Предоставляет функционал для выполнения переводов и получения истории операций.
 *
 * @author Георгий Шельгаас
 */
public interface TransferService {

    /**
     * Выполняет перевод денежных средств между картами пользователя.
     * Пользователь может переводить средства только между своими картами.
     *
     * @param transferRequest данные перевода (карты, сумма, описание)
     * @param userId          идентификатор пользователя, выполняющего перевод
     * @return информация о выполненном переводе
     * @throws NotFoundException  если одна из карт не найдена
     * @throws ForbiddenException если карта отправителя не принадлежит пользователю
     * @throws ConflictException  если карты не активны или недостаточно средств
     */
    TransferResponseDto transferBetweenCards(TransferRequestDto transferRequest, Long userId);

    /**
     * Получает историю переводов пользователя.
     * Включает как исходящие, так и входящие переводы.
     *
     * @param userId идентификатор пользователя
     * @return список переводов пользователя
     */
    List<TransferResponseDto> getUserTransfers(Long userId);
}