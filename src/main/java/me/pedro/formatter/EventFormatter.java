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
}
