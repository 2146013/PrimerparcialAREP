package edu.escuelaing.AREP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SenCos {
    private static String selectedFunction = "cos";
    private static boolean num1;
    private static Double checkPi (String num){
        Double pi= 1.0;
        String[] listnum;
        String number = num;
        Double ans=0.0;
        if(num.contains("π")) {
            if(num.length()>1){
                listnum=num.trim().split("π");
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
    private static Double calculateFun (String num1,String num2){
        System.out.println(num1+" "+num2);
        Double pi1= checkPi(num1);
        Double pi2= checkPi(num2);
        Double answer=0.0;
        if(selectedFunction.equals("sin")){
            answer=Math.sin(pi1/pi2);
        }
        else if(selectedFunction.equals("cos")){
            answer=Math.cos(pi1/pi2);
        }
        else if(selectedFunction.equals("tan")){
            answer=Math.tan(pi1/pi2);
        }
        return answer;
    }
    private static Double calculateFun (String num){
        Double pi= checkPi(num);
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

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35001);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35001.");
            System.exit(1);
        }
        Socket clientSocket = null;
        try {
            System.out.println("iniciando puerto");
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        clientSocket.getInputStream()));
        String inputLine, outputLine;
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Mensaje recibido: " + inputLine);
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
                        Answer = calculateFun(values[0],values[1]);

                    }
                    else{
                        Answer = calculateFun(inputLine.trim());
                    }



                    outputLine = "Respuesta "+inputLine +" :" + Answer;

            }


            out.println(outputLine);
            if (outputLine.equals("Bye.")) break;
        }
        System.out.println("cerrando server");
        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }
}
