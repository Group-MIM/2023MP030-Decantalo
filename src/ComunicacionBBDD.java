import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Clase para gestionar la comunicacion con la base de datos.
 */
public class ComunicacionBBDD {

	/**
	 * Metodo para actualizar el valor de nProcess en la base de datos.
	 *
	 * @param url                  URL de la base de datos.
	 * @param username             Nombre de usuario para la base de datos.
	 * @param password             Password para la base de datos.
	 * @param nProcessValorAntiguo Valor antiguo de nProcess.
	 * @param nProcessValorNuevo   Valor nuevo de nProcess.
	 * @throws ClassNotFoundException Si no se encuentra el driver JDBC.
	 * @throws SQLException           Si hay un error de SQL.
	 */
	public static void cambiarValornProcess(String url, String username, String password, int nProcessValorAntiguo,
			int nProcessValorNuevo) throws ClassNotFoundException, SQLException {

		// Cargar el driver JDBC
		Class.forName("com.mysql.cj.jdbc.Driver");

		int retryCount = 0;
		// Define un numero maximo de reintentos
		int maxRetries = 3;

		// Reintentar la conexion en caso de fallo
		while (!comprobarInternet() && retryCount < maxRetries) {
			try {
				// Espera 2 segundos antes de reintentar
				Thread.sleep(2000);
				retryCount++;
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		// Si la conexion es exitosa, proceder con la actualizacion
		if (retryCount < maxRetries) {
			try (Connection connection = DriverManager.getConnection(url, username, password)) {

				String query = "UPDATE decantalo.production SET nProcess = ? WHERE nProcess = ?";
				try (PreparedStatement stmt = connection.prepareStatement(query)) {
					stmt.setInt(1, nProcessValorNuevo);
					stmt.setInt(2, nProcessValorAntiguo);
					stmt.executeUpdate();

					// Informar del cambio realizado
					LoggerManager.logInfo("Todos los registros de nProcess = " + nProcessValorAntiguo
							+ " cambiados a nProcess = " + nProcessValorNuevo + ".");
				} catch (Exception e) {
					LoggerManager.logError("Error: " + e.getMessage());
				}

			} catch (Exception e) {
				LoggerManager.logError("Error: " + e.getMessage());
			}
		} else {
			// Informar si se alcanzo el maximo de reintentos
			LoggerManager.logInfo("Se alcanzo el numero maximo de reintentos. La operacion no pudo completarse.");
		}
	}

	/**
	 * Metodo para actualizar el valor de nProcess en la base de datos basado en
	 * ID.
	 *
	 * @param url      URL de la base de datos.
	 * @param username Nombre de usuario para la base de datos.
	 * @param password Password para la base de datos.
	 * @param nProcess Nuevo valor de nProcess.
	 * @param ID      ID para identificar el registro.
	 * @throws ClassNotFoundException Si no se encuentra el driver JDBC.
	 * @throws SQLException           Si hay un error de SQL.
	 */
	public static void cambiarValornProcessByID(String url, String username, String password, int nProcess, long ID)
			throws ClassNotFoundException, SQLException {

		// Cargar el driver JDBC
		Class.forName("com.mysql.cj.jdbc.Driver");

		int retryCount = 0;
		// Define un numero maximo de reintentos
		int maxRetries = 3;

		// Reintentar la conexion en caso de fallo
		while (!comprobarInternet() && retryCount < maxRetries) {
			try {
				// Espera 2 segundos antes de reintentar
				Thread.sleep(2000);
				retryCount++;
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		// Si la conexion es exitosa, proceder con la actualizacion
		if (retryCount < maxRetries) {
			try (Connection connection = DriverManager.getConnection(url, username, password)) {

				String query = "UPDATE decantalo.production SET nProcess = ? WHERE ID = ?";
				try (PreparedStatement stmt = connection.prepareStatement(query)) {
					stmt.setInt(1, nProcess);
					stmt.setLong(2, ID);
					stmt.executeUpdate();

					// Informar del cambio realizado
					LoggerManager.logInfo("Cambiado a nProcess = " + nProcess + " el paquete ID = " + ID + ".");
				} catch (Exception e) {
					LoggerManager.logError("Error: " + e.getMessage());
				}
			} catch (Exception e) {
				LoggerManager.logError("Error: " + e.getMessage());
			}
		} else {
			// Informar si se alcanzo el maximo de reintentos
			LoggerManager.logInfo("Se alcanzo el numero maximo de reintentos. La operacion no pudo completarse.");
		}
	}

	/**
	 * Metodo para comprobar la conexion a Internet.
	 *
	 * @return true si hay conexion a Internet, false en caso contrario.
	 */
	public static boolean comprobarInternet() {
		String dirWeb = "www.google.com";
		int puerto = 80;
		boolean conectado;

		// Intentar establecer conexion
		try (Socket socket = new Socket(dirWeb, puerto)) {
			conectado = socket.isConnected();
		} catch (Exception e) {
			Globals.ContadorEjecucion = 0;
			conectado = false;
		}

		// Comprobar si el estado de la conexion ha cambiado
		if (Globals.EstadoConexionAnterior == null || Globals.EstadoConexionAnterior != conectado) {
			Globals.EstadoConexionAnterior = conectado; // actualizar el estado anterior
			if (conectado) {
				LoggerManager.logInfo("Conectado a internet.");
				LoggerManager.logInfo("Esperando nueva lectura...");

			} else {
				LoggerManager.logError("Se ha perdido la conexion a internet.");
				Globals.HayIncidenciaPendiente = true;
			}
		}

		// Pausa de 1/2 segundo
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// Restaura el estado interrumpido
			Thread.currentThread().interrupt();
		}

		return conectado;
	}

	/**
	 * Metodo para comunicarse con la base de datos y procesar registros.
	 *
	 * @param url      URL de la base de datos.
	 * @param username Nombre de usuario para la base de datos.
	 * @param password Password para la base de datos.
	 * @throws ClassNotFoundException Si no se encuentra el driver JDBC.
	 * @throws SQLException           Si hay un error de SQL.
	 */
	public static void comunicaConBBDD(String url, String username, String password)
			throws ClassNotFoundException, SQLException {

		// Comprobar la conexion a Internet antes de proceder
		if (comprobarInternet()) {
			LinkedList<Paquete> listaDePaquetes = new LinkedList<>();
			// Cargar el driver JDBC
			Class.forName("com.mysql.cj.jdbc.Driver");

			// Establecer conexion y preparar la consulta
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement stmt = connection.prepareStatement("select * FROM production WHERE nProcess = 1");
					ResultSet resultset = stmt.executeQuery()) {

				// Procesar el resultado de la consulta
				while (resultset.next()) {
					Paquete paquete = Paquete.leerPaquete(resultset);
					listaDePaquetes.add(paquete);
					Globals.IdPaqueteEnCurso = paquete.ID;
				}

				// Si hay paquetes, procesar cada uno
				if (!listaDePaquetes.isEmpty()) {
					for (Paquete paquete : listaDePaquetes) {
						paquete.nProcess = 2;
						paquete = ComunicacionERP.enviarAlERP(paquete);

						// Actualizar la base de datos con la informacion procesada
						try (PreparedStatement updateStmt = connection.prepareStatement(
								"UPDATE `decantalo`.`production` SET `nProcess` = ?, `OriginalTarget` = ? WHERE `ID`= ?")) {
							updateStmt.setInt(1, paquete.nProcess);
							updateStmt.setString(2, paquete.OriginalTarget);
							updateStmt.setLong(3, paquete.ID);
							updateStmt.executeUpdate();

						} catch (Exception e) {
							LoggerManager.logError("Error: " + e.getMessage());
						}
					}
				}
			} catch (Exception e) {
				LoggerManager.logError("Error: " + e.getMessage());
			}
		} else {
			// Informar del cambio de estado si la conexion se pierde
			LoggerManager.logInfo("Cambiando el valor nProcess = 3 del paquete " + Globals.IdPaqueteEnCurso + ".");

			cambiarValornProcessByID(Globals.DatabaseUrl, Globals.DatabaseUsername, Globals.DatabasePassword, 3,
					Globals.IdPaqueteEnCurso);
		}
	}

	/**
	 * Metodo para eliminar registros antiguos de la base de datos.
	 *
	 * @param dias Numero de dias a partir del cual se eliminaran los registros.
	 * @throws SQLException           Si hay un error de SQL.
	 * @throws ClassNotFoundException Si no se encuentra el driver JDBC.
	 */
	public static void eliminarRegistrosBBDD(String dias) throws SQLException, ClassNotFoundException {
		// Verificar si hay conexion a Internet antes de proceder.
		if (comprobarInternet()) {
			// Cargar el driver JDBC para MySQL.
			Class.forName("com.mysql.cj.jdbc.Driver");

			// Intentar establecer una conexion con la base de datos y preparar la
			// declaracion SQL.
			try (Connection con = DriverManager.getConnection(Globals.DatabaseUrl, Globals.DatabaseUsername,
					Globals.DatabasePassword);
					PreparedStatement stmt = con.prepareStatement(
							"DELETE FROM decantalo.production WHERE TimeStamp < CURRENT_DATE() - INTERVAL ? DAY")) {

				// Determinar el intervalo de dias para la eliminacion de registros.
				int daysInterval = dias.equals("0") ? 7 : Integer.parseInt(dias);
				stmt.setInt(1, daysInterval);

				// Ejecutar la declaracion SQL y obtener el numero de registros afectados.
				int result = stmt.executeUpdate();

				// Registrar el numero de registros eliminados si hay alguno.
				if (result > 0) {
					LoggerManager.logInfo("Filas borradas: " + result + ".");
				}
				// Capturar y registrar cualquier excepcion que ocurra durante la ejecucion.
			} catch (Exception e) {
				LoggerManager.logError("Error: " + e.getMessage());
			}
		}
	}

}