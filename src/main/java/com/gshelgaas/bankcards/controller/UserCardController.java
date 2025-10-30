package com.gshelgaas.bankcards.controller;

import com.gshelgaas.bankcards.dto.BlockRequestDto;
import com.gshelgaas.bankcards.dto.BlockRequestResponseDto;
import com.gshelgaas.bankcards.dto.CardResponseDto;
import com.gshelgaas.bankcards.service.CardService;
import com.gshelgaas.bankcards.entity.User;
import com.gshelgaas.bankcards.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Контроллер для работы пользователей с банковскими картами.
 * Предоставляет endpoint'ы для просмотра карт, баланса и запросов на блокировку.
 * Доступно только аутентифицированным пользователям.
 *
 * @author Георгий Шельгаас
 */
@Slf4j
@RestController
@RequestMapping("/user/cards")
@RequiredArgsConstructor
public class UserCardController {

    private final CardService cardService;
    private final UserService userService;

    /**
     * Получает страницу карт текущего пользователя с возможностью фильтрации по статусу.
     *
     * @param status статус карт для фильтрации (опционально)
     * @param page   номер страницы (начинается с 0)
     * @param size   количество карт на странице
     * @return страница с картами пользователя
     */
    @GetMapping
    public Page<CardResponseDto> getUserCards(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        User currentUser = userService.getCurrentUser();
        log.info("GET /user/cards - get cards for user: {}, status: {}, page: {}, size: {}",
                currentUser.getId(), status, page, size);

        Pageable pageable = PageRequest.of(page, size);
        return cardService.getUserCards(currentUser.getId(), status, pageable);
    }

    /**
     * Получает информацию о конкретной карте пользователя.
     *
     * @param cardId идентификатор карты
     * @return информация о карте
     */
    @GetMapping("/{cardId}")
    public CardResponseDto getUserCard(@PathVariable Long cardId) {
        log.info("GET /user/cards/{} - get user card by id", cardId);
        return cardService.getCardById(cardId);
    }

    /**
     * Получает баланс конкретной карты пользователя.
     *
     * @param cardId идентификатор карты
     * @return текущий баланс карты
     */
    @GetMapping("/{cardId}/balance")
    public BigDecimal getCardBalance(@PathVariable Long cardId) {
        User currentUser = userService.getCurrentUser();
        log.info("GET /user/cards/{}/balance - get balance for user: {}", cardId, currentUser.getId());
        return cardService.getCardBalance(cardId, currentUser.getId());
    }

    /**
     * Создает запрос на блокировку карты.
     *
     * @param cardId          идентификатор карты для блокировки
     * @param blockRequestDto данные запроса с причиной блокировки
     * @return информация о созданном запросе на блокировку
     */
    @PostMapping("/{cardId}/block-request")
    public BlockRequestResponseDto requestCardBlock(
            @PathVariable Long cardId,
            @Valid @RequestBody BlockRequestDto blockRequestDto) {

        User currentUser = userService.getCurrentUser();
        log.info("POST /user/cards/{}/block-request - block request for user: {}", cardId, currentUser.getId());
        return cardService.requestCardBlock(cardId, currentUser.getId(), blockRequestDto);
    }
}