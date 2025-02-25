package me.pedro;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray; 
import org.json.JSONObject;

public class ApiConsumer {
  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Usage: java ApiConsumer <username>");
      return;
    }

    try {
      
      URL url = new URL("https://api.github.com/users/" + args[0] + "/events");

      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Accept", "application/json");

      if (conn.getResponseCode() != 200) {
        throw new RuntimeException("Erro HTTP: " + conn.getResponseCode());
      }

      BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      StringBuilder response = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
        response.append(line);
      }
      br.close();

      String responseStr = response.toString();
      JSONArray jsonEvents = new JSONArray(responseStr);
      System.out.println("Output:");
      for (int i = 0; i < jsonEvents.length(); i++) {
        System.out.println("- " + displayActivity(jsonEvents.getJSONObject(i)));
      }

      conn.disconnect();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String displayActivity(JSONObject events) {
    switch (events.getString("type")) {
      case "PushEvent": 
        return String.format("Pushed %d commit(s) to %s", 
            events.getJSONObject("payload").getJSONArray("commits").length(), events.getJSONObject("repo").getString("name"));
      case "IssuesEvent":
        return String.format("");
      case "WatchEvent":
        return String.format("Starred %s", events.getJSONObject("repo").getString("name"));
      case "ForkEvent":
        return String.format("Forked %s", events.getJSONObject("repo").getString("name"));
      case "CreateEvent":
        return String.format("Created %s in %s", 
            events.getJSONObject("payload").getString("ref_type"), events.getJSONObject("repo").getString("name"));
      default:
        return String.format("%s in %s", 
            events.getString("type").replace("Event", ""), events.getJSONObject("repo").getString("name"));
    }
  }
}
