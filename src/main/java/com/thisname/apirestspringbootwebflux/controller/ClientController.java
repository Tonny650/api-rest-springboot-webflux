package com.thisname.apirestspringbootwebflux.controller;

import com.thisname.apirestspringbootwebflux.documents.Client;
import com.thisname.apirestspringbootwebflux.service.IClientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private IClientService service;

    @Value("${config.uploads.path}")
    private String path;

    @PostMapping("/registerAndPhoto")
    public Mono<ResponseEntity<Client>> clientRegisterWithPhoto(Client client,
                                                                @RequestPart FilePart file){
        client.setFoto(UUID.randomUUID().toString() + "-" + file.filename()
                .replace(" ","")
                .replace(":","")
                .replace("//",""));

        return file.transferTo(new File(path + client.getFoto())).then(service.save(client))
                .map( c -> ResponseEntity.created(URI.create("/api/clients/".concat(c.getId())))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(c));

    }

    @PostMapping("/upload/{id}")
    public Mono<ResponseEntity<Client>> subir(@PathVariable String id, @RequestPart FilePart file){

        return service.findById(id).flatMap( c -> {
            c.setFoto(UUID.randomUUID().toString() + "-" + file.filename()
                    .replace(" ","")
                    .replace(":","")
                    .replace("//",""));

            return file.transferTo(new File(path + c.getFoto())).then(service.save(c));

        }).map(c -> ResponseEntity.ok(c)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("")
    public Mono<ResponseEntity<Flux<Client>>> listClients(){
        return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(service.findAll()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Client>> ClientById(@PathVariable String id){
        return service.findById(id).map(c -> ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(c)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping()
    public Mono<ResponseEntity<Map<String,Object>>> save(@Valid @RequestBody Mono<Client> clientMono){
        Map<String,Object> respuesta = new HashMap<>();

        return clientMono.flatMap(client -> {
           return service.save(client).map( c -> {
               respuesta.put("client", c);
               respuesta.put("mensaje","Cliente Guardado con exito");
               respuesta.put("timestamp", new Date());
               return ResponseEntity.created(URI.create("/api/clients/".concat(c.getId())))
                       .contentType(MediaType.APPLICATION_JSON_UTF8).body(respuesta);
           });
        }).onErrorResume(t -> {
            return Mono.just(t).cast(WebExchangeBindException.class)
                    .flatMap(e -> Mono.just(e.getFieldErrors()))
                    .flatMapMany(Flux::fromIterable)
                    .map(fieldErrors -> "El campo : " + fieldErrors.getField() + " " + fieldErrors.getDefaultMessage())
                    .collectList()
                    .flatMap(list -> {
                        respuesta.put("errors", list);
                        respuesta.put("timestamp", new Date());
                        respuesta.put("status", HttpStatus.BAD_REQUEST.value());

                        return Mono.just(ResponseEntity.badRequest().body(respuesta));
                    });
            });
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Client>> editClient (@RequestBody Client client, @PathVariable String id){

        return service.findById(id).flatMap(c -> {
            c.setName(client.getName());
            c.setLastName(client.getLastName());
            c.setEdad(client.getEdad());
            c.setSueldo(client.getSueldo());

            return service.save(c);
        }).map(c -> ResponseEntity.created(URI.create("/api/clients/".concat(c.getId())))
                .contentType(MediaType.APPLICATION_JSON_UTF8).body(c))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id){
        return service.findById(id).flatMap(c -> {
            return service.delete(c).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
        }).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }

}


