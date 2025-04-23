package com._data._data.user.repository;

import com._data._data.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    boolean existsByEmail(String email);
    Users findByEmail(String email);
}
