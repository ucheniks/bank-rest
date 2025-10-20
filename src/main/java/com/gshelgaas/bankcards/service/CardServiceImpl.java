package com.gshelgaas.bankcards.service;

import com.gshelgaas.bankcards.dto.BlockRequestDto;
import com.gshelgaas.bankcards.dto.BlockRequestResponseDto;
import com.gshelgaas.bankcards.dto.CardRequestDto;
import com.gshelgaas.bankcards.dto.CardResponseDto;
import com.gshelgaas.bankcards.entity.BlockRequest;
import com.gshelgaas.bankcards.entity.Card;
import com.gshelgaas.bankcards.entity.User;
import com.gshelgaas.bankcards.exception.ConflictException;
import com.gshelgaas.bankcards.exception.ForbiddenException;
import com.gshelgaas.bankcards.exception.NotFoundException;
import com.gshelgaas.bankcards.repository.BlockRequestRepository;
import com.gshelgaas.bankcards.repository.CardRepository;
import com.gshelgaas.bankcards.repository.UserRepository;
import com.gshelgaas.bankcards.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final BlockRequestRepository blockRequestRepository;
    private final EncryptionUtil encryptionUtil;

    @Override
    @Transactional
    public CardResponseDto createCard(CardRequestDto cardRequestDto, Long userId) {
        log.info("Creating card for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        if (cardRepository.existsByCardNumber(cardRequestDto.getCardNumber())) {
            throw new ConflictException("Card with this number already exists");
        }

        Card card = Card.builder()
                .cardNumber(encryptionUtil.encrypt(cardRequestDto.getCardNumber()))
                .cardHolder(cardRequestDto.getCardHolder())
                .expiryDate(cardRequestDto.getExpiryDate())
                .status(Card.CardStatus.ACTIVE)
                .balance(cardRequestDto.getBalance())
                .user(user)
                .createdAt(LocalDate.now())
                .build();

        Card savedCard = cardRepository.save(card);
        log.info("Card created with id: {}", savedCard.getId());

        return mapToResponseDto(savedCard);
    }

    @Override
    public CardResponseDto getCardById(Long cardId) {
        log.info("Getting card by id: {}", cardId);
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found with id: " + cardId));
        return mapToResponseDto(card);
    }

    @Override
    public Page<CardResponseDto> getUserCards(Long userId, Pageable pageable) {
        log.info("Getting cards for user: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found with id: " + userId);
        }

        return cardRepository.findByUserId(userId, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional
    public CardResponseDto blockCard(Long cardId) {
        log.info("Blocking card: {}", cardId);
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found with id: " + cardId));

        card.setStatus(Card.CardStatus.BLOCKED);
        Card updatedCard = cardRepository.save(card);

        return mapToResponseDto(updatedCard);
    }

    @Override
    @Transactional
    public CardResponseDto activateCard(Long cardId) {
        log.info("Activating card: {}", cardId);
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found with id: " + cardId));

        card.setStatus(Card.CardStatus.ACTIVE);
        Card updatedCard = cardRepository.save(card);

        return mapToResponseDto(updatedCard);
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId) {
        log.info("Deleting card: {}", cardId);
        if (!cardRepository.existsById(cardId)) {
            throw new NotFoundException("Card not found with id: " + cardId);
        }
        cardRepository.deleteById(cardId);
    }

    @Override
    @Transactional
    public BlockRequestResponseDto requestCardBlock(Long cardId, Long userId, BlockRequestDto blockRequestDto) {
        log.info("Requesting block for card: {} by user: {}", cardId, userId);
        checkCardOwnership(cardId, userId);

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found"));

        Optional<BlockRequest> existingRequest = blockRequestRepository
                .findByCardIdAndStatus(cardId, BlockRequest.BlockStatus.PENDING);

        if (existingRequest.isPresent()) {
            throw new ConflictException("Block request already exists for this card");
        }

        card.setStatus(Card.CardStatus.PENDING_BLOCK);
        cardRepository.save(card);

        BlockRequest blockRequest = BlockRequest.builder()
                .card(card)
                .reason(blockRequestDto.getReason())
                .status(BlockRequest.BlockStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        BlockRequest savedRequest = blockRequestRepository.save(blockRequest);
        return mapToBlockRequestResponseDto(savedRequest);
    }

    @Override
    @Transactional
    public CardResponseDto approveCardBlock(Long cardId) {
        log.info("Approving block for card: {}", cardId);
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found"));

        BlockRequest blockRequest = blockRequestRepository
                .findByCardIdAndStatus(cardId, BlockRequest.BlockStatus.PENDING)
                .orElseThrow(() -> new NotFoundException("No pending block request for this card"));

        card.setStatus(Card.CardStatus.BLOCKED);
        cardRepository.save(card);

        blockRequest.setStatus(BlockRequest.BlockStatus.APPROVED);
        blockRequest.setProcessedAt(LocalDateTime.now());
        blockRequestRepository.save(blockRequest);

        return mapToResponseDto(card);
    }

    private void checkCardOwnership(Long cardId, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found"));

        if (!card.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Card does not belong to user");
        }
    }

    private CardResponseDto mapToResponseDto(Card card) {
        return CardResponseDto.builder()
                .id(card.getId())
                .cardNumber(maskCardNumber(encryptionUtil.decrypt(card.getCardNumber())))
                .cardHolder(card.getCardHolder())
                .expiryDate(card.getExpiryDate())
                .status(card.getStatus().name())
                .balance(card.getBalance())
                .userId(card.getUser().getId())
                .build();
    }

    private BlockRequestResponseDto mapToBlockRequestResponseDto(BlockRequest blockRequest) {
        return BlockRequestResponseDto.builder()
                .id(blockRequest.getId())
                .cardId(blockRequest.getCard().getId())
                .reason(blockRequest.getReason())
                .status(blockRequest.getStatus().name())
                .requestedAt(blockRequest.getRequestedAt())
                .build();
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return cardNumber;
        }

        String cleanNumber = cardNumber.replaceAll("\\s+", "");

        if (cleanNumber.length() == 16) {
            return "**** **** **** " + cleanNumber.substring(12);
        } else {
            return "****" + cleanNumber.substring(cleanNumber.length() - 4);
        }
    }
}