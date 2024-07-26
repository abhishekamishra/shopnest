package com.e_comm.shopnest.dto;

import com.e_comm.shopnest.entity.Category;
import com.e_comm.shopnest.entity.CategoryMapping;
import lombok.Data;

import java.util.List;

@Data
public class CategoryAndMappingList {

    private Category category;
    CategoryMapping categoryMappings;
}
