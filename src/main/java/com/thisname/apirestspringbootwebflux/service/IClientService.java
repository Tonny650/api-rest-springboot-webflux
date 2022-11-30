package com.thisname.apirestspringbootwebflux.service;

import com.thisname.apirestspringbootwebflux.documents.Client;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IClientService {

    public Flux<Client> findAll();
    public Mono<Client> findById(String id);
    public Mono<Client> save(Client client);
    public Mono<Void> delete(Client client);
}
