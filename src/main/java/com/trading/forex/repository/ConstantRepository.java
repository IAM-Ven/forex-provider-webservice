package com.trading.forex.repository;

import com.trading.forex.entity.Constant;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ConstantRepository extends CrudRepository<Constant, Long> {
    Constant findByName(String name);

    @Modifying
    @Query("update Constant const set const.val=?1 where const.name=?2")
    void updateValByName(String val, String name);
}
