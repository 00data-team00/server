package com._data._data.user.repository;

import com._data._data.user.entity.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    boolean existsByEmailAndIsDeletedFalse(String email);
    Users findByEmailAndIsDeletedFalse(String email);
    Optional<Users> findByEmailAndIsDeletedTrue(String email);
}
