package gov.usgs.wma.nwql.spikelot.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This is a wrapper class for returning lists of domain objects. Wrapper object
 * will include metadata about the list. We need it so Jersey can detect how to
 * serialize results.
 *
 * @author kmschoep
 */
@XmlRootElement(name = "list")
@XmlType(propOrder = {"count", "list"})
public class ResultRowCollection implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The current results in this page.
	 */
	private List<ResultRow> list;

	/**
	 * Total record count for entire result set, not just the page size.
	 */
	private Integer totalCount;

	/**
	 * Current page size.
	 */
	private Integer pageSize;

	@XmlElement(name = "pageSize")
	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	@XmlElement(name = "startingRow")
	public Integer getStartingRow() {
		return startingRow;
	}

	public void setStartingRow(Integer startingRow) {
		this.startingRow = startingRow;
	}

	/**
	 * The row where the page of results starts.
	 */
	private Integer startingRow;

	//JAXB requires an empty constructor
	public ResultRowCollection() {
	}

	@XmlElementWrapper(name = "records")
	@XmlElement(name = "record")
	public List<ResultRow> getList() {
		return list;
	}

	public void setList(List<ResultRow> list) {
		this.list = list;
	}

	public void setTotalCount(Integer count) {
		this.totalCount = count;
	}

	@XmlElement(name = "count")
	public Integer getListSize() {
		if (totalCount != null) {
			return totalCount;
		}

		if (list == null) {
			return 0;
		}
		return list.size();
	}
}
