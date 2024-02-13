import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * La clase CustomFormatter personaliza el formato de los registros de log,
 * extendiendo la clase Formatter. Especifica como se deben formatear los
 * mensajes de log en la salida.
 *
 */
public class CustomFormatter extends Formatter {

	// DateFormat para formatear la fecha y hora en un formato especifico
	private static final DateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");

	/**
	 * Formatea el registro de log proporcionado.
	 *
	 * @param record el registro de log que sera formateado.
	 * @return la cadena formateada del registro de log.
	 */
	@Override
	public String format(LogRecord record) {
		StringBuilder builder = new StringBuilder(1000);
		// Incorpora la fecha y hora formateada
		builder.append(df.format(record.getMillis())).append(" - ");
		// Incorpora la clase y el metodo que generaron el log
		builder.append("[").append(record.getSourceClassName()).append(".");
		builder.append(record.getSourceMethodName()).append("] - ");
		// Incorpora el nivel del log
		builder.append("[").append(record.getLevel()).append("] - ");
		// Incorpora el mensaje del log
		builder.append(formatMessage(record));
		// Incorpora un salto de linea al final
		builder.append("\n");
		return builder.toString();
	}

	/**
	 * Retorna el encabezado de la secuencia de salida de registros de log. En este
	 * caso, se utiliza la implementacion predeterminada de la superclase.
	 *
	 * @param handler el manejador de log para el cual se esta generando el
	 *                encabezado.
	 * @return el encabezado como una cadena de texto.
	 */
	@Override
	public String getHead(java.util.logging.Handler handler) {
		return super.getHead(handler);
	}

	/**
	 * Retorna el pie de la secuencia de salida de registros de log. En este caso,
	 * se utiliza la implementacion predeterminada de la superclase.
	 *
	 * @param handler el manejador de log para el cual se esta generando el pie.
	 * @return el pie como una cadena de texto.
	 */
	@Override
	public String getTail(java.util.logging.Handler handler) {
		return super.getTail(handler);
	}
}
