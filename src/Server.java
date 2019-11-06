import java.net.*;
import java.util.HashMap;
import java.util.Map;

class Server {
    private int port;
    private String host;
    private static Map<ListeningServer, String> socketList = new HashMap<>();

    Server(String host, int port){
        this.host = host;
        this.port = port;
    }

    void server() throws Exception{
        InetAddress IpAdress = InetAddress.getByName(host);
        ServerSocket server = new ServerSocket(port, 0, IpAdress);
        try {
            try {
                while (true) {
                    Socket socket = server.accept();
                    try {
                       ListeningServer listeningServer = new ListeningServer(socket, (HashMap<ListeningServer, String>) socketList);
                       listeningServer.setListeningServer(listeningServer);
                    }
                    catch (Exception ex){
                        System.out.println("Закрываем сокет...");
                        socket.close();
                    }
                }
            }
            finally {
                System.out.println("Сервер зыкрыт.");
                server.close();
            }
        }
        catch (Exception ex){
            System.err.println(ex);
        }
    }
}