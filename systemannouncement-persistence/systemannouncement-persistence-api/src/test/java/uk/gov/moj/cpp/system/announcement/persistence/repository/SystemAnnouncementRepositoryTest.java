package uk.gov.moj.cpp.system.announcement.persistence.repository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.services.jdbc.persistence.PreparedStatementWrapper;
import uk.gov.justice.services.jdbc.persistence.PreparedStatementWrapperFactory;
import uk.gov.justice.services.jdbc.persistence.ViewStoreJdbcDataSourceProvider;
import uk.gov.moj.cpp.system.announcement.domain.common.Category;
import uk.gov.moj.cpp.system.announcement.domain.common.SystemAnnouncement;
import uk.gov.moj.cpp.system.announcement.domain.common.Type;
import uk.gov.moj.cpp.system.announcement.persistence.entity.SystemAnnouncementEntity;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.common.converter.ZonedDateTimes.toSqlTimestamp;

@ExtendWith(MockitoExtension.class)
public class SystemAnnouncementRepositoryTest {

    @Mock
    private PreparedStatementWrapperFactory preparedStatementWrapperFactory;

    @Mock
    private ViewStoreJdbcDataSourceProvider viewStoreJdbcDataSourceProvider;

    @Mock
    private DataSource dataSource;

    @Mock
    private PreparedStatementWrapper preparedStatementWrapper;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private SystemAnnouncementRepository systemAnnouncementRepository;

    @BeforeEach
    public void setUp() {
        when(viewStoreJdbcDataSourceProvider.getDataSource()).thenReturn(dataSource);
        systemAnnouncementRepository.initialiseDataSource();
    }

    @Test
    public void shouldSaveSystemAnnouncement() throws SQLException {
        SystemAnnouncementEntity entity = new SystemAnnouncementEntity();
        entity.setId(UUID.randomUUID());
        entity.setCreatedBy("test user");
        entity.setCategory(Category.UNPLANNED);
        entity.setType(Type.CRITICAL);
        entity.setStartDate(LocalDate.now());
        entity.setEndDate(LocalDate.now().plusDays(1));
        entity.setStartTime(LocalTime.now());
        entity.setEndTime(LocalTime.now().plusHours(1));
        entity.setTitle("test title");
        entity.setDetails("test details");
        entity.setCreatedAt(ZonedDateTime.now());

        when(preparedStatementWrapperFactory.preparedStatementWrapperOf(any(DataSource.class), anyString())).thenReturn(preparedStatementWrapper);

        systemAnnouncementRepository.save(entity);

        verify(preparedStatementWrapper, times(1)).executeUpdate();
    }

    @Test
    public void shouldSortSystemAnnouncements() {
        SystemAnnouncementEntity entity1 = new SystemAnnouncementEntity();
        entity1.setStartDate(LocalDate.now().plusDays(10));
        entity1.setStartTime(LocalTime.now().plusHours(10));

        SystemAnnouncementEntity entity2 = new SystemAnnouncementEntity();
        entity2.setStartDate(LocalDate.now().plusDays(5));
        entity2.setStartTime(LocalTime.now().plusHours(1).plusMinutes(10));

        SystemAnnouncementEntity entity3 = new SystemAnnouncementEntity();
        entity3.setStartDate(LocalDate.now().plusDays(5));
        entity3.setStartTime(LocalTime.now().plusHours(1).plusMinutes(5));

        List<SystemAnnouncementEntity> entities = Arrays.asList(entity1, entity2, entity3);

        List<SystemAnnouncement> sortedAnnouncements = systemAnnouncementRepository.sortSystemAnnouncements(entities);

        assertNotNull(sortedAnnouncements);
        assertEquals(3, sortedAnnouncements.size());
        assertEquals(entity3.getStartDate(), sortedAnnouncements.get(0).startDate());
        assertEquals(entity3.getStartTime(), sortedAnnouncements.get(0).startTime());
        assertEquals(entity2.getStartDate(), sortedAnnouncements.get(1).startDate());
        assertEquals(entity2.getStartTime(), sortedAnnouncements.get(1).startTime());
        assertEquals(entity1.getStartDate(), sortedAnnouncements.get(2).startDate());
        assertEquals(entity1.getStartTime(), sortedAnnouncements.get(2).startTime());
    }

    @Test
    public void shouldFindSystemAnnouncements() throws SQLException {
        when(preparedStatementWrapperFactory.preparedStatementWrapperOf(any(DataSource.class), anyString())).thenReturn(preparedStatementWrapper);
        when(preparedStatementWrapper.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("id")).thenReturn(UUID.randomUUID().toString());
        when(resultSet.getString("created_by")).thenReturn("test user");
        when(resultSet.getString("category")).thenReturn(Category.UNPLANNED.name());
        when(resultSet.getString("type")).thenReturn(Type.CRITICAL.name());
        when(resultSet.getDate("start_date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(resultSet.getDate("end_date")).thenReturn(Date.valueOf(LocalDate.now().plusDays(1)));
        when(resultSet.getTime("start_time")).thenReturn(Time.valueOf(LocalTime.now()));
        when(resultSet.getTime("end_time")).thenReturn(Time.valueOf(LocalTime.now().plusHours(1)));
        when(resultSet.getString("title")).thenReturn("test title");
        when(resultSet.getString("details")).thenReturn("test details");
        when(resultSet.getTimestamp("created_at")).thenReturn(Timestamp.from(Instant.now()));

        List<SystemAnnouncementEntity> entities = systemAnnouncementRepository.findSystemAnnouncements();

        assertNotNull(entities);
        assertEquals(1, entities.size());
        verify(preparedStatementWrapper, times(1)).executeQuery();
    }

    @Test
    public void shouldFindSystemAnnouncementsWithNullEndDate() throws SQLException {
        when(preparedStatementWrapperFactory.preparedStatementWrapperOf(any(DataSource.class), anyString())).thenReturn(preparedStatementWrapper);
        when(preparedStatementWrapper.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("id")).thenReturn(UUID.randomUUID().toString());
        when(resultSet.getString("created_by")).thenReturn("test user");
        when(resultSet.getString("category")).thenReturn(Category.UNPLANNED.name());
        when(resultSet.getString("type")).thenReturn(Type.CRITICAL.name());
        when(resultSet.getDate("start_date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(resultSet.getDate("end_date")).thenReturn(null);
        when(resultSet.getTime("start_time")).thenReturn(Time.valueOf(LocalTime.now()));
        when(resultSet.getTime("end_time")).thenReturn(null);
        when(resultSet.getString("title")).thenReturn("test title");
        when(resultSet.getString("details")).thenReturn("test details");
        when(resultSet.getTimestamp("created_at")).thenReturn(Timestamp.from(Instant.now()));

        List<SystemAnnouncementEntity> entities = systemAnnouncementRepository.findSystemAnnouncements();

        assertNotNull(entities);
        assertEquals(1, entities.size());
        assertNull(entities.get(0).getEndDate());
        assertNull(entities.get(0).getEndTime());
        verify(preparedStatementWrapper, times(1)).executeQuery();
    }

    @Test
    public void shouldUpdateSystemAnnouncement() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        SystemAnnouncementEntity entity = new SystemAnnouncementEntity();
        entity.setId(id);
        entity.setCreatedBy("test user");
        entity.setCategory(Category.UNPLANNED);
        entity.setType(Type.CRITICAL);
        entity.setStartDate(LocalDate.now());
        entity.setEndDate(LocalDate.now().plusDays(1));
        entity.setStartTime(LocalTime.now());
        entity.setEndTime(LocalTime.now().plusHours(1));
        entity.setTitle("test title");
        entity.setDetails("test details");
        entity.setCreatedAt(ZonedDateTime.now());
        entity.setOrderIndex((short) 1);

        when(preparedStatementWrapperFactory.preparedStatementWrapperOf(any(DataSource.class), anyString())).thenReturn(preparedStatementWrapper);

        // Act
        systemAnnouncementRepository.update(entity);

        // Assert
        verify(preparedStatementWrapperFactory, times(1)).preparedStatementWrapperOf(any(DataSource.class), eq("UPDATE system_announcement SET created_by = ?, category = ?, type = ?, start_date = ?, end_date = ?, start_time = ?, end_time = ?, title = ?, details = ?, created_at = ? , order_index = ? WHERE id = ?"));
        verify(preparedStatementWrapper, times(1)).setObject(1, "test user");
        verify(preparedStatementWrapper, times(1)).setObject(2, Category.UNPLANNED.name());
        verify(preparedStatementWrapper, times(1)).setObject(3, Type.CRITICAL.name());
        verify(preparedStatementWrapper, times(1)).setObject(4, entity.getStartDate());
        verify(preparedStatementWrapper, times(1)).setObject(5, entity.getEndDate());
        verify(preparedStatementWrapper, times(1)).setObject(6, entity.getStartTime());
        verify(preparedStatementWrapper, times(1)).setObject(7, entity.getEndTime());
        verify(preparedStatementWrapper, times(1)).setObject(8, "test title");
        verify(preparedStatementWrapper, times(1)).setObject(9, "test details");
        verify(preparedStatementWrapper, times(1)).setObject(10, toSqlTimestamp(entity.getCreatedAt()));
        verify(preparedStatementWrapper, times(1)).setObject(11, (short) 1);
        verify(preparedStatementWrapper, times(1)).setObject(12, id);
        verify(preparedStatementWrapper, times(1)).executeUpdate();
    }
}