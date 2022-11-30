package com.thisname.apirestspringbootwebflux.documents;



import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Document(collection= "client")
@Getter @Setter
public class Client {

    @Id
    private String id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String lastName;
    @NotNull
    private Integer edad;
    @NotNull
    private Double sueldo;

    private String foto;

    public Client() {
    }
}
