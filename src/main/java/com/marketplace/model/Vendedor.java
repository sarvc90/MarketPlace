package com.marketplace.model;

import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import com.marketplace.util.Utilidades;

public class Vendedor extends Persona {
    private List<Producto> publicaciones;
    private List<Vendedor> redDeContactos;
    private Utilidades utilidades;

    public Vendedor() {
        super();
    }

    public Vendedor(String id, String nombre, String apellido, String cedula, String direccion, String contraseña,
            List<Producto> publicaciones, List<Vendedor> redDeContactos) {
        super(id, nombre, apellido, cedula, direccion, contraseña);
        this.publicaciones = publicaciones;
        this.redDeContactos = redDeContactos;
        this.utilidades = Utilidades.getInstance();
    }

    public List<Producto> getPublicaciones() {
        return publicaciones;
    }

    public void setPublicaciones(List<Producto> publicaciones) {
        this.publicaciones = publicaciones;
    }

    public List<Vendedor> getRedDeContactos() {
        return redDeContactos;
    }

    public void setRedDeContactos(List<Vendedor> redDeContactos) {
        this.redDeContactos = redDeContactos;
    }

    public Utilidades getUtilidades() {
        return utilidades;
    }

    // CRUD PRODUCTO
    public void crearProducto(Producto producto) {
        utilidades.guardarProductoEnArchivo(producto);
        utilidades.actualizarSerializacionProductos();
    }

    public void eliminarProducto(String id) {
        List<Producto> productosPropios = utilidades.obtenerProductosDeVendedor(getId());
        boolean productoEncontrado = false;

        for (Producto producto : productosPropios) {
            if (producto.getId().equals(id)) {
                productoEncontrado = true;
                break;
            }
        }

        if (productoEncontrado) {
            utilidades.eliminarProducto(id);
            utilidades.actualizarSerializacionProductos();
        } else {
            System.out.println("El producto con ID " + id + " no pertenece a este vendedor.");
        }
    }

    public void actualizarProducto(Producto producto) {
        List<Producto> productosPropios = utilidades.obtenerProductosDeVendedor(getId());
        boolean productoEncontrado = false;

        for (Producto p : productosPropios) {
            if (p.getId().equals(producto.getId())) {
                productoEncontrado = true;
                break;
            }
        }

        if (productoEncontrado) {
            utilidades.modificarProducto(producto);
            utilidades.actualizarSerializacionProductos();
        } else {
            System.out.println("El producto con ID " + producto.getId() + " no pertenece a este vendedor.");
        }
    }

    public void leerProducto() {
        List<Producto> productosPropios = utilidades.obtenerProductosDeVendedor(getId());
        for (Producto producto : productosPropios) {
            System.out.println(producto); // Asumiendo que Producto tiene un método toString
        }
    }

    public List<Producto> obtenerProductosPropiosOrdenadosPorFecha() {
        List<Producto> productosPropios = utilidades.obtenerProductosDeVendedor(getId());
    
        // Ordenar la lista de productos por fecha de publicación
        Collections.sort(productosPropios, new Comparator<Producto>() {
            @Override
            public int compare(Producto p1, Producto p2) {
                return p1.getFechaPublicacion().compareTo(p2.getFechaPublicacion());
            }
        });
    
        return productosPropios; // Retorna la lista ordenada
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Productos Propios (Ordenados por Fecha de Publicación):\n");
        List<Producto> productosPropiosOrdenados = obtenerProductosPropiosOrdenadosPorFecha();
        for (Producto producto : productosPropiosOrdenados) {
            sb.append(producto.toString()).append("\n"); // Asumiendo que Producto tiene un método toString
        }
        return sb.toString();
    }

}
