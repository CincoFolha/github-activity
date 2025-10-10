package me.pedro.formatter;

import org.json.JSONArray;
import org.json.JSONObject;

public class EventFormatter {

  public void displayEvents(JSONArray events) {
    if (events.isEmpty()) {
      System.out.println("Nenhum evento encontrado.");
      return;
    }

    printHeader();
    printEvents(events);
    printFooter(events.length());
  }

  private void printHeader() {
    System.out.println("\nAtividades recentes do GitHub:");
    System.out.println("-".repeat(50));
  }

  private void printEvents(JSONArray events) {
    int maxEvents = Math.min(events.length(), MAX_EVENTS_TO_DISPLAY);

    for (int i = 0; i < maxEvents; i++) {
      JSONObject event = events.getJSONObject(i);
      System.out.println(". " + formatEvent(event));
    }
  }

  private void printFooter(int totalEvents) {
    if (totalEvents > MAX_EVENTS_TO_DISPLAY) {
      System.out.println("\n... e mais " + (totalEvents - MAX_EVENTS_TO_DISPLAY) + " evento(s)");
    }
  }

  private static String formatEvent(JSONObject event) {
    String type = event.optString("type", "UnknownEvent");
    JSONObject payload = event.optJSONObject("payload");
    JSONObject repo = event.optJSONObject("repo");
    String repoName = extractRepoName(repo);

    return switch (type) {
      case "PushEvent" -> formatPushEvent(payload, repoName);
      case "IssuesEvent" -> formatIssuesEvent(payload, repoName);
      case "PullRequestEvent" -> formatPullRequestEvent(payload, repoName);
      case "WatchEvent" -> "Deu estrela em " + repoName;
      case "ForkEvent" -> "Fez fork de " + repoName;
      case "CreateEvent" -> formatCreateEvent(payload, repoName);
      case "DeleteEvent" -> formatDeleteEvent(payload, repoName);
      case "ReleaseEvent" -> "Publicou release em " + repoName;
      default -> formatGenericEvent(type, repoName);
    };
  }

  private String extractRepoName(JSONObject repo) {
    return repo != null ? repo.optString("name", "desconhecido") : "desconhecido";
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
        capitalizeFirst(action), repoName);
  }

  private static String formatPullRequestEvent(JSONObject payload, String repoName) {
    if (payload == null) {
      return "Interagiu com pull request em " + repoName;
    }

    String action = payload.optString("action", "interagiu com");
    return String.format("%s pull request em %s",
        capitalizeFirst(action), repoName);
  }

  private static String formatCreateEvent(JSONObject payload, String repoName) {
    if (payload == null) {
      return "Criou recurso em " + repoName;
    }

    String refType = payload.optString("ref_type", "recurso");
    String refTypeTranslated = translateRefType(refType);

    return String.format("Criou %s em %s", refTypeTranslated, repoName);
  }

  private static String formatDeleteEvent(JSONObject payload, String repoName) {
    if (payload == null) {
      return "Deletou recurso em " + repoName;
    }

    String refType = payload.optString("ref_type", "recurso");
    return String.format("Deletou %s em %s", refType, repoName);
  }

  private static String formatGenericEvent(String type, String repoName) {
    String eventName = type.replace("Event", "");
    return String.format("%s em %s", eventName, repoName);
  }

  private String translateRefType(String refType) {
    return switch (refType) {
      case "repository" -> "repositótio";
      case "branch" -> "branch";
      case "tag" -> "tag";
      default -> refType;
    };
  }

  private String capitalizeFirst(String text) {
    if (text == null || text.isEmpty()) {
      return text;
    }
    return text.substring(0, 1).toUpperCase() + text.substring(1);
  }
}
