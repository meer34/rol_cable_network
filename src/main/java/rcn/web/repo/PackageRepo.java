package rcn.web.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import rcn.web.model.ChannelPackage;

public interface PackageRepo extends JpaRepository<ChannelPackage, Long>, JpaSpecificationExecutor<ChannelPackage>{

}
