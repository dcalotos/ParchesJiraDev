package com.dc.jira.jira.workflow;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.index.IssueIndexManager;
import com.atlassian.jira.user.UserUtils;
import com.atlassian.jira.util.ImportUtils;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class GDVDestinoFechas extends AbstractJiraFunctionProvider {
    private static final Logger log = LoggerFactory.getLogger(GDVDestinoFechas.class);
    public static final String USER_ADMIN = "userAdmin";

    @SuppressWarnings({"rawtypes"})
    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        String destination = "";
        String TravelSummary = "";
        Timestamp DateFrom = new Timestamp(0);
        Timestamp DateUntil = new Timestamp(0);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy");

        MutableIssue issue = getIssue(transientVars);
        IssueManager issueManager = ComponentAccessor.getIssueManager();
        //UserUtil uu = ComponentAccessor.getUserUtil();

        CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
        List<CustomField> customFields = customFieldManager.getCustomFieldObjects(issue);

        if (customFields.contains(customFieldManager.getCustomFieldObjectByName("Destination"))) {
            if (customFieldManager.getCustomFieldObjectByName("Destination").getValue(issue) != null) {
                destination = customFieldManager.getCustomFieldObjectByName("Destination").getValue(issue).toString();
            } else {
                destination = "";
                log.warn("Field Destination contains null value.");
            }
        } else {
            destination = "";
            log.warn("CustomField Destination not exists.");
        }
        log.warn("Destination: [" + destination + "]");

        if (customFields.contains(customFieldManager.getCustomFieldObjectByName("Date From"))) {
            if (customFieldManager.getCustomFieldObjectByName("Date From").getValue(issue) != null) {
                DateFrom = (Timestamp) customFieldManager.getCustomFieldObjectByName("Date From").getValue(issue);
            } else {
                DateFrom = new Timestamp(0);
                log.warn("Field Date From contains null value.");
            }
        } else {
            DateFrom = new Timestamp(0);
            log.warn("CustomField Date From not exists.");
        }
        log.warn("Date From: [" + DateFrom + "]");

        if (customFields.contains(customFieldManager.getCustomFieldObjectByName("Date Until"))) {
            if (customFieldManager.getCustomFieldObjectByName("Date Until").getValue(issue) != null) {
                DateUntil = (Timestamp) customFieldManager.getCustomFieldObjectByName("Date Until").getValue(issue);
            } else {
                DateUntil = new Timestamp(0);
                log.warn("Field Date Until contains null value.");
            }
        } else {
            DateUntil = new Timestamp(0);
            log.warn("CustomField Date Until not exists.");
        }
        log.warn("Date Until: [" + DateUntil + "]");

        TravelSummary = destination + ". Desde el " + new SimpleDateFormat("dd/MMM/yyyy").format(DateFrom) + " hasta el " + new SimpleDateFormat("dd/MMM/yyyy").format(DateUntil) + ".";

        issue.setCustomFieldValue(ComponentAccessor.getCustomFieldManager().getCustomFieldObjectByName("Travel summary"), TravelSummary);

        IssueIndexManager issueIndexManager = ComponentAccessor.getIssueIndexManager();
        ImportUtils.setIndexIssues(false);
        try {
            issueIndexManager.deIndex(issue);
        } catch (Exception ie) {
            log.warn("Failed to deindex issue: " + issue.getKey(), ie);
        }
        try {
            issueManager.updateIssue(UserUtils.getUser((String) args.get("userAdmin")), issue, EventDispatchOption.DO_NOT_DISPATCH, false);
        } catch (Exception ie) {
            log.warn("Failed to updateIssue issue: " + issue.getKey(), ie);
        }
        ImportUtils.setIndexIssues(true);
        try {
            issueIndexManager.reIndex(issue);
        } catch (Exception e) {
            log.warn("Failed to reindex issue: " + issue.getKey(), e);
        }
        log.warn("Cambio de campo realizado.");
    }
}
