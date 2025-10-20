package com.gshelgaas.bankcards.service;

import com.gshelgaas.bankcards.dto.TransferRequestDto;
import com.gshelgaas.bankcards.dto.TransferResponseDto;
import com.gshelgaas.bankcards.entity.Card;
import com.gshelgaas.bankcards.entity.Transfer;
import com.gshelgaas.bankcards.entity.User;
import com.gshelgaas.bankcards.exception.ConflictException;
import com.gshelgaas.bankcards.exception.ForbiddenException;
import com.gshelgaas.bankcards.exception.NotFoundException;
import com.gshelgaas.bankcards.repository.CardRepository;
import com.gshelgaas.bankcards.repository.TransferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private TransferServiceImpl transferService;

    private Card createTestCard(Long id, Long userId, Card.CardStatus status) {
        User user = User.builder().id(userId).build();
        return Card.builder()
                .id(id)
                .cardNumber("encrypted")
                .cardHolder("Test User")
                .expiryDate(LocalDate.now().plusYears(1))
                .status(status)
                .balance(BigDecimal.valueOf(500))
                .user(user)
                .build();
    }

    @Test
    void transferBetweenCards_withValidData_completesSuccessfully() {
        Long userId = 1L;
        User user = User.builder().id(userId).build();

        TransferRequestDto request = TransferRequestDto.builder()
                .fromCardId(1L)
                .toCardId(2L)
                .amount(BigDecimal.valueOf(100))
                .description("Test transfer")
                .build();

        Card fromCard = createTestCard(1L, userId, Card.CardStatus.ACTIVE);
        Card toCard = createTestCard(2L, 2L, Card.CardStatus.ACTIVE);

        Transfer savedTransfer = Transfer.builder()
                .id(1L)
                .fromCard(fromCard)
                .toCard(toCard)
                .amount(BigDecimal.valueOf(100))
                .transferDate(LocalDateTime.now())
                .status(Transfer.TransferStatus.SUCCESS)
                .build();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transferRepository.save(any(Transfer.class))).thenReturn(savedTransfer);

        TransferResponseDto result = transferService.transferBetweenCards(request, userId);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(100), result.getAmount());
        assertEquals(1L, result.getFromCardId());
        assertEquals(2L, result.getToCardId());
        assertEquals(BigDecimal.valueOf(400), fromCard.getBalance());
        assertEquals(BigDecimal.valueOf(600), toCard.getBalance());
        verify(transferRepository).save(any(Transfer.class));
    }

    @Test
    void transferBetweenCards_withInsufficientFunds_throwsException() {
        Long userId = 1L;
        User user = User.builder().id(userId).build();

        TransferRequestDto request = TransferRequestDto.builder()
                .fromCardId(1L)
                .toCardId(2L)
                .amount(BigDecimal.valueOf(1000))
                .build();

        Card fromCard = createTestCard(1L, userId, Card.CardStatus.ACTIVE);
        Card toCard = createTestCard(2L, 2L, Card.CardStatus.ACTIVE);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        assertThrows(ConflictException.class,
                () -> transferService.transferBetweenCards(request, userId));

        verify(transferRepository, never()).save(any(Transfer.class));
    }

    @Test
    void transferBetweenCards_withForeignCard_throwsException() {
        Long userId = 1L;
        TransferRequestDto request = TransferRequestDto.builder()
                .fromCardId(1L)
                .toCardId(2L)
                .amount(BigDecimal.valueOf(100))
                .build();

        Card fromCard = createTestCard(1L, 999L, Card.CardStatus.ACTIVE);
        Card toCard = createTestCard(2L, 2L, Card.CardStatus.ACTIVE);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        assertThrows(ForbiddenException.class,
                () -> transferService.transferBetweenCards(request, userId));

        verify(transferRepository, never()).save(any(Transfer.class));
    }

    @Test
    void transferBetweenCards_withExpiredFromCard_throwsException() {
        Long userId = 1L;
        User user = User.builder().id(userId).build();

        TransferRequestDto request = TransferRequestDto.builder()
                .fromCardId(1L)
                .toCardId(2L)
                .amount(BigDecimal.valueOf(100))
                .build();

        Card fromCard = createTestCard(1L, userId, Card.CardStatus.ACTIVE);
        fromCard.setExpiryDate(LocalDate.now().minusDays(1));
        Card toCard = createTestCard(2L, 2L, Card.CardStatus.ACTIVE);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(ConflictException.class,
                () -> transferService.transferBetweenCards(request, userId));
    }

    @Test
    void transferBetweenCards_withBlockedCard_throwsException() {
        Long userId = 1L;
        User user = User.builder().id(userId).build();

        TransferRequestDto request = TransferRequestDto.builder()
                .fromCardId(1L)
                .toCardId(2L)
                .amount(BigDecimal.valueOf(100))
                .build();

        Card fromCard = createTestCard(1L, userId, Card.CardStatus.BLOCKED);
        Card toCard = createTestCard(2L, 2L, Card.CardStatus.ACTIVE);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        assertThrows(ConflictException.class,
                () -> transferService.transferBetweenCards(request, userId));
    }

    @Test
    void transferBetweenCards_fromCardNotFound_throwsException() {
        Long userId = 1L;
        TransferRequestDto request = TransferRequestDto.builder()
                .fromCardId(1L)
                .toCardId(2L)
                .amount(BigDecimal.valueOf(100))
                .build();

        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> transferService.transferBetweenCards(request, userId));
    }

    @Test
    void getUserTransfers_returnsUserTransfers() {
        Long userId = 1L;

        User user = User.builder().id(userId).build();
        Card fromCard = createTestCard(1L, userId, Card.CardStatus.ACTIVE);
        Card toCard = createTestCard(2L, 2L, Card.CardStatus.ACTIVE);

        Transfer transfer = Transfer.builder()
                .id(1L)
                .fromCard(fromCard)
                .toCard(toCard)
                .amount(BigDecimal.valueOf(100))
                .transferDate(LocalDateTime.now())
                .status(Transfer.TransferStatus.SUCCESS)
                .build();

        when(transferRepository.findByFromCardUserIdOrToCardUserId(userId, userId))
                .thenReturn(List.of(transfer));

        List<TransferResponseDto> result = transferService.getUserTransfers(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(transferRepository).findByFromCardUserIdOrToCardUserId(userId, userId);
    }

    @Test
    void getUserTransfers_withNoTransfers_returnsEmptyList() {
        Long userId = 1L;
        when(transferRepository.findByFromCardUserIdOrToCardUserId(userId, userId))
                .thenReturn(List.of());

        List<TransferResponseDto> result = transferService.getUserTransfers(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void transferBetweenCards_toCardNotFound_throwsException() {
        Long userId = 1L;
        TransferRequestDto request = TransferRequestDto.builder()
                .fromCardId(1L)
                .toCardId(2L)
                .amount(BigDecimal.valueOf(100))
                .build();

        Card fromCard = createTestCard(1L, userId, Card.CardStatus.ACTIVE);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> transferService.transferBetweenCards(request, userId));
    }
}