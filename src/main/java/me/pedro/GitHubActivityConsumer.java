package me.pedro;

import me.pedro.formatter.EventFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GitHubActivityConsumer {
  private static final String GITHUB_API_URL = "https://api.github.com/users/%s/events";
  private static final String USER_AGENT = "GitHubActivityConsumer/1.0";

  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Uso: java GitHubActivityConsumer <username>");
      System.exit(1);
    }

    String username = args[0];
    EventFormatter formatter = new EventFormatter();

    try {
      JSONArray events = fetchGithubEvents(username);
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

  private static JSONArray fetchGithubEvents(String username) throws IOException {
    String apiUrl = String.format(GITHUB_API_URL, username);
    URL url = new URL(apiUrl);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

    try {
      configureConnection(conn);
      validateResponse(conn);
      return parseResponse(conn);
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
}
