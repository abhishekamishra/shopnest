package com.e_comm.shopnest.serviceImpl;

import com.e_comm.shopnest.dto.CategoryAndMappingList;
import com.e_comm.shopnest.dto.CategoryDTO;
import com.e_comm.shopnest.entity.Category;
import com.e_comm.shopnest.entity.CategoryMapping;
import com.e_comm.shopnest.repository.CategoryMappingRepository;
import com.e_comm.shopnest.repository.CategoryRepository;
import com.e_comm.shopnest.service.CategoryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMappingRepository categoryMappingRepository;

    @Override
    @Transactional
    public Optional<List<CategoryDTO>> saveCategory(List<CategoryDTO> categoryDTOS) {

        if (!categoryDTOS.isEmpty()) {

            for (CategoryDTO categoryDTO : categoryDTOS) {

                Optional<Category> existingCategory = categoryRepository.findByCategoryId(categoryDTO.getCategoryId());

                if (existingCategory.isPresent()) {
                    // throw duplicate exception
                } else {

                    CategoryAndMappingList categoryAndMappingList = createCategory(categoryDTO);

                    categoryRepository.saveAndFlush(categoryAndMappingList.getCategory());
                    categoryMappingRepository.saveAndFlush(categoryAndMappingList.getCategoryMappings());
                }
            }
        } else {
            // throw exception
        }
        return Optional.ofNullable(categoryDTOS);
    }

    @Override
    // caching
    public Optional<List<CategoryDTO>> getCategories() {

        // Get all Category data
        List<Category> categories = categoryRepository.findAll();

        // creating a map from categories table where key = category_id and value = category object
        Map<Long, Category> categoryMap = categories.stream().collect(Collectors.toMap(Category::getCategoryId, Function.identity()));

        // get all category_mapping data
        List<CategoryMapping> categoryMappings = categoryMappingRepository.findAll();

        // create a map where key = category_id and value = List of child objects from category_mapping table
        Map<Long, List<CategoryMapping>> categoryMappingMap = new HashMap<>();

        // create a list of category mapping objects which would be the value for a specific category id
        List<CategoryMapping> categoryMappingList = new ArrayList<>();
        for (CategoryMapping categoryMapping : categoryMappings) {
            categoryMappingList = new ArrayList<>();
            if (categoryMappingMap.containsKey(categoryMapping.getCategoryId())) {
                categoryMappingMap.get(categoryMapping.getCategoryId()).add(categoryMapping);
                categoryMappingList = categoryMappingMap.get(categoryMapping.getCategoryId());
                categoryMappingMap.put(categoryMapping.getCategoryId(), categoryMappingList);
            } else {
                categoryMappingList.add(categoryMapping);
                categoryMappingMap.put(categoryMapping.getCategoryId(), categoryMappingList);
            }
        }

        // this list has all parents with their children categories
        List<CategoryDTO> categoryDTOS = new ArrayList<>();
        Set<CategoryDTO> children = new HashSet<>();
        CategoryDTO categoryDTO = new CategoryDTO();
        for (Category category : categories) {
            children = new HashSet<>();
            if (categoryMappingMap.containsKey(category.getCategoryId())) {

                categoryDTO = new CategoryDTO();
                categoryDTO.setCategoryId(category.getCategoryId());
                categoryDTO.setCategoryName(category.getCategoryName());

                for (CategoryMapping categoryMapping : categoryMappingMap.get(category.getCategoryId())) {
                    if (categoryMapping.getChildId() != null) {
                        Category existingCategory = categoryMap.get(categoryMapping.getChildId());
                        CategoryDTO categoryDto = new CategoryDTO();
                        categoryDto.setCategoryName(existingCategory.getCategoryName());
                        categoryDto.setCategoryId(existingCategory.getCategoryId());

                        children.add(categoryDto);
                    }
                }
                categoryDTO.setChildren(children);
            }
            categoryDTOS.add(categoryDTO);
        }

        // we should map children to parents
        for (CategoryDTO dto : categoryDTOS) {

            // main logic
        }
        return Optional.of(categoryDTOS);
    }

    @Override
    public Optional<CategoryDTO> updateCategory(CategoryDTO categoryDTO) {

        CategoryDTO dto = null;

        // find by PK from category table
        Optional<Category> category = categoryRepository.findById(categoryDTO.getId());

        if (category.isPresent()) {

            // finding any reference of old category_id in category_mapping table
            Optional<List<CategoryMapping>> categoryMappings = categoryMappingRepository.getCategoryMappingById(category.get().getCategoryId());

            List<CategoryMapping> categoryMappingList = new ArrayList<>();

            // updating all old category_id references with new category_id
            if (categoryMappings.isPresent()) {

                for (CategoryMapping categoryMapping : categoryMappings.get()) {

                    if (categoryMapping.getChildId() != null && Long.compare(categoryMapping.getChildId(), category.get().getCategoryId()) == 0) {
                        categoryMapping.setChildId(categoryDTO.getCategoryId());
                    }

                    if (categoryMapping.getParentId() != null && Long.compare(categoryMapping.getParentId(), category.get().getCategoryId()) == 0) {
                        categoryMapping.setParentId(categoryDTO.getCategoryId());
                    }

                    if (categoryMapping.getCategoryId() != null && Long.compare(categoryMapping.getCategoryId(), category.get().getCategoryId()) == 0) {
                        categoryMapping.setCategoryId(categoryDTO.getCategoryId());
                    }

                    categoryMappingList.add(categoryMapping);
                }

            }

            // setting new value to existing object
            category.get().setCategoryId(categoryDTO.getCategoryId());
            category.get().setCategoryName(categoryDTO.getCategoryName());

            categoryMappingRepository.saveAllAndFlush(categoryMappingList);

            Category result = categoryRepository.saveAndFlush(category.get());

            dto.setId(result.getId());
            dto.setCategoryId(result.getCategoryId());
            dto.setCategoryName(result.getCategoryName());
        }

        return Optional.ofNullable(dto);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {

        Optional<Category> category = categoryRepository.findByCategoryId(categoryId);
        if (category.isPresent()) {

            // finding any reference of old category_id in category_mapping table
            Optional<List<CategoryMapping>> categoryMappings = categoryMappingRepository.getCategoryMappingById(categoryId);

            List<CategoryMapping> updateList = new ArrayList<>();
            List<CategoryMapping> deleteList = new ArrayList<>();

            if (categoryMappings.isPresent()) {

                for (CategoryMapping categoryMapping : categoryMappings.get()) {

                    // checks if the child id has category id references then update the references to NULL and to updatelist
                    if (categoryMapping.getChildId() != null && Long.compare(categoryMapping.getChildId(), categoryId) == 0) {
                        categoryMapping.setChildId(null);
                        updateList.add(categoryMapping);
                    }

                    // checks if the category id has any references then add the mapping object to deletelist
                    if (categoryMapping.getCategoryId() != null && Long.compare(categoryMapping.getCategoryId(), categoryId) == 0) {
                        deleteList.add(categoryMapping);
                    }
                }

                // updates existing child references to NULL
                categoryMappingRepository.saveAllAndFlush(updateList);

                // deletes the matching mappings from mapping table
                categoryMappingRepository.deleteAllInBatch(deleteList);
            }

            // deletes the data from category table
            categoryRepository.deleteById(category.get().getId());
        }else{
            // throw exception
        }
    }

    /*
    Create category object
     */
    private CategoryAndMappingList createCategory(CategoryDTO categoryDTO) {

        CategoryAndMappingList categoryAndMappingList = new CategoryAndMappingList();
        Category category = new Category();
        category.setCategoryName(categoryDTO.getCategoryName());
        category.setCategoryId(categoryDTO.getCategoryId());

        categoryAndMappingList.setCategoryMappings(createCategoryMapping(categoryDTO));
        categoryAndMappingList.setCategory(category);

        return categoryAndMappingList;
    }

    /*
    Create Category Mapping data for given Category object
     */
    private CategoryMapping createCategoryMapping(CategoryDTO categoryDTO) {

        CategoryMapping categoryMapping = new CategoryMapping();
        categoryMapping.setCategoryId(categoryDTO.getCategoryId());
        categoryMapping.setParentId(categoryDTO.getParentId());
        updateParentsChildId(categoryDTO.getParentId(), categoryDTO.getCategoryId(), categoryDTO);
        categoryMapping.setChildId(null);

        return categoryMapping;
    }

    /*
    Update CHILD_ID of existing parent CATEGORY
     */
    private void updateParentsChildId(Long parentId, Long categoryId, CategoryDTO categoryDTO) {
        Optional<List<CategoryMapping>> categoryMapping = categoryMappingRepository.findByCategoryId(parentId);
        if (categoryMapping.isPresent() && categoryMapping.get().size() != 0) {

            if (categoryMapping.get().get(0).getChildId() == null) {
                categoryMapping.get().get(0).setChildId(categoryId);
            } else if (categoryMapping.get().get(0).getChildId() != null) {
                // new
                createAndSaveCategoryMapping(categoryDTO);
            }
        }
    }

    private void createAndSaveCategoryMapping(CategoryDTO categoryDTO) {

        CategoryMapping categoryMapping = new CategoryMapping();
        categoryMapping.setCategoryId(categoryDTO.getParentId());
        Optional<List<CategoryMapping>> parentObject = categoryMappingRepository.findByCategoryId(categoryDTO.getParentId());
        categoryMapping.setParentId(parentObject.isPresent() ? parentObject.get().get(0).getParentId() : null);
        categoryMapping.setChildId(categoryDTO.getCategoryId());

        categoryMappingRepository.saveAndFlush(categoryMapping);
    }

}
