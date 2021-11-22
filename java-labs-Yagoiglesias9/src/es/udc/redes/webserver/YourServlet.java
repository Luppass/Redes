package es.udc.redes.webserver;
import java.util.Map;

/**
 * This class must be filled to complete servlets option (.do requests).
 */
public class YourServlet implements MiniServlet {
	

	public YourServlet(){

	}
        @Override
	public String doGet (Map<String, String> parameters){
			String name = parameters.get("name");
			String firstSurname = parameters.get("firstSurname");
			String secondSurname = parameters.get("secondSurname");
			String fullName = name + " " + firstSurname + " " + secondSurname;

			return printHeader() + printBody(fullName) + printEnd();

	}	

	private String printHeader() {
		return "<html><head> <title>Greetings</title> </head> ";
	}


	private String printBody(String message) {
		return "<body> <h1> Hola " + message + "</h1></body>";
	}

	private String printEnd() {
		return "</html>";
	}
}
