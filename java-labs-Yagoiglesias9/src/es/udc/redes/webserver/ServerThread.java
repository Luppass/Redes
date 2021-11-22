package es.udc.redes.webserver;

import java.io.*;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static es.udc.redes.webserver.WebServer.requestedRes;

public class ServerThread extends Thread {

    private final Socket socket;
    private final boolean allow;
    File accessFile;
    private BufferedWriter accessLog;
    File errorFile;
    private BufferedWriter errorLog;

    public ServerThread(Socket socket, Properties config) throws IOException {
        this.socket = socket;
        allow = Boolean.parseBoolean(config.getProperty("ALLOW", "false"));

        accessFile = new File("log/access.log");
        if(!accessFile.exists()) accessFile.createNewFile();
        accessLog = new BufferedWriter(new FileWriter(accessFile, true));

        errorFile = new File("log/error.log");
        if(!errorFile.exists()) errorFile.createNewFile();
        errorLog = new BufferedWriter(new FileWriter(errorFile, true));
    }

    public void run() {
        try{
            handleRequest();
        }catch (Exception e){
            System.err.println("Error ocurred: " + e.getMessage());
        }
    }

    private synchronized void writeLog(String request, int code, long len) {

        StringBuilder message = new StringBuilder();
        BufferedWriter logFile = accessLog;
        if(code >= 400 && code < 600){
            logFile = errorLog;
        }

        message.append("REQUEST: ").append(request).append(". ");
        message.append("ClIENT IP: ").append(socket.getInetAddress()).append(". ");
        message.append("DATE: ").append(new Date()).append(". ");

        if(logFile == accessLog) {
            message.append("STATUS: ").append(code).append(". ");
            message.append("SIZE: ").append(len).append(". ");
        }

        if(logFile == errorLog) message.append("ERROR: ").append(code).append(". ");

        try {
            logFile.write(message.toString());
            logFile.newLine();
            logFile.flush();
        } catch (IOException e) {
            System.err.println("writeLog: Error: " + e.getMessage());
        }
    }

    private void handleRequest() throws Exception {
        InputStream input;
        OutputStream output;
        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();
            serverRequest(input, output);
            output.flush();
            input.close();
            output.close();

        } catch (Exception e){
            throw new Exception("Error: " + e.getMessage());
        }
    }

    private String ModifiedDate(long Modified) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",
                new Locale("EN", "ES"));
        return dateFormat.format(new Date(Modified));
    }

    private void serverRequest(InputStream input, OutputStream output) throws Exception {

        String line;
        int code;
        boolean directory = false;
        Properties prop = new Properties();
        prop.load(new FileInputStream("server.properties"));
        BufferedReader bf = new BufferedReader(new InputStreamReader(input));
        File notfound = new File("error/error404.html");
        File badrequest = new File("error/error400.html");

        line = bf.readLine();
        if (line != null) {
            File file;
            String[] filename = line.split(" ");

            if (filename[1].equals("/") || new File(filename[1].replace("/", "") + "/index.html").exists()) {
                filename[1] = prop.getProperty("DEFAULT_FILE");
            }


            String Archivo;
            Archivo = filename[1];
            String Version = filename[2];

            if (Archivo.length()>15) {
               file = new File(Archivo.replaceFirst("/",""));
            }else file = new File(Archivo.replace("/", ""));

            String HttpAndState;
            String Method = filename[0];
            WebServer.PrintPetitions(Archivo, Integer.parseInt(prop.getProperty("PORT")), filename);

            if ((Method.equals("GET") || Method.equals("HEAD")) && Version.equals("HTTP/1.0")
                    || Version.equals("HTTP/1.1") || Version.equals("HTTP/2.0")) {
                if (file.isDirectory() && !(new File(file + "/index.html").exists())){
                    HttpAndState = (Version + " 200 OK\n");
                    String CONTENT_TYPE = "Content-type: text/html\n\n";
                    String SERVER = "Server: WebServer_205\n";
                    String DATE = "Date: " + new Date() + "\n";
                    output.write((HttpAndState + DATE + SERVER + CONTENT_TYPE).getBytes());
                    AllowRequest(output, file);
                }
                if (file.exists() && allow){
                    HttpAndState = (Version + " 200 OK\n");
                    code = 200;
                } else {
                    HttpAndState = (Version + " 404 Not found\n");
                    code = 404;
                }
            } else {
                HttpAndState = (Version + " 400 Bad request\n");
                code = 400;
            }
                if (!file.isDirectory()) {
                    String CONTENT_TYPE;
                    String SERVER = "Server: WebServer_205\n";
                    String DATE = "Date: " + new Date() + "\n";
                    CONTENT_TYPE = "Content-type: " + URLConnection.guessContentTypeFromName(file.getName()) + "\n\n";

                    String LENGTH = "Content-Length: " + file.length() + "\n";
                    String HeaderNoBody = HttpAndState + SERVER + DATE + LENGTH + CONTENT_TYPE;

                    if (HttpAndState.equals(Version + " 200 OK\n")) {
                        String lastModified = "Last-Modified: " + ModifiedDate(file.lastModified()) + "\n";
                        String HeaderWithBody = HttpAndState + SERVER + DATE + lastModified + LENGTH + CONTENT_TYPE;
                        output.write(HeaderWithBody.getBytes());
                        if (!Method.equals("HEAD")) {
                            Files.copy(Paths.get(file.toString()), output);
                        }
                    } else if (HttpAndState.equals(Version + " 404 Not found\n")) {
                        output.write(HeaderNoBody.getBytes());
                        Files.copy(Paths.get(notfound.toString()), output);
                    } else if (HttpAndState.equals(Version + " 400 Bad request\n")) {
                        output.write(HeaderNoBody.getBytes());
                        Files.copy(Paths.get(file.toString()), output);
                    }
                }
                long len = file.length();
                writeLog(line, code, len);

        }
    }

        private void AllowRequest(OutputStream output, File file) throws IOException {

            File[] contenido = file.listFiles();
            StringBuilder html = new StringBuilder();
            html.append("<html>\n");
            html.append("<head>\n");
            html.append("\t<title> Allow </title>\n");
            html.append("</head>\n");
            html.append(" <body>\n");
            html.append("\t<h1>").append("Directorios y contenidos: ").append(file.getName())
                    .append("</a><br>").append("\n");
            assert contenido != null;
            for (File f : contenido){
                html.append("\t <a href=\"").append(f.toString()).
                        append("\"/>").append(f.getName()).append("</a><br>").append("\n");
            }
            html.append("</body>\n");
            html.append("</html>\n");
            output.write(html.toString().getBytes());
        }
    }


