/**
 * La clase Globals proporciona un almacenamiento centralizado para las
 * constantes y las variables globales que se utilizan a lo largo de la
 * aplicacion. Estas incluyen las credenciales de la base de datos, URLs, y
 * otras configuraciones relevantes. Al almacenar estos valores en una clase
 * central, se facilita la gestion y la actualizacion de estas configuraciones
 * en toda la aplicacion.
 */
public class Globals {

	// URL de la base de datos.
	public static String DatabaseUrl = "jdbc:mysql://127.0.0.1:3306/decantalo";

	// Nombre de usuario para la base de datos.
	public static String DatabaseUsername = "root";

	// Password para la base de datos.
	public static String DatabasePassword = "root";

	// ID del paquete en curso.
	public static long IdPaqueteEnCurso = 0;

	// Version de la aplicacion.
	public static String Version = "V1.0";

	// URL del ERP.
	public static String ERPUrl = "https://erp.decantalo.com/sorter/rampa";

	// Contador de ejecucion.
	public static int ContadorEjecucion = 0;

	// Estado de la conexion anterior.
	public static Boolean EstadoConexionAnterior = null;

	// URL de la API de Robomap.
	//public static String ApiRobomapURL = "http://20.240.251.235:8000/api/";

	// Token de Robomap.
	//public static String TokenRobomap = null;

	// Email para la autenticacion en Robomap.
	//public static String EmailRobomap = "eduard.lecha@outlook.com";

	// Password para la autenticacion en Robomap.
	//public static String PasswordRobomap = "Eduard2793";

	// Coordenadas del dispositivo
	//public static String CoordenadasDispositivo = "lat:41.4889077,lon:2.0835688";

	// Incidencia guardada por si no se puede enviar a Robomap por algun motivo
	public static boolean HayIncidenciaPendiente = false;
	public static String UrlIncidenciaRobomapParaEnviar = null;
	public static String IncidenciaRobomapParaEnviar = null;
}
