import java.io.BufferedReader;
import java.io.InputStreamReader;

public class main {
    public static void main(String[] args) throws Exception {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Введите хост: ");
        String host = input.readLine();
        System.out.print("Введите порт: ");
        int port = Integer.parseInt(input.readLine());
        Server server = new Server(host, port);
        server.server();
    }
}