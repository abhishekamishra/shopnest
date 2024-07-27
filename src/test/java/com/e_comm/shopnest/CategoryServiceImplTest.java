package com.e_comm.shopnest;

import com.e_comm.shopnest.dto.CategoryDTO;
import com.e_comm.shopnest.entity.Category;
import com.e_comm.shopnest.repository.CategoryMappingRepository;
import com.e_comm.shopnest.repository.CategoryRepository;
import com.e_comm.shopnest.service.CategoryService;
import com.e_comm.shopnest.serviceImpl.CategoryServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryServiceImplTest {

    /*@Autowired
    private MockMvc mockMvc;*/

    //@Mock
    @Autowired
    private CategoryRepository categoryRepository;

   // @Mock
   @Autowired
    private CategoryMappingRepository categoryMappingRepository;

    //@InjectMocks
    @Autowired
    private CategoryServiceImpl categoryServiceImpl;

    /*@BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(categoryServiceImpl).build();
    }*/

    @Test
    @Order(1)
    @DisplayName(value = "Test saveCategory method")
    void test_saveCategory_success_scenario(){

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

        Optional<CategoryDTO> category = categoryServiceImpl.saveCategory(categoryDTO);

        assertNotNull(categoryDTO);
        assertEquals(3l, category.get().getCategoryId());
    }

    void test_saveCategory_negative_scenario(){

        Optional<CategoryDTO> category = categoryServiceImpl.saveCategory(null);

        assertEquals(Optional.empty(), category.get());
    }

}
