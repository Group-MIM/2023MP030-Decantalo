import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * La clase IntercambiarInformacionConREST se encarga de realizar interacciones
 * con servicios REST externos, especificamente para intercambiar informacion
 * entre la aplicacion y un servidor ERP. Utiliza una arquitectura basada en
 * hilos (threads) para gestionar las solicitudes y respuestas, permitiendo una
 * ejecucion asincra y la capacidad de manejar multiples solicitudes
 * simultaneamente.
 */
public class IntercambiarInformacionConREST extends Thread {
	public static String jsonRequest = "";
	public static String jsonResponse = "";
	volatile static boolean ejecutar = true;

	/**
	 * Constructor por defecto.
	 */
	public IntercambiarInformacionConREST() {
		super();
	}

	/**
	 * Obtener el JSON de la solicitud.
	 *
	 * @return String que representa el cuerpo JSON de la solicitud.
	 */
	public static String getJsonRequest() {
		return jsonRequest;
	}

	/**
	 * Establecer el JSON de la solicitud.
	 *
	 * @param jsonRequest String que representa el cuerpo JSON de la solicitud.
	 */
	public static void setJsonRequest(String jsonRequest) {
		IntercambiarInformacionConREST.jsonRequest = jsonRequest;
	}

	/**
	 * Obtener el JSON de la respuesta.
	 *
	 * @return String que representa el cuerpo JSON de la respuesta.
	 */
	public static String getJsonResponse() {
		return jsonResponse;
	}

	/**
	 * Establecer el JSON de la respuesta.
	 *
	 * @param jsonResponse String que representa el cuerpo JSON de la respuesta.
	 */
	public static void setJsonResponse(String jsonResponse) {
		IntercambiarInformacionConREST.jsonResponse = jsonResponse;
	}

	/**
	 * Metodo para intercambiar informacion con el servidor ERP.
	 *
	 * @param json    String que representa el cuerpo JSON de la solicitud.
	 * @param paquete Objeto Paquete que contiene la informacion del paquete.
	 * @return String que representa el cuerpo JSON de la respuesta.
	 */
	public static String intercambiarInformacion(StringBuilder json, Paquete paquete) {
		Thread hilo = new IntercambiarInformacionConREST();
		setJsonRequest(json.toString());

		try {
			hilo.setName("" + paquete.getnID());
		} catch (Exception e1) {
			LoggerManager.logError("Error: " + e1.getMessage());
		}

		try {
			hilo.start();
		} catch (Exception e1) {
			LoggerManager.logError("Error: " + e1.getMessage());
		}

		// Creacion de una instancia de Timer, que permite la planificacion de tareas
		// para su ejecucion futura.
		Timer timer = new Timer();

		// Planificacion de una tarea para ejecutarse despues de un retardo de 3000
		// milisegundos (3 segundos).
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// Verificacion si no se ha recibido una respuesta (la respuesta JSON esta
				// vacia).
				if (getJsonResponse().isEmpty()) {
					try {
						// En caso de que no se haya recibido una respuesta, se cambia el valor de
						// nProcess en la base de datos
						// para el paquete con ID especificado, utilizando el metodo
						// cambiarValornProcessBynId de la clase ComunicacionBBDD.
						ComunicacionBBDD.cambiarValornProcessByID(Globals.DatabaseUrl, Globals.DatabaseUsername,
								Globals.DatabasePassword, 1000, paquete.ID);
						// Registro de informacion indicando que se esta cerrando el hilo debido a un
						// tiempo de espera en la respuesta del ERP.
						LoggerManager.logWarning("Cerrando hilo del paquete " + hilo.getName()
								+ " tras TimeOut en respuesta del ERP...");

						// Interrupcion del hilo de ejecucion actual.
						hilo.interrupt();

					} catch (ClassNotFoundException | SQLException e) {
						// Registro de cualquier error que ocurra durante la ejecucion del bloque try.
						LoggerManager.logError("Error: " + e);
					}
				} else {
					// Si se ha recibido una respuesta, se registra la informacion indicando que se
					// esta cerrando el hilo
					// tras recibir la respuesta del ERP.
					LoggerManager
							.logWarning(
									"Cerrando el hilo del paquete " + hilo.getName() + " tras respuesta del ERP...");

					// Interrupcion del hilo de ejecucion actual.
					hilo.interrupt();
				}
			}
			// El retardo antes de ejecutar la tarea es de 3000 milisegundos (3 segundos).
		}, 3000);

		return getJsonResponse();
	}

	/**
	 * Este metodo se ejecuta automaticamente cuando se inicia el hilo (Thread). Se
	 * encarga de establecer una conexion con el servidor ERP, enviar una solicitud
	 * HTTP POST con un cuerpo JSON y recibir una respuesta del servidor.
	 */
	@Override
	public void run() {
		// Inicializacion de un StringBuffer para almacenar la respuesta del servidor.
		StringBuffer response = new StringBuffer();

		try {
			// Obtencion de la URL del servidor ERP desde la clase Globals.
			String urlErp = Globals.ERPUrl;
			// Creacion de un objeto URL.
			URL url = new URL(urlErp);

			// Apertura de una conexion HTTP con la URL especificada.
			HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
			// Configuracion del metodo de la solicitud HTTP como POST.
			conexion.setRequestMethod("POST");
			// Configuracion de la propiedad "Content-Type" de la solicitud HTTP.
			conexion.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			// Habilitacion de la salida de datos hacia la URL (necesario para enviar el
			// cuerpo JSON).
			conexion.setDoOutput(true);

			// Creacion de un flujo de salida de datos (DataOutputStream) para escribir el
			// cuerpo JSON en la conexion.
			DataOutputStream output = new DataOutputStream(conexion.getOutputStream());
			// Escritura del cuerpo JSON en el flujo de salida de datos.
			output.writeBytes(getJsonRequest());
			// Vaciado del buffer del flujo de salida de datos, asegurando que todos los
			// datos se envien.
			output.flush();
			// Cierre del flujo de salida de datos.
			output.close();

			// Creacion de un BufferedReader para leer la respuesta del servidor.
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
			String inputLine;

			// Lectura de la respuesta del servidor linea por linea y almacenamiento en el
			// StringBuffer.
			while ((inputLine = bufferedReader.readLine()) != null) {
				response.append(inputLine);
			}

			// Cierre del BufferedReader.
			bufferedReader.close();
			// Almacenamiento de la respuesta del servidor en la variable estatica
			// jsonResponse.
			setJsonResponse(response.toString());

		} catch (Exception ex) {
			// Registro de cualquier excepcion que ocurra durante la ejecucion del metodo.
			LoggerManager.logError("Error: " + ex);
		}
	}

}