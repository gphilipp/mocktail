package org.mocktail;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;

public abstract class AbstractDbUnitTest {

<<<<<<< HEAD
	public void init() throws Exception {
		System.out.println("Cleaning & inserting data");
		DatabaseOperation.CLEAN_INSERT.execute(getConnection(), getDataSet());
	}

	@After
	public void after() throws Exception {
		DatabaseOperation.DELETE_ALL.execute(getConnection(), getDataSet());
	}

	private IDataSet getDataSet() throws Exception {
		File file = new File(getDataSetFile());
		return new FlatXmlDataSet(file);
	}

	private IDatabaseConnection getConnection()
			throws SQLException {
		Connection con = getSqlConnection();
		IDatabaseConnection dbUnitCon = new DatabaseConnection(con);
		return dbUnitCon;
	}

	protected int getRowCountsForQuery(String rowCountQuery) throws SQLException {
		ResultSet resultSet = getSqlConnection().createStatement().executeQuery(rowCountQuery);
		resultSet.next();
		int recordCounts = resultSet.getInt(1);
		resultSet.close();
		return recordCounts;
	}

	protected int getTableTotalRows(String tableName) throws SQLException {
		return getRowCountsForQuery("select count(*) from " + tableName);
	}

	protected String getStringValueForQuery(String queryForStringValue) throws SQLException {
		ResultSet resultSet = getSqlConnection().createStatement().executeQuery(queryForStringValue);
		resultSet.next();
		String stringValue = resultSet.getString(1);
		resultSet.close();
		return stringValue;
	}

	protected abstract Connection getSqlConnection() throws SQLException;

	protected abstract String getDataSetFile();
=======
    public void init() throws Exception {
        System.out.println("Cleaning & inserting data");
        DatabaseOperation.CLEAN_INSERT.execute(getConnection(), getDataSet());
    }

    @After
    public void after() throws Exception {
        DatabaseOperation.DELETE_ALL.execute(getConnection(), getDataSet());
    }

    private IDataSet getDataSet() throws Exception {
        File file = new File(getDataSetFile());
        return new FlatXmlDataSetBuilder().build(file);
    }

    private IDatabaseConnection getConnection()
            throws CannotGetJdbcConnectionException, SQLException, DatabaseUnitException {
        Connection con = getSqlConnection();
        IDatabaseConnection dbUnitCon = new DatabaseConnection(con);
        return dbUnitCon;
    }

    protected int getRowCountsForQuery(String rowCountQuery)
            throws SQLException {
        ResultSet resultSet = getSqlConnection().createStatement()
                .executeQuery(rowCountQuery);
        resultSet.next();
        int recordCounts = resultSet.getInt(1);
        resultSet.close();
        return recordCounts;
    }

    protected int getTableTotalRows(String tableName) throws SQLException {
        return getRowCountsForQuery("select count(*) from " + tableName);
    }

    protected String getStringValueForQuery(String queryForStringValue)
            throws SQLException {
        ResultSet resultSet = getSqlConnection().createStatement()
                .executeQuery(queryForStringValue);
        resultSet.next();
        String stringValue = resultSet.getString(1);
        resultSet.close();
        return stringValue;
    }

    protected abstract Connection getSqlConnection() throws SQLException;

    protected abstract String getDataSetFile();
>>>>>>> dc038b3d24a102aa117552e10a66b0a3a77b5284
}
