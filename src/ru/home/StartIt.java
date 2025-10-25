/**
 * Echo-клиент
 * 
 * Данное приложение представляет собой echo-клиент,
 * работающий по стандарту RFC #862 ("The Echo
 * Protocol").
 * 
 * @author Ефремов А. В., 21.10.2024
 */


package ru.home;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import ru.home.networking.NetworkClient;

public class StartIt {

  static {
    System.out.println("********************");
    System.out.println("Клиент Shouty");
    System.out.println("********************");
  }

  private static String host = null;
  private static int port = 0;

  private static String getLine() throws IOException {
    return new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8)).readLine().trim();
  }

  public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
    boolean isRunning = true;
    String cmdToServer = "help";
    System.out.print("Введите хост сервера Shouty (IP или домен)> ");
    host = getLine().toLowerCase();
    if (! host.isEmpty()) {
      System.out.print("Введите порт сервера Shouty (1 - 65535)> ");
      try {
        port = Integer.parseInt(getLine());
      }
      catch (NumberFormatException e) {}
      if ((port <= 0) || (port > 65535)) {
        System.err.println("Порт задан неверно.");
        isRunning = false;
      }
    } else {
      System.out.println("Хост не задан - значит, происходит выход из программы.");
      isRunning = false;
    }
    while (isRunning) {
      System.out.print("Введите команду для сервера (пусто - выход)> ");
      cmdToServer = getLine();
      if (! cmdToServer.isEmpty()) {
        System.out.println("Работа с сервером...");
        NetworkClient objNetworkClient = new NetworkClient(host, port, cmdToServer);
        System.out.println("Ответ с сервера: " + String.valueOf(objNetworkClient.msgFromServer.length) + " строк.");
      } else {
        System.out.println("Введена пустая строка.");
        isRunning = false;
      }
    }
    System.out.println("Работа программы завершена.");
    System.out.println(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Calendar.getInstance().getTime()));
  }

}
