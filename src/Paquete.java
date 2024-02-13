import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * La clase Paquete representa un paquete que contiene informacion sobre un
 * item, como un conjunto de codigos, una orden, entre otros.
 */
public class Paquete {

	// Variables miembro para almacenar la informacion del paquete.
	long ID;
	Timestamp TimeStamp;
	String OriginalTarget;
	String Target;
	String[] codigos;
	String _Order;
	short nProcess;

	// Getters y Setters para acceder o modificar las variables miembro.
	public long getnID() {
		return ID;
	}

	public void setnID(long nID) {
		this.ID = nID;
	}

	public Timestamp getDtTimeStamp() {
		return TimeStamp != null ? TimeStamp : new Timestamp(System.currentTimeMillis());
	}

	public void setDtTimeStamp(Timestamp dtTimeStamp) {
		this.TimeStamp = dtTimeStamp;
	}

	public String getOriginal_Target() {
		return OriginalTarget;
	}

	public void setnOriginal_Target(String Original_Target) {
		this.OriginalTarget = Original_Target;
	}

	public String getnTarget() {
		return Target;
	}

	public void setnTarget(String nTarget) {
		this.Target = nTarget;
	}

	public String[] getCodigos() {
		if (this.codigos == null) {
			// Retorna un arreglo vacío si codigos es null.
			return new String[0];
		}
		// Filtra los elementos que no son null.
		// Crea un nuevo arreglo con los elementos filtrados.
		return Stream.of(this.codigos)
				.filter(codigo -> codigo != null)
				.toArray(String[]::new);
	}

	public void setCodigos(String[] codigos) {
		this.codigos = codigos;
	}

	public String getsOrder() {
		return _Order;
	}

	public void setsOrder(String sOrder) {
		this._Order = sOrder;
	}

	public short getnProcess() {
		return nProcess;
	}

	public void setnProcess(short nProcess) {
		this.nProcess = nProcess;
	}

	/**
	 * Metodo toString para mostrar la informacion del paquete bien formateada.
	 *
	 * @return Una cadena de texto que representa la informacion del paquete.
	 */
	@Override
	public String toString() {
		return "Paquete [ID=" + ID + ", TimeStamp=" + TimeStamp + "OriginalTarget=" + OriginalTarget + ", Target=" + Target + ", codigos="
				+ Arrays.toString(codigos) + ", Order=" + _Order + ", nProcess=" + nProcess + "]";
	}

	/**
	 * Metodo para leer un paquete desde un ResultSet, creando un nuevo objeto
	 * Paquete y llenando sus campos con la informacion obtenida.
	 *
	 * @param resultset El ResultSet desde el cual leer la informacion.
	 * @return Un objeto Paquete con la informacion leida.
	 */
	public static Paquete leerPaquete(ResultSet resultset) {
		Paquete paquete = new Paquete();
		paquete.codigos = new String[4];

		try {
			// Rellena los campos del objeto paquete con la informacion del ResultSet.
			paquete.ID = resultset.getLong("ID");
			paquete.TimeStamp = resultset.getTimestamp("TimeStamp");
			paquete.OriginalTarget = resultset.getString("OriginalTarget");
			paquete.Target = resultset.getString("Target");


			for (int i = 1; i <= 4; ++i) {
			    paquete.codigos[i - 1] = resultset.getString("Code_" + i);
			}

			paquete._Order = resultset.getString("_Order");
			paquete.nProcess = resultset.getShort("nProcess");
		} catch (SQLException sqlex) {
			// Manejo de la excepcion SQLException.
			LoggerManager.logError("Error: " + sqlex.getMessage());
			LoggerManager.logInfo("Cambiando el valor nProcess = 3 del paquete " + Globals.IdPaqueteEnCurso + ".");
			// Intenta cambiar el valor nProcess en caso de error.
			try {
				ComunicacionBBDD.cambiarValornProcessByID(Globals.DatabaseUrl, Globals.DatabaseUsername,
						Globals.DatabasePassword, 3, Globals.IdPaqueteEnCurso);
			} catch (ClassNotFoundException | SQLException e) {
				LoggerManager.logError("Error: " + e.getMessage());
			}
			LoggerManager.logError("Error: " + sqlex.getMessage());
		}
		LoggerManager.logInfo("Paquete leido -> " + paquete.toString() + ".");

		return paquete;
	}

	/**
	 * Metodo para asignar la salida del paquete a partir de una respuesta en
	 * formato JSON.
	 *
	 * @param response La respuesta en formato JSON que contiene la informacion para
	 *                 asignar la salida.
	 */
	public void asignarSalida(String response) {
	    // Crea un objeto JSONObject a partir de la respuesta en formato JSON.
	    JSONObject jsonObject = new JSONObject(response);

	    // Obtiene el array de números de rampa del objeto JSON.
	    JSONArray rampasArray = jsonObject.getJSONArray("numero_de_rampa");

	    // Convierte el JSONArray en una cadena de texto con los números separados por comas.
	    StringBuilder rampasString = new StringBuilder();
	    for (int i = 0; i < rampasArray.length(); i++) {
	        rampasString.append(rampasArray.getInt(i));
	        if (i < rampasArray.length() - 1) {
	            rampasString.append(", ");
	        }
	    }

	    // Asigna la cadena de números de rampa al campo Original_Target.
	    this.OriginalTarget = rampasString.toString();

	    // Restablecer la variable global IdPaqueteEnCurso.
	    Globals.IdPaqueteEnCurso = 0;
	}

}