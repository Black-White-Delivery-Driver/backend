package com.sparta.blackwhitedeliverydriver.repository;

import com.sparta.blackwhitedeliverydriver.entity.AI;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AIRepository extends JpaRepository<AI, UUID> {
}
