package com.e_comm.shopnest.serviceImpl;

import com.e_comm.shopnest.dto.CategoryAndMappingDTO;
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
    public Optional<List<CategoryDTO>> saveCategory(List<CategoryDTO> categoryDTOS) {

        if (!categoryDTOS.isEmpty()) {

            for (CategoryDTO categoryDTO : categoryDTOS) {

                saveCategoryAndChildCategories(categoryDTO);

                if (categoryDTO.getChildren().size() != 0) {

                    for (CategoryDTO dto : categoryDTO.getChildren()) {
                        saveCategoryAndChildCategories(dto);
                    }
                }
            }
        } else {
            // throw exception
        }
        return Optional.ofNullable(categoryDTOS);
    }

    @Transactional
    private void saveCategoryAndChildCategories(CategoryDTO categoryDTO) {
        Optional<Category> existingCategory = categoryRepository.findByCategoryId(categoryDTO.getCategoryId());

        if (existingCategory.isPresent()) {
            // throw duplicate exception
        } else {

            CategoryAndMappingList categoryAndMappingList = createCategory(categoryDTO);

            categoryRepository.saveAndFlush(categoryAndMappingList.getCategory());
            categoryMappingRepository.saveAndFlush(categoryAndMappingList.getCategoryMappings());
        }
    }

    @Override
    // caching
    public Optional<List<CategoryDTO>> getCategories() {

        // fetches all data with their child details
        Optional<List<Object[]>> categoryAndMappingDTOS = categoryMappingRepository.getCategoryAndMappingDTOs();

        List<CategoryAndMappingDTO> categoryAndMappingDTOList = new ArrayList<>();

        // converting and mapping above data into category table data
        for (Object[] object : categoryAndMappingDTOS.get()) {
            categoryAndMappingDTOList.add(createCategoryAndMappingDTO(object));
        }

        List<Long> categoryIds = categoryAndMappingDTOList.stream().map(e -> e.getCategoryId()).collect(Collectors.toList());

        Map<Long, List<CategoryAndMappingDTO>> categoryAndMappingMap = new HashMap<>();
        List<CategoryAndMappingDTO> categoryMappingList = new ArrayList<>();
        for (CategoryAndMappingDTO categoryAndMappingDTO : categoryAndMappingDTOList) {
            categoryMappingList = new ArrayList<>();
            if (categoryAndMappingMap.containsKey(categoryAndMappingDTO.getCategoryId())) {
                categoryAndMappingMap.get(categoryAndMappingDTO.getCategoryId()).add(categoryAndMappingDTO);
                categoryMappingList = categoryAndMappingMap.get(categoryAndMappingDTO.getCategoryId());
                categoryAndMappingMap.put(categoryAndMappingDTO.getCategoryId(), categoryMappingList);
            } else {
                categoryMappingList.add(categoryAndMappingDTO);
                categoryAndMappingMap.put(categoryAndMappingDTO.getCategoryId(), categoryMappingList);
            }
        }


        CategoryDTO categoryDTO = new CategoryDTO();
        for (Map.Entry<Long, List<CategoryAndMappingDTO>> entry : categoryAndMappingMap.entrySet()) {

            for (int i=0; i< entry.getValue().size(); i++) {

                if (categoryAndMappingMap.containsKey(entry.getValue().get(i).getChildId())){

                    // convert List<categoryAndMappingDTO> to List<CategoryDTO>
                    categoryDTO = convertCategoryAndMappingDTOtoCategoryDTO(categoryAndMappingMap.get(entry.getValue().get(i).getChildId()));
                }
            }

        }


        return Optional.ofNullable(null);
    }

    private CategoryDTO convertCategoryAndMappingDTOtoCategoryDTO(List<CategoryAndMappingDTO> categoryAndMappingDTOS) {

        Set<CategoryDTO> children = new HashSet<>();
        CategoryDTO categoryDTO = new CategoryDTO();

        for (CategoryAndMappingDTO categoryAndMappingDTO: categoryAndMappingDTOS) {

            categoryDTO.setId(categoryAndMappingDTO.getId());
            categoryDTO.setCategoryId(categoryAndMappingDTO.getCategoryId());
            categoryDTO.setCategoryName(categoryAndMappingDTO.getCategoryName());
            categoryDTO.setChildId(categoryAndMappingDTO.getChildId());
            categoryDTO.setParentId(categoryAndMappingDTO.getParentId());
            //categoryDTO.setChildren(categoryAndMappingDTO.get);

            children.add(categoryDTO);
        }
        categoryDTO.setChildren(children);

        return categoryDTO;
    }

    /***
     * Creating CategoryAndMappingDTO object from category and mapping table
     * @param object
     * @return categoryAndMappingDTO
     */
    private CategoryAndMappingDTO createCategoryAndMappingDTO(Object[] object) {

        CategoryAndMappingDTO categoryAndMappingDTO = new CategoryAndMappingDTO();

        categoryAndMappingDTO.setId(object[0] == null ? null : Long.valueOf(object[0].toString()));
        categoryAndMappingDTO.setCategoryId(object[1] == null ? null : Long.valueOf(object[1].toString()));
        categoryAndMappingDTO.setCategoryName(object[2] == null ? null : object[2].toString());
        categoryAndMappingDTO.setChildId(object[3] == null ? null : Long.valueOf(object[3].toString()));
        categoryAndMappingDTO.setChildName(object[4] == null ? null : object[4].toString());
        categoryAndMappingDTO.setParentId(object[5] == null ? null : Long.valueOf(object[5].toString()));

        return categoryAndMappingDTO;
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
        } else {
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
