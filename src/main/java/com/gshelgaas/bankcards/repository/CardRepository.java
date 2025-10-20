package com.gshelgaas.bankcards.repository;

import com.gshelgaas.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    boolean existsByCardNumber(String cardNumber);

    @Query("SELECT c FROM Card c WHERE " +
            "c.user.id = :userId AND " +
            "(:status IS NULL OR c.status = :status)")
    Page<Card> findByUserIdWithFilters(
            @Param("userId") Long userId,
            @Param("status") Card.CardStatus status,
            Pageable pageable);
}