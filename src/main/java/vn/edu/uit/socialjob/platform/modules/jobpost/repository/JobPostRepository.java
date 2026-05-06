package vn.edu.uit.socialjob.platform.modules.jobpost.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.uit.socialjob.platform.modules.jobpost.entity.JobPost;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobPostRepository extends JpaRepository<JobPost, UUID> {

    @Query("SELECT j FROM JobPost j WHERE j.isDeleted = false")
    List<JobPost> findAll();

    @Query("SELECT j FROM JobPost j WHERE j.id = :id AND j.isDeleted = false")
    Optional<JobPost> findById(@Param("id") UUID id);

    @Query("SELECT j FROM JobPost j WHERE j.company.id = :companyId AND j.isDeleted = false")
    List<JobPost> findByCompanyId(@Param("companyId") UUID companyId);

    @Query("SELECT j FROM JobPost j WHERE j.postedBy.id = :postedById AND j.isDeleted = false")
    List<JobPost> findByPostedById(@Param("postedById") UUID postedById);
}