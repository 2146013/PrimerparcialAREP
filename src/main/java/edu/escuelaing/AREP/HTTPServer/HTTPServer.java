package edu.escuelaing.AREP.HTTPServer;



import java.net.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

//solo pued haber una instancia de http server con singleton para que no haya mas instancias
public class HTTPServer {
    private static HTTPServer _instance = new HTTPServer();
    private HTTPServer(){

    }
    private static HTTPServer getInstance(){
        return _instance;
    }
    public static void main(String... args) throws IOException{
        HTTPServer.getInstance().startServer(args);
    }


    public  void startServer(String[] args) throws IOException {
        int port = getPort();
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: "+port);
            System.exit(1);
        }
        Socket clientSocket = null;
        boolean running = true;
        while (running){
            try {
                System.out.println("Listo para recibir en puerto ..."+port);
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            processRequest(clientSocket);
        }



        serverSocket.close();
    }

    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 35000; //returns default port if heroku-port isn't set(i.e. on localhost)
    }

    public  void processRequest(Socket clientSocket) throws IOException{
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        String inputLine, outputLine;
        String method="";
        String path = "";
        String version = "";
        List<String> headers = new ArrayList<String>();
        while ((inputLine = in.readLine()) != null) {
            if(method.isEmpty()){
                String[] requestStrings = inputLine.split(" ");
                method = requestStrings[0];
                path = requestStrings[1];
                version = requestStrings[2];
                System.out.println("reques: "+method +" "+ path + " "+ version);
            } else{
                System.out.println("header: "+inputLine);
                headers.add(inputLine);
            }
            System.out.println("Received: " + inputLine);
            if (!in.ready()) {
                break;
            }
        }


        String responseMessage = createResponse(path);
        out.println(responseMessage);

        out.close();

        in.close();

        clientSocket.close();
    }
    public String createResponse(String path){
        String type = "text/html";
        if(path.endsWith(".css")){
            type = "text/css";
        } else if(path.endsWith(".js") ){
            type = "text/javascript";
        }
        else if(path.endsWith(".jpeg")){
            type = "image/jpeg";
        }else if(path.endsWith(".png")){
            type = "image/png";
        }
        //para leer archivos
        Path file = Paths.get("./www"+path);
        Charset charset = Charset.forName("UTF-8");
        String outmsg ="";
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                outmsg = outmsg + line;
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: "+type+"\r\n"
                + "\r\n"+ outmsg;
    }
}