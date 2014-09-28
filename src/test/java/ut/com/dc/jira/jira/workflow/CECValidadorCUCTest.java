package ut.com.dc.jira.jira.workflow;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.SubTaskManager;
import com.atlassian.jira.exception.DataAccessException;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.IssueImpl;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.label.LabelManager;
import com.atlassian.jira.issue.security.IssueSecurityLevelManager;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.version.VersionManager;
import com.atlassian.jira.user.util.UserManager;
import org.ofbiz.core.entity.GenericValue;
import com.opensymphony.workflow.InvalidInputException;
import java.util.HashMap;
import java.util.Map;

import com.dc.jira.jira.workflow.CECValidadorCUC;

public class CECValidadorCUCTest
{
    public static final String FIELD_WORD="word";

    protected CECValidadorCUC validator;
    protected MutableIssue issue;

    @Before
    public void setup() {

        issue = createPartialMockedIssue();

        validator = new CECValidadorCUC();
    }

    @Test
    public void testValidates() throws Exception
    {
        Map transientVars = new HashMap();
        transientVars.put(FIELD_WORD,"test");
        transientVars.put("issue",issue);
        issue.setDescription("This description has test in it.");

        validator.validate(transientVars,null,null);

        assertTrue("validator should pass",true);
    }

    @Test(expected=InvalidInputException.class)
    public void testFailsValidation() throws Exception
    {
        Map transientVars = new HashMap();
        transientVars.put(FIELD_WORD,"test");
        transientVars.put("issue",issue);
        issue.setDescription("This description does not have the magic word in it.");

        validator.validate(transientVars,null,null);

        assertTrue("validator should not pass",true);
    }

    private MutableIssue createPartialMockedIssue() {
        GenericValue genericValue = mock(GenericValue.class);
        IssueManager issueManager = mock(IssueManager.class);
        ProjectManager projectManager = mock(ProjectManager.class);
        VersionManager versionManager = mock(VersionManager.class);
        IssueSecurityLevelManager issueSecurityLevelManager = mock(IssueSecurityLevelManager.class);
        ConstantsManager constantsManager = mock(ConstantsManager.class);
        SubTaskManager subTaskManager = mock(SubTaskManager.class);
        AttachmentManager attachmentManager = mock(AttachmentManager.class);
        LabelManager labelManager = mock(LabelManager.class);
        ProjectComponentManager projectComponentManager = mock(ProjectComponentManager.class);
        UserManager userManager = mock(UserManager.class);

        return new IssueImpl(genericValue, issueManager, projectManager, versionManager, issueSecurityLevelManager, constantsManager, subTaskManager, attachmentManager, labelManager, projectComponentManager, userManager);
    }

}
