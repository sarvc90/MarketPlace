package com.marketplace.model;

import java.time.LocalDateTime;

public class Mensaje {
    private String contenido;
    private Vendedor emisor;
    private LocalDateTime horaDeEnvio;

    // Constructor
    public Mensaje(String contenido, Vendedor emisor) {
        this.contenido = contenido;
        this.emisor = emisor;
        this.horaDeEnvio = LocalDateTime.now(); // Asignamos la hora de envío actual
    }

    @Override
    public String toString() {
        return "Mensaje [contenido=" + contenido + ", emisor=" + emisor + ", horaDeEnvio=" + horaDeEnvio + "]";
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Vendedor getEmisor() {
        return emisor;
    }

    public void setEmisor(Vendedor emisor) {
        this.emisor = emisor;
    }

    public LocalDateTime getHoraDeEnvio() {
        return horaDeEnvio;
    }

    public void setHoraDeEnvio(LocalDateTime horaDeEnvio) {
        this.horaDeEnvio = horaDeEnvio;
    }

    //toString
}
