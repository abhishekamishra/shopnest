package com.e_comm.shopnest.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@Entity
@Table(name = "category_mapping")
public class CategoryMapping implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "CATEGORY_ID")
    private Long categoryId;

    @Column(name = "PARENT_ID")
    private Long parentId;

    @Column(name = "CHILD_ID")
    private Long childId;
}
