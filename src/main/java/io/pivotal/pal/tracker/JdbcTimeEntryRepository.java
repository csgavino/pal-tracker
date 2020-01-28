package io.pivotal.pal.tracker;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;


public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            String sql = "INSERT INTO time_entries (project_id, user_id, date, hours) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = connection
                    .prepareStatement(sql, RETURN_GENERATED_KEYS);

            ps.setLong(1, timeEntry.getProjectId());
            ps.setLong(2, timeEntry.getUserId());
            ps.setDate(3, Date.valueOf(timeEntry.getDate()));
            ps.setInt(4, timeEntry.getHours());
            return ps;
        }, generatedKeyHolder);

        return find(generatedKeyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(long id) {
        ResultSetExtractor<TimeEntry> extractor = new ResultSetExtractor<TimeEntry>() {
            @Override
            public TimeEntry extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (!rs.next()) {
                    return null;
                }

                TimeEntryMapper mapper = new TimeEntryMapper();
                return mapper.mapRow(rs, 1);
            }
        };

        String sql = "SELECT id, project_id, user_id, date, hours FROM time_entries WHERE id = ?";
        return jdbcTemplate.query(
                sql,
                new Object[]{id},
                extractor
        );
    }

    @Override
    public List<TimeEntry> list() {
        return jdbcTemplate.query("SELECT id, project_id, user_id, date, hours FROM time_entries WHERE 1", new TimeEntryMapper());
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM time_entries WHERE id = ?";
        jdbcTemplate.update(
                sql,
                id);
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        String sql = "UPDATE time_entries SET project_id = ?, user_id = ?, date = ?, hours = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                timeEntry.getProjectId(),
                timeEntry.getUserId(),
                Date.valueOf(timeEntry.getDate()),
                timeEntry.getHours(),
                id);

        return find(id);
    }

}
