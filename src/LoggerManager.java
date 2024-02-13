import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que proporciona un mecanismo centralizado para registrar mensajes y
 * errores. Configura y maneja un logger que escribe tanto en un archivo de
 * texto como en la consola. El formato de los mensajes de registro se gestiona
 * mediante la clase CustomFormatter.
 */
public class LoggerManager {

	// Constante para el Logger que se utilizara en toda la aplicacion.
	private static final Logger LOGGER = Logger.getLogger(LoggerManager.class.getName());

	// Handler para escribir los registros en un archivo.
	private static FileHandler fileHandler;

	// Handler para escribir los registros en la consola.
	private static ConsoleHandler consoleHandler;

	// Variable estática para prevenir la re-entrada en el envio de incidencias
	@SuppressWarnings("unused")
	private static boolean isSendingIncidence = false;

	// Bloque estatico para inicializar el logger y los handlers.
	static {
		try {
			// Configurando el nivel de log para capturar todos los niveles de log.
			LOGGER.setLevel(Level.ALL);

			// Configurando el FileHandler para escribir los logs en un archivo con la fecha
			// actual en su nombre.
			String pattern = "C:\\logs\\log-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".txt";
			fileHandler = new FileHandler(pattern, true);

			// Creando una instancia del CustomFormatter para personalizar el formato de los
			// registros.
			CustomFormatter formatter = new CustomFormatter();

			// Estableciendo el CustomFormatter en el FileHandler.
			fileHandler.setFormatter(formatter);

			// Creando y configurando el ConsoleHandler para escribir los registros en la
			// consola.
			consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(formatter); // Usando el mismo CustomFormatter para el ConsoleHandler.

			// Agregando los handlers al LOGGER.
			LOGGER.addHandler(fileHandler);
			LOGGER.addHandler(consoleHandler);

			// Evitando que los mensajes se propaguen a los manejadores del logger padre.
			LOGGER.setUseParentHandlers(false);

		} catch (IOException e) {
			// Logueando cualquier error que ocurra durante la configuracion del logger y
			// los handlers.
			LOGGER.log(Level.SEVERE, "Error al configurar el logger.", e);
		}
	}

	/**
	 * Metodo para registrar mensajes informativos.
	 *
	 * @param message El mensaje que se desea registrar.
	 */
	public static void logInfo(String message) {
		LOGGER.log(Level.INFO, message);
	}

	/**
	 * Metodo para registrar errores.
	 *
	 * @param message El mensaje que se desea registrar.
	 * @param thrown  La excepcion que se desea registrar.
	 */
	public static void logError(String message) {
		LOGGER.log(Level.SEVERE, message);
		isSendingIncidence = true; // Desmarcar después de enviar la incidencia
	}

	/**
	 * Metodo para registrar warnings.
	 *
	 * @param message El mensaje que se desea registrar.
	 */
	public static void logWarning(String message) {

		LOGGER.log(Level.WARNING, message);
		isSendingIncidence = true; // Desmarcar después de enviar la incidencia

	}

	// Método para restablecer el estado cuando se entra en el bucle inicial del
	// programa
	public static void resetIntentoDeIncidencia() {
		isSendingIncidence = false;
	}
}
