package com.warmup.warmup_task.user.model;


import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
     Optional<User> findByUsername(String username);
     List<User> findUsersByUsername(String username);
     void deleteByUsername(String username);

}
