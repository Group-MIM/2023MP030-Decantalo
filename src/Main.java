import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

/**
 * La clase Main sirve como punto de entrada para la ejecucion del programa.
 */
public class Main {

	// Controla el ciclo principal del programa.
	private static volatile boolean enEjecucion = true;

	public static void main(String[] args) {

		LoggerManager.resetIntentoDeIncidencia();

		// Controla la impresion de mensajes en consola.
		boolean imprimirEjecutandoPrograma = true;

		// Crear un hilo para verificar la conexion a internet.
		Thread threadVerificacion = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					// Verifica la conexion a internet.
					ComunicacionBBDD.comprobarInternet();
				} catch (Exception e) {
					LoggerManager.logError("Error inesperado en el hilo de verificacion de conexion");
				}
			}
		});

		// Inicia el hilo de verificacion.
		threadVerificacion.start();

		// Ciclo principal del programa.
		while (enEjecucion) {
			Globals.ContadorEjecucion++;
			LoggerManager.resetIntentoDeIncidencia();
			if (Globals.EstadoConexionAnterior != null && Globals.EstadoConexionAnterior) {
				if (imprimirEjecutandoPrograma) {
					LoggerManager.logInfo("Inicio de programa.");
					imprimirEjecutandoPrograma = false;
				}

				if (Globals.ContadorEjecucion == 1) {
					try {
						// Resetea los valores de proceso en la base de datos.
						resetearValoresProcesoBaseDeDatos();
						LoggerManager.logInfo("Programa reanudado.");
					} catch (ClassNotFoundException e1) {
						LoggerManager.logError("Error: " + e1.getMessage());
					}
				}

				try {
					// Comunicacion con la base de datos.
					ComunicacionBBDD.comunicaConBBDD(Globals.DatabaseUrl, Globals.DatabaseUsername,
							Globals.DatabasePassword);
				} catch (ClassNotFoundException | SQLException ex) {
					LoggerManager.logError("Error: " + ex.getMessage());
					// Maneja los errores de conexion a la base de datos.
					manejarErrorConexionBaseDeDatos(ex);
				}

				try {
					// Elimina registros de la base de datos.
					ComunicacionBBDD.eliminarRegistrosBBDD("0");
				} catch (SQLException | ClassNotFoundException ex) {
					LoggerManager.logError("Error: " + ex.getMessage());
				}
			}

			// Verifica si existe un archivo "stop.txt" para terminar la ejecucion.
			if (Files.exists(Paths.get("stop.txt"))) {
				enEjecucion = false;
				LoggerManager.logInfo(
						"La aplicacion ha finalizado. Archivo 'stop.txt' encontrado. Finalizando ejecucion...");
				imprimirEjecutandoPrograma = true;
			}
		}

		// Interrumpe el hilo de verificacion de conexion al finalizar el ciclo
		// principal.
		threadVerificacion.interrupt();
	}

	/**
	 * Este metodo se encarga de resetear los valores de proceso en la base de
	 * datos. Se realiza dos llamadas a la base de datos para cambiar el valor de
	 * nProcess a 1 y luego a 0.
	 *
	 * @throws ClassNotFoundException si no se puede cargar el driver de la base de
	 *                                datos.
	 */
	private static void resetearValoresProcesoBaseDeDatos() throws ClassNotFoundException {
		try {
			// Cambia el valor de nProcess a 1 para un proceso especifico en la base de
			// datos.
			ComunicacionBBDD.cambiarValornProcess(Globals.DatabaseUrl, Globals.DatabaseUsername,
					Globals.DatabasePassword, 1, 3);
		} catch (ClassNotFoundException | SQLException e) {
			// Registra un error si hay una excepcion.
			LoggerManager.logError("Error: " + e.getMessage());
		}
		try {
			// Cambia el valor de nProcess a 0 para un proceso especifico en la base de
			// datos.
			ComunicacionBBDD.cambiarValornProcess(Globals.DatabaseUrl, Globals.DatabaseUsername,
					Globals.DatabasePassword, 0, 3);
		} catch (ClassNotFoundException | SQLException e) {
			// Registra un error si hay una excepcion.
			LoggerManager.logError("Error: " + e.getMessage());
		}
	}

	/**
	 * Este metodo maneja los errores de conexion con la base de datos. Cambia el
	 * valor de nProcess a 3 para el paquete en curso en caso de un error.
	 *
	 * @param ex La excepcion que ocurrio.
	 */
	private static void manejarErrorConexionBaseDeDatos(Exception ex) {
		try {
			// Cambia el valor de nProcess a 3 para el paquete en curso en la base de datos.
			ComunicacionBBDD.cambiarValornProcessByID(Globals.DatabaseUrl, Globals.DatabaseUsername,
					Globals.DatabasePassword, 3, Globals.IdPaqueteEnCurso);
		} catch (ClassNotFoundException | SQLException e) {
			// Registra un error si hay una excepcion.
			LoggerManager.logError("Error: " + e.getMessage());
		}
		// Registra el error original.
		LoggerManager.logError("Error: " + ex.getMessage());
	}

}