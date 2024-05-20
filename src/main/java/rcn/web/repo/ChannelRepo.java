package rcn.web.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import rcn.web.model.Channel;

public interface ChannelRepo extends JpaRepository<Channel, Long>, JpaSpecificationExecutor<Channel>{

}
