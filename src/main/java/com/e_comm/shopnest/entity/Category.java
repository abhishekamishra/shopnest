package com.e_comm.shopnest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "category")
public class Category {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "CATEGORY_ID")
    private String categoryId;

    @Column(name = "CATEGORY_NAME")
    private String categoryName;

    private Boolean isParent;

    private Boolean isChild;

    private String parentCategory;

    @OneToMany(mappedBy = "category")
    private List<Category> categories;

    @ManyToOne
    @JoinColumn(referencedColumnName = "ID")
    private Category category;
}

