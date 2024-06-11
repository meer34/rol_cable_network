package rcn.web.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import rcn.web.model.Settlement;

public interface SettlementRepo extends JpaRepository<Settlement, Long>, JpaSpecificationExecutor<Settlement> {
	@Query("FROM Settlement settlement WHERE settlement.settledBy = (FROM AppUser appUser WHERE appUser.id = :appUserId)")
	List<Settlement> findAllForAppUser(Long appUserId);
	
	@Query("FROM Settlement settlement WHERE settlement.settledBy = (FROM AppUser appUser WHERE appUser.id = :appUserId)")
	Page<Settlement> findAllForAppUser(Long appUserId, Pageable pageable);
}
