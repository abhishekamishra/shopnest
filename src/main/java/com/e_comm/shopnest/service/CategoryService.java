package com.e_comm.shopnest.service;

import com.e_comm.shopnest.dto.CategoryDTO;
import com.e_comm.shopnest.entity.Category;

import java.util.List;
import java.util.Optional;

/***
 * Service interface for Category Entity
 * @author ABHISHEKA
 * @version 1.0
 */
public interface CategoryService {

    public Optional<CategoryDTO> saveCategory(CategoryDTO categoryDTO);

    public Optional<List<CategoryDTO>> getCategories();

    public  Optional<CategoryDTO> updateCategory(CategoryDTO categoryDTO);

    public void deleteCategory(Long categoryId);
}
