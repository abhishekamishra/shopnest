package com.e_comm.shopnest.controller;

import com.e_comm.shopnest.dto.CategoryDTO;
import com.e_comm.shopnest.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/***
 * Controller class for Category Entity
 * @author ABHISHEKA
 * @version 1.0
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /***
     * Fetch all categories details from database
     * @return ResponseEntity
     */
    @GetMapping("/v1/get-categories")
    public ResponseEntity getCategories() {

        ResponseEntity responseEntity = null;

        try {
            Optional<List<CategoryDTO>> categoryDTOs = categoryService.getCategories();

            if (categoryDTOs.isPresent()) {
                responseEntity = ResponseEntity.status(HttpStatus.OK).body(categoryDTOs.get());
            } else {
                responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body("success");
            }
        } catch (Exception exception) {
        }

        return responseEntity;
    }

    /***
     * Saves all categories details to the database
     * @return ResponseEntity
     */
    @PostMapping("/v1/save-categories")
    public ResponseEntity saveCategory(@RequestBody List<CategoryDTO> categoryDTOS) {

        ResponseEntity responseEntity = null;

        try {
            Optional<List<CategoryDTO>> categoryDTO = categoryService.saveCategory(categoryDTOS);
        /*
            Check duplicate entries
            409 status code
         */
            responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(categoryDTO.isPresent() ? categoryDTO.get() : "No data saved");
        } catch (Exception exception) {
        }
        return null;
    }

    /***
     * Update the category details with the existing category value along with all the references (parent and child)
     * @return ResponseEntity
     */
    @PutMapping("/v1/update-category")
    public ResponseEntity updateCategory(@RequestBody CategoryDTO categoryDTO) {

        ResponseEntity responseEntity = null;

        try {
            Optional<CategoryDTO> category = categoryService.updateCategory(categoryDTO);

            if (category.isPresent()) {
                responseEntity = ResponseEntity.status(HttpStatus.OK).body(category.get());
            } else {
                responseEntity = ResponseEntity.status(HttpStatus.OK).body("update failed");
            }
        } catch (Exception exception) {
            // throw
        }

        return responseEntity;
    }

    /***
     * Deletes the category and it's references from database
     * @return ResponseEntity
     */
    @DeleteMapping("/v1/delete-category/{categoryId}")
    public ResponseEntity deleteCategory(@PathVariable Long categoryId) {

        try {
            categoryService.deleteCategory(categoryId);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }
}
