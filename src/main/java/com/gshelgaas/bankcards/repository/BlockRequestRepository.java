package com.gshelgaas.bankcards.repository;

import com.gshelgaas.bankcards.entity.BlockRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRequestRepository extends JpaRepository<BlockRequest, Long> {
    List<BlockRequest> findByCardUserId(Long userId);
    Optional<BlockRequest> findByCardIdAndStatus(Long cardId, BlockRequest.BlockStatus status);
    List<BlockRequest> findByStatus(BlockRequest.BlockStatus status);
}