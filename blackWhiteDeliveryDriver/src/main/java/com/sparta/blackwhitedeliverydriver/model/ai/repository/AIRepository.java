package com.sparta.blackwhitedeliverydriver.model.ai.repository;

import com.sparta.blackwhitedeliverydriver.model.ai.entity.AI;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AIRepository extends JpaRepository<AI, UUID> {
}
