package me.pedro;

import me.pedro.formatter.EventFormatter;
import me.pedro.service.GitHubService;
import org.json.JSONArray;

import java.io.IOException;

public class GitHubActivityConsumer {
  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Uso: java GitHubActivityConsumer <username>");
      System.exit(1);
    }

    String username = args[0];
    GitHubService service = new GitHubService();
    EventFormatter formatter = new EventFormatter();

    try {
      JSONArray events = service.fetchUserEvents(username);
      formatter.displayEvents(events);
    } catch (IOException e) {
      System.err.println("Erro ao buscar eventos: " + e.getMessage());
      System.exit(1);
    } catch (Exception e) {
      System.err.println("Erro inesperado: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }
}
