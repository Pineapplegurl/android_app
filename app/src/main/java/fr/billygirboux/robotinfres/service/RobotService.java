package fr.billygirboux.robotinfres.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RobotService {

    private Context context;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private Handler handler;

    private String videoUrl = "https://picsum.photos/300/300";

    private RequestQueue requestQueue;
    private boolean retrieveVideoStarted;

    private RobotService() {

    }

    public RobotService(Handler handler, Context context) {
        this.handler = handler;
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public void connect(String ip, int port) {

        //videoUrl = "http://" + ip + ":" + port  + "/vueRobot.jpg";

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ip, port);
                    out = new PrintWriter(socket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    Message message = Message.obtain();
                    message.what = 1;

                    handler.sendMessage(message);

                } catch (IOException e) {

                    Message message = Message.obtain();
                    message.what = -1;

                    handler.sendMessage(message);
                }
            }
        };

        new Thread(runnable).start();
    }



    public void sendCommand(String command) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {

                    out.println(command);
                    String response = in.readLine();
                    resolveResponse(response);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(runnable).start();
    }


    private void resolveResponse(String response) {
        if (response.contains("TRAME_NOK")) {
            Message m1 = Message.obtain();
            m1.what = -2;
            handler.sendMessage(m1);
        }
        if (response.startsWith("DETECT")) {
            Message m2 = Message.obtain();
            m2.what = 3;
            m2.obj = response;
            handler.sendMessage(m2);
        }
    }

    public void retrieveVideo() {
        retrieveVideoStarted = true;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while(retrieveVideoStarted) {
                    ImageRequest imageRequest = new ImageRequest(
                            videoUrl,
                            new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap response) {
                                    if (retrieveVideoStarted) {
                                        Message message = Message.obtain();
                                        message.what = 2;
                                        message.obj = response;

                                        handler.sendMessage(message);
                                    }
                                }
                            },
                            0,
                            0,
                            ImageView.ScaleType.CENTER_CROP,
                            null,
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }
                    );

                    imageRequest.setShouldCache(false);

                    requestQueue.add(imageRequest);

                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        new Thread(runnable).start();

    }

    public void stopVideo() {
        retrieveVideoStarted = false;
    }



    public void retrieveDetectorsProx() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while(true) {
                    sendCommand("DETECT");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        new Thread(runnable).start();

    }

}
