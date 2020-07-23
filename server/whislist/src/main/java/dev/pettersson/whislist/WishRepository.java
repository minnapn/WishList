package dev.pettersson.whislist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
interface WishRepository extends JpaRepository<Wish, Long> {}

