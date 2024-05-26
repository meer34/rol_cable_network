package rcn.web.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import rcn.security.Role;

public interface RoleRepo extends JpaRepository<Role, Long>{

}
