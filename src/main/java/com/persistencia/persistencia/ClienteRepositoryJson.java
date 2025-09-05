package com.persistencia.persistencia;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.persistencia.modelo.Cliente;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class ClienteRepositoryJson {
    private final Path path;
    private final ObjectMapper mapper;
    private final TypeReference<List<Cliente>> LIST_TYPE = new TypeReference<>(){};

    public ClienteRepositoryJson(Path path){
        this.path = path;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.findAndRegisterModules();
    }

    private List<Cliente> cargarTodo() throws IOException {

        if(!Files.exists(path)) return new ArrayList<>();
        byte[] bytes = Files.readAllBytes(path);
        if (bytes.length == 0) return new  ArrayList<>();

        return mapper.readValue(bytes, LIST_TYPE);

    }

    //guardar todo
    private void guardarTodo(List<Cliente> clientes) throws IOException{
        byte[] json = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(clientes);

        // escritura Atómica
        Path temp = path.resolveSibling(path.getFileName() + ".tmp");
        Files.write(temp, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        try{
            Files.move(temp, path, ATOMIC_MOVE, REPLACE_EXISTING);
        }catch (Exception e){
            Files.move(temp, path, REPLACE_EXISTING);
        }
    }

    // CRUD

    //Búsqueda simple
    public List<Cliente> buscarPorNombre(String texto) throws IOException{
        String t = texto.toLowerCase(Locale.ROOT);
        List<Cliente> out = new ArrayList<>();
        for(Cliente c:cargarTodo()){
            if(c.getNombre() != null && c.getNombre().toLowerCase(Locale.ROOT).contains(t)){
                out.add(c);
            };
        }
        return out;
    }

}

