package com.petconnect.project.repository;

import com.petconnect.project.entity.PersonalityProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonalityProfileRepository extends JpaRepository<PersonalityProfile, UUID> {

    Optional<PersonalityProfile> findByPetId(UUID petId);

    boolean existsByPetId(UUID petId);

    void deleteByPetId(UUID petId);
}



