package com.nosbielc.blogspringdatajpaspecification.infrastructure.persistence.repositories;

import com.nosbielc.blogspringdatajpaspecification.infrastructure.persistence.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

}
