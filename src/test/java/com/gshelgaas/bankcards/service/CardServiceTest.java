package com.gshelgaas.bankcards.service;

import com.gshelgaas.bankcards.dto.CardRequestDto;
import com.gshelgaas.bankcards.dto.CardResponseDto;
import com.gshelgaas.bankcards.entity.Card;
import com.gshelgaas.bankcards.entity.User;
import com.gshelgaas.bankcards.exception.ConflictException;
import com.gshelgaas.bankcards.exception.NotFoundException;
import com.gshelgaas.bankcards.repository.CardRepository;
import com.gshelgaas.bankcards.repository.UserRepository;
import com.gshelgaas.bankcards.util.EncryptionUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EncryptionUtil encryptionUtil;

    @InjectMocks
    private CardServiceImpl cardService;

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
    void createCard_withValidData_returnsCardResponse() {
        Long userId = 1L;
        CardRequestDto requestDto = CardRequestDto.builder()
                .cardNumber("4111111111111111")
                .cardHolder("Test User")
                .expiryDate(LocalDate.now().plusYears(2))
                .balance(BigDecimal.valueOf(1000))
                .build();

        User user = User.builder().id(userId).build();
        Card savedCard = createTestCard(1L, userId, Card.CardStatus.ACTIVE);
        savedCard.setBalance(BigDecimal.valueOf(1000));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.existsByCardNumber(anyString())).thenReturn(false);
        when(encryptionUtil.encrypt(anyString())).thenReturn("encrypted");
        when(encryptionUtil.decrypt(anyString())).thenReturn("4111111111111111");
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);

        CardResponseDto result = cardService.createCard(requestDto, userId);

        assertNotNull(result);
        assertEquals("Test User", result.getCardHolder());
        assertEquals(BigDecimal.valueOf(1000), result.getBalance());
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void createCard_withExistingCardNumber_throwsException() {
        Long userId = 1L;
        CardRequestDto requestDto = CardRequestDto.builder()
                .cardNumber("4111111111111111")
                .cardHolder("Test User")
                .expiryDate(LocalDate.now().plusYears(2))
                .balance(BigDecimal.valueOf(1000))
                .build();

        User user = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.existsByCardNumber(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, () -> cardService.createCard(requestDto, userId));
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void createCard_withExpiredDate_throwsException() {
        Long userId = 1L;
        CardRequestDto requestDto = CardRequestDto.builder()
                .cardNumber("4111111111111111")
                .cardHolder("Test User")
                .expiryDate(LocalDate.now().minusDays(1))
                .balance(BigDecimal.valueOf(1000))
                .build();

        User user = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> cardService.createCard(requestDto, userId));
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void createCard_userNotFound_throwsException() {
        Long userId = 999L;
        CardRequestDto requestDto = CardRequestDto.builder()
                .cardNumber("4111111111111111")
                .cardHolder("Test User")
                .expiryDate(LocalDate.now().plusYears(2))
                .balance(BigDecimal.valueOf(1000))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardService.createCard(requestDto, userId));
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void getCardById_withValidId_returnsCardResponse() {
        Long cardId = 1L;
        Card card = createTestCard(cardId, 1L, Card.CardStatus.ACTIVE);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(encryptionUtil.decrypt(anyString())).thenReturn("4111111111111111");

        CardResponseDto result = cardService.getCardById(cardId);

        assertNotNull(result);
        assertEquals(cardId, result.getId());
        assertEquals("Test User", result.getCardHolder());
        verify(cardRepository).findById(cardId);
    }

    @Test
    void getCardById_withNonExistingId_throwsException() {
        Long cardId = 999L;
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardService.getCardById(cardId));
    }

    @Test
    void getUserCards_withValidUser_returnsPageOfCards() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Card card = createTestCard(1L, userId, Card.CardStatus.ACTIVE);
        Page<Card> cardPage = new PageImpl<>(List.of(card));

        when(userRepository.existsById(userId)).thenReturn(true);
        when(cardRepository.findByUserIdWithFilters(userId, null, pageable)).thenReturn(cardPage);
        when(encryptionUtil.decrypt(anyString())).thenReturn("4111111111111111");

        Page<CardResponseDto> result = cardService.getUserCards(userId, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(cardRepository).findByUserIdWithFilters(userId, null, pageable);
    }

    @Test
    void getUserCards_withStatusFilter_returnsFilteredCards() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Card card = createTestCard(1L, userId, Card.CardStatus.ACTIVE);
        Page<Card> cardPage = new PageImpl<>(List.of(card));

        when(userRepository.existsById(userId)).thenReturn(true);
        when(cardRepository.findByUserIdWithFilters(userId, Card.CardStatus.ACTIVE, pageable)).thenReturn(cardPage);
        when(encryptionUtil.decrypt(anyString())).thenReturn("4111111111111111");

        Page<CardResponseDto> result = cardService.getUserCards(userId, "ACTIVE", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(cardRepository).findByUserIdWithFilters(userId, Card.CardStatus.ACTIVE, pageable);
    }

    @Test
    void getUserCards_withNonExistingUser_throwsException() {
        Long userId = 999L;
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> cardService.getUserCards(userId, null, pageable));
    }

    @Test
    void getUserCards_withInvalidStatus_throwsException() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.existsById(userId)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> cardService.getUserCards(userId, "INVALID_STATUS", pageable));

        verify(cardRepository, never()).findByUserIdWithFilters(any(), any(), any());
    }

    @Test
    void getAllCards_returnsPageOfCards() {
        Pageable pageable = PageRequest.of(0, 10);

        Card card = createTestCard(1L, 1L, Card.CardStatus.ACTIVE);
        Page<Card> cardPage = new PageImpl<>(List.of(card));

        when(cardRepository.findAll(pageable)).thenReturn(cardPage);
        when(encryptionUtil.decrypt(anyString())).thenReturn("4111111111111111");

        Page<CardResponseDto> result = cardService.getAllCards(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(cardRepository).findAll(pageable);
    }

    @Test
    void blockCard_withValidCard_blocksCard() {
        Long cardId = 1L;
        Card card = createTestCard(cardId, 1L, Card.CardStatus.ACTIVE);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(encryptionUtil.decrypt(anyString())).thenReturn("4111111111111111");

        CardResponseDto result = cardService.blockCard(cardId);

        assertNotNull(result);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void blockCard_withExpiredCard_throwsException() {
        Long cardId = 1L;
        Card card = createTestCard(cardId, 1L, Card.CardStatus.ACTIVE);
        card.setExpiryDate(LocalDate.now().minusDays(1));

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        assertThrows(ConflictException.class, () -> cardService.blockCard(cardId));
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void activateCard_withValidCard_activatesCard() {
        Long cardId = 1L;
        Card card = createTestCard(cardId, 1L, Card.CardStatus.BLOCKED);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(encryptionUtil.decrypt(anyString())).thenReturn("4111111111111111");

        CardResponseDto result = cardService.activateCard(cardId);

        assertNotNull(result);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void deleteCard_withValidId_deletesCard() {
        Long cardId = 1L;
        when(cardRepository.existsById(cardId)).thenReturn(true);

        cardService.deleteCard(cardId);

        verify(cardRepository).deleteById(cardId);
    }

    @Test
    void deleteCard_withNonExistingId_throwsException() {
        Long cardId = 999L;
        when(cardRepository.existsById(cardId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> cardService.deleteCard(cardId));
        verify(cardRepository, never()).deleteById(cardId);
    }
}