package com.cims.user.repositories;

import com.cims.user.entities.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a repository interface for performing database operations on UserAccount entities.
 */
@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    boolean existsByTokenAndUserName(String token, String userName);

    List<UserAccount> findAllByBranchCode(String branchCode);

    UserAccount findByUserName(String userName);

    UserAccount findByToken(String token);

    UserAccount findByAdminUserIdAndBranchCode(String adminUserId, String branchCode);
}
