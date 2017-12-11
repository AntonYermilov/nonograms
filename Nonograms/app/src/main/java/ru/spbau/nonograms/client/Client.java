package ru.spbau.nonograms.client;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

class Client {

    private static final String host = "eranik.me";
    private static final int port = 5222;

    private static final int BUFFER_SIZE = 2048;
    private static byte[] buffer = new byte[BUFFER_SIZE];

    /**
     * Sends data to server. Returns the response from server.
     * @param data data to be sent
     * @return response from server
     */
    public static String send(String data) {
        try {
            Socket socket = new Socket(host, port);
            sendData(socket, data);
            String response = receiveResponse(socket);
            socket.close();
            return response;
        } catch (IOException err) {
            err.printStackTrace();
            Log.e("Client::send",  err.getMessage(), err);
            return null;
        }
    }

    private static void sendData(Socket socket, String data) throws IOException {
        OutputStream out = socket.getOutputStream();
        out.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                .putInt(data.length()).array());
        out.write(data.getBytes(StandardCharsets.UTF_8));
        Log.i("Client::sendData", "Data was successfully sent to server");
    }

    private static String receiveResponse(Socket socket) throws IOException {
        InputStream in = socket.getInputStream();

        int bytesRead = in.read(buffer, 0, 4);
        int length = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getInt();

        ByteArrayOutputStream response = new ByteArrayOutputStream();
        while (bytesRead != -1 && length != 0) {
            bytesRead = in.read(buffer, 0, Math.min(length, BUFFER_SIZE));
            if (bytesRead != -1) {
                length -= bytesRead;
                response.write(buffer, 0, bytesRead);
            }
        }

        if (length != 0) {
            Log.e("Client::receiveResponse", "Could not read all data");
            return null;
        }
        Log.i("Client::receiveResponse", "Response was successfully received from server");

        return new String(response.toByteArray(), "UTF-8");
    }

}
