package com.gshelgaas.bankcards.controller;

import com.gshelgaas.bankcards.dto.CardRequestDto;
import com.gshelgaas.bankcards.dto.CardResponseDto;
import com.gshelgaas.bankcards.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для административного управления банковскими картами.
 * Предоставляет endpoint'ы для создания, блокировки, активации и удаления карт.
 * Доступно только пользователям с ролью ADMIN.
 *
 * @author Георгий Шельгаас
 */
@Slf4j
@RestController
@RequestMapping("/admin/cards")
@RequiredArgsConstructor
public class CardControllerAdmin {

    private final CardService cardService;

    /**
     * Создает новую банковскую карту для указанного пользователя.
     *
     * @param userId         идентификатор пользователя-владельца
     * @param cardRequestDto данные для создания карты
     * @return созданная карта с замаскированным номером
     */
    @PostMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CardResponseDto createCard(
            @PathVariable Long userId,
            @Valid @RequestBody CardRequestDto cardRequestDto) {

        log.info("POST /admin/cards/user/{} - create card for user", userId);
        return cardService.createCard(cardRequestDto, userId);
    }

    /**
     * Получает информацию о карте по идентификатору.
     *
     * @param cardId идентификатор карты
     * @return информация о карте
     */
    @GetMapping("/{cardId}")
    public CardResponseDto getCard(@PathVariable Long cardId) {
        log.info("GET /admin/cards/{} - get card by id", cardId);
        return cardService.getCardById(cardId);
    }

    /**
     * Получает все карты в системе с пагинацией.
     *
     * @param page номер страницы (начинается с 0)
     * @param size количество карт на странице
     * @return страница со всеми картами системы
     */
    @GetMapping
    public Page<CardResponseDto> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /admin/cards - get all cards, page: {}, size: {}", page, size);
        return cardService.getAllCards(PageRequest.of(page, size));
    }

    /**
     * Одобряет запрос на блокировку карты.
     *
     * @param cardId идентификатор карты
     * @return обновленная информация о карте
     */
    @PatchMapping("/{cardId}/approve-block")
    public CardResponseDto approveCardBlock(@PathVariable Long cardId) {
        log.info("PATCH /admin/cards/{}/approve-block - approve block request", cardId);
        return cardService.approveCardBlock(cardId);
    }

    /**
     * Блокирует карту.
     *
     * @param cardId идентификатор карты для блокировки
     * @return обновленная информация о карте
     */
    @PatchMapping("/{cardId}/block")
    public CardResponseDto blockCard(@PathVariable Long cardId) {
        log.info("PATCH /admin/cards/{}/block - block card", cardId);
        return cardService.blockCard(cardId);
    }

    /**
     * Активирует заблокированную карту.
     *
     * @param cardId идентификатор карты для активации
     * @return обновленная информация о карте
     */
    @PatchMapping("/{cardId}/activate")
    public CardResponseDto activateCard(@PathVariable Long cardId) {
        log.info("PATCH /admin/cards/{}/activate - activate card", cardId);
        return cardService.activateCard(cardId);
    }

    /**
     * Удаляет карту из системы.
     *
     * @param cardId идентификатор карты для удаления
     */
    @DeleteMapping("/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable Long cardId) {
        log.info("DELETE /admin/cards/{} - delete card", cardId);
        cardService.deleteCard(cardId);
    }
}