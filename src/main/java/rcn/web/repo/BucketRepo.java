package rcn.web.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import rcn.web.model.Bucket;

public interface BucketRepo extends JpaRepository<Bucket, Long>, JpaSpecificationExecutor<Bucket>{

}
