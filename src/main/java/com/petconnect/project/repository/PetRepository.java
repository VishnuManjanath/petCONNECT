package com.petconnect.project.repository;

import com.petconnect.project.entity.Pet;
import com.petconnect.project.entity.PetAgeGroup;
import com.petconnect.project.entity.PetSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PetRepository extends JpaRepository<Pet, UUID> {

    List<Pet> findByAvailableTrue();

    List<Pet> findByShelterIdAndAvailableTrue(UUID shelterId);

    List<Pet> findBySpeciesIgnoreCaseAndAvailableTrue(String species);

    List<Pet> findBySizeAndAvailableTrue(PetSize size);

    List<Pet> findByAgeGroupAndAvailableTrue(PetAgeGroup ageGroup);

    @Query("SELECT p FROM Pet p WHERE p.available = true AND p.adoptionFee <= :maxFee")
    List<Pet> findAvailablePetsWithinBudget(@Param("maxFee") BigDecimal maxFee);

    @Query("SELECT p FROM Pet p LEFT JOIN FETCH p.personalityProfile WHERE p.available = true AND p.personalityProfile IS NOT NULL")
    List<Pet> findAvailablePetsWithPersonalityProfiles();

    @Query("SELECT p FROM Pet p LEFT JOIN FETCH p.shelter LEFT JOIN FETCH p.personalityProfile WHERE p.id = :id")
    Optional<Pet> findByIdWithDetails(@Param("id") UUID id);

    @Query("SELECT p FROM Pet p WHERE p.shelter.id = :shelterId")
    List<Pet> findByShelter(@Param("shelterId") UUID shelterId);

    @Query("SELECT p FROM Pet p WHERE p.available = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.species) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.breed) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Pet> searchAvailablePets(@Param("search") String search);

    @Query("SELECT p FROM Pet p WHERE p.available = true AND " +
           "(:species IS NULL OR LOWER(p.species) = LOWER(:species)) AND " +
           "(:size IS NULL OR p.size = :size) AND " +
           "(:ageGroup IS NULL OR p.ageGroup = :ageGroup) AND " +
           "(:maxFee IS NULL OR p.adoptionFee <= :maxFee)")
    List<Pet> findAvailablePetsWithFilters(
            @Param("species") String species,
            @Param("size") PetSize size,
            @Param("ageGroup") PetAgeGroup ageGroup,
            @Param("maxFee") BigDecimal maxFee
    );

    @Query("SELECT COUNT(p) FROM Pet p WHERE p.shelter.id = :shelterId AND p.available = true")
    long countAvailablePetsByShelter(@Param("shelterId") UUID shelterId);

    @Query("SELECT p FROM Pet p WHERE p.goodWithKids = true AND p.available = true")
    List<Pet> findKidFriendlyPets();

    @Query("SELECT p FROM Pet p WHERE p.goodWithDogs = true AND p.available = true")
    List<Pet> findDogFriendlyPets();

    @Query("SELECT p FROM Pet p WHERE p.goodWithCats = true AND p.available = true")
    List<Pet> findCatFriendlyPets();
}

