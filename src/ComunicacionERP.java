import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * La clase ComunicacionERP se encarga de la interaccion con un sistema ERP.
 * Posee metodos para construir una URL de solicitud al ERP, enviar una
 * solicitud al ERP, y procesar la respuesta del ERP. La comunicacion se realiza
 * a traves de peticiones HTTP.
 *
 */
public class ComunicacionERP {

	/**
	 * Envia la informacion al ERP con los datos del paquete usando la URL y recibe
	 * de respuesta un Paquete como objeto.
	 *
	 * @param paquete El paquete que contiene la informacion a enviar al ERP.
	 * @return Objeto Paquete con la informacion de respuesta del ERP.
	 */
	public static Paquete enviarAlERP(Paquete paquete) {
		// Construye la URL para la peticion al ERP.
		String requestURL = construirURL(paquete);

		// Realiza la peticion al ERP y obtiene la respuesta.
		String response = intercambiarInformacion(requestURL);
		LoggerManager.logInfo("Esperando respuesta del ERP...");
		LoggerManager.logInfo("Respuesta del ERP: " + response + ".");
		LoggerManager.logInfo("Esperando nueva lectura...");

		// Asigna la salida del ERP al paquete y retorna el paquete actualizado.
		if (!response.isEmpty()) {
			paquete.asignarSalida(response);
		} else {
			LoggerManager.logWarning("No se ha conseguido respuesta del ERP...");
			LoggerManager.logInfo("Esperando nueva lectura...");
		}

		return paquete;
	}

	/**
	 * Construye la URL con los parametros necesarios para enviar al ERP.
	 *
	 * @param paquete El paquete que contiene la informacion a incluir en la URL.
	 * @return String representando la URL completa.
	 */
	public static String construirURL(Paquete paquete) {
		String base = Globals.ERPUrl;
		StringBuilder url = new StringBuilder(base);

		// Incorpora los parametros necesarios a la URL.
		url.append("?id=").append(paquete.getnID());

		// Contar cuantos codigos no son null para ajustar el numero total de codigos.
		int countNotNull = 0;
		for (String codigo : paquete.getCodigos()) {
			if (codigo != null && !codigo.isEmpty()) {
		        countNotNull++;
		    }
		}
		// Incorpora el parametro del numero de codigos no nulos a la URL.
		url.append("&num_de_codigos=").append(countNotNull);

		// Inicializa un indice para los codigos actualmente en procesamiento.
		int actualCodeIndex = 1;

		for (int i = 0; i < paquete.getCodigos().length; i++) {
			// Si el codigo no es nulo, incorpora el codigo como un parametro a la URL.
			if (paquete.getCodigos()[i] != null && !paquete.getCodigos()[i].isEmpty()) {
				url.append("&codigo_").append(actualCodeIndex).append("=").append(paquete.getCodigos()[i]);
				actualCodeIndex++;
			}
		}

		if(paquete.getnTarget() == null || paquete.getnTarget().isEmpty()) {
			paquete.setnTarget("0");
		}

		// Incorpora el parametro de la rampa provisional a la URL.
		url.append("&rampa_provisional=").append(paquete.getnTarget());

		LoggerManager.logInfo("Peticion al ERP: " + url.toString());

		// Retorna la URL construida como una cadena.
		return url.toString();

	}

	/**
	 * Intercambia informacion con el ERP enviando una peticion GET a la URL
	 * proporcionada y recogiendo la respuesta.
	 *
	 * @param requestURL URL completa para la peticion.
	 * @return String con la respuesta del ERP.
	 */
	private static String intercambiarInformacion(String requestURL) {
		String responseStr = "";
		HttpURLConnection con = null;
		try {
			URL url = new URL(requestURL);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			// Leer la respuesta del ERP.
			try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
				StringBuilder response = new StringBuilder();
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
				responseStr = response.toString();
			} catch (Exception e) {
				LoggerManager.logError("Error: " + e.getMessage());
			}
		} catch (Exception e) {
			LoggerManager.logError("Error: " + e.getMessage());
		} finally {
			if (con != null) {
				con.disconnect(); // Cierra la conexion
			}
		}
		return responseStr;
	}
}
