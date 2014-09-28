package com.dc.jira.jira.workflow;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class CECValidadorCUC implements Validator {
    private static final Logger log = LoggerFactory.getLogger(CECValidadorCUC.class);

    private final CustomFieldManager customFieldManager;

    private static final String FIELD_NAME = "field";

    public CECValidadorCUC(CustomFieldManager customFieldManager) {
        this.customFieldManager = customFieldManager;
    }

    @SuppressWarnings("rawtypes")
    public void validate(Map transientVars, Map args, PropertySet ps) throws InvalidInputException {
        String CUC = "";
        Object cfValue = null;

        Issue issue = (Issue) transientVars.get("issue");
        String field = (String) args.get(FIELD_NAME);

        CustomField customField = customFieldManager.getCustomFieldObjectByName(field);
        CUC = (String) issue.getCustomFieldValue(customField).toString();

        if (!CUC.contains("7956627")) {
            Pattern p = Pattern.compile("[0-9]+[-|/][0-9]+[-|/][0-9]+");
            Matcher m = p.matcher(CUC);
            if (!m.find()) {
                throw new InvalidInputException(
                        "<B>El campo CUC no tiene el formato correcto.</B><BR />"
                                + "El formato correcto es CLIENTE-UBICACION-CONTACTO. Muy importante los guiones o barras separadoras.");
            }
        }
    }
}
