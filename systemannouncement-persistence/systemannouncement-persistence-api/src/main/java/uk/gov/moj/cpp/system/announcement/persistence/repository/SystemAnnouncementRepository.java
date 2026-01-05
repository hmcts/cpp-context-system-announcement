package uk.gov.moj.cpp.system.announcement.persistence.repository;

import static java.lang.String.format;
import static uk.gov.justice.services.common.converter.ZonedDateTimes.toSqlTimestamp;

import uk.gov.justice.services.jdbc.persistence.JdbcRepositoryException;
import uk.gov.justice.services.jdbc.persistence.PreparedStatementWrapper;
import uk.gov.justice.services.jdbc.persistence.PreparedStatementWrapperFactory;
import uk.gov.justice.services.jdbc.persistence.ViewStoreJdbcDataSourceProvider;
import uk.gov.moj.cpp.system.announcement.domain.common.Category;
import uk.gov.moj.cpp.system.announcement.domain.common.SystemAnnouncement;
import uk.gov.moj.cpp.system.announcement.domain.common.SystemBannerAnnouncement;
import uk.gov.moj.cpp.system.announcement.domain.common.Type;
import uk.gov.moj.cpp.system.announcement.persistence.entity.SystemAnnouncementEntity;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;

import com.google.common.collect.Lists;

@ApplicationScoped
public class SystemAnnouncementRepository {

    private static final String INSERT = "INSERT INTO system_announcement (id, created_by, category, type, start_date, end_date, start_time, end_time, title, details, created_at, order_index ) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String FIND_BY_CURRENT_TIME = "SELECT * FROM system_announcement WHERE ((end_date > ? ) OR (end_date = ? AND end_time > ?) OR end_date IS NULL)";
    private static final String FIND_ACTIVE_ANNOUNCEMENTS_BY_CURRENT_TIME = "SELECT * FROM system_announcement WHERE ((end_date > ?) OR (end_date = ? AND end_time > ?) OR end_date IS NULL) AND (start_date < ? OR (start_date = ? AND start_time <= ?))";

    private static final String DELETE_BY_ID = "DELETE FROM system_announcement WHERE id = ?";
    private static final String DELETE_BY_END_DATE = "DELETE FROM system_announcement WHERE end_date <=  ?";
    private static final String FIND_BY_ID = "SELECT * FROM system_announcement WHERE id = ?";
    private static final String UPDATE = "UPDATE system_announcement SET created_by = ?, category = ?, type = ?, start_date = ?, end_date = ?, start_time = ?, end_time = ?, title = ?, details = ?, created_at = ? , order_index = ? WHERE id = ?";

    @Inject
    private PreparedStatementWrapperFactory preparedStatementWrapperFactory;
    @Inject
    private ViewStoreJdbcDataSourceProvider viewStoreJdbcDataSourceProvider;
    private DataSource dataSource;

    private static void addParams(final PreparedStatementWrapper ps, final List<Object> params) throws SQLException {
        for (int i = 1; i <= params.size(); i++) {
            ps.setObject(i, params.get(i - 1));
        }
    }

    @PostConstruct
    protected void initialiseDataSource() {
        dataSource = viewStoreJdbcDataSourceProvider.getDataSource();
    }

    public void save(final SystemAnnouncementEntity systemAnnouncementEntity) {
        final List<Object> params = Lists.newArrayList(
                systemAnnouncementEntity.getId(),
                systemAnnouncementEntity.getCreatedBy(),
                systemAnnouncementEntity.getCategory().name(),
                systemAnnouncementEntity.getType().name(),
                systemAnnouncementEntity.getStartDate(),
                systemAnnouncementEntity.getEndDate(),
                systemAnnouncementEntity.getStartTime(),
                systemAnnouncementEntity.getEndTime(),
                systemAnnouncementEntity.getTitle(),
                systemAnnouncementEntity.getDetails(),
                toSqlTimestamp(systemAnnouncementEntity.getCreatedAt()),
                systemAnnouncementEntity.getOrderIndex());
        executeUpdate(INSERT, params);
    }

    public List<SystemAnnouncementEntity> findSystemAnnouncements() {
        Date curentDate = Date.valueOf(LocalDate.now());
        Time curentTime = Time.valueOf(LocalTime.now());
        return executeQuery(FIND_BY_CURRENT_TIME, List.of(curentDate, curentDate, curentTime));
    }

    public List<SystemAnnouncementEntity> findActiveSystemAnnouncements() {
        Date curentDate = Date.valueOf(LocalDate.now());
        Time curentTime = Time.valueOf(LocalTime.now());
        return executeQuery(FIND_ACTIVE_ANNOUNCEMENTS_BY_CURRENT_TIME, List.of(curentDate, curentDate, curentTime, curentDate, curentDate, curentTime));
    }

    private List<SystemAnnouncementEntity> executeQuery(final String query, final List<Object> params) {
        try (final PreparedStatementWrapper ps = preparedStatementWrapperFactory.preparedStatementWrapperOf(dataSource, query)) {
            addParams(ps, params);
            try (final ResultSet resultSet = ps.executeQuery()) {
                return mapResultSetToEntities(resultSet);
            }
        } catch (SQLException e) {
            throw new JdbcRepositoryException(format("Exception while executing query: %s, with params: %s", query, params.toString()), e);
        }
    }

    private void executeUpdate(final String query, final List<Object> params) {
        try (final PreparedStatementWrapper ps = preparedStatementWrapperFactory.preparedStatementWrapperOf(dataSource, query)) {
            addParams(ps, params);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new JdbcRepositoryException(format("Exception while executing update: %s, with params: %s", query, params.toString()), e);
        }
    }

    private List<SystemAnnouncementEntity> mapResultSetToEntities(final ResultSet resultSet) throws SQLException {
        List<SystemAnnouncementEntity> entities = Lists.newArrayList();
        while (resultSet.next()) {
            SystemAnnouncementEntity entity = new SystemAnnouncementEntity();
            entity.setId(getUUID(resultSet, "id"));
            entity.setCreatedBy(resultSet.getString("created_by"));
            entity.setCategory(Category.valueOf(resultSet.getString("category")));
            entity.setType(Type.valueOf(resultSet.getString("type")));
            entity.setStartDate(resultSet.getDate("start_date").toLocalDate());
            entity.setEndDate(resultSet.getDate("end_date") != null ? resultSet.getDate("end_date").toLocalDate() : null);
            entity.setStartTime(resultSet.getTime("start_time").toLocalTime());
            entity.setEndTime(resultSet.getTime("end_time") != null ? resultSet.getTime("end_time").toLocalTime() : null);
            entity.setTitle(resultSet.getString("title"));
            entity.setDetails(resultSet.getString("details"));
            entity.setCreatedAt(resultSet.getTimestamp("created_at").toInstant().atZone(ZoneId.systemDefault()));
            entity.setOrderIndex(resultSet.getShort("order_index"));

            entities.add(entity);
        }
        return entities;
    }

    private UUID getUUID(ResultSet resultSet, String columnLabel) throws SQLException {
        String value = resultSet.getString(columnLabel);
        return value != null ? UUID.fromString(value) : null;
    }

    public void deleteById(final UUID systemAnnouncementId) {
        final List<Object> params = Lists.newArrayList(systemAnnouncementId);
        executeUpdate(DELETE_BY_ID, params);
    }

    public void deleteByEndDate(final Date endDate) {
        final List<Object> params = Lists.newArrayList(endDate);
        executeUpdate(DELETE_BY_END_DATE, params);
    }

    public SystemAnnouncementEntity findById(final UUID id) {
        final List<Object> params = Lists.newArrayList(id);
        final List<SystemAnnouncementEntity> results = executeQuery(FIND_BY_ID, params);
        return results.isEmpty() ? null : results.get(0);
    }

    public void update(final SystemAnnouncementEntity systemAnnouncementEntity) {
        final List<Object> params = Lists.newArrayList(
                systemAnnouncementEntity.getCreatedBy(),
                systemAnnouncementEntity.getCategory().name(),
                systemAnnouncementEntity.getType().name(),
                systemAnnouncementEntity.getStartDate(),
                systemAnnouncementEntity.getEndDate(),
                systemAnnouncementEntity.getStartTime(),
                systemAnnouncementEntity.getEndTime(),
                systemAnnouncementEntity.getTitle(),
                systemAnnouncementEntity.getDetails(),
                toSqlTimestamp(systemAnnouncementEntity.getCreatedAt()),
                systemAnnouncementEntity.getOrderIndex(),
                systemAnnouncementEntity.getId()
        );
        executeUpdate(UPDATE, params);
    }

    public List<SystemBannerAnnouncement> sort(final List<SystemAnnouncementEntity> result) {
        final Comparator<SystemAnnouncementEntity> orderIndexComparator = Comparator.comparingInt(SystemAnnouncementEntity::getOrderIndex)
                .thenComparing(SystemAnnouncementEntity::getStartDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(SystemAnnouncementEntity::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(SystemAnnouncementEntity::getCreatedAt);

        result.sort(orderIndexComparator);
        return result.stream().map(this::createBannerAnnouncement).toList();
    }

    public List<SystemAnnouncement> sortSystemAnnouncements(final List<SystemAnnouncementEntity> result) {
        final Comparator<SystemAnnouncementEntity> startDateTimeComparator =
                Comparator.comparing(SystemAnnouncementEntity::getStartDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(SystemAnnouncementEntity::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()));

        result.sort(startDateTimeComparator);
        return result.stream().map(this::createSystemAnnouncement).toList();
    }

    private SystemBannerAnnouncement createBannerAnnouncement(final SystemAnnouncementEntity sr) {
        return new SystemBannerAnnouncement.Builder()
                .category(sr.getCategory())
                .type(sr.getType())
                .startDate(sr.getStartDate())
                .startTime(sr.getStartTime())
                .endDate(sr.getEndDate())
                .endTime(sr.getEndTime())
                .title(sr.getTitle())
                .details(sr.getDetails())
                .build();
    }

    private SystemAnnouncement createSystemAnnouncement(final SystemAnnouncementEntity sr) {
        return new SystemAnnouncement.Builder()
                .id(sr.getId())
                .category(sr.getCategory())
                .type(sr.getType())
                .startDate(sr.getStartDate())
                .startTime(sr.getStartTime())
                .endDate(sr.getEndDate())
                .endTime(sr.getEndTime())
                .title(sr.getTitle())
                .details(sr.getDetails())
                .createdBy(sr.getCreatedBy())
                .build();
    }

    public List<SystemAnnouncement> createSystemAnnouncementResult(List<SystemAnnouncementEntity> result) {
        return result.stream().map(this:: createSystemAnnouncement).toList();
    }
}