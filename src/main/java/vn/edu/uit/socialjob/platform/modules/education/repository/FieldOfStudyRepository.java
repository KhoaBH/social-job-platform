package vn.edu.uit.socialjob.platform.modules.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.uit.socialjob.platform.modules.education.entity.FieldOfStudy;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FieldOfStudyRepository extends JpaRepository<FieldOfStudy, UUID> {
    @Query("SELECT fos FROM FieldOfStudy fos WHERE fos.slug = :slug AND fos.isDeleted = false")
    Optional<FieldOfStudy> findBySlug(@Param("slug") String slug);
    
    @Query("SELECT fos FROM FieldOfStudy fos WHERE fos.name = :name AND fos.isDeleted = false")
    Optional<FieldOfStudy> findByName(@Param("name") String name);
    
    @Query("SELECT fos FROM FieldOfStudy fos WHERE fos.isDeleted = false")
    List<FieldOfStudy> findAll();
    
    @Query("SELECT fos FROM FieldOfStudy fos WHERE fos.id = :id AND fos.isDeleted = false")
    Optional<FieldOfStudy> findById(@Param("id") UUID id);
}
