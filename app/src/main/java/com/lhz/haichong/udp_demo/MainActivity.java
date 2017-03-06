package com.lhz.haichong.udp_demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int DEFAULT_PORT = 60000;
    private Button startBroadcast, stopBroadcast;
    private EditText send_message, receive_message;
    private String ssid, pass;
    private boolean start = false;
    private MulticastSocket ms = null;
    private DatagramPacket dp;
    private Socket socket = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        startBroadcast.setOnClickListener(this);
        stopBroadcast.setOnClickListener(this);

    }

    private void init() {
        startBroadcast = (Button) findViewById(R.id.StartBroadcast);
        stopBroadcast = (Button) findViewById(R.id.StopBroadcast);
        send_message = (EditText) findViewById(R.id.send_message);
        receive_message = (EditText) findViewById(R.id.receive_message);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.StartBroadcast:
                start = true;
                startBroadcast.setEnabled(false);
                stopBroadcast.setEnabled(true);
                ssid = send_message.getText().toString();
                pass = receive_message.getText().toString();
                //System.out.println("-----------------ssid: " + ssid );
                //System.out.println("-----------------pass: " + pass );
                new Thread(new sendBroadcast()).start();
                //new sendBroadcast(send_message.getText().toString()).run();
                break;
            case R.id.StopBroadcast:
                start = false;
                startBroadcast.setEnabled(true);
                stopBroadcast.setEnabled(false);
                break;
            default:
                break;

        }
    }

    private class sendBroadcast implements Runnable {
        InetAddress group = null;
        DatagramPacket dj = null;
        MulticastSocket sender = null;
        //byte[] data = new byte[100];
        byte[] data = new Char_Split(ssid, pass).construct();

        sendBroadcast() {
            //this.data = message.getBytes();
        }

        @Override
        public void run() {
            try {
                sender = new MulticastSocket();
                group = InetAddress.getByName("192.168.213.128");
                dj = new DatagramPacket(data, data.length, group, DEFAULT_PORT);

                while (start) {
                    sender.send(dj);
                    Thread.sleep(1000);
                }

                sender.close();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class receiveBroadcast implements Runnable {


        @Override
        public void run() {
            byte[] data = new byte[1024];
            try {
                InetAddress groupAddress = InetAddress.getByName("255.255.255.255");
                ms = new MulticastSocket(DEFAULT_PORT);
                ms.joinGroup(groupAddress);
            } catch (Exception e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    dp = new DatagramPacket(data, data.length);
                    if (ms != null)
                        ms.receive(dp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (dp.getAddress() != null) {
                    final String quest_ip = dp.getAddress().toString();
                    /* 若udp包的ip地址 是 本机的ip地址的话，丢掉这个包(不处理)*/
                    //String host_ip = getLocalIPAddress();
                    String host_ip = getLocalHostIp();
                    System.out.println("host_ip:  --------------------  " + host_ip);
                    System.out.println("quest_ip: --------------------  " + quest_ip.substring(1));
                    if ((!host_ip.equals("")) && host_ip.equals(quest_ip.substring(1))) {
                        continue;
                    }
                    final String codeString = new String(data, 0, dp.getLength());
                    receive_message.post(new Runnable() {
                        @Override
                        public void run() {
                            receive_message.append("收到来自: \n" + quest_ip.substring(1) + "\n" + "的udp请求\n");
                            receive_message.append("请求内容: " + codeString + "\n\n");
                        }
                    });
                    try {
                        final String target_ip = dp.getAddress().toString().substring(1);
                        send_message.post(new Runnable() {
                            @Override
                            public void run() {
                                send_message.append("发送tcp请求到: \n" + target_ip + "\n");
                            }
                        });
                        socket = new Socket(target_ip, 8080);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (socket != null)
                                socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /* 接收tcp连接
    private class tcpReceive extends  Thread {
        ServerSocket serverSocket;
        Socket socket;
        BufferedReader in;
        String source_address;
        @Override
        public void run() {
            while(true) {
                serverSocket = null;
                socket = null;
                in = null;
                try {
                    Log.i("Tcp Receive"," new ServerSocket ++++++++++");
                    serverSocket = new ServerSocket(8080);
                    socket = serverSocket.accept();
                    Log.i("Tcp Receive"," get socket ++++++++++++++++");
                    if(socket != null) {
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        sb.append(socket.getInetAddress().getHostAddress());
                        String line = null;
                        while ((line = in.readLine()) != null ) {
                            sb.append(line);
                        }
                        source_address = sb.toString().trim();
                        receive_message.post(new Runnable() {
                            @Override
                            public void run() {
                                receive_message.append("收到来自: "+"\n" +source_address+"\n"+"的tcp请求\n\n");
                            }
                        });
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally {
                    try {
                        if (in != null)
                            in.close();
                        if (socket != null)
                            socket.close();
                        if (serverSocket != null)
                            serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }*/
    public String getLocalHostIp() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()) {
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("getLocalHostIp", "获取本地ip地址失败");
            e.printStackTrace();
        }
        return ipaddress;
    }

    /*private String getLocalIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("123", ex.toString());
        }
        return null;
    }*/
    // 按下返回键时，关闭 多播socket ms
    @Override
    public void onBackPressed() {
        ms.close();
        super.onBackPressed();
    }

}

