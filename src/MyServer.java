import java.io.*;
import java.net.*;
import java.util.Scanner;

public class MyServer {
    public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    public static void main(String[] args){
        try{
            //int[] buffer = new int[] {0x11223344, 0x11335577, 0x44332211, 0x77553311};
            byte[] buffer = new byte[] {0x11, 0x22, 0x33, 0x44, 0x11, 0x33, 0x55, 0x77, 0x44, 0x33, 0x22, 0x11, 0x77, 0x55, 0x33, 0x11};
            ServerSocket ss=new ServerSocket(65000);
            Socket s=ss.accept();//establishes connection
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            Scanner in = new Scanner(s.getInputStream());
            String  str = in.nextLine();
            System.out.println("message= "+str);
//            try (Writer wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
//                wr.write("Foo hello");
//                wr.flush();
//            }
            byte[] arr = new byte[16];
            //for (int i = 0; i < buffer.length; i++) {
                dos.write(buffer, 0, 16);
            //
            dos.flush();
            //dos.writeBytes("Hello from server");
            //dos.flush();
            System.out.println("Sent message to client");

            // receive data from the client
            int av;
            while ((av = dis.available()) == 0){}
            byte[] resbuf = new byte[av];
            dis.read(resbuf, 0, av);
            for (int j = 0; j < av/4; j++) {
                for (int k = 0; k < 4; k++) {
                    System.out.print(String.format("%x", resbuf[j*4+k]));
                }
                System.out.println();
            }
            ss.close();
        }catch(Exception e){System.out.println(e);}
    }
}  