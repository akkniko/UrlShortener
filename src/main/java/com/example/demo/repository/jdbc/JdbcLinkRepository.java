package com.example.demo.repository.jdbc;

import com.example.demo.exception.AliasTakenException;
import com.example.demo.exception.DataAccessException;
import com.example.demo.model.Link;
import com.example.demo.repository.LinkRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Optional;

public class JdbcLinkRepository implements LinkRepository {
    private static final String FIND_BY_CODE = """
            SELECT id, original_url, code, created_at, expire_at
            FROM links WHERE code = ?
            """;
    private static final String INSERT = """
            INSERT INTO links (original_url, code, created_at, expire_at)
            VALUES (?, ?, ?, ?)
            RETURNING id, original_url, code, created_at, expire_at
            """;

    private final DataSource dataSource;

    public JdbcLinkRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Link> findByCode(String code) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CODE)) {
            statement.setString(1, code);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapLink(resultSet));
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Could not find link by code", exception);
        }
    }

    @Override
    public Link save(Link link) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setString(1, link.getOriginalUrl());
            statement.setString(2, link.getCode());
            statement.setTimestamp(3, Timestamp.from(link.getCreatedAt()));
            if (link.getExpireAt() == null) {
                statement.setNull(4, Types.TIMESTAMP);
            } else {
                statement.setTimestamp(4, Timestamp.from(link.getExpireAt()));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new DataAccessException("Database did not return the new link id", null);
                }
                return mapLink(resultSet);
            }
        } catch (SQLException exception) {
            if ("23505".equals(exception.getSQLState())) {
                throw new AliasTakenException(link.getCode());
            }
            throw new DataAccessException("Could not save link", exception);
        }
    }

    private Link mapLink(ResultSet resultSet) throws SQLException {
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        Timestamp expireAt = resultSet.getTimestamp("expire_at");
        return new Link(
                resultSet.getLong("id"),
                resultSet.getString("original_url"),
                resultSet.getString("code"),
                createdAt.toInstant(),
                expireAt == null ? null : expireAt.toInstant()
        );
    }
}
