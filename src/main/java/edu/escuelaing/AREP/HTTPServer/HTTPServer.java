package edu.escuelaing.AREP.HTTPServer;

import com.sun.xml.internal.ws.api.ha.StickyFeature;

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
    private static String selectedFunction = "cos";
    private static boolean numero1;
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

    private static Double funcionPi (String numero){
        Double pi= 1.0;
        String[] listnum;
        String number = numero;
        Double ans=0.0;
        if(numero.contains("π")) {
            if(numero.length()>1){
                listnum=numero.trim().split("π");
                number = listnum[0];
                pi=Math.PI;
                ans=Double.valueOf(number)*pi;
            }
            else{
                ans=Math.PI;
            }

        }
        return ans;
    }
    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 35000; //returns default port if heroku-port isn't set(i.e. on localhost)
    }



    private static Double TrigCalculator (String numero1,String numero2){
        System.out.println(numero1+" "+numero2);
        Double funpi1= funcionPi(numero1);
        Double funpi2= funcionPi(numero2);
        Double answer=0.0;
        if(selectedFunction.equals("sin")){
            answer=Math.sin(funpi1/funpi2);
        }
        else if(selectedFunction.equals("cos")){
            answer=Math.cos(funpi1/funpi2);
        }
        else if(selectedFunction.equals("tan")){
            answer=Math.tan(funpi1/funpi2);
        }
        return answer;
    }

    public  void processRequest(Socket clientSocket) throws IOException{
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        String inputLine, outputLine="";
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
                System.out.println(path);

            } else{

                System.out.println("path"+path);

                if(path.contains("/calculadora.html") && path.contains("number")){
                    Double Answer = 0.0;
                    outputLine="";
                    ;switch (inputLine){
                        case "fun:sin":
                            selectedFunction = "sin";
                            break;
                        case "fun:cos":
                            selectedFunction = "cos";
                            break;
                        case "fun:tan":
                            selectedFunction = "tan";
                            break;
                        default:
                            int pi = -1;
                            int div = -1;
                            String[] values = new String[0];
                            if(inputLine.contains("/")){
                                values= inputLine.trim().split("/");
                                Answer = TrigCalculator(values[0],values[1]);

                            }
                            else{
                                Answer = TrigCalculator(inputLine.trim());
                            }



                            outputLine = "Respuesta "+inputLine +" :" + Answer;


                    }
                }
                System.out.println("header: "+inputLine);
                //System.out.println("outpusadaskhdbaskdbhkasbdiasbdkjbaskdbaksdbaskdb: "+inputLine);
                headers.add(inputLine);
            }
            System.out.println("Received: " + inputLine);
            if (!in.ready()) {
                break;
            }
        }

        System.out.println(outputLine);
        String responseMessage = createResponse(path);
        out.println(responseMessage);

        out.close();

        in.close();

        clientSocket.close();
    }
    private static Double TrigCalculator (String num){
        Double pi= funcionPi(num);
        Double answer=0.0;
        if(selectedFunction.equals("sin")){
            answer=Math.sin(pi);
        }
        else if(selectedFunction.equals("cos")){
            answer=Math.cos(pi);
        }
        else if(selectedFunction.equals("tan")){
            System.out.println(pi);
            System.out.println(Math.tan(pi));
            answer=Math.tan(pi);
        }
        return answer;
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