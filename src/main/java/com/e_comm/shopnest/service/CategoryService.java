package com.e_comm.shopnest.service;

import com.e_comm.shopnest.dto.CategoryDTO;
import com.e_comm.shopnest.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    public Optional<List<CategoryDTO>> saveCategory(List<CategoryDTO> categoryDTOS);

    public Optional<List<CategoryDTO>> getCategories();

    public  Optional<CategoryDTO> updateCategory(CategoryDTO categoryDTO);

    public void deleteCategory(Long categoryId);
}
