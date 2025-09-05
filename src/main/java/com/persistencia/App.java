package com.persistencia;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.persistencia.modelo.Cliente;
import com.persistencia.persistencia.ClienteRepositoryJson;

import java.nio.*;
import java.nio.file.Paths;
import java.util.*;

public class App {
    public static void main(String[] args) {
        var repo = new ClienteRepositoryJson(Paths.get("clientes.json"));
        var sc = new Scanner(System.in);
        var mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());
        mapper.findAndRegisterModules();

        while (true){
            System.out.println("====== CRUD Clientes JSON ======");
            System.out.println("1 - Crear clientes por (teclado) ");
            System.out.println("2 - Crear clientes por (hardcode) ");
            System.out.println("3 - Listar cientes ");
            System.out.println("4 - Buscar por ID" );
            System.out.println("5 - Actualizar nombre por ID ");
            System.out.println("6 - Elimninar por ID " );
            System.out.println("0 - Salir " );

            String op = sc.nextLine();

            try{

                switch (op){
                    case "1" -> {

                        System.out.println("Nombre: ");
                        String nombre = sc.nextLine().trim();
                        System.out.println("Email: ");
                        String email = sc.nextLine();
                        Cliente c = new Cliente(nombre, email);
                        repo.crear(c);
                        System.out.println("Creado (JSON): \n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(c));

                    }
                    case "2" -> {

                        Cliente c = new Cliente("Ana Maria", "anamaria@gmail.com");
                        repo.crear(c);
                        System.out.println("Creado (JSON): " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(c));

                    }
                    case "3" -> {
                        List<Cliente> lista = repo.listar();

                        if (lista.isEmpty()) {
                            System.out.println("Sin datos");
                        } else {
                            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(lista));
                        }

                    }
                    case "4" -> {

                        System.out.println("Id: ");
                        String id = sc.nextLine().trim();
                        Optional<Cliente> c = repo.buscarPorId(id);
                        System.out.println(c.map(Objects::toString).orElse("No encontrado"));

                    }
                    case "5" -> {

                        System.out.println("Id: ");
                        String id = sc.nextLine().trim();
                        var opt = repo.buscarPorId(id);
                        if(opt.isEmpty()){
                            System.out.println("No encontrado.");
                            break;
                        }
                        Cliente c = opt.get();
                        System.out.println("Nombre nuevo: ");
                        c.setNombre(sc.nextLine().trim());
                        repo.actualizar(c);
                        System.out.println("Actualizado " + c);

                    }
                    case "6" -> {

                        System.out.println("Id: ");
                        String id = sc.nextLine().trim();
                        repo.eliminar(id);
                        System.out.println("Eliminado!");

                    }
                    case "0" -> {
                        return;
                    }

                    default -> System.out.println("Opción invalida ");
                }

            }catch (Exception e){
                System.out.println("Error: " + e.getMessage());
            }


        }

    }
}
