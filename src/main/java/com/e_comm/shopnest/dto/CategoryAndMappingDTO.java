package com.e_comm.shopnest.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CategoryAndMappingDTO implements Serializable {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private Long childId;
    private String childName;
    private Long parentId;
}
