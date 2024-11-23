package com.sparta.blackwhitedeliverydriver.ai.repository;

import com.sparta.blackwhitedeliverydriver.ai.entity.AI;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AIRepository extends JpaRepository<AI, UUID> {
}
