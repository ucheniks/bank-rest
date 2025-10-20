package com.gshelgaas.bankcards.controller;

import com.gshelgaas.bankcards.dto.TransferRequestDto;
import com.gshelgaas.bankcards.dto.TransferResponseDto;
import com.gshelgaas.bankcards.entity.User;
import com.gshelgaas.bankcards.service.TransferService;
import com.gshelgaas.bankcards.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user/transfers")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;
    private final UserService userService;

    @PostMapping
    public TransferResponseDto transferBetweenCards(@Valid @RequestBody TransferRequestDto transferRequest) {
        User currentUser = userService.getCurrentUser();
        log.info("POST /user/transfers - transfer from user: {}", currentUser.getId());

        return transferService.transferBetweenCards(transferRequest, currentUser.getId());
    }

    @GetMapping
    public List<TransferResponseDto> getUserTransfers() {
        User currentUser = userService.getCurrentUser();
        log.info("GET /user/transfers - get transfers for user: {}", currentUser.getId());

        return transferService.getUserTransfers(currentUser.getId());
    }
}