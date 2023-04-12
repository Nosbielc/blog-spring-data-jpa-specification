package com.nosbielc.blogspringdatajpaspecification.infrastructure.persistence.repositories;

import com.nosbielc.blogspringdatajpaspecification.infrastructure.persistence.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
}
