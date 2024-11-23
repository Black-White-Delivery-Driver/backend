package com.sparta.blackwhitedeliverydriver.model.address.repository;

import com.sparta.blackwhitedeliverydriver.model.address.entity.Address;
import com.sparta.blackwhitedeliverydriver.model.user.entity.User;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    Page<Address> findAllByUserAndDeletedByIsNullAndDeletedDateIsNull(User user, Pageable pageable);
}
