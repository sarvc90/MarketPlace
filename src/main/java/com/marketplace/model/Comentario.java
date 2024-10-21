package com.marketplace.model;

import java.time.LocalDateTime;

public class Comentario {
    private String id;
    private Vendedor autor;
    private LocalDateTime fechaPublicacion;
    private String texto;

    // Constructor
    public Comentario(String id, Vendedor autor, LocalDateTime fechaPublicacion, String texto) {
        this.id = id;
        this.autor = autor;
        this.fechaPublicacion = fechaPublicacion;
        this.texto = texto;
    }

    @Override
    public String toString() {
        return "Comentario [autor=" + autor + ", fechaPublicacion=" + fechaPublicacion + ", texto=" + texto + "]";
    }

    public Vendedor getAutor() {
        return autor;
    }

    public void setAutor(Vendedor autor) {
        this.autor = autor;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
}
