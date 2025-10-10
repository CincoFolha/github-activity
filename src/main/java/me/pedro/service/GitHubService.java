package me.pedro.service;

import me.pedro.client.HttpClientWrapper;

public class GitHubService {
  private static final String GITHUB_API_URL = "https://api.github.com/users/%s/events";
  private final HttpClientWrapper httpClient;
}
