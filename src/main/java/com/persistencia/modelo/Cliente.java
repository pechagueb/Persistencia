package com.persistencia.modelo;

import java.util.Objects;
import java.util.UUID;

public class Cliente {

    private String id; //UUID en texto
    private String nombre;
    private String email;

    public Cliente() {} // Necesario para Jackson

    public Cliente(String nombre, String email) {

        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.email = email;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;

        if (!(o instanceof Cliente)) return false;

        Cliente c = (Cliente) o;

        return Objects.equals(id, c.id);
    }

}
