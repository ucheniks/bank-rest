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

@Slf4j
@RestController
@RequestMapping("/user/cards")
@RequiredArgsConstructor
public class UserCardController {
    private final CardService cardService;
    private final UserService userService;

    @GetMapping
    public Page<CardResponseDto> getUserCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        User currentUser = userService.getCurrentUser();
        log.info("GET /user/cards - get cards for user: {}, page: {}, size: {}",
                currentUser.getId(), page, size);

        Pageable pageable = PageRequest.of(page, size);
        return cardService.getUserCards(currentUser.getId(), pageable);
    }

    @GetMapping("/{cardId}")
    public CardResponseDto getUserCard(@PathVariable Long cardId) {
        log.info("GET /user/cards/{} - get user card by id", cardId);
        return cardService.getCardById(cardId);
    }

    @PostMapping("/{cardId}/block-request")
    public BlockRequestResponseDto requestCardBlock(
            @PathVariable Long cardId,
            @Valid @RequestBody BlockRequestDto blockRequestDto) {

        User currentUser = userService.getCurrentUser();
        log.info("POST /user/cards/{}/block-request - block request for user: {}",
                cardId, currentUser.getId());

        return cardService.requestCardBlock(cardId, currentUser.getId(), blockRequestDto);
    }
}