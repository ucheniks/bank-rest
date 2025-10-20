package com.gshelgaas.bankcards.repository;

import com.gshelgaas.bankcards.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findByFromCardUserIdOrToCardUserId(Long fromUserId, Long toUserId);
}