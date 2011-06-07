package pl.marchwicki.ejb.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "CALC_RESULTS_TB")
public class CalculationResult implements Serializable {

	private static final long serialVersionUID = -3706396749558191166L;

	@Id
	@Column(name = "CALC_ID", unique = true, nullable = false, precision = 10, scale = 0)
	@GeneratedValue(generator = "CALC_ID_SEQ")
	@SequenceGenerator(name = "CALC_ID_SEQ", sequenceName = "CALC_ID_SEQ")
	private long id;

	@Column(name = "OPERATION")
	private String operation;

	@Column(name = "ELEMENTS")
	private String elements;

	@Column(name = "RESULT")
	private int result;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getElements() {
		return elements;
	}

	public void setElements(String elements) {
		this.elements = elements;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((elements == null) ? 0 : elements.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result
				+ ((operation == null) ? 0 : operation.hashCode());
		result = prime * result + this.result;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CalculationResult other = (CalculationResult) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		if (id != other.id)
			return false;
		if (operation == null) {
			if (other.operation != null)
				return false;
		} else if (!operation.equals(other.operation))
			return false;
		if (result != other.result)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CalculationResult [elements=" + elements + ", id=" + id
				+ ", operation=" + operation + ", result=" + result + "]";
	}

}
