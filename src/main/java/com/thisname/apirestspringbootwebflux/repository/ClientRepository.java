package com.thisname.apirestspringbootwebflux.repository;

import com.thisname.apirestspringbootwebflux.documents.Client;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ClientRepository extends ReactiveMongoRepository<Client, String> {
}
