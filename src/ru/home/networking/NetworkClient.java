package ru.home.networking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class NetworkClient {

  private String host;
  private int port;
  private String cmdToServer; // запрос на сервер

  public String[] msgFromServer = null; // ответ с сервера

  public NetworkClient(String host, int port, String cmdToServer) throws UnknownHostException, IOException, InterruptedException {
    if (host.trim().isEmpty()) {
      throw new UnknownHostException("The host cannot be empty");
    }
    if ((port <= 0) || (port > 65535)) {
      throw new UnknownHostException("The port should be a natural number between 1 and 65535.");
    }
    this.host = host.trim().toLowerCase();
    this.port = port;
    this.cmdToServer = cmdToServer.trim();
    writeAndRead();
  }

  private String getCurrentTime() {
    return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
  }

  private void printMessage(String msg) {
    System.out.println("[" + getCurrentTime() + "] >> " + msg.trim());
  }

  /**
   * Отправка на сервер запроса и получение с сервера ответа
   * 
   * @throws UnknownHostException
   * @throws IOException
   * @throws InterruptedException 
   */
  private void writeAndRead() throws UnknownHostException, IOException, InterruptedException { // http://habr.com/ru/articles/330676/
    final long TIMEOUT = 2000; // пауза (в миллисекундах) для готовности сервера к работе
    final Charset codepage = StandardCharsets.UTF_8;
    printMessage("Соединение с сервером...");
    try (
        Socket socket = new Socket();
        ) {
      socket.connect(new InetSocketAddress(this.host, this.port));
      socket.setSoTimeout(10000);
      printMessage("Соединение установлено.");
      Thread.sleep(TIMEOUT);
      if ((socket.isConnected()) && (! socket.isOutputShutdown()) && (! socket.isInputShutdown())) { // проверяем, живой ли канал, и работаем, если живой
        try (
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), codepage)), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), codepage));
            ) {
          printMessage("Отправка запроса на сервер...");
          out.println(this.cmdToServer);
          out.flush();
          printMessage("Запрос на сервер отправлен.");
          Thread.sleep(TIMEOUT);
          ArrayList<String> lst_obj = new ArrayList<String>();
          if (in.ready()) { // данные для чтения от сервера подоспели?
            printMessage("Чтение ответа от сервера...");
            String line01 = new String();
            while ((in.ready()) && ((line01 = in.readLine().trim()) != null)) {
              lst_obj.add(line01);
              System.out.println("\"" + line01 + "\"");
            }
            printMessage("Чтение ответа от сервера завершено.");
            this.msgFromServer = lst_obj.toArray(new String[lst_obj.size()]); // dumping into a normal array
            printMessage("Ok.");
          } else {
            printMessage("Ответ от сервера не пришёл.");
            this.msgFromServer = new String[] {};
          }
        }
      }
    }
  }

}
