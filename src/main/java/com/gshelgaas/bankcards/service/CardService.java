package com.gshelgaas.bankcards.service;

import com.gshelgaas.bankcards.dto.BlockRequestDto;
import com.gshelgaas.bankcards.dto.BlockRequestResponseDto;
import com.gshelgaas.bankcards.dto.CardRequestDto;
import com.gshelgaas.bankcards.dto.CardResponseDto;
import com.gshelgaas.bankcards.exception.ForbiddenException;
import com.gshelgaas.bankcards.exception.NotFoundException;
import com.gshelgaas.bankcards.exception.ConflictException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

/**
 * Сервис для управления банковскими картами.
 * Предоставляет функционал для создания, блокировки, активации карт
 * и обработки запросов на блокировку от пользователей.
 *
 * @author Георгий Шельгаас
 */
public interface CardService {

    /**
     * Создает новую банковскую карту для указанного пользователя.
     *
     * @param cardRequestDto данные для создания карты
     * @param userId         идентификатор пользователя-владельца карты
     * @return созданная карта с замаскированным номером
     * @throws NotFoundException если пользователь не найден
     * @throws ConflictException если карта с таким номером уже существует
     *                           или срок действия истек
     */
    CardResponseDto createCard(CardRequestDto cardRequestDto, Long userId);

    /**
     * Получает информацию о карте по идентификатору.
     *
     * @param cardId идентификатор карты
     * @return информация о карте с актуальным статусом
     * @throws NotFoundException если карта не найдена
     */
    CardResponseDto getCardById(Long cardId);

    /**
     * Получает страницу карт пользователя с возможностью фильтрации по статусу.
     *
     * @param userId   идентификатор пользователя
     * @param status   статус для фильтрации (ACTIVE, BLOCKED, EXPIRED). Может быть null
     * @param pageable параметры пагинации
     * @return страница с картами пользователя
     * @throws NotFoundException        если пользователь не найден
     * @throws IllegalArgumentException если передан некорректный статус
     */
    Page<CardResponseDto> getUserCards(Long userId, String status, Pageable pageable);

    /**
     * Блокирует карту. Доступно только для администратора.
     *
     * @param cardId идентификатор карты для блокировки
     * @return обновленная информация о карте
     * @throws NotFoundException если карта не найдена
     * @throws ConflictException если карта уже истекла
     */
    CardResponseDto blockCard(Long cardId);

    /**
     * Активирует заблокированную карту. Доступно только для администратора.
     *
     * @param cardId идентификатор карты для активации
     * @return обновленная информация о карте
     * @throws NotFoundException если карта не найдена
     * @throws ConflictException если карта уже истекла
     */
    CardResponseDto activateCard(Long cardId);

    /**
     * Удаляет карту из системы. Доступно только для администратора.
     *
     * @param cardId идентификатор карты для удаления
     * @throws NotFoundException если карта не найдена
     */
    void deleteCard(Long cardId);

    /**
     * Создает запрос на блокировку карты от пользователя.
     *
     * @param cardId          идентификатор карты для блокировки
     * @param userId          идентификатор пользователя, создающего запрос
     * @param blockRequestDto данные запроса с причиной блокировки
     * @return информация о созданном запросе на блокировку
     * @throws NotFoundException  если карта не найдена
     * @throws ForbiddenException если карта не принадлежит пользователю
     * @throws ConflictException  если карта уже истекла или уже есть pending запрос
     */
    BlockRequestResponseDto requestCardBlock(Long cardId, Long userId, BlockRequestDto blockRequestDto);

    /**
     * Одобряет запрос на блокировку карты. Доступно только для администратора.
     *
     * @param cardId идентификатор карты
     * @return обновленная информация о карте
     * @throws NotFoundException если карта не найдена или нет pending запроса
     * @throws ConflictException если карта уже истекла
     */
    CardResponseDto approveCardBlock(Long cardId);

    /**
     * Получает все карты в системе с пагинацией. Доступно только для администратора.
     *
     * @param pageable параметры пагинации
     * @return страница со всеми картами системы
     */
    Page<CardResponseDto> getAllCards(Pageable pageable);

    /**
     * Получает баланс карты с проверкой прав доступа.
     *
     * @param cardId идентификатор карты
     * @param userId идентификатор пользователя
     * @return текущий баланс карты
     * @throws NotFoundException  если карта не найдена
     * @throws ForbiddenException если карта не принадлежит пользователю
     */
    BigDecimal getCardBalance(Long cardId, Long userId);
}