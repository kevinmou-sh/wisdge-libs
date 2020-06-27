package com.wisdge.dataservice.rowset;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * 数据集合的行单元属性信息
 * 
 * @author KevinMOU
 *
 */
@XmlType(name = "RowMeta")
@XmlAccessorType(XmlAccessType.FIELD)
public class RowMeta implements Serializable {
	private static final long serialVersionUID = 1981135464258064555L;
	private int column;
	private String fieldName;
	private String typeName;
	private String className;
	private int percision;
	private int scale;
	private boolean nullable;
	private boolean primaryKey;
	private boolean foreignKey;

	public RowMeta() {
		super();
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getPercision() {
		return percision;
	}

	public void setPercision(int percision) {
		this.percision = percision;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	
	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isForeignKey() {
		return foreignKey;
	}

	public void setForeignKey(boolean foreignKey) {
		this.foreignKey = foreignKey;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nName=").append(this.fieldName).append("\n");
		sb.append("TypeName=").append(this.typeName).append("\n");
		sb.append("ClassName=").append(this.className).append("\n");
		sb.append("Precision=").append(this.percision).append("\n");
		sb.append("Scale=").append(this.scale).append("\n");
		sb.append("Nullable=").append(this.nullable).append("\n");
		return sb.toString();
	}

}