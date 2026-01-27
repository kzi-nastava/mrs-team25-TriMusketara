package com.example.demo.repositories;

import com.example.demo.model.Panic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PanicRepository extends JpaRepository<Panic, Long> {
    List<Panic> findAllByOrderByCreatedAtDesc();
}
