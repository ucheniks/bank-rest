package com.gshelgaas.bankcards.service;

import com.gshelgaas.bankcards.dto.TransferRequestDto;
import com.gshelgaas.bankcards.dto.TransferResponseDto;
import com.gshelgaas.bankcards.entity.Card;
import com.gshelgaas.bankcards.entity.Transfer;
import com.gshelgaas.bankcards.exception.ConflictException;
import com.gshelgaas.bankcards.exception.ForbiddenException;
import com.gshelgaas.bankcards.exception.NotFoundException;
import com.gshelgaas.bankcards.repository.CardRepository;
import com.gshelgaas.bankcards.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Реализация сервиса для управления переводами между банковскими картами.
 * Обрабатывает бизнес-логику переводов и валидацию операций.
 *
 * @author Георгий Шельгаас
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final CardRepository cardRepository;

    /**
     * {@inheritDoc}
     * <p>
     * Реализация включает:
     * - Проверку прав доступа к карте отправителя
     * - Валидацию статусов карт
     * - Проверку достаточности средств
     * - Атомарное обновление балансов
     */
    @Override
    @Transactional
    public TransferResponseDto transferBetweenCards(TransferRequestDto transferRequest, Long userId) {
        log.info("Transfer request from card {} to card {}, amount: {}",
                transferRequest.getFromCardId(), transferRequest.getToCardId(), transferRequest.getAmount());

        Card fromCard = cardRepository.findById(transferRequest.getFromCardId())
                .orElseThrow(() -> new NotFoundException("From card not found"));
        Card toCard = cardRepository.findById(transferRequest.getToCardId())
                .orElseThrow(() -> new NotFoundException("To card not found"));

        if (!fromCard.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Card does not belong to user");
        }

        if (fromCard.getStatus() != Card.CardStatus.ACTIVE || toCard.getStatus() != Card.CardStatus.ACTIVE) {
            throw new ConflictException("Cards must be active for transfer");
        }

        if (fromCard.getBalance().compareTo(transferRequest.getAmount()) < 0) {
            throw new ConflictException("Insufficient funds");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(transferRequest.getAmount()));
        toCard.setBalance(toCard.getBalance().add(transferRequest.getAmount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        Transfer transfer = Transfer.builder()
                .fromCard(fromCard)
                .toCard(toCard)
                .amount(transferRequest.getAmount())
                .transferDate(LocalDateTime.now())
                .status(Transfer.TransferStatus.SUCCESS)
                .description(transferRequest.getDescription())
                .build();

        Transfer savedTransfer = transferRepository.save(transfer);
        log.info("Transfer completed successfully with id: {}", savedTransfer.getId());

        return mapToResponseDto(savedTransfer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TransferResponseDto> getUserTransfers(Long userId) {
        log.info("Getting transfers for user: {}", userId);

        List<Transfer> transfers = transferRepository.findByFromCardUserIdOrToCardUserId(userId, userId);
        return transfers.stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    /**
     * Преобразует сущность Transfer в DTO для ответа.
     */
    private TransferResponseDto mapToResponseDto(Transfer transfer) {
        return TransferResponseDto.builder()
                .id(transfer.getId())
                .fromCardId(transfer.getFromCard().getId())
                .toCardId(transfer.getToCard().getId())
                .amount(transfer.getAmount())
                .transferDate(transfer.getTransferDate())
                .status(transfer.getStatus().name())
                .description(transfer.getDescription())
                .build();
    }
}