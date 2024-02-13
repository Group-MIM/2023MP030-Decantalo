import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;

/**
 * La clase DailyFileHandler extiende la clase FileHandler y proporciona un
 * manejador de archivos de log que rota los archivos de log diariamente,
 * basandose en la fecha.
 */
public class DailyFileHandler extends FileHandler {
	private String pattern; // Patron utilizado para generar el nombre del archivo
	private Date currentDate; // La fecha actual, usada para determinar si el dia ha cambiado

	/**
	 * Constructor que inicializa el DailyFileHandler con un patron especifico.
	 *
	 * @param pattern El patron utilizado para nombrar los archivos de log.
	 * @throws IOException       Si ocurre un error de I/O.
	 * @throws SecurityException Si un SecurityManager esta presente y niega el
	 *                           acceso.
	 */
	public DailyFileHandler(String pattern) throws IOException, SecurityException {
		// invoca al constructor de FileHandler con el patron proporcionado y append
		// como true
		super(pattern, true);
		this.pattern = pattern;
		// inicializa la fecha actual
		this.currentDate = new Date();
	}

	/**
	 * Publica el registro de log proporcionado. Verifica si ha cambiado el dia y,
	 * de ser asi, rota el archivo de log.
	 *
	 * @param record El registro de log a publicar.
	 */
	@Override
	public synchronized void publish(LogRecord record) {
		// obtiene la fecha actual
		Date now = new Date();
		// verifica si el dia ha cambiado
		if (isDifferentDay(now)) {
			try {
				// cierra el archivo de log actual
				close();
				// actualiza la fecha actual
				currentDate = now;
				// invoca al metodo close de FileHandler
				super.close();
				// genera el nuevo patron con la fecha actual
				String newPattern = generatePattern(now);
				// establece el nuevo archivo de log
				super.setOutputStream(new FileOutputStream(newPattern, true));
			} catch (IOException e) {
				// registra el error
				LoggerManager.logError("Error: " + e.getMessage());
			}
		}
		// publica el registro de log en el archivo
		super.publish(record);
	}

	/**
	 * Verifica si la fecha proporcionada es diferente a la fecha actual.
	 *
	 * @param now La fecha a verificar.
	 * @return true si la fecha proporcionada es diferente a la fecha actual, false
	 *         de lo contrario.
	 */
	private boolean isDifferentDay(Date now) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		// compara las fechas formateadas
		return !sdf.format(now).equals(sdf.format(currentDate));
	}

	/**
	 * Genera el patron para el nombre del archivo de log basado en la fecha
	 * proporcionada.
	 *
	 * @param date La fecha utilizada para generar el patron.
	 * @return El patron generado.
	 */
	private String generatePattern(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// reemplaza el patron con la fecha formateada
		return pattern.replace("%g", sdf.format(date));
	}
}
