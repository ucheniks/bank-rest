package com.gshelgaas.bankcards.service;

import com.gshelgaas.bankcards.dto.BlockRequestDto;
import com.gshelgaas.bankcards.dto.BlockRequestResponseDto;
import com.gshelgaas.bankcards.dto.CardRequestDto;
import com.gshelgaas.bankcards.dto.CardResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardService {
    CardResponseDto createCard(CardRequestDto cardRequestDto, Long userId);
    CardResponseDto getCardById(Long cardId);
    Page<CardResponseDto> getUserCards(Long userId, Pageable pageable);
    CardResponseDto blockCard(Long cardId);
    CardResponseDto activateCard(Long cardId);
    void deleteCard(Long cardId);
    BlockRequestResponseDto requestCardBlock(Long cardId, Long userId, BlockRequestDto blockRequestDto);
    CardResponseDto approveCardBlock(Long cardId);
}