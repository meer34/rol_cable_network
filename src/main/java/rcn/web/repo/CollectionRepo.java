package rcn.web.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import rcn.web.model.Collection;

public interface CollectionRepo extends JpaRepository<Collection, Long>, JpaSpecificationExecutor<Collection> {
	
	@Query("FROM Collection collection WHERE collection.consumer = (FROM Consumer consumer WHERE consumer.id = :consumerId)")
	List<Collection> findAllForConsumer(Long consumerId);
	
	@Query("FROM Collection collection WHERE collection.consumer = (FROM Consumer consumer WHERE consumer.id = :consumerId)")
	Page<Collection> findAllForConsumer(Long consumerId, Pageable pageable);
	
	@Query("FROM Collection collection WHERE collection.collectedBy = (FROM AppUser appUser WHERE appUser.id = :appUserId)")
	Page<Collection> findAllForAppUser(Long appUserId, Pageable pageable);
	
	@Query("FROM Collection collection WHERE collection.collectedBy = (FROM AppUser appUser WHERE appUser.name = :appUserName)")
	Page<Collection> findAllForAppUserName(String appUserName, Pageable pageable);

	List<Collection> findAllByBills_Id(Long id);
	
}
