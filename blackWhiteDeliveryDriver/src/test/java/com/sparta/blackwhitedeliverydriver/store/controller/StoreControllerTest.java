package com.sparta.blackwhitedeliverydriver.store.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.blackwhitedeliverydriver.config.TestSecurityConfig;
import com.sparta.blackwhitedeliverydriver.store.dto.StoreResponseDto;
import com.sparta.blackwhitedeliverydriver.model.user.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.mock.user.MockUser;
import com.sparta.blackwhitedeliverydriver.store.service.StoreService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StoreController.class)
@Import(TestSecurityConfig.class)
public class StoreControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    StoreService storeService;

    @Autowired
    ObjectMapper mapper;

    private static final String BASE_URL = "/api/v1";

    @BeforeEach
    void setUp(){

    }

    // 접근권한 : CUSTOMER, OWNER, MANAGER, MASTER
    // CUSTER -> getStores 성공
    // 삭제된 점포는 조회 x
    // isPublic = false 는 조회 x
    @Test
    @DisplayName("사용자 모든 점포 조회")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void findAllStoresByCustomer() throws Exception {
        int page = 1;
        int size = 10;
        String sortBy = "createdDate";
        boolean isAsc = true;

        List<StoreResponseDto> mockResponse = List.of(StoreResponseDto.builder().build());
        when(storeService.getStores(page - 1, size, sortBy, isAsc)).thenReturn(mockResponse);

        mvc.perform(get(BASE_URL + "/stores/")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sortBy", sortBy)
                        .param("isAsc", String.valueOf(isAsc))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // OWNER -> getStores 성공
    // 삭제된 점포는 조회 x
    // isPublic = false 는 조회 x

    // MANAGER -> getStores 성공
    // 삭제된 점포는 조회 x
    // isPublic = false 는 조회 x

    // MASTER -> getStores 성공
    // 삭제된 점포는 조회 x
    // isPublic = false 는 조회 x
}
