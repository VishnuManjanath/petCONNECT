package com.petconnect.project.repository;

import com.petconnect.project.entity.LifestyleProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LifestyleProfileRepository extends JpaRepository<LifestyleProfile, UUID> {

    Optional<LifestyleProfile> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}



