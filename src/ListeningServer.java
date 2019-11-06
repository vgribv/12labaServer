import java.io.*;
import java.net.Socket;
import java.util.*;

public class ListeningServer extends Thread {
    private Socket socket;
    private String name;
    private BufferedWriter out;
    private BufferedReader in;
    private Map<ListeningServer,String> socketList;
    private ListeningServer listeningServer;

    ListeningServer(Socket socket, HashMap<ListeningServer, String> socketList) throws IOException {
        this.socket = socket;
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.socketList = socketList;
        start();
    }

    private void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        }
        catch (IOException ignored) {}
    }

    private void sender(String str, String toWhom, int userSelect){//1 - определенному пользователю, 2 - всем без текущего пользователя, 3 - всем
        boolean flag = false;
        for (Map.Entry<ListeningServer, String> entry : socketList.entrySet()) {
            ListeningServer key = (ListeningServer) ((Map.Entry) entry).getKey();
            String value = (String) ((Map.Entry) entry).getValue();
            switch (userSelect) {
                case 1 :
                    if (value.equals(toWhom)) key.send("(Вам)" + str);
                    if (!flag) System.out.println(toWhom + " только " + str);
                    flag = true;
                    break;
                case 2 :
                    if (!(key == listeningServer)) key.send(str);
                    if (!flag) System.out.println(str);
                    flag = true;
                    break;
                case 3 :
                    key.send(str);
                    if (!flag) System.out.println(str);
                    flag = true;
                    break;
            }
        }
    }

    void setListeningServer(ListeningServer listeningServer){
        this.listeningServer = listeningServer;
    }

    @Override
    public void run(){
        try {
            try {
                boolean flagConnection = false;
                String prevName = name = in.readLine();
                socketList.put(listeningServer, name);
                send("Добро пожаловать в чат, " + name + "!");
                sender(name + " вступил в чат!", null, 2);
                long millis = System.currentTimeMillis();

                while (true){
                    boolean flag = false;
                    if (in.ready()){
                        String str = in.readLine();
                        if (str.startsWith("@connectionCheck")){
                            flagConnection = false;
                            flag = true;
                        }
                        else if (str.startsWith("@name ")){
                            flag = true;
                            name = str.substring(6);
                            sender(prevName + " изменил имя на " + name, null,  2);
                            send("Имя успешно изменено.");
                            prevName = name;
                        }
                        else if (str.startsWith("@senduser ")){
                            String user = str.substring(10, str.indexOf(" ", 10));
                            str = str.substring(str.indexOf(" ", 10) + 1);
                            sender(name + ": " + str, user, 1);
                            flag = true;
                        }

                        if (!flag) sender(name + ": " + str, null, 2);
                        millis = System.currentTimeMillis();
                    }

                    if (!flagConnection && (millis + 2000 < System.currentTimeMillis())){
                        flagConnection = true;
                        send("@connectionCheck");
                    }
                    else if (millis + 5000 < System.currentTimeMillis()) this.closeService();
                }
            }
            catch (IOException ex){
                System.out.println(name + " покинул чат.");
                sender(name + " покинул чат.", null, 2);
                this.closeService();
            }
        }
        catch (Exception ex){
            System.err.println(ex);
        }
    }

    private void closeService(){
        try {
            if(!socket.isClosed()){
                socket.close();
                in.close();
                out.close();
            }
            for (ListeningServer vr : socketList.keySet()) {
                if(vr.equals(this)) vr.interrupt();
                socketList.remove(this);
            }
        }
        catch (Exception ignored){}
    }
}