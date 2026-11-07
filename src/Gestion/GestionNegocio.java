package Gestion;

import java.util.ArrayList;
import modelo.*;

public class GestionNegocio {

    private ArrayList<Cliente> clientes = new ArrayList<>();
    private ArrayList<Bicicletas> inventario = new ArrayList<>();
    private ArrayList<Alquiler> alquileresActivos = new ArrayList<>();
    private double totalRecaudado = 0;

    public GestionNegocio() {
        
        for (int i = 1; i <= 15; i++) {
            String codigo = "URB-" + (i < 10 ? "0" + i : i); 
            inventario.add(new BiciUrbana(codigo, "Giant")); 
        }

      
        for (int i = 1; i <= 10; i++) {
            String codigo = "ELE-" + (i < 10 ? "0" + i : i); 
            inventario.add(new BiciElectrica(codigo, "Xiaomi")); 
        }
        
       
        for (int i = 1; i <= 10; i++) {
            String codigo = "MON-" + (i < 10 ? "0" + i : i); 
            inventario.add(new BiciMontania(codigo, "Trek")); 
        }
    }
    
    public boolean registrarCliente(String documento, String nombre, String telefono) {
        for (Cliente c : clientes) {
            if (c.getDocumento().equals(documento)) {
                return false;
            }
        }
        clientes.add(new Cliente(documento, nombre, telefono));
        return true;
    }

    public boolean registrarBicicleta(Bicicletas bici) {
        for (Bicicletas b : inventario) {
            if (b.getCodigo().equals(bici.getCodigo())) {
                return false;
            }
        }
        inventario.add(bici);
        return true;
    }

    public void listarBicicletasDisponibles() {
        int cantUrbanas = 0;
        int cantElectricas = 0;
        int cantMontania = 0;

        for (Bicicletas b : inventario) {
            if (b.getEstado().equals("Disponible")) {
                if (b.getCodigo().startsWith("URB")) {
                    cantUrbanas++;
                } else if (b.getCodigo().startsWith("ELE")) {
                    cantElectricas++;
                } else if (b.getCodigo().startsWith("MON")) {
                    cantMontania++;
                }
            }
        }

        System.out.println("--- RESUMEN DE INVENTARIO DISPONIBLE ---");
        if (cantUrbanas > 0) {
            System.out.println("[Urbana]    | Cantidad: " + cantUrbanas + " bicicletas | Codigos: [URB-01 al URB-15] | Estado: Disponible");
        }
        if (cantElectricas > 0) {
            System.out.println("[Electrica] | Cantidad: " + cantElectricas + " bicicletas | Codigos: [ELE-01 al ELE-10] | Estado: Disponible");
        }
        if (cantMontania > 0) {
            System.out.println("[Montania]   | Cantidad: " + cantMontania + " bicicletas | Codigos: [MON-01 al MON-10] | Estado: Disponible");
        }
        if (cantUrbanas == 0 && cantElectricas == 0 && cantMontania == 0) {
            System.out.println("No hay bicicletas disponibles en este momento.");
        }
    }

    public Cliente buscarCliente(String documento) {
        for (Cliente c : clientes) {
            if (c.getDocumento().equals(documento)) {
                return c;
            }
        }
        return null;
    }

    public Bicicletas buscarBicicletaDisponible(String codigo) {
        for (Bicicletas b : inventario) {
            if (b.getCodigo().equals(codigo) && b.getEstado().equals("Disponible")) {
                return b;
            }
        }
        return null;
    }

    public boolean realizarAlquiler(String documento, String codigoBici, int horas) {
        Cliente c = buscarCliente(documento);
        Bicicletas b = buscarBicicletaDisponible(codigoBici);

        if (c != null && b != null && horas >= 1 && horas <= 8) {
            alquileresActivos.add(new Alquiler(c, b, horas));
            b.setEstado("Ocupada");
            return true;
        }
        return false;
    }

    public void procesarDevolucion(String codigoBici, int horasReales) {
        Alquiler alquiler = null;

        for (Alquiler a : alquileresActivos) {
            if (a.getBicicleta().getCodigo().equals(codigoBici)) {
                alquiler = a;
                break;
            }
        }

        if (alquiler == null) {
            System.out.println("No existe un alquiler activo con esa bicicleta.");
            return;
        }

        Bicicletas b = alquiler.getBicicleta();
        int horas = alquiler.getHorasPactadas();

        double costo;
        if (horas >= 3) {
            costo = b.calcularCosto(horas, 0.10);
        } else {
            costo = b.calcularCosto(horas);
        }

        double recargo = 0;
        if (horasReales > horas) {
            recargo = (horasReales - horas) * (b.getTarifaBase() * 1.5);
        }

        double total = costo + recargo;
        totalRecaudado += total;

        b.setEstado("Disponible");
        alquileresActivos.remove(alquiler);

        System.out.println("--- FACTURA ---");
        System.out.println("Cliente: " + alquiler.getCliente().getNombre());
        System.out.println("Bicicleta: " + b.getCodigo());
        System.out.println("Costo base: $" + costo);
        System.out.println("Recargo: $" + recargo);
        System.out.println("TOTAL: $" + total);
    }

    public void mostrarGanancias() {
        System.out.println("Ganancia total del dia: $" + totalRecaudado);
    }
}
