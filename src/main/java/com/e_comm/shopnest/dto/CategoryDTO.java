package com.e_comm.shopnest.dto;

import com.e_comm.shopnest.entity.Category;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Set;

/***
 * Category DTO class that contains and help to transfer Category Table's data over network
 * @author ABHISHEKA
 */
@Data
public class CategoryDTO implements Serializable {

    private Long id;

    private Long categoryId;

    private String categoryName;

    // stores children data which will create a hierarchical structure
    private Set<CategoryDTO> children;

    private Long parentId;

    private Long childId;
}
