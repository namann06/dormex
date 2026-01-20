package com.dormex.repository;

import com.dormex.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {

    Optional<Block> findByName(String name);

    boolean existsByName(String name);

    List<Block> findByActiveTrue();

    long countByActiveTrue();
}
