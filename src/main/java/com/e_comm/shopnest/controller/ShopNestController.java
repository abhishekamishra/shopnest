package com.e_comm.shopnest.controller;

import com.e_comm.shopnest.entity.Category;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/category")
public class ShopNestController {

    @GetMapping("/getCategories")
    public ResponseEntity getCategories() {

        List<Category> subCategories = new ArrayList<>();
        Category category = new Category();
        category.setId(1l);
        //category.setCategoryName("shopnest");

        Category category1 = new Category();
        category1.setId(2l);
        category1.setCategoryName("men");
        subCategories.add(category1);

        category1 = new Category();
        category1.setId(3l);
        category1.setCategoryName("women");
        subCategories.add(category1);

        //category.setSubCategories(subCategories);

        return ResponseEntity.status(HttpStatus.OK).body(category);
    }

    @PostMapping("/saveCategory")
    public ResponseEntity saveCategory() {

        /*
            Check duplicate entries
            409 status code
         */
        return ResponseEntity.status(HttpStatus.CREATED).body("saveCategory successfully.");
    }

    @PutMapping("/updateCategory")
    public ResponseEntity updateCategory() {
        return ResponseEntity.status(HttpStatus.OK).body("updateCategory successfully...");
    }

    @DeleteMapping("/deleteCategory/{id}")
    public ResponseEntity deleteCategory() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
