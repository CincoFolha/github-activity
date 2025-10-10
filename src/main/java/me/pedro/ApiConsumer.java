package me.pedro;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiConsumer {

  private static final String GITHUB_API_URL = "https://api.github.com/users/%s/events";
  
  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Usage: java ApiConsumer <username>");
      return;
    }

    String username = args[0];
    try {
      JSONArray jsonEvents = fetchGithubEvents(username);
      displayEvents(jsonEvents);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static JSONArray fetchGithubEvents(String userName) throws Exception {
    String apiUrl = String.format(GITHUB_API_URL, userName);
    URL url = new URL(apiUrl);

    HttpURLConnection conn = (HrrpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    conn.setRequestProperty("Accept", "application/json");
    
    int responseCode = conn.getResponseCode();
    if (responseCode != HttpURLConnection.HTTP_OK) {
      throw new IOExceoption("Erro HTTP: " + responseCode);
    }

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
      StringBuilder response = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        response.append(line);
      }
      return new JSONArray(response.toString());
    } finally {
      conn.disconnect();
    }
  }

  private static void displayEvents(JSONArray events) {
    if (events.isEmpty()) {
      System.out.println("Nenhum evento encontrado.");
      return;
    }

    System.out.println("\nAtividades recentes:");
    for (int i = 0; i < events.length(); i++) {
      JSONObject event = events.getJSONObject(i);
      System.out.println("- " + formatEvent(event));
    }
  }

  private static String formatEvent(JSONObject event) {
    String type = event.optString("type", "UnknownEvent");
    JSONObject payload = event.optJSONObject("payload");
    JSONObject repo = event.optJSONObject("repo");

    String repoName = repo != null ? repo.optString("name", "desconhecido") : "desconhecido";

    return switch (type) {
      case "PushEvent" -> {
        int commits = payload != null && payload.has("commits")
          ? payload.getJSONArray("commits").length()
          : 0;
        yield String.format("Pushed %d commit(s) para %s", commits, repoName);
        }
      case "IssuesEvent" -> "Interagiu com issues em " + repoName;
      case "WatchEvent" -> "Deu estrela em " + repoName;
      case "ForkEvent" -> "Fez fork de " + repoName;
      case "CreateEvent" -> {
        String refType = payload != null ? payload.optString("ref_type", "recurso") : "recurso";
        yield String.format("Criou %s em %s", refType, repoName);
      }
      default -> return String.format("%s em %s", type.replace("Event", ""), repoName);
    };
  }
}
