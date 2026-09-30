package com.tienda.modelo;

/**
 * Representa a las personas que realizan compras (RF05-RF07).
 */
public class Cliente {

    private final int idCliente;
    private String nombre;
    private String cedula;
    private String telefono;

    public Cliente(int idCliente, String nombre, String telefono) {
        this(idCliente, nombre, "", telefono);
    }

    public Cliente(int idCliente, String nombre, String cedula, String telefono) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.cedula = cedula;
        this.telefono = telefono;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return String.format("[%d] %-20s tel:%s", idCliente, nombre, telefono);
    }
}
