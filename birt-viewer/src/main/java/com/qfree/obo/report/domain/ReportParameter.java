package com.qfree.obo.report.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.constraints.NotBlank;

import com.qfree.obo.report.dto.ReportParameterResource;
import com.qfree.obo.report.util.DateUtils;

/**
 * The persistent class for the "report_parameter" database table.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "report_parameter", schema = "reporting",
		uniqueConstraints = {
				//				@UniqueConstraint(columnNames = { "report_id", "order_index" },
				@UniqueConstraint(columnNames = { "report_version_id", "order_index" },
						name = "uc_reportparameter_reportversion_orderindex") })
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class ReportParameter implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "report_parameter_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID reportParameterId;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@NotNull
	@JoinColumn(name = "report_version_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_reportparameter_report") ,
			columnDefinition = "uuid")
	private ReportVersion reportVersion;
	//	@JoinColumn(name = "report_id", nullable = false,
	//			foreignKey = @ForeignKey(name = "fk_reportparameter_report"),
	//			columnDefinition = "uuid")
	//	private Report report;

	/**
	 * The name of the report parameter as defined in the BIRT report.
	 * 
	 * This is the string that must be used to refer to the report parameter
	 * in a URL when requesting a report. It is used to provide one or more
	 * values for the parameter. 
	 */
	@NotBlank
	@Column(name = "name", nullable = false, length = 80)
	private String name;

	@NotBlank
	@Column(name = "prompt_text", nullable = false, length = 132)
	private String promptText;

	/**
	 * If this is false, a GUI should display a checkbox labeled "Is null" (or 
	 * something similar) next to the input widget for the parameter. If this
	 * is checked, the widget for entering a value should be disabled to 
	 * indicate that no value can be entered.
	 */
	@NotNull
	@Column(name = "required", nullable = false)
	private Boolean required;

	@NotNull
	@Column(name = "multivalued", nullable = false)
	private Boolean multivalued;

	@NotNull
	@Column(name = "order_index", nullable = false)
	private Integer orderIndex;

	/*
	 * Possible values for "DataType" are:
	 * 
	 *     IParameterDefn.TYPE_ANY       = 0
	 *     IParameterDefn.TYPE_STRING    = 1
	 *     IParameterDefn.TYPE_FLOAT     = 2
	 *     IParameterDefn.TYPE_DECIMAL   = 3
	 *     IParameterDefn.TYPE_DATE_TIME = 4
	 *     IParameterDefn.TYPE_BOOLEAN   = 5
	 *     IParameterDefn.TYPE_INTEGER   = 6
	 *     IParameterDefn.TYPE_DATE      = 7
	 *     IParameterDefn.TYPE_TIME      = 8
	 */
	@NotNull
	@Column(name = "data_type", nullable = false)
	private Integer dataType;

	/*
	 * Possible values for "ControlType" are:
	 * 
	 *     IScalarParameterDefn.TEXT_BOX     = 0  (default)
	 *     IScalarParameterDefn.LIST_BOX     = 1
	 *     IScalarParameterDefn.RADIO_BUTTON = 2
	 *     IScalarParameterDefn.CHECK_BOX    = 3
	 */
	@NotNull
	@Column(name = "control_type", nullable = false)
	private Integer controlType;

	/*
	* The default value for the parameter, as a String. This field may be null
	* to signify that there is no default. For a parameter of type String, a
	* legal value may be the empty string "", so we should differentiate 
	* between "" and null.
	*/
	@Column(name = "default_value", nullable = true, length = 80)
	private String defaultValue;

	/*
	 * The locale-specific display name for the parameter. The locale used 
	 * is the locale in the getParameterDefinition task. Not sure if this is
	 * of interest.
	 */
	@Column(name = "display_name", nullable = true, length = 80)
	private String displayName;

	/*
	 * The locale-specific help text. The locale used is the locale in the 
	 * getParameterDefinition task.
	 */
	@Column(name = "help_text", nullable = true, length = 1024)
	private String helpText;

	/*
	 * These are the formatting instructions for the parameter value within 
	 * the parameter prompt UI. It does not influence the value passed to
	 * the report.
	 */
	@Column(name = "display_format", nullable = true, length = 132)
	private String displayFormat;

	/*
	 * Possible values for "Alignment" are:
	 * 
	 *     IScalarParameterDefn.AUTO   = 0  (default)
	 *     IScalarParameterDefn.LEFT   = 1
	 *     IScalarParameterDefn.CENTER = 2
	 *     IScalarParameterDefn.RIGHT  = 3
	 */
	@NotNull
	@Column(name = "alignment", nullable = false)
	private Integer alignment;

	/*
	 * Specifies whether the parameter is a hidden parameter.
	 */
	@NotNull
	@Column(name = "hidden", nullable = false)
	private Boolean hidden;

	/*
	 * This should be true for passwords, possibly for bank account numbers, ...
	 */
	@NotNull
	@Column(name = "value_concealed", nullable = false)
	private Boolean valueConcealed;

	/*
	 * Applies only to parameters with a selection list. Specifies whether 
	 * the user can enter a value different from values in a selection list. 
	 * Usually, a parameter with allowNewValue=true is displayed as a 
	 * combo-box, while a parameter with allowNewValue=false is displayed 
	 * as a list. This is only a UI directive. The BIRT engine does not 
	 * validate whether the value passed in is in the list.
	 */
	@NotNull
	@Column(name = "allow_new_values", nullable = false)
	private Boolean allowNewValues;

	/*
	 * Specifies whether the UI should display the selection list in a fixed
	 * order. Only applies to parameters with a selection list.
	 */
	@NotNull
	@Column(name = "display_in_fixed_order", nullable = false)
	private Boolean displayInFixedOrder;

	/*
	 * Possible values for "ParameterType" are:
	 * 
	 *     IParameterDefnBase.SCALAR_PARAMETER = 0
	 *     IParameterDefnBase.FILTER_PARAMETER = 1
	 *     IParameterDefnBase.LIST_PARAMETER = 2
	 *     IParameterDefnBase.TABLE_PARAMETER = 3
	 *     IParameterDefnBase.PARAMETER_GROUP = 4
	 *     IParameterDefnBase.CASCADING_PARAMETER_GROUP = 5
	 * 
	 * Some of these values will never appear here since these constants
	 * are also used in other contexts, such as for parameter groups.
	 */
	@NotNull
	@Column(name = "parameter_type", nullable = false)
	private Integer parameterType;

	/*
	 * The number of values that a picklist could have. Not clear what this 
	 * could be used for.
	 */
	@NotNull
	@Column(name = "auto_suggest_threshold", nullable = false)
	private Integer autoSuggestThreshold;

	/*
	 * Specifies the type of the parameter selection list.		 * 
	 *     IParameterDefn.SELECTION_LIST_NONE    = 0
	 *     IParameterDefn.SELECTION_LIST_DYNAMIC = 1
	 *     IParameterDefn.SELECTION_LIST_STATIC  = 2
	 */
	@NotNull
	@Column(name = "selection_list_type", nullable = false)
	private Integer selectionListType;

	//	/*
	//	 * Possible values for "TypeName" are:
	//	 * 
	//	 *     "scalar", ... 
	//	 */
	//	@Column(name = "type_name", nullable = true, length = 80)
	//	private String typeName;

	/*
	 * This is an expression on the data row from the dynamic list data set 
	 * that returns the value for the choice.
	 */
	@Column(name = "value_expr", nullable = true, length = 132)
	private String valueExpr;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 * 
	 * A report parameter need not be a member of a group, so this foreign key 
	 * can be null.
	 */
	@JoinColumn(name = "parameter_group_id", nullable = true,
			foreignKey = @ForeignKey(name = "fk_reportparameter_parametergroup") ,
			columnDefinition = "uuid")
	private ParameterGroup parameterGroup;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a ReportParameter will delete all of its 
	 *     SelectionListValue's.
	 */
	@OneToMany(mappedBy = "reportParameter", cascade = CascadeType.ALL)
	private List<SelectionListValue> selectionListValues;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a ReportParameter will delete all of its 
	 *     RoleParameterValue's.
	 */
	@OneToMany(mappedBy = "reportParameter", cascade = CascadeType.ALL)
	private List<RoleParameterValue> roleParameterValues;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a ReportParameter will delete all of its 
	 *     SubscriptionParameterValue's.
	 */
	@OneToMany(mappedBy = "reportParameter", cascade = CascadeType.ALL)
	private List<SubscriptionParameterValue> subscriptionParameterValues;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a ReportParameter will delete all of its 
	 *     JobParameterValue's.
	 */
	@OneToMany(mappedBy = "reportParameter", cascade = CascadeType.ALL)
	private List<JobParameterValue> jobParameterValues;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	public ReportParameter() {
	}

	public ReportParameter(ReportVersion reportVersion, Integer orderIndex,
			Integer dataType, Integer controlType, String name, String promptText,
			Boolean required, Boolean multivalued,
			String defaultValue, String displayName, String helpText, String displayFormat, Integer alignment,
			Boolean hidden, Boolean valueConcealed, Boolean allowNewValues, Boolean displayInFixedOrder,
			Integer parameterType, Integer autoSuggestThreshold, Integer selectionListType,
			//String typeName, 
			String valueExpr,
			ParameterGroup parameterGroup) {
		this(null, reportVersion, orderIndex,
				dataType, controlType, name, promptText, required, multivalued,
				defaultValue, displayName, helpText, displayFormat, alignment,
				hidden, valueConcealed, allowNewValues, displayInFixedOrder,
				parameterType, autoSuggestThreshold, selectionListType,
				//typeName, 
				valueExpr,
				parameterGroup, DateUtils.nowUtc());
	}

	public ReportParameter(
			ReportParameterResource reportParameterResource,
			ReportVersion reportVersion,
			ParameterGroup parameterGroup) {
		this(
				reportParameterResource.getReportParameterId(),
				reportVersion,
				reportParameterResource.getOrderIndex(),
				reportParameterResource.getDataType(),
				reportParameterResource.getControlType(),
				reportParameterResource.getName(),
				reportParameterResource.getPromptText(),
				reportParameterResource.getRequired(),
				reportParameterResource.getMultivalued(),
				reportParameterResource.getDefaultValue(),
				reportParameterResource.getDisplayName(),
				reportParameterResource.getHelpText(),
				reportParameterResource.getDisplayFormat(),
				reportParameterResource.getAlignment(),
				reportParameterResource.getHidden(),
				reportParameterResource.getValueConcealed(),
				reportParameterResource.getAllowNewValues(),
				reportParameterResource.getDisplayInFixedOrder(),
				reportParameterResource.getParameterType(),
				reportParameterResource.getAutoSuggestThreshold(),
				reportParameterResource.getSelectionListType(),
				//reportParameterResource.getTypeName(),
				reportParameterResource.getValueExpr(),
				parameterGroup,
				reportParameterResource.getCreatedOn()
		);
	}

	public ReportParameter(UUID reportParameterId, ReportVersion reportVersion, Integer orderIndex,
			Integer dataType, Integer controlType, String name, String promptText,
			Boolean required, Boolean multivalued,
			String defaultValue, String displayName, String helpText, String displayFormat, Integer alignment,
			Boolean hidden, Boolean valueConcealed, Boolean allowNewValues, Boolean displayInFixedOrder,
			Integer parameterType, Integer autoSuggestThreshold, Integer selectionListType,
			//String typeName, 
			String valueExpr,
			ParameterGroup parameterGroup, Date createdOn) {

		if (promptText == null || promptText.isEmpty()) {
			promptText = name + ":";// sensible default value
		}

		this.reportParameterId = reportParameterId;
		this.reportVersion = reportVersion;
		this.orderIndex = orderIndex;
		this.dataType = dataType;
		this.controlType = controlType;
		//this.name = name;
		setName(name);
		this.promptText = promptText;
		this.required = required;
		this.multivalued = multivalued;
		this.defaultValue = defaultValue;
		this.displayName = displayName;
		this.helpText = helpText;
		this.displayFormat = displayFormat;
		this.alignment = alignment;
		this.hidden = hidden;
		this.valueConcealed = valueConcealed;
		this.allowNewValues = allowNewValues;
		this.displayInFixedOrder = displayInFixedOrder;
		this.parameterType = parameterType;
		this.autoSuggestThreshold = autoSuggestThreshold;
		this.selectionListType = selectionListType;
		//		this.typeName = typeName;
		this.valueExpr = valueExpr;
		this.parameterGroup = parameterGroup;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public UUID getReportParameterId() {
		return this.reportParameterId;
	}

	public ReportVersion getReportVersion() {
		return reportVersion;
	}

	public void setReportVersion(ReportVersion reportVersion) {
		this.reportVersion = reportVersion;
	}

	public List<RoleParameterValue> getRoleParameterValues() {
		return roleParameterValues;
	}

	public void setRoleParameterValues(List<RoleParameterValue> roleParameterValuess) {
		this.roleParameterValues = roleParameterValuess;
	}

	public Integer getDataType() {
		return dataType;
	}

	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}

	public Integer getControlType() {
		return controlType;
	}

	public void setControlType(Integer controlType) {
		this.controlType = controlType;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		//	/*
		//	 * This truncates the length of the string to the length defined for
		//	 * the database column if the Java String length exceeds the database
		//	 * column length.
		//	 */
		//	try {
		//		int size = getClass().getDeclaredField("name").getAnnotation(Column.class).length();
		//		int inLength = name.length();
		//		if (inLength > size) {
		//			name = name.substring(0, size);
		//		}
		//	} catch (NoSuchFieldException ex) {
		//	}
		this.name = name;
	}

	public String getPromptText() {
		return promptText;
	}

	public void setPromptText(String promptText) {
		this.promptText = promptText;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public Boolean getMultivalued() {
		return multivalued;
	}

	public void setMultivalued(Boolean multivalued) {
		this.multivalued = multivalued;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	public String getDisplayFormat() {
		return displayFormat;
	}

	public void setDisplayFormat(String displayFormat) {
		this.displayFormat = displayFormat;
	}

	public Integer getAlignment() {
		return alignment;
	}

	public void setAlignment(Integer alignment) {
		this.alignment = alignment;
	}

	public Boolean getHidden() {
		return hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	public Boolean getValueConcealed() {
		return valueConcealed;
	}

	public void setValueConcealed(Boolean valueConcealed) {
		this.valueConcealed = valueConcealed;
	}

	public Boolean getAllowNewValues() {
		return allowNewValues;
	}

	public void setAllowNewValues(Boolean allowNewValues) {
		this.allowNewValues = allowNewValues;
	}

	public Boolean getDisplayInFixedOrder() {
		return displayInFixedOrder;
	}

	public void setDisplayInFixedOrder(Boolean displayInFixedOrder) {
		this.displayInFixedOrder = displayInFixedOrder;
	}

	public Integer getParameterType() {
		return parameterType;
	}

	public void setParameterType(Integer parameterType) {
		this.parameterType = parameterType;
	}

	public Integer getAutoSuggestThreshold() {
		return autoSuggestThreshold;
	}

	public void setAutoSuggestThreshold(Integer autoSuggestThreshold) {
		this.autoSuggestThreshold = autoSuggestThreshold;
	}

	public Integer getSelectionListType() {
		return selectionListType;
	}

	public void setSelectionListType(Integer selectionListType) {
		this.selectionListType = selectionListType;
	}

	public String getValueExpr() {
		return valueExpr;
	}

	public void setValueExpr(String valueExpr) {
		this.valueExpr = valueExpr;
	}

	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	public ParameterGroup getParameterGroup() {
		return parameterGroup;
	}

	public void setParameterGroup(ParameterGroup parameterGroup) {
		this.parameterGroup = parameterGroup;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public List<SelectionListValue> getSelectionListValues() {
		return selectionListValues;
	}

	public void setSelectionListValues(List<SelectionListValue> selectionListValues) {
		this.selectionListValues = selectionListValues;
	}

	public List<SubscriptionParameterValue> getSubscriptionParameterValues() {
		return subscriptionParameterValues;
	}

	public void setSubscriptionParameterValues(List<SubscriptionParameterValue> subscriptionParameterValues) {
		this.subscriptionParameterValues = subscriptionParameterValues;
	}

	public List<JobParameterValue> getJobParameterValues() {
		return jobParameterValues;
	}

	public void setJobParameterValues(List<JobParameterValue> jobParameterValues) {
		this.jobParameterValues = jobParameterValues;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReportParameter [reportParameterId=");
		builder.append(reportParameterId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", reportVersion=");
		builder.append(reportVersion);
		builder.append(", dataType=");
		builder.append(dataType);
		builder.append(", controlType=");
		builder.append(controlType);
		builder.append(", parameterGroup=");
		builder.append(parameterGroup);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}

}