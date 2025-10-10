package me.pedro.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpClientWrapper {
  private static final String USER_AGENT = "GitHubActivityConsumer/1.0";
  private static final int CONNECT_TIMEOUT = 5000;
  private static final int READ_TIMEOUT = 5000;

  public String get(String urlString) throws IOException {
    URL url = new URL(urlString);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

    try {
      configureConnection(conn);
      validateResponse(conn);
      return readResponse(conn);
    } finally {
      conn.disconnect();
    }
  }

  private void configureConnection(HttpURLConnection conn) throws IOException {
    conn.setRequestMethod("GET");
    conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
    conn.setRequestProperty("User-Agent", USER_AGENT);
    conn.setConnectTimeout(CONNECT_TIMEOUT);
    conn.setReadTimeout(READ_TIMEOUT);
  }

  private void validateResponse(HttpURLConnection conn) throws IOException {
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

  private String readResponse(HttpURLConnection conn) throws IOException {
    try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

      StringBuilder response = new StringBuilder();
      String line;

      while ((line = reader.readLine()) != null) {
        response.append(line);
      }

      return response.toString();
    }
  }
}
