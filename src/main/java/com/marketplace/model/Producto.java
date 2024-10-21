package com.marketplace.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Producto implements Serializable{
    private String id;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaPublicacion;
    private String imagenRuta;
    private int precio;
    private int meGustas;
    private List<Comentario> comentarios; 
    private Estado estado;
    private Categoria categoria;

    public Producto(){

    }
    // Constructor
    public Producto(String id, String nombre, String descripcion, String fechaPublicacion, String imagenRuta, int precio, int meGustas, Estado estado, Categoria categoria) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaPublicacion = LocalDateTime.parse(fechaPublicacion);
        this.imagenRuta = imagenRuta;
        this.precio = precio;
        this.meGustas = meGustas;
        this.comentarios = new ArrayList<>();
        this.estado = estado;
        this.categoria = categoria;
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


    public String getDescripcion() {
        return descripcion;
    }


    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }


    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }


    public String getImagenRuta() {
        return imagenRuta;
    }


    public void setImagenRuta(String imagenRuta) {
        this.imagenRuta = imagenRuta;
    }


    public int getPrecio() {
        return precio;
    }


    public void setPrecio(int precio) {
        this.precio = precio;
    }


    public int getMeGustas() {
        return meGustas;
    }


    public void setMeGustas(int meGustas) {
        this.meGustas = meGustas;
    }


    public List<Comentario> getComentarios() {
        return comentarios;
    }


    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }
    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

        @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "ID: " + id + 
               ", Nombre: " + nombre + 
               ", Descripción: " + descripcion + 
               ", Fecha de Publicación: " + fechaPublicacion.format(formatter);
    }



    public void agregarComentario(Comentario comentario) {
        if (comentario != null) {
            comentarios.add(comentario);
        }
    }
    public boolean eliminarComentario(String comentarioId) {
        return comentarios.removeIf(comentario -> comentario.getId().equals(comentarioId));
    }
    public boolean actualizarComentario(String comentarioId, String nuevoTexto) {
        for (Comentario comentario : comentarios) {
            if (comentario.getId().equals(comentarioId)) {
                comentario.setTexto(nuevoTexto); // Asumiendo que Comentario tiene un método setTexto
                return true; // Actualización exitosa
            }
        }
        return false; // Comentario no encontrado
    }
    
}
