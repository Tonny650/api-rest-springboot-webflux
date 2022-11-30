package com.thisname.apirestspringbootwebflux.service;

import com.thisname.apirestspringbootwebflux.documents.Client;
import com.thisname.apirestspringbootwebflux.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ClientServiceImp implements IClientService{

    @Autowired
   private ClientRepository repository;

    @Override
    public Flux<Client> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Client> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Client> save(Client client) {
        return repository.save(client);
    }

    @Override
    public Mono<Void> delete(Client client) {
        return repository.delete(client);
    }
}
