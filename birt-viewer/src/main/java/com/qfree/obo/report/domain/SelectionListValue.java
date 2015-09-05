package com.qfree.obo.report.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.qfree.obo.report.util.DateUtils;

/**
 * The persistent class for the "selection_list_value" database table.
 * 
 * Instances/rows specify selection list values for a report parameter.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "selection_list_value", schema = "reporting")
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class SelectionListValue implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "selection_list_value_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID selectionListValueId;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@NotNull
	@JoinColumn(name = "report_parameter_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_selectionlistvalue_reportparameter") ,
			columnDefinition = "uuid")
	private ReportParameter reportParameter;

	/**
	 * Used to sort the parameters into the order that they should be presented
	 * in a UI.
	 */
	@NotNull
	@Column(name = "order_index", nullable = false)
	private Integer orderIndex;

	/**
	 * The value assigned to the report parameter.
	 * 
	 * Since this value is stored as a String, it must be cast to the 
	 * appropriate data type before being assigned to the parameter
	 * 
	 * This value may be blank ("").
	 */
	@NotNull
	@Column(name = "value_assigned", nullable = false, length = 132)
	private String valueAssigned;

	/**
	 * The value displayed for the selection list item.
	 * 
	 * This value will be displayed in the selection list. If it is chosen by
	 * the reporting user, the value "value_assigned" will be assigned to the 
	 * report parameter (after it is cast to the appropriate data type for the
	 * parameter).
	 * 
	 * This value may be blank ("").
	 */
	@NotNull
	@Column(name = "value_displayed", nullable = false, length = 132)
	private String valueDisplayed;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private SelectionListValue() {
	}

	public SelectionListValue(ReportParameter reportParameter, Integer orderIndex,
			String valueAssigned, String valueDisplayed) {
		this(reportParameter, orderIndex, valueAssigned, valueDisplayed, DateUtils.nowUtc());
	}

	public SelectionListValue(ReportParameter reportParameter, Integer orderIndex,
			String valueAssigned, String valueDisplayed, Date createdOn) {
		this.reportParameter = reportParameter;
		this.orderIndex = orderIndex;
		this.valueAssigned = valueAssigned;
		this.valueDisplayed = valueDisplayed;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public UUID getSelectionListValueId() {
		return selectionListValueId;
	}

	public ReportParameter getReportParameter() {
		return reportParameter;
	}

	public void setReportParameter(ReportParameter reportParameter) {
		this.reportParameter = reportParameter;
	}

	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	public String getValueAssigned() {
		return valueAssigned;
	}

	public void setValueAssigned(String valueAssigned) {
		this.valueAssigned = valueAssigned;
	}

	public String getValueDisplayed() {
		return valueDisplayed;
	}

	public void setValueDisplayed(String valueDisplayed) {
		this.valueDisplayed = valueDisplayed;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SelectionListValue [selectionListValueId=");
		builder.append(selectionListValueId);
		builder.append(", reportParameter=");
		builder.append(reportParameter);
		builder.append(", orderIndex=");
		builder.append(orderIndex);
		builder.append(", valueAssigned=");
		builder.append(valueAssigned);
		builder.append(", valueDisplayed=");
		builder.append(valueDisplayed);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}

}
