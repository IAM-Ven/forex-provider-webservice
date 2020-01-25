package com.trading.forex.repository;

import com.trading.forex.entity.Recipient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipientRepository extends JpaRepository<Recipient, Long> {

    @Query("from Recipient as rt inner join User as u where u.username=:username")
    Page<Recipient> findAllByUsername(@Param("username") String username, Pageable pageable);

    Recipient findByName(String recipientName);

    void deleteByName(String recipientName);
}
