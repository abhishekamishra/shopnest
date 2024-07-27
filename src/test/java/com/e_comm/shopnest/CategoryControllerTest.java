package com.e_comm.shopnest;

import com.e_comm.shopnest.controller.CategoryController;
import com.e_comm.shopnest.dto.CategoryDTO;
import com.e_comm.shopnest.service.CategoryService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

//import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }

    @Test
    @Order(1)
    @DisplayName(value = "Test Controller saveCategory method")
    void test_saveCategory_success_scenario() throws Exception {

        Set<CategoryDTO> children = new HashSet<>();

        // Child - 1
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(1l);
        categoryDTO.setCategoryId(1l);
        categoryDTO.setCategoryName("MEN");
        categoryDTO.setParentId(3l);
        categoryDTO.setChildId(null);
        children.add(categoryDTO);

        // Child - 2
        categoryDTO = new CategoryDTO();
        categoryDTO.setId(2l);
        categoryDTO.setCategoryId(2l);
        categoryDTO.setCategoryName("WOMEN");
        categoryDTO.setParentId(3l);
        categoryDTO.setChildId(null);
        children.add(categoryDTO);

        categoryDTO = new CategoryDTO();
        categoryDTO.setId(3l);
        categoryDTO.setCategoryId(3l);
        categoryDTO.setCategoryName("Head");
        categoryDTO.setChildren(children);

        Mockito.when(categoryService.saveCategory(Mockito.any(CategoryDTO.class))).thenReturn(Optional.of(categoryDTO));

    }
}
