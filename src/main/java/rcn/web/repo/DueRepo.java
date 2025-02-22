package rcn.web.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import rcn.web.model.Channel;
import rcn.web.model.Due;

public interface DueRepo extends JpaRepository<Due, Long>, JpaSpecificationExecutor<Channel>{

}
