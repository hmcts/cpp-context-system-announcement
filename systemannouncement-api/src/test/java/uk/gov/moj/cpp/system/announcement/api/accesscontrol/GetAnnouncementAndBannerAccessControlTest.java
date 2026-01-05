package uk.gov.moj.cpp.system.announcement.api.accesscontrol;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.cpp.accesscontrol.common.providers.UserAndGroupProvider;
import uk.gov.moj.cpp.accesscontrol.drools.Action;
import uk.gov.moj.cpp.accesscontrol.test.utils.BaseDroolsAccessControlTest;

import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetAnnouncementAndBannerAccessControlTest extends BaseDroolsAccessControlTest {

    private Action action;

    @Mock
    private UserAndGroupProvider userAndGroupProvider;


    public GetAnnouncementAndBannerAccessControlTest() {
        super("COMMAND_API_SESSION");
    }

    @Override
    protected Map<Class<?>, Object> getProviderMocks() {
        return singletonMap(UserAndGroupProvider.class, userAndGroupProvider);
    }


    @Test
    public void shouldAllowGetAllAnnouncementsIfMemberOfAnyOfTheSuppliedGroups() throws Exception {
        final Action action = createActionFor("systemannouncement.get-all-announcements");
        when(userAndGroupProvider.hasPermission(action, ExpectedPermissionConstants.expectedPermissionForSystemAnnouncement())).thenReturn(true);
        assertSuccessfulOutcome(executeRulesWith(action));
    }

    @Test
    public void shouldAllowCreateAnnouncement() throws Exception {
        final Action action = createActionFor("systemannouncement.create-system-announcement");
        when(userAndGroupProvider.hasPermission(action, ExpectedPermissionConstants.expectedPermissionForSystemAnnouncement())).thenReturn(true);
        assertSuccessfulOutcome(executeRulesWith(action));
    }

    @Test
    public void shouldAllowUpdateAnnouncement() throws Exception {
        final Action action = createActionFor("systemannouncement.update-system-announcement");
        when(userAndGroupProvider.hasPermission(action, ExpectedPermissionConstants.expectedPermissionForSystemAnnouncement())).thenReturn(true);
        assertSuccessfulOutcome(executeRulesWith(action));
    }

    @Test
    public void shouldAllowDeleteAnnouncement() throws Exception {
        final Action action = createActionFor("systemannouncement.delete-expired-system-announcements");
        when(userAndGroupProvider.hasPermission(action, ExpectedPermissionConstants.expectedPermissionForSystemAnnouncement())).thenReturn(true);
        assertSuccessfulOutcome(executeRulesWith(action));
    }
}
