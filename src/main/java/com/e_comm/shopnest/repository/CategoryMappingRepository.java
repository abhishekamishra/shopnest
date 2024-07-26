package com.e_comm.shopnest.repository;

import com.e_comm.shopnest.entity.CategoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryMappingRepository extends JpaRepository<CategoryMapping, Long> {

    public Optional<List<CategoryMapping>> findByCategoryId(Long categoryId);

    @Query(nativeQuery = true, value = "SELECT * FROM shopnest.category_mapping where category_id = :id or child_id =:id or parent_id=:id")
    public Optional<List<CategoryMapping>> getCategoryMappingById(@Param("id") Long id);
}
