package com.petconnect.project.repository;

import com.petconnect.project.entity.AdoptionApplication;
import com.petconnect.project.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdoptionApplicationRepository extends JpaRepository<AdoptionApplication, UUID> {

    List<AdoptionApplication> findByApplicantId(UUID applicantId);

    List<AdoptionApplication> findByPetId(UUID petId);

    List<AdoptionApplication> findByShelterId(UUID shelterId);

    List<AdoptionApplication> findByShelterIdAndStatus(UUID shelterId, ApplicationStatus status);

    List<AdoptionApplication> findByStatus(ApplicationStatus status);

    Optional<AdoptionApplication> findByApplicantIdAndPetId(UUID applicantId, UUID petId);

    @Query("SELECT a FROM AdoptionApplication a LEFT JOIN FETCH a.applicant LEFT JOIN FETCH a.pet LEFT JOIN FETCH a.shelter WHERE a.shelter.id = :shelterId")
    List<AdoptionApplication> findByShelterIdWithDetails(@Param("shelterId") UUID shelterId);

    @Query("SELECT a FROM AdoptionApplication a LEFT JOIN FETCH a.applicant LEFT JOIN FETCH a.pet LEFT JOIN FETCH a.shelter WHERE a.applicant.id = :applicantId")
    List<AdoptionApplication> findByApplicantIdWithDetails(@Param("applicantId") UUID applicantId);

    @Query("SELECT a FROM AdoptionApplication a WHERE a.shelter.id = :shelterId AND a.status = 'PENDING' ORDER BY a.applicationDate ASC")
    List<AdoptionApplication> findPendingApplicationsByShelter(@Param("shelterId") UUID shelterId);

    @Query("SELECT COUNT(a) FROM AdoptionApplication a WHERE a.shelter.id = :shelterId AND a.status = 'PENDING'")
    long countPendingApplicationsByShelter(@Param("shelterId") UUID shelterId);

    @Query("SELECT COUNT(a) FROM AdoptionApplication a WHERE a.pet.id = :petId AND a.status = 'PENDING'")
    long countPendingApplicationsForPet(@Param("petId") UUID petId);

    boolean existsByApplicantIdAndPetId(UUID applicantId, UUID petId);
}
