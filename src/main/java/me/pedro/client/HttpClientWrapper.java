package me.pedro.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
      validadeResponse(conn);
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
}
