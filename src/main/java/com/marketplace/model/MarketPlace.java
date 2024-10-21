package com.marketplace.model;

import java.io.Serializable;
import java.util.List;

import com.marketplace.util.Utilidades;

public class MarketPlace implements Serializable{
    private List<Vendedor> vendedores;
    private List<Solicitud> solicitudes;
    private Admin administrador;
    private Utilidades utilidades;

    // Constructor
    public MarketPlace() {
        this.administrador = new Admin("1", "Juana", "Arias", "123", "direccion", "contraseña");
        this.utilidades = Utilidades.getInstance();
        this.vendedores = utilidades.leerVendedoresDesdeArchivo();
        this.solicitudes = utilidades.leerSolicitudesDesdeArchivo();
    }

    public List<Vendedor> getVendedores() {
        return vendedores;
    }

    public void setVendedores(List<Vendedor> vendedores) {
        this.vendedores = vendedores;
    }

    public Admin getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Admin administrador) {
        this.administrador = administrador;
    }

    public Utilidades getUtilidades() {
        return utilidades;
    }

    public void setSolicitudes(List<Solicitud> solicitudes){
        this.solicitudes = solicitudes;
    }

    public List<Solicitud> getSolicitudes(){
        return solicitudes;
    }

    public Vendedor buscarVendedor(String id) {
        for (Vendedor vendedor : vendedores) {
            if (vendedor.getId().equals(id)) {
                utilidades.logInfo("Vendedor identificado con"+ id + "encontrado: " + vendedor.getNombre());
                return vendedor;
            }
        }
        utilidades.logWarning("No se encontro el vendedor identificado con: " + id);
        return null; // Retorna null si no se encuentra el vendedor
    }

    //CRUD VENDEDOR

    public void crearVendedor(Vendedor vendedor){
        utilidades.guardarVendedorEnArchivo(vendedor);
        utilidades.actualizarSerializacionVendedores();
    }

    public void eliminarVendedor(String id){
        utilidades.eliminarVendedor(id);
        utilidades.actualizarSerializacionVendedores();
    }

    public void actualizarVendedor(Vendedor vendedor){
        utilidades.modificarVendedor(vendedor);
        utilidades.actualizarSerializacionVendedores();
    }

    public void leerVendedores(){
        utilidades.leerVendedoresDesdeArchivo();
    }

    //CRUD SOLICITUDES
    public void crearSolicitud(Solicitud solicitud){
        utilidades.guardarSolicitudEnArchivo(solicitud);
        utilidades.actualizarSerializacionSolicitudes();
    }

    public void eliminarSolicitud(String id){
        utilidades.eliminarSolicitud(id);
        utilidades.actualizarSerializacionSolicitudes();
    }

    public void actualizarSolicitud(String id, EstadoSolicitud estado ){
        utilidades.cambiarEstadoSolicitud(id, estado);
        utilidades.actualizarSerializacionSolicitudes();
    }
    
    public void leerSolicitudes(){
        utilidades.leerSolicitudesDesdeArchivo();
    }

    //Reputacion 
    //CRUD chat
}
