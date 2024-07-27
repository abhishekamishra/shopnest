package com.e_comm.shopnest.repository;

import com.e_comm.shopnest.dto.CategoryAndMappingDTO;
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

    @Query(nativeQuery = true, value = "select t1.id,t1.category_id,t1.category_name,t2.category_id as child_id,t2.category_name as child_name, t1.parent_id from (SELECT cat.id, cat.category_id,cat.category_name, map.child_id, map.parent_id FROM shopnest.category cat left join shopnest.category_mapping map on cat.category_id = map.category_id) as t1 left join shopnest.category t2 on t1.child_id = t2.category_id")
    public Optional<List<Object[]>> getCategoryAndMappingDTOs();
}
