package io.pivotal.pal.tracker;

import org.springframework.stereotype.Repository;

import java.util.*;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private Long id = 0l;
    private Map<Long, TimeEntry> map = new HashMap<>();

    public TimeEntry create(TimeEntry timeEntry) {
        TimeEntry newTimeEntry = new TimeEntry(
                ++id,
                timeEntry.getProjectId(),
                timeEntry.getUserId(),
                timeEntry.getDate(),
                timeEntry.getHours()
        );

        map.put(
                newTimeEntry.getId(),
                newTimeEntry);
        return newTimeEntry;
    }

    public TimeEntry find(long timeEntryId) {
        for (TimeEntry timeEntry : map.values()) {
            if (timeEntry.getTimeEntryId() == timeEntryId) {
                return timeEntry;
            }
        }

        return null;
    }

    public List<TimeEntry> list() {
        List<TimeEntry> timeEntries = new ArrayList<>();
        Set<Long> timeEntryIds = map.keySet();

        for (Long timeEntryId : timeEntryIds) {
            if (map.get(timeEntryId) != null) {
                timeEntries.add(map.get(timeEntryId));
            }
        }

        return timeEntries;
    }

    public void delete(long id) {
        map.remove(id);
    }

    public TimeEntry update(long id, TimeEntry newTimeEntry) {
        TimeEntry timeEntry = find(id);
        if (timeEntry == null) return null;

        timeEntry.setProjectId(newTimeEntry.getProjectId());
        timeEntry.setUserId(newTimeEntry.getUserId());
        timeEntry.setDate(newTimeEntry.getDate());
        timeEntry.setHours(newTimeEntry.getHours());

        map.put(id, timeEntry);
        return timeEntry;
    }
}
