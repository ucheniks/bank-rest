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

@Slf4j
@RestController
@RequestMapping("/admin/cards")
@RequiredArgsConstructor
public class CardControllerAdmin {
    private final CardService cardService;

    @PostMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CardResponseDto createCard(
            @PathVariable Long userId,
            @Valid @RequestBody CardRequestDto cardRequestDto) {
        log.info("POST /admin/cards/user/{} - create card for user", userId);
        return cardService.createCard(cardRequestDto, userId);
    }

    @GetMapping("/{cardId}")
    public CardResponseDto getCard(@PathVariable Long cardId) {
        log.info("GET /admin/cards/{} - get card by id", cardId);
        return cardService.getCardById(cardId);
    }

    @GetMapping
    public Page<CardResponseDto> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /admin/cards - get all cards, page: {}, size: {}", page, size);
        return cardService.getAllCards(PageRequest.of(page, size));
    }

    @PatchMapping("/{cardId}/approve-block")
    public CardResponseDto approveCardBlock(@PathVariable Long cardId) {
        log.info("PATCH /admin/cards/{}/approve-block - approve block request", cardId);
        return cardService.approveCardBlock(cardId);
    }

    @PatchMapping("/{cardId}/block")
    public CardResponseDto blockCard(@PathVariable Long cardId) {
        log.info("PATCH /admin/cards/{}/block - block card", cardId);
        return cardService.blockCard(cardId);
    }

    @PatchMapping("/{cardId}/activate")
    public CardResponseDto activateCard(@PathVariable Long cardId) {
        log.info("PATCH /admin/cards/{}/activate - activate card", cardId);
        return cardService.activateCard(cardId);
    }

    @DeleteMapping("/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable Long cardId) {
        log.info("DELETE /admin/cards/{} - delete card", cardId);
        cardService.deleteCard(cardId);
    }
}