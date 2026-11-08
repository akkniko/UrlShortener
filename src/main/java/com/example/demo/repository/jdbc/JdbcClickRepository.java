package com.example.demo.repository.jdbc;

import com.example.demo.dto.DayStat;
import com.example.demo.exception.DataAccessException;
import com.example.demo.model.Click;
import com.example.demo.repository.ClickRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class JdbcClickRepository implements ClickRepository {
    private static final String INSERT = """
            INSERT INTO clicks (link_id, clicked_at, referrer, user_agent)
            VALUES (?, ?, ?, ?)
            RETURNING id
            """;
    private static final String COUNT = "SELECT COUNT(*) FROM clicks WHERE link_id = ?";
    private static final String DAILY_STATS = """
            SELECT (clicked_at AT TIME ZONE 'UTC')::date AS day, COUNT(*) AS clicks
            FROM clicks
            WHERE link_id = ?
            GROUP BY day
            ORDER BY day
            """;

    private final DataSource dataSource;

    public JdbcClickRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Click save(Click click) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setLong(1, click.linkId());
            statement.setTimestamp(2, Timestamp.from(click.clickedAt()));
            statement.setString(3, click.referrer());
            statement.setString(4, click.userAgent());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new DataAccessException("Database did not return the new click id", null);
                }
                return new Click(resultSet.getLong("id"), click.linkId(), click.clickedAt(),
                        click.referrer(), click.userAgent());
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Could not save click", exception);
        }
    }

    @Override
    public long countByLinkId(Long linkId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT)) {
            statement.setLong(1, linkId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getLong(1);
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Could not count clicks", exception);
        }
    }

    @Override
    public List<DayStat> findDailyStatsByLinkId(Long linkId) {
        List<DayStat> stats = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DAILY_STATS)) {
            statement.setLong(1, linkId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    stats.add(new DayStat(resultSet.getDate("day").toLocalDate(), resultSet.getLong("clicks")));
                }
            }
            return stats;
        } catch (SQLException exception) {
            throw new DataAccessException("Could not read daily click statistics", exception);
        }
    }
}
