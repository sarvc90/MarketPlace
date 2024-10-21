package com.marketplace.model;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    private List<Mensaje> mensajes;
    private Vendedor vendedor1;
    private Vendedor vendedor2;

    // Constructor
    public Chat(Vendedor vendedor1, Vendedor vendedor2) {
        this.vendedor1 = vendedor1;
        this.vendedor2 = vendedor2;
        this.mensajes = new ArrayList<>();
    }

    public List<Mensaje> getMensajes() {
        return mensajes;
    }

    public void setMensajes(List<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    public Vendedor getVendedor1() {
        return vendedor1;
    }

    public void setVendedor1(Vendedor vendedor1) {
        this.vendedor1 = vendedor1;
    }

    public Vendedor getVendedor2() {
        return vendedor2;
    }

    public void setVendedor2(Vendedor vendedor2) {
        this.vendedor2 = vendedor2;
    }

    // Método CRUD para mensajes
}
