package com.gshelgaas.bankcards.repository;

import com.gshelgaas.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с пользователями системы.
 * Предоставляет методы для доступа к данным пользователей в базе данных.
 *
 * @author Георгий Шельгаас
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Находит пользователя по email.
     *
     * @param email email пользователя
     * @return пользователь с указанным email
     */
    Optional<User> findByEmail(String email);

    /**
     * Проверяет существование пользователя с указанным email.
     *
     * @param email email для проверки
     * @return true если пользователь с таким email существует, иначе false
     */
    boolean existsByEmail(String email);
}