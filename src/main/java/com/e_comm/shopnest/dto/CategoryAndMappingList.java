package com.e_comm.shopnest.dto;

import com.e_comm.shopnest.entity.Category;
import com.e_comm.shopnest.entity.CategoryMapping;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CategoryAndMappingList implements Serializable {

    private Category category;
    CategoryMapping categoryMappings;
}
