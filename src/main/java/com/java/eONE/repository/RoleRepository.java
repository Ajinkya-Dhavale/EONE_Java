package com.java.eONE.repository;

import com.java.eONE.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByNameNot(String name);
    
    Role findByName(String name);
    List<Role> findByNameIn(List<String> names);
}
