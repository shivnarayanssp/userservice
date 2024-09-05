package dev.shiv4u.userservice.services;

import dev.shiv4u.userservice.models.Role;
import dev.shiv4u.userservice.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private RoleRepository roleRepository;
    @Autowired
    public RoleService(RoleRepository roleRepository){
        this.roleRepository=roleRepository;
    }
    public Role createRole(String name){
        Role role=new Role();
        role.setRole(name);
        return roleRepository.save(role);
    }
}
