package ar.edu.et32.FlujoDatos;

import java.io.BufferedReader;

public class Lecturas {
	 
	
	/* Canales de comunicacion Standard de una APP/OS
	 * Salida -> OUT
	 * Entrada -> in
	 * Errores -> ERR
	 * 
	 * System.OUT.println(); OUT
	 * Scanner sc = new Scanner (System.in);
	 * try{
	 *  //aca codigo encerrado con posible error
	 * }catch( tipoERrror ){
	 *  //como resuelvo
	 *  er.printStackTrace //muestran el error que hubo
	 *  logger
	 * }
	 */
	
	private BufferedReader lector;
	
	public Lecturas() {
		//      Buffered(almacenamiento) -> (algo que une) -> consola(canal)
		lector = new BufferedReader( new InputStreamReader(System.in) );
	}
	
	
}
