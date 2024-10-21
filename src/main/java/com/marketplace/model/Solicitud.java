package com.marketplace.model;

import java.io.Serializable;

public class Solicitud implements Serializable {
    private String id;
    private Vendedor emisor;
    private Vendedor receptor;
    private EstadoSolicitud estado;

    public Solicitud(String id, Vendedor emisor, Vendedor receptor, EstadoSolicitud estado) {
        this.id = id;
        this.emisor = emisor;
        this.receptor = receptor;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Vendedor getEmisor() {
        return emisor;
    }

    public void setEmisor(Vendedor emisor) {
        this.emisor = emisor;
    }

    public Vendedor getReceptor() {
        return receptor;
    }

    public void setReceptor(Vendedor receptor) {
        this.receptor = receptor;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Solicitud{" +
                "id='" + id + '\'' +
                ", emisor=" + (emisor != null ? emisor.getId() : "null") +
                ", receptor=" + (receptor != null ? receptor.getId() : "null") +
                ", estado=" + estado +
                '}';
    }

}