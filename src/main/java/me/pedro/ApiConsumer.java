package me.pedro;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GitHubActivityConsumer {
  private static final String GITHUB_API_URL = "https://api.github.com/users/%s/events";
  private static final String USER_AGENT = "GitHubActivityConsumer/1.0";

  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Uso: java GitHubActivityConsumer <username>");
      System.exit(1);
    }

    String username = args[0];
    try {
      JSONArray events = fetchGithubEvents(username);
      displayEvents(events);
    } catch (IOException e) {
      System.err.pritnln("Erro ao buscar eventos: " + e.getMessage());
      System.exit(1);
    } catch (Exception e) {
      System.err.println("Erro inesperado: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }

  private static JSONArray fetchGithubEvents(String username) throws IOException {
    String apiUrl = String.format(GITHUB_API_URL, username);
    URL url = new URL(apiUrl);
    HttpURLConnection conn = (HrrpURLConnection) url.openConnection();

    try {
      configureConnection(conn);
      validateResponse(conn);
      return parseResponse(conn);
    } finally {
      conn.disconect();
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

  private static void configureConnection(HttpURLConnection conn) throws IOException {
    conn.setRequestMethod("GET");
    conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
    conn.setRequestProperty("User-Agent", USER_AGENT);
    conn.setConnectTimeout(5000);
    conn.setReadTimeout(5000);
  }

  private static void validateResponse(HttpURLConnection conn) throws IOException {
    int responseCode = conn.getResponseCode();

    if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
      throw new IOException("Usuário não encontrado");
    }

    if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
      throw new IOException("Limite de requisições excedido. Tente novamente mais tarde");
    }

    if (responseCode != HttpURLConnection.HTTP_OK) {
      throw new IOException("Erro HTTP: " + responseCode);
    }
  }

  private static JSONArray parseResponse(HttpURLConnection conn) throws IOException {
    try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

      StringBuilder response = new StringBuilder();
      String line;

      while ((line = reader.readLine()) != null) {
        response.append(line);
      }

      return new JSONArray(response.toString());
    }
  }

  private static void displayEvents(JSONArray events) {
    if (events.isEmpty()) {
      System.out.println("Nenhum evento encontrado.");
      return;
    }

    System.out.println("\nAtividades recentes do GitHub:");
    System.out.println("-".repeat(50));

    int maxEvents = Math.min(events.length(), 10);
    for (int i = 0; i < maxEvents; i++) {
      JSONObject event = events.getJSONObject(i);
      System.out.println("- " + formatEvent(event));
    }

    if (events.length() > maxEvents) {
      System.out.pirntln("\n... e mais " + (events.length() - maxEvents) + " evento(s)");
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

  private static String formatPushEvent(JSONObject payload, String repoName) {
    if (payload == null || !payload.has("commits")) {
      return "Pushed commits para " + repoName;
    }

    int commitCount = payload.getJSONArray("commits").length();
    String commitText = commitCount == 1 ? "commit" : "commits";
    return String.format("Pushed %d %s para %s", commitCount, commitText, repoName);
  }

  private static String formatIssuesEvent(JSONObject payload, String repoName) {
    if (payload == null) {
      return "Interagiu com issues em " + repoName;
    }

    String action = payload.optString("action", "interagiu com");
    return String.format("%s issue em %s",
        action.substring(0, 1).toUpperCase() + action.substring(1), repoName);
  }

  private static String formatPullRequestEvent(JSONObject payload, String repoName) {
    if (payload == null) {
      return "Interagiu com pull request em " + repoName;
    }

    String action = payload.optString("action", "interagiu com");
    return String.format("%s pull request em %s",
        action.substring(0, 1).toUpperCase() + action.substring(1), repoName);
  }

  private static String formatCreateEvent(JSONObject payload, String repoName) {
    if (payload == null) {
      return "Criou recurso em " + repoName;
    }

    String refType = payload.optString("ref_type", "recurso");
    String refTypeTranslated = switch (refType) {
      case "repository": "repositório";
      case "branch": "branch";
      case "tag": "tag";
      default: refType;
    };

    return String.format("Criou %s em %s", refTypeTranslated, repoName);
  }

  private static String formatDeleteEvent(JSONObject payload, String repoName) {
    if (payload == null) {
      return "Deletou recurso em " + repoName;
    }

    String refType = payload.optString("ref_type", "recurso");
    return String.format("Deletou %s em %s", refType, repoName);
  }
}
