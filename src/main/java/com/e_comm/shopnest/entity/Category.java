package com.e_comm.shopnest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

/***
 * Category Entity class
 * @author ABHISHEKA
 * @version 1.0
 */
@Data
@Entity
@Table(name = "category")
public class Category {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "CATEGORY_ID")
    private Long categoryId;

    @Column(name = "CATEGORY_NAME")
    private String categoryName;

}

