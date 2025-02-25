package me.pedro;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiConsumer {
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

  private static JSONArray fetchGithubEvents(String username) throws Exception {
    URL url = new URL("https://api.github.com/users/" + username + "/events");
    HttpURLConnection conn = (HrrpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    conn.setRequestProperty("Accept", "application/json");

    if (conn.getResponseCode != 200) {
      throw new RuntimeExceoption("Erro HTTP: " + conn.getResponseCode());
    }

    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    StringBuilder response = new StringBuilder();
    String line;
    while ((line = br.readLine()) != null) {
      response.append(line);
    }
    br.close();
    conn.disconnet();

    return new JSONArray(response.toString());
  }

  private static void displayEvents(JSONArray jsonEvents) {
    System.out.println("Output:");
    for (int i = 0; i < jsonEvents.length(); i++) {
      System.out.println("- " + displayActivity(jsonEvents.getJSONObject(i)));
    }
  }

  private static String displayActivity(JSONObject event) {
    String eventType = event.getString("type");
    JSONObject payload = event.optJSONObject("payload");
    JSONObject repo = event.getJSONObject("repo");

    switch (eventType) {
      case "PushEvent": 
        return String.format("Pushed %d commit(s) to %s", 
            payload.getJSONArray("commits").length(), repo.getString("name"));
      case "IssuesEvent":
        return String.format("");
      case "WatchEvent":
        return String.format("Starred %s", repo.getString("name"));
      case "ForkEvent":
        return String.format("Forked %s", repo.getString("name"));
      case "CreateEvent":
        return String.format("Created %s in %s", 
            payload.getString("ref_type"), repo.getString("name"));
      default:
        return String.format("%s in %s", 
            eventType.replace("Event", ""), repo.getString("name"));
    }
  }
}
