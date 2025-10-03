package com.petconnect.project.repository;

import com.petconnect.project.entity.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, UUID> {

    Optional<Shelter> findByAdminUserId(UUID adminUserId);

    List<Shelter> findByNameContainingIgnoreCase(String name);

    List<Shelter> findByCityIgnoreCase(String city);

    List<Shelter> findByStateIgnoreCase(String state);

    @Query("SELECT s FROM Shelter s WHERE s.city = :city AND s.state = :state")
    List<Shelter> findByCityAndState(@Param("city") String city, @Param("state") String state);

    @Query("SELECT s FROM Shelter s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.city) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.state) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Shelter> searchShelters(@Param("search") String search);

    @Query("SELECT s FROM Shelter s LEFT JOIN FETCH s.pets WHERE s.id = :id")
    Optional<Shelter> findByIdWithPets(@Param("id") UUID id);

    @Query("SELECT COUNT(p) FROM Pet p WHERE p.shelter.id = :shelterId AND p.available = true")
    long countAvailablePets(@Param("shelterId") UUID shelterId);

    boolean existsByAdminUserId(UUID adminUserId);
}


