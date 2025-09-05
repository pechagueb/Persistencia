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
    // C - Create
    public Cliente crear(Cliente nuevo) throws IOException{
        List<Cliente> clientes = cargarTodo();
        //Validar que no exista cliente con el mismo email
        boolean existeEmail = clientes.stream().anyMatch(c -> c.getEmail().equalsIgnoreCase(nuevo.getEmail()));
        if(existeEmail) throw new IllegalArgumentException("Ya existe un cliente con ese email.");
        clientes.add(nuevo);
        guardarTodo(clientes);
        return nuevo;
    }

    // R - Read (uno)
    public Optional<Cliente> buscarPorId(String id) throws IOException {
        return cargarTodo().stream()
                .filter( c -> Objects.equals(c.getId(), id))
                .findFirst();
    }

    // R - Read (todos)
    public List<Cliente> listar() throws IOException{
        return Collections.unmodifiableList(cargarTodo());
    }

    // U - Update
    public void actualizar(Cliente actualizado) throws IOException{
        List<Cliente> clientes = cargarTodo();
        boolean modificado = false;
        for(int i = 0; i < clientes.size(); i++){
            // compara los ids
            if(Objects.equals(clientes.get(i).getId(), actualizado.getId())){
                clientes.set(i, actualizado);
                modificado = true;
                break;
            }
        }
        if(!modificado) throw new NoSuchElementException("Cliente no encontrado.");
        guardarTodo(clientes);
    }

    // D - Delete: eliminar por id
    public void eliminar(String id) throws IOException{
        List<Cliente> clientes = cargarTodo();
        boolean removed = clientes.removeIf(c -> Objects.equals(c.getId(), id));
        if(!removed) throw new NoSuchElementException("Cliente no encontrado.");
        guardarTodo(clientes);
    }



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

