import javax.management.relation.RoleUnresolved;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;

public class MultiConnectionServer implements Runnable {
    protected ServerSocket server = null;
    protected boolean isStopped = false;

    public MultiConnectionServer() {
        try {
            server = new ServerSocket(65000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Starting the server ...");
        MultiConnectionServer server = new MultiConnectionServer();
        new Thread(server).start();
    }

    @Override
    public void run() {
        while (!isStopped) {
            Socket client = null;
            try {
                client = server.accept();
                new Thread(new ClientRunnable(client, "foo")).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ClientRunnable implements Runnable {
        protected Socket clientSocket = null;
        protected String serverText;

        public ClientRunnable(Socket client, String text) {
            clientSocket = client;
            serverText = text;
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();
                DataInputStream dis = new DataInputStream(inputStream);
                // read data
                int av;
                while ((av = dis.available()) == 0){}
                byte[] resbuf = new byte[av];
                dis.read(resbuf, 0, av);
                System.out.println(Base64.getEncoder().encodeToString(resbuf));
                // write response
                outputStream.write(("Hello to server with text: " + serverText).getBytes());
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
