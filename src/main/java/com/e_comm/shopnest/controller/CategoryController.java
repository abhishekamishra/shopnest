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

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/getCategories")
    public ResponseEntity getCategories() {

        Optional<List<CategoryDTO>> categoryDTOs = categoryService.getCategories();

        if (categoryDTOs.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(categoryDTOs.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("success");
        }
    }

    @PostMapping("/saveCategory")
    public ResponseEntity saveCategory(@RequestBody List<CategoryDTO> categoryDTOS) {

        Optional<List<CategoryDTO>> categoryDTO = categoryService.saveCategory(categoryDTOS);
        /*
            Check duplicate entries
            409 status code
         */
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryDTO.isPresent() ? categoryDTO.get() : "No data saved");
    }

    @PutMapping("/updateCategory")
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

    @DeleteMapping("/deleteCategory/{categoryId}")
    public ResponseEntity deleteCategory(@PathVariable Long categoryId) {

        try {
            categoryService.deleteCategory(categoryId);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }
}
