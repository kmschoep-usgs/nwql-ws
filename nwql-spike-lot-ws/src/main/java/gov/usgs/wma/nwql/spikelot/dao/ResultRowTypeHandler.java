package gov.usgs.wma.nwql.spikelot.dao;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import gov.usgs.wma.nwql.spikelot.model.ResultRow;

/**
 * Used by mybatis, simple row to ResultRow conversion (this is just a LinkMap
 * of column-value pairs)
 *
 * @author kmschoep
 */
public class ResultRowTypeHandler implements TypeHandler<ResultRow> {

	/**
	 * {@inheritDoc}
	 *
	 * @see org.apache.ibatis.type.TypeHandler
	 * #setParameter(java.sql.PreparedStatement, int, java.lang.Object,
	 * org.apache.ibatis.type.JdbcType)
	 */
	@Override
	public void setParameter(final PreparedStatement ps, final int i,
			final ResultRow parameter, final JdbcType jdbcType) throws SQLException {
		// Do nothing
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.apache.ibatis.type.TypeHandler#getResult(java.sql.ResultSet,
	 * java.lang.String)
	 */
	@Override
	public ResultRow getResult(final ResultSet rs, final String columnName) throws SQLException {
		ResultRow row = new ResultRow();
		for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
			Object object = rs.getObject(i);

			if (rs.getObject(i) == null) {
				object = "";
			}

			row.put(rs.getMetaData().getColumnName(i), object);
		}
		return row;
	} // getResult

	/**
	 * {@inheritDoc}
	 *
	 * @see
	 * org.apache.ibatis.type.TypeHandler#getResult(java.sql.CallableStatement,
	 * int)
	 */
	@Override
	public ResultRow getResult(final CallableStatement cs, final int columnIndex)
			throws SQLException {
		return null;
	}

	/**
	 * For every row, cycle through the columns and create a ResultRow with all
	 * column names filled with values.
	 *
	 * @return ResultRow will call columns populated with values.
	 */
	@Override
	public ResultRow getResult(ResultSet rs, int columnIndex) throws SQLException {

		ResultRow row = new ResultRow();
		for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
			row.put(rs.getMetaData().getColumnName(i),
					rs.getObject(i) == null ? "" : rs.getObject(i));
		}

		return row;
	}

}
