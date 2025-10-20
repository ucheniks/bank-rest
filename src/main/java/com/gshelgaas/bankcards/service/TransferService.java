package com.gshelgaas.bankcards.service;

import com.gshelgaas.bankcards.dto.TransferRequestDto;
import com.gshelgaas.bankcards.dto.TransferResponseDto;

import java.util.List;

public interface TransferService {
    TransferResponseDto transferBetweenCards(TransferRequestDto transferRequest, Long userId);
    List<TransferResponseDto> getUserTransfers(Long userId);
}