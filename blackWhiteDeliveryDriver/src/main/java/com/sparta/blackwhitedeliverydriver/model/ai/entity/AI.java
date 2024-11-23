package com.sparta.blackwhitedeliverydriver.model.ai.entity;

import com.sparta.blackwhitedeliverydriver.ai.dto.AIRequestDto;
import com.sparta.blackwhitedeliverydriver.ai.dto.AIResponseDto;
import com.sparta.blackwhitedeliverydriver.common.entity.BaseEntity;
import com.sparta.blackwhitedeliverydriver.model.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "p_ai")
public class AI extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_username", nullable = false)
    private User user;

    @Column(nullable = false)
    private String prompt;

    @Column(nullable = false)
    private String answer;

    @Builder
    private AI(User user, String prompt, String answer) {
        this.user = user;
        this.prompt = prompt;
        this.answer = answer;
    }

    // 정적 팩토리 메서드
    public static AI fromRequestDtoAndResponseDtoToAI(AIRequestDto requestDto, AIResponseDto responseDto, User user) {
        return AI.builder()
                .prompt(requestDto.getPrompt())
                .answer(responseDto.getAnswer())
                .user(user)
                .build();
    }
}
