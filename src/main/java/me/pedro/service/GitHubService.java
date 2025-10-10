package me.pedro.service;

import me.pedro.client.HttpClientWrapper;
import org.json.JSONArray;

import java.io.IOException;

public class GitHubService {
  private static final String GITHUB_API_URL = "https://api.github.com/users/%s/events";
  private final HttpClientWrapper httpClient;

  public GitHubService() {
    this.httpClient = new HttpClientWrapper();
  }

  public GitHubService(HttpClientWrapper httpClient) {
    this.httpClient = httpClient;
  }

  public JSONArray fetchUserEvents(String username) throws IOException {
    String apiUrl = String.format(GITHUB_API_URL, username);
    String response = httpClient.get(apiUrl);
    return new JSONArray(response);
  }
}
