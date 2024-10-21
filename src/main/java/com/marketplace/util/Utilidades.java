package com.marketplace.util;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.marketplace.model.Categoria;
import com.marketplace.model.Comentario;
import com.marketplace.model.Estado;
import com.marketplace.model.EstadoSolicitud;
import com.marketplace.model.MarketPlace;
import com.marketplace.model.Producto;
import com.marketplace.model.Solicitud;
import com.marketplace.model.Vendedor;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;

public class Utilidades {
    private static Utilidades instancia;
    private static final Logger logger = Logger.getLogger(Utilidades.class.getName());
    private Properties propiedades;
    private static final String RUTA_DIRECTORIO = "Persistencia";

    private Utilidades() {
        crearDirectorio();
        propiedades = new Properties();
        try (FileInputStream fis = new FileInputStream("resources/config.properties")) {
            propiedades.load(fis);
        } catch (IOException e) {
            escribirLog("Error al cargar propiedades: " + e.getMessage(), Level.SEVERE);
        }
        try {
            String ruta = propiedades.getProperty("ruta.log");
            FileHandler fileHandler = new FileHandler(ruta, true);
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logInfo("Logger configurado correctamente");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al configurar el logger", e);
        }
    }

    // Método para crear el directorio si no existe
    private void crearDirectorio() {
        File directorio = new File(RUTA_DIRECTORIO);
        if (!directorio.exists()) {
            boolean creado = directorio.mkdirs();
            if (creado) {
                escribirLog("Directorio creado en: " + RUTA_DIRECTORIO, Level.INFO);
            } else {
                escribirLog("No se pudo crear el directorio en: " + RUTA_DIRECTORIO, Level.SEVERE);
            }
        } else {
            escribirLog("El directorio ya existe en: " + RUTA_DIRECTORIO, Level.INFO);
        }
    }

    // Método para guardar el modelo serializado en un único archivo
    public void guardarModeloSerializadoBin(Object modelo) {
        String rutaArchivo = RUTA_DIRECTORIO + "/modelo_serializado.bin"; // Define el nombre del archivo

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            oos.writeObject(modelo);
            oos.close();
            escribirLog("Modelo guardado exitosamente en: " + rutaArchivo, Level.INFO);
        } catch (IOException e) {
            escribirLog("Error al guardar el modelo: " + e.getMessage(), Level.SEVERE);
        }
    }

    public void guardarModeloSerializadoXML(MarketPlace modelo) {
        String rutaArchivo = RUTA_DIRECTORIO + "/modelo_serializado.xml";
        try {
            JAXBContext contexto = JAXBContext.newInstance(MarketPlace.class);
            Marshaller marshaller = contexto.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Guardar el modelo en un archivo XML
            marshaller.marshal(modelo, new File(rutaArchivo));

            logInfo("Modelo serializado a XML y guardado en: " + rutaArchivo);
        } catch (JAXBException e) {
            logSevere("Error al serializar el modelo a XML: " + e.getMessage());
        }
    }

    // Método para cargar el modelo desde el archivo
    public Object cargarModeloSerializadoBin() {
        String rutaArchivo = RUTA_DIRECTORIO + "/modelo_serializado.bin"; // Define el nombre del archivo
        Object modelo = null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rutaArchivo))) {
            modelo = ois.readObject();
            escribirLog("Modelo cargado exitosamente desde: " + rutaArchivo, Level.INFO);
        } catch (IOException | ClassNotFoundException e) {
            escribirLog("Error al cargar el modelo: " + e.getMessage(), Level.SEVERE);
        }

        return modelo;
    }

    public MarketPlace cargarModeloSerializadoDesdeXML() {
        String rutaArchivo = RUTA_DIRECTORIO + "/modelo_serializado.xml";
        MarketPlace modelo = null;
        try {
            JAXBContext contexto = JAXBContext.newInstance(MarketPlace.class);
            Unmarshaller unmarshaller = contexto.createUnmarshaller();

            // Cargar el modelo desde el archivo XML
            modelo = (MarketPlace) unmarshaller.unmarshal(new File(rutaArchivo));

            logInfo("Modelo cargado desde XML: " + rutaArchivo);
        } catch (JAXBException e) {
            logSevere("Error al cargar el modelo desde XML: " + e.getMessage());
        }
        return modelo;
    }

    public static Utilidades getInstance() {
        if (instancia == null) {
            instancia = new Utilidades();
        }
        return instancia;
    }

    public void escribirLog(String mensaje, Level nivel) {
        logger.log(nivel, mensaje);
    }

    public List<Producto> obtenerProductosDeVendedor(String idVendedor) {
        List<Vendedor> listaVendedores = leerVendedoresDesdeArchivo(); // Leer vendedores desde el archivo
        for (Vendedor vendedor : listaVendedores) {
            if (vendedor.getId().equals(idVendedor)) {
                return vendedor.getPublicaciones(); // Retorna la lista de productos publicados por el vendedor
            }
        }
        return new ArrayList<>(); // Retorna una lista vacía si el vendedor no se encuentra
    }

    // Método para buscar un vendedor por ID
    public Vendedor buscarVendedorPorId(String id) {
        List<Vendedor> listaVendedores = leerVendedoresDesdeArchivo();
        for (Vendedor vendedor : listaVendedores) {
            if (vendedor.getId().equals(id)) {
                return vendedor; // Retorna el vendedor encontrado
            }
        }
        return null; // Retorna null si no se encuentra el vendedor
    }

    // Método para buscar un producto por ID
    public Producto buscarProductoPorId(String id) {
        List<Producto> listaProductos = leerProductosDesdeArchivo();
        for (Producto producto : listaProductos) {
            if (producto.getId().equals(id)) {
                return producto; // Retorna el producto encontrado
            }
        }
        return null; // Retorna null si no se encuentra el producto
    }

    public List<Solicitud> buscarSolicitudPorEmisor(String emisorId) {
        List<Solicitud> solicitudesEncontradas = new ArrayList<>();
        List<Solicitud> listaSolicitudes = leerSolicitudesDesdeArchivo();

        for (Solicitud solicitud : listaSolicitudes) {
            if (solicitud.getEmisor() != null && solicitud.getEmisor().getId().equals(emisorId)) {
                solicitudesEncontradas.add(solicitud);
            }
        }

        escribirLog("Solicitudes encontradas para el emisor ID: " + emisorId, Level.INFO);
        return solicitudesEncontradas;
    }

    public List<Solicitud> buscarSolicitudPorReceptor(String receptorId) {
        List<Solicitud> solicitudesEncontradas = new ArrayList<>();
        List<Solicitud> listaSolicitudes = leerSolicitudesDesdeArchivo();

        for (Solicitud solicitud : listaSolicitudes) {
            if (solicitud.getReceptor() != null && solicitud.getReceptor().getId().equals(receptorId)) {
                solicitudesEncontradas.add(solicitud);
            }
        }

        escribirLog("Solicitudes encontradas para el receptor ID: " + receptorId, Level.INFO);
        return solicitudesEncontradas;
    }

    public void gestionarArchivos(List<Vendedor> listaVendedores, List<Producto> listaProductos,
            List<Solicitud> listaSolicitudes) {
        String rutaVendedores = propiedades.getProperty("rutaVendedores.txt");
        String rutaProductos = propiedades.getProperty("rutaProductos.txt");
        String rutaSolicitudes = propiedades.getProperty("rutaSolicitudes.txt");
        escribirListaEnArchivo(rutaVendedores, listaVendedores);
        escribirListaEnArchivo(rutaProductos, listaProductos);
        escribirListaEnArchivo(rutaSolicitudes, listaSolicitudes);
        escribirLog("Archivos gestionados correctamente", Level.INFO);
    }

    private void escribirListaEnArchivo(String ruta, List<?> lista) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ruta))) {
            for (Object objeto : lista) {
                if (objeto instanceof Vendedor) {
                    Vendedor vendedor = (Vendedor) objeto;
                    writer.write(vendedor.getId() + "%" + vendedor.getNombre() + "%" + vendedor.getApellido() + "%"
                            + vendedor.getCedula() + "%" + vendedor.getDireccion() + "%" + vendedor.getContraseña()
                            + "%" + vendedor.getPublicaciones() + "%" + vendedor.getRedDeContactos() + "%");
                } else if (objeto instanceof Producto) {
                    Producto producto = (Producto) objeto;
                    writer.write(producto.getId() + "%" + producto.getNombre() + "%" + producto.getDescripcion() + "%"
                            + producto.getFechaPublicacion() + "%" + producto.getImagenRuta() + "%"
                            + producto.getPrecio()
                            + producto.getMeGustas() + "%" + producto.getComentarios() + "%" + producto.getEstado()
                            + "%" + producto.getCategoria());
                } else {
                    Solicitud solicitud = (Solicitud) objeto;
                    writer.write(solicitud.getId() + "%" + solicitud.getEmisor() + "%"
                            + solicitud.getReceptor() + "%" + solicitud.getEstado());
                }
                writer.newLine();
            }
            logInfo("Lista escrita en archivo correctamente");
        } catch (IOException e) {
            logSevere("Error al escribir en el archivo: " + ruta);
        }
    }

    public void guardarSolicitudEnArchivo(Solicitud solicitud) {
        String rutaSolicitudes = propiedades.getProperty("rutaSolicitudes.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaSolicitudes, true))) {
            String emisorId = solicitud.getEmisor() != null ? solicitud.getEmisor().getId() : "";
            String receptorId = solicitud.getReceptor() != null ? solicitud.getReceptor().getId() : "";
            writer.write(solicitud.getId() + "%" + emisorId + "%" + receptorId + "%" + solicitud.getEstado());
            writer.newLine();
            escribirLog("Solicitud guardada exitosamente: " + solicitud, Level.INFO);
        } catch (IOException e) {
            escribirLog("Error al guardar la solicitud: " + solicitud, Level.SEVERE);
        }
    }

    public void guardarVendedorEnArchivo(Vendedor vendedor) {
        String rutaVendedores = propiedades.getProperty("rutaVendedores.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaVendedores, true))) {
            String publicacionesStr = vendedor.getPublicaciones().stream()
                    .map(Producto::getId)
                    .reduce((p1, p2) -> p1 + "," + p2).orElse("");

            String contactosStr = vendedor.getRedDeContactos().stream()
                    .map(Vendedor::getId)
                    .reduce((c1, c2) -> c1 + "," + c2).orElse("");

            writer.write(vendedor.getId() + "%" + vendedor.getNombre() + "%" + vendedor.getApellido() + "%"
                    + vendedor.getCedula() + "%" + vendedor.getDireccion() + "%" + vendedor.getContraseña() + "%"
                    + publicacionesStr + "%" + contactosStr + "%");
            writer.newLine();
            escribirLog("Vendedor guardado exitosamente: " + vendedor, Level.INFO);
        } catch (IOException e) {
            escribirLog("Error al guardar el vendedor: " + vendedor, Level.SEVERE);
        }
    }

    public void guardarProductoEnArchivo(Producto producto) {
        String rutaProductos = propiedades.getProperty("rutaProductos.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaProductos))) {
            writer.write(producto.getId() + "%" + producto.getNombre() + "%" + producto.getDescripcion() + "%"
                    + producto.getFechaPublicacion() + "%" + producto.getImagenRuta() + "%" + producto.getPrecio()
                    + producto.getMeGustas() + "%" + producto.getComentarios() + "%" + producto.getEstado() + "%"
                    + producto.getCategoria());
            writer.newLine();
            escribirLog("Producto guardado exitosamente: " + producto, Level.INFO);
        } catch (IOException e) {
            escribirLog("Error al guardar el vendedor: " + producto, Level.SEVERE);
        }
    }

    public List<Vendedor> leerVendedoresDesdeArchivo() {
        List<Vendedor> listaVendedores = new ArrayList<>();
        String rutaVendedores = propiedades.getProperty("rutaVendedores.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaVendedores))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("%");
                List<Producto> publicaciones = new ArrayList<>();
                if (datos.length > 6) {
                    String[] idsPublicaciones = datos[6].split(",");
                    for (String id : idsPublicaciones) {
                        Producto producto = buscarProductoPorId(id);
                        publicaciones.add(producto);
                    }
                }

                List<Vendedor> contactos = new ArrayList<>();
                if (datos.length > 7) {
                    String[] idsContactos = datos[7].split(",");
                    for (String id : idsContactos) {
                        Vendedor contacto = buscarVendedorPorId(id);
                        contactos.add(contacto);
                    }
                }

                Vendedor vendedor = new Vendedor(
                        datos[0], // ID
                        datos[1], // Nombre
                        datos[2], // Apellido
                        datos[3], // Cedula
                        datos[4], // Direccion
                        datos[5], // Contraseña
                        publicaciones, // Publicaciones
                        contactos // Red de contactos
                );
                listaVendedores.add(vendedor);
            }
            escribirLog("Vendedores leídos desde el archivo correctamente.", Level.INFO);
        } catch (IOException e) {
            escribirLog("Error al leer vendedores desde el archivo: " + rutaVendedores, Level.SEVERE);
        }
        return listaVendedores;
    }

    // Método para leer productos desde archivo
    public List<Producto> leerProductosDesdeArchivo() {
        List<Producto> listaProductos = new ArrayList<>();
        String rutaProductos = propiedades.getProperty("rutaProductos.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaProductos))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("%");
                Producto producto = new Producto(
                        datos[0], // ID
                        datos[1], // Nombre
                        datos[2], // Descripcion
                        datos[3], // Fecha Publicacion (string)
                        datos[4], // Imagen Ruta
                        Integer.parseInt(datos[5]), // Precio
                        Integer.parseInt(datos[6]), // Me Gustas
                        Estado.valueOf(datos[7]), // Estado (convertido a Enum)
                        Categoria.valueOf(datos[8]) // Categoria (convertido a Enum)
                );
                listaProductos.add(producto);
            }
            escribirLog("Productos leídos desde el archivo correctamente.", Level.INFO);
        } catch (IOException e) {
            escribirLog("Error al leer productos desde el archivo: " + rutaProductos, Level.SEVERE);
        }
        return listaProductos;
    }

    // Método para leer solicitudes desde archivo
    public List<Solicitud> leerSolicitudesDesdeArchivo() {
        List<Solicitud> listaSolicitudes = new ArrayList<>();
        String rutaSolicitudes = propiedades.getProperty("rutaSolicitudes.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaSolicitudes))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("%");
                // Suponiendo que el archivo tiene el formato: id%emisorId%receptorId%estado
                String id = datos[0];
                String emisorId = datos[1];
                String receptorId = datos[2];
                EstadoSolicitud estado = EstadoSolicitud.valueOf(datos[3]); // Convertir a Enum

                // Buscar los Vendedores por ID
                Vendedor emisor = buscarVendedorPorId(emisorId);
                Vendedor receptor = buscarVendedorPorId(receptorId);

                // Crear la solicitud
                Solicitud solicitud = new Solicitud(id, emisor, receptor, estado);
                listaSolicitudes.add(solicitud);
            }
            escribirLog("Solicitudes leídas desde el archivo correctamente.", Level.INFO);
        } catch (IOException e) {
            escribirLog("Error al leer solicitudes desde el archivo: " + rutaSolicitudes, Level.SEVERE);
        }
        return listaSolicitudes;
    }

    public void actualizarSerializacionVendedores() {
        List<Vendedor> listaVendedores = leerVendedoresDesdeArchivo();
        serializarLista(listaVendedores, true);
        serializarLista(listaVendedores, false);
        escribirLog("Serialización de vendedores actualizada correctamente.", Level.INFO);
    }

    public void actualizarSerializacionProductos() {
        List<Producto> listaProductos = leerProductosDesdeArchivo();
        serializarLista(listaProductos, false);
        serializarLista(listaProductos, true);
        escribirLog("Serialización de productos actualizada correctamente.", Level.INFO);
    }

    public void actualizarSerializacionSolicitudes() {
        List<Solicitud> listaSolicitudes = leerSolicitudesDesdeArchivo();
        serializarLista(listaSolicitudes, false);
        serializarLista(listaSolicitudes, true);
        escribirLog("Serialización de solicitudes actualizada correctamente.", Level.INFO);
    }

    // Método para eliminar solicitud
    public void eliminarSolicitud(String idSolicitud) {
        List<Solicitud> listaSolicitudes = leerSolicitudesDesdeArchivo();
        listaSolicitudes.removeIf(solicitud -> solicitud.getId().equals(idSolicitud));
        gestionarArchivos(leerVendedoresDesdeArchivo(), leerProductosDesdeArchivo(), listaSolicitudes);
        escribirLog("Solicitud eliminada exitosamente con ID: " + idSolicitud, Level.INFO);
    }

    // Método para cambiar el estado de una solicitud
    public void cambiarEstadoSolicitud(String idSolicitud, EstadoSolicitud nuevoEstado) {
        List<Solicitud> listaSolicitudes = leerSolicitudesDesdeArchivo();
        for (Solicitud solicitud : listaSolicitudes) {
            if (solicitud.getId().equals(idSolicitud)) {
                solicitud.setEstado(nuevoEstado);
                escribirLog("Estado de la solicitud cambiado exitosamente: " + solicitud, Level.INFO);
                break;
            }
        }
        gestionarArchivos(leerVendedoresDesdeArchivo(), leerProductosDesdeArchivo(), listaSolicitudes);
    }

    // Método para leer todas las solicitudes
    public List<Solicitud> leerTodasLasSolicitudes() {
        return leerSolicitudesDesdeArchivo();
    }

    // Método para modificar un vendedor
    public void modificarVendedor(Vendedor vendedorModificado) {
        List<Vendedor> listaVendedores = leerVendedoresDesdeArchivo();
        for (int i = 0; i < listaVendedores.size(); i++) {
            if (listaVendedores.get(i).getId().equals(vendedorModificado.getId())) {
                listaVendedores.set(i, vendedorModificado);
                break;
            }
        }
        gestionarArchivos(listaVendedores, leerProductosDesdeArchivo(), leerSolicitudesDesdeArchivo());
        escribirLog("Vendedor modificado exitosamente: " + vendedorModificado, Level.INFO);
    }

    // Método para modificar un producto
    public void modificarProducto(Producto productoModificado) {
        List<Producto> listaProductos = leerProductosDesdeArchivo();
        for (int i = 0; i < listaProductos.size(); i++) {
            if (listaProductos.get(i).getId().equals(productoModificado.getId())) {
                listaProductos.set(i, productoModificado);
                break;
            }
        }
        gestionarArchivos(leerVendedoresDesdeArchivo(), listaProductos, leerSolicitudesDesdeArchivo());
        escribirLog("Producto modificado exitosamente: " + productoModificado, Level.INFO);
    }

    // Método para eliminar un vendedor
    public void eliminarVendedor(String idVendedor) {
        List<Vendedor> listaVendedores = leerVendedoresDesdeArchivo();
        listaVendedores.removeIf(vendedor -> vendedor.getId().equals(idVendedor));
        gestionarArchivos(listaVendedores, leerProductosDesdeArchivo(), leerSolicitudesDesdeArchivo());
        escribirLog("Vendedor eliminado exitosamente con ID: " + idVendedor, Level.INFO);
    }

    // Método para eliminar un producto
    public void eliminarProducto(String idProducto) {
        List<Producto> listaProductos = leerProductosDesdeArchivo();
        listaProductos.removeIf(producto -> producto.getId().equals(idProducto));
        gestionarArchivos(leerVendedoresDesdeArchivo(), listaProductos, leerSolicitudesDesdeArchivo());
        escribirLog("Producto eliminado exitosamente con ID: " + idProducto, Level.INFO);
    }

    public void serializarLista(List<?> lista, boolean esXML) {
        String ruta;
        if (lista.isEmpty()) {
            escribirLog("La lista está vacía, no se puede serializar.", Level.WARNING);
            return;
        }

        Object primerElemento = lista.get(0);
        if (primerElemento instanceof Producto) {
            ruta = esXML ? propiedades.getProperty("rutaProductos.xml") : propiedades.getProperty("rutaProductos.bin");
        } else if (primerElemento instanceof Vendedor) {
            ruta = esXML ? propiedades.getProperty("rutaVendedores.xml")
                    : propiedades.getProperty("rutaVendedores.bin");
        } else {
            escribirLog("Tipo de lista no reconocido para serialización.", Level.SEVERE);
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(ruta)) {
            if (esXML) {
                XMLEncoder encoder = new XMLEncoder(fos);
                for (Object obj : lista) {
                    encoder.writeObject(obj);
                }
                encoder.close();
            } else {
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                for (Object obj : lista) {
                    oos.writeObject(obj);
                }
                oos.close();
            }
            escribirLog("Lista serializada exitosamente a " + ruta, Level.INFO);
        } catch (IOException e) {
            escribirLog("Error al serializar la lista: " + ruta, Level.SEVERE);
        }
    }

    public void serializarObjeto(Object obj, String ruta, boolean esXML) {
        try (FileOutputStream fos = new FileOutputStream(ruta)) {
            if (esXML) {
                XMLEncoder encoder = new XMLEncoder(fos);
                encoder.writeObject(obj);
                encoder.close();
            } else {
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(obj);
                oos.close();
            }
            escribirLog("Objeto serializado exitosamente a " + ruta, Level.INFO);
        } catch (IOException e) {
            escribirLog("Error al serializar el objeto: " + ruta, Level.SEVERE);
        }
    }

    public List<Producto> deserializarProductos(boolean esXML) {
        List<Producto> productos = new ArrayList<>();
        String ruta = esXML ? propiedades.getProperty("ruta.productos.xml")
                : propiedades.getProperty("ruta.productos.bin");

        try (FileInputStream fis = new FileInputStream(ruta)) {
            if (esXML) {
                XMLDecoder decoder = new XMLDecoder(fis);
                while (true) {
                    try {
                        Object obj = decoder.readObject();
                        if (obj instanceof Producto) {
                            productos.add((Producto) obj);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        break; // Fin del archivo
                    }
                }
                decoder.close();
            } else {
                ObjectInputStream ois = new ObjectInputStream(fis);
                while (true) {
                    try {
                        Object obj = ois.readObject();
                        if (obj instanceof Producto) {
                            productos.add((Producto) obj);
                        }
                    } catch (EOFException e) {
                        break; // Fin del archivo
                    }
                }
                ois.close();
            }
            escribirLog("Productos deserializados exitosamente desde " + ruta, Level.INFO);
        } catch (IOException | ClassNotFoundException e) {
            escribirLog("Error al deserializar productos desde: " + ruta, Level.SEVERE);
        }
        return productos;
    }

    public List<Vendedor> deserializarVendedores(boolean esXML) {
        List<Vendedor> vendedores = new ArrayList<>();
        String ruta = esXML ? propiedades.getProperty("ruta.vendedores.xml")
                : propiedades.getProperty("ruta.vendedores.bin");

        try (FileInputStream fis = new FileInputStream(ruta)) {
            if (esXML) {
                XMLDecoder decoder = new XMLDecoder(fis);
                while (true) {
                    try {
                        Object obj = decoder.readObject();
                        if (obj instanceof Vendedor) {
                            vendedores.add((Vendedor) obj);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        break; // Fin del archivo
                    }
                }
                decoder.close();
            } else {
                ObjectInputStream ois = new ObjectInputStream(fis);
                while (true) {
                    try {
                        Object obj = ois.readObject();
                        if (obj instanceof Vendedor) {
                            vendedores.add((Vendedor) obj);
                        }
                    } catch (EOFException e) {
                        break; // Fin del archivo
                    }
                }
                ois.close();
            }
            escribirLog("Vendedores deserializados exitosamente desde " + ruta, Level.INFO);
        } catch (IOException | ClassNotFoundException e) {
            escribirLog("Error al deserializar vendedores desde: " + ruta, Level.SEVERE);
        }
        return vendedores;
    }

    // Método para agregar un comentario a un producto
    public void agregarComentarioAProducto(String productoId, Comentario comentario) {
        Producto producto = buscarProductoPorId(productoId);
        if (producto != null) {
            producto.agregarComentario(comentario);
            modificarProducto(producto); // Guardar cambios
            escribirLog("Comentario agregado al producto ID: " + productoId, Level.INFO);
        } else {
            escribirLog("Producto no encontrado para agregar comentario.", Level.WARNING);
        }
    }

    // Método para eliminar un comentario de un producto
    public void eliminarComentarioDeProducto(String productoId, String comentarioId) {
        Producto producto = buscarProductoPorId(productoId);
        if (producto != null) {
            boolean eliminado = producto.eliminarComentario(comentarioId);
            if (eliminado) {
                modificarProducto(producto); // Guardar cambios
                escribirLog("Comentario eliminado del producto ID: " + productoId, Level.INFO);
            } else {
                escribirLog("Comentario no encontrado para eliminar en el producto ID: " + productoId, Level.WARNING);
            }
        } else {
            escribirLog("Producto no encontrado para eliminar comentario.", Level.WARNING);
        }
    }

    // Método para actualizar un comentario de un producto
    public void actualizarComentarioDeProducto(String productoId, String comentarioId, String nuevoTexto) {
        Producto producto = buscarProductoPorId(productoId);
        if (producto != null) {
            boolean actualizado = producto.actualizarComentario(comentarioId, nuevoTexto);
            if (actualizado) {
                modificarProducto(producto); // Guardar cambios
                escribirLog("Comentario actualizado en el producto ID: " + productoId, Level.INFO);
            } else {
                escribirLog("Comentario no encontrado para actualizar en el producto ID: " + productoId, Level.WARNING);
            }
        } else {
            escribirLog("Producto no encontrado para actualizar comentario.", Level.WARNING);
        }
    }

    public int contarProductosPorRangoFecha(String fechaInicio, String fechaFin) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // Ajusta el patrón según tu
                                                                                       // formato
        LocalDateTime inicio = LocalDateTime.parse(fechaInicio, formatter);
        LocalDateTime fin = LocalDateTime.parse(fechaFin, formatter);

        List<Producto> listaProductos = leerProductosDesdeArchivo();
        int contador = 0;

        for (Producto producto : listaProductos) {
            LocalDateTime fechaPublicacion = producto.getFechaPublicacion();
            if ((fechaPublicacion.isEqual(inicio) || fechaPublicacion.isAfter(inicio)) &&
                    (fechaPublicacion.isEqual(fin) || fechaPublicacion.isBefore(fin))) {
                contador++;
            }
        }

        escribirLog("Cantidad de productos publicados entre " + fechaInicio + " y " + fechaFin + ": " + contador,
                Level.INFO);
        return contador;
    }

    public int contarProductosPorVendedor(String idVendedor) {
        List<Vendedor> listaVendedores = leerVendedoresDesdeArchivo();

        for (Vendedor vendedor : listaVendedores) {
            if (vendedor.getId().equals(idVendedor)) {
                int cantidadProductos = vendedor.getPublicaciones().size();
                escribirLog(
                        "Cantidad de productos publicados por el vendedor ID " + idVendedor + ": " + cantidadProductos,
                        Level.INFO);
                return cantidadProductos;
            }
        }

        escribirLog("Vendedor no encontrado con ID: " + idVendedor, Level.WARNING);
        return 0;
    }

    public int contarContactosPorVendedor(String idVendedor) {
        Vendedor vendedor = buscarVendedorPorId(idVendedor);

        if (vendedor != null) {
            int cantidadContactos = vendedor.getRedDeContactos().size();
            escribirLog("Cantidad de contactos para el vendedor ID " + idVendedor + ": " + cantidadContactos,
                    Level.INFO);
            return cantidadContactos;
        } else {
            escribirLog("Vendedor no encontrado con ID: " + idVendedor, Level.WARNING);
            return 0;
        }
    }

    public List<Producto> obtenerTop10ProductosPopulares() {
        List<Producto> listaProductos = leerProductosDesdeArchivo();

        // Ordenar productos por la cantidad de "me gusta"
        listaProductos.sort((p1, p2) -> Integer.compare(p2.getMeGustas(), p1.getMeGustas()));

        // Obtener los 10 primeros productos
        List<Producto> top10 = listaProductos.stream().limit(10).collect(Collectors.toList());

        escribirLog("Top 10 productos más populares obtenidos.", Level.INFO);
        return top10;
    }


    public void exportarEstadisticas(String ruta, String nombreUsuario, String fechaInicio, String fechaFin, String idVendedor) {
        // Formatear la fecha actual
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaActual = LocalDateTime.now().format(formatter);
    
        // Obtener información para el reporte
        int cantidadProductosPublicados = contarProductosPorRangoFecha(fechaInicio, fechaFin);
        int cantidadProductosPorVendedor = contarProductosPorVendedor(idVendedor);
        int cantidadContactos = contarContactosPorVendedor(idVendedor);
        List<Producto> top10Productos = obtenerTop10ProductosPopulares();
    
        // Crear el contenido del reporte
        StringBuilder reporte = new StringBuilder();
        reporte.append("<Título>Reporte de Listado de Clientes\n");
        reporte.append("<fecha>Fecha: ").append(fechaActual).append("\n");
        reporte.append("<Usuario>Reporte realizado por: ").append(nombreUsuario).append("\n\n");
        reporte.append("Información del reporte:\n");
        reporte.append("Cantidad de productos publicados entre ").append(fechaInicio.formatted(formatter)).append(" y ").append(fechaFin.formatted(formatter)).append(": ").append(cantidadProductosPublicados).append("\n");
        reporte.append("Cantidad de productos publicados por el vendedor ID ").append(idVendedor).append(": ").append(cantidadProductosPorVendedor).append("\n");
        reporte.append("Cantidad de contactos para el vendedor ID ").append(idVendedor).append(": ").append(cantidadContactos).append("\n");
        reporte.append("Top 10 productos con más 'me gusta':\n");
    
        for (Producto producto : top10Productos) {
            reporte.append("- ").append(producto.getNombre()).append(" (ID: ").append(producto.getId()).append(") con ").append(producto.getMeGustas()).append(" me gusta(s)\n");
        }
    
        reporte.append("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n");
        reporte.append("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n");
    
        // Escribir el reporte en el archivo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ruta))) {
            writer.write(reporte.toString());
            escribirLog("Reporte exportado exitosamente a: " + ruta, Level.INFO);
        } catch (IOException e) {
            escribirLog("Error al exportar el reporte: " + e.getMessage(), Level.SEVERE);
        }
    }
    public void registrarAccion(String tipoUsuario, String accion, String interfaz) {
        // Formatear la fecha y hora actual
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaHora = LocalDateTime.now().format(formatter);

        // Crear el mensaje de log
        String mensaje = String.format("Tipo de Usuario: %s, Acción: %s, Interfaz: %s, Fecha y Hora: %s", 
                                        tipoUsuario, accion, interfaz, fechaHora);

        // Registrar en el log
        escribirLog(mensaje, Level.INFO);
    }

    // Métodos para registrar mensajes en diferentes niveles de severidad
    public void logSevere(String mensaje) {
        escribirLog(mensaje, Level.SEVERE);
    }

    public void logWarning(String mensaje) {
        escribirLog(mensaje, Level.WARNING);
    }

    public void logInfo(String mensaje) {
        escribirLog(mensaje, Level.INFO);
    }

    public void logConfig(String mensaje) {
        escribirLog(mensaje, Level.CONFIG);
    }

    public void logFine(String mensaje) {
        escribirLog(mensaje, Level.FINE);
    }

    public void logFiner(String mensaje) {
        escribirLog(mensaje, Level.FINER);
    }

    public void logFinest(String mensaje) {
        escribirLog(mensaje, Level.FINEST);
    }
}
