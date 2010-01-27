package org.ensembl.healthcheck.util;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * An lightweight, generic analogue of Spring's JdbcTemplate code (docs are available from <a
 * href="http://www.springframework.org/docs/api/org/springframework/jdbc/core/JdbcTemplate.html">
 * Spring's Javadoc site</a>. 
 *
 * <p>
 * The object attempts to use as many new features from Java5. This means that
 * some methods will use generics, others will accept the vargs construct. Not
 * all methods will accept them because of the problems with auto-boxing &
 * continuation of arrays. Javadoc will indicate when vargs is being using
 *
 * <p>
 * The methods in this class attempt to provide easy access to retrieve single/
 * multiple objects via the RowMapper pattern. When you wish to perform more
 * complex mappings then please use
 * {@link #queryForList(String, RowMapper, Object[])} or
 * {@link #queryForObject(String, RowMapper, Object[])}.
 *
 * <p>
 * Most of the time you probably will be using the
 * {@link #queryForDefaultObject(String, Class, Object[])} and
 * {@link #queryForDefaultObjectList(String, Class, Object[])} methods. Both of
 * these have example usage in their Javadoc. The methods use the
 * {@link DefaultObjectRowMapper} object to map the result of column 1 for any
 * query given to it. Please consult {@link DefaultObjectRowMapper} for the
 * available Objects to map.
 *
 * <p>
 * There is also the {@link #queryForMap(String, MapRowMapper, Object[])} which
 * can be used to translate a result set into a Map of results. This is very
 * useful for grouping outer joins by a single key for easy lookup. This is used
 * in conjunction with {@link MapRowMapper} which defines callbacks for the
 * lifecycle of the {@link #queryForMap(String, MapRowMapper, Object[])} method.
 *
 * <p>
 * This class should be used in conjunction with {@link DbUtils} and the default
 * implementation {@link ConnectionBasedSqlTemplateImpl}.
 *
 * @author ayates
 * @author dstaines (adapted for pure JDBC use)
 */
public interface SqlTemplate {

	/**
	 * The core method which takes the output of a ResultSet and will output a
	 * List of objects. This method provides a very useful manner to parse the
	 * outputs of result sets however it is recommended that you use a more
	 * custom method for the procedure.
	 *
	 * @param <T>
	 *            The required output type
	 * @param resultSet
	 *            The input result set
	 * @param mapper
	 *            The mapper object to use to map from result set to object
	 * @param rowLimit
	 *            Indicates that there is an expected row limit that when
	 *            exceeded we want a runtime exception raised. If set to -1 or 0
	 *            this is ignored. If set then exceeding the row limit or a
	 *            return count of 0 will cause an exception to be raised
	 * @param sql
	 *            The SQL used to execute this statement. Used for error
	 *            reporting
	 * @param args
	 *            The args used to execute this statement. Used for error
	 *            reporting
	 * @return A list of objects which were created by the mapper
	 * @throws SqlServiceUncheckedException
	 *             Thrown if SQLExceptions were raised during the mapping
	 *             process or row limit was exceeded
	 */
	<T> List<T> mapResultSetToList(ResultSet resultSet, RowMapper<T> mapper,
			final int rowLimit, String sql, Object[] args);

	/**
	 * Wrapper version for
	 * {@link #mapResultSetToList(ResultSet, RowMapper, int, String, Object[])}
	 * which returns a single object
	 */
	<T> T mapResultSetToSingleObject(ResultSet resultSet,
			RowMapper<T> mapper, String sql, Object[] args);

	/**
	 * Used to call both the {@link #executeSql(String, Object[])} and then call
	 * out to
	 * {@link #mapResultSetToSingleObject(ResultSet, RowMapper, String, Object[])}.
	 * This also means that this method will deal with resource handling
	 * correctly.
	 *
	 * @param <T>
	 *            The input type param
	 * @param sql
	 *            The SQL to run
	 * @param mapper
	 *            The mapper to use
	 * @param args
	 *            The arguments for the SQL
	 * @return A single object returned from the given SQL query
	 */
	<T> T queryForObject(String sql, RowMapper<T> mapper, Object... args);

	/**
	 * Runs {@link #executeSql(String, Object[])} and then call out to
	 * {@link #mapResultSetToList(ResultSet, RowMapper, int, String, Object[])}
	 * for processing into a list.
	 *
	 * @param <T>
	 *            The expected return type
	 * @param sql
	 *            The SQL to execute
	 * @param mapper
	 *            The mapper to use
	 * @param args
	 *            Arguments to use in the SQL
	 * @return The list of specified objects
	 */
	<T> List<T> queryForList(String sql, RowMapper<T> mapper, Object... args);

	/**
	 * See {@link DefaultObjectRowMapper} for more information about supported
	 * mappings. Will map column 1 from a result set into a given object. This
	 * method assumes that you always expect one result back from the database
	 * and to recieve more or less than this is an erronious situation. Example
	 * usage:
	 *
	 * <code>
	 * SqlServiceTemplate template = getTemplate(); //Resolved from somewhere
	 * int count = template.queryForDefaultObject("select count(*) from person", Integer.class);
	 * </code>
	 *
	 * In the above example we have queried for a count which we know must exist
	 * and will only return one value. We tell the method that we are going to
	 * be querying for an Integer object and that this will be autoboxed to an
	 * int. Since the method relies heavily on Generics this will deal with the
	 * problems of casting & conversion of result to specified data type.
	 *
	 * @throws SqlServiceUncheckedException
	 *             Thrown if the given query brings back less than or more than
	 *             1 result.
	 */
	<T> T queryForDefaultObject(String sql, Class<T> expected, Object... args);

	/**
	 * See {@link DefaultObjectRowMapper} for more information about supported
	 * mappings. Will map column 1 from a result set into a given object.
	 * Example usage:
	 *
	 * <code>
	 * SqlServiceTemplate template = getTemplate(); //Resolved from somewhere
	 * List&lt;Date&gt; = template.queryForDefaultObjectList("select dates from date_table", Date.class);
	 * </code>
	 *
	 * In the above example we are querying for all dates from a specified
	 * table. The list will never be null but maybe empty if no results were
	 * found.
	 */
	<T> List<T> queryForDefaultObjectList(String sql, Class<T> expected,
			Object... args);

	/**
	 * Provides very similar functionality to
	 * {@link #queryForList(String, uk.ac.ebi.proteome.util.sql.RowMapper, Object[])}
	 * however this assumes that there is a difference in the expected mapping
	 * from results to domain object. You use {@link MapRowMapper} objects to
	 * help this method to decode from the ResultSet into a Map. The procedure
	 * run is
	 *
	 * <ol>
	 * <li>Get the map to populate from {@link MapRowMapper#getMap()}</li>
	 * <li>Run the query with the given arguments</li>
	 * <li>Iterate through the results set</li>
	 * <li>Call {@link MapRowMapper#getKey(ResultSet)} with the results</li>
	 * <li>Query against the map to see if the key has already been seen or not</li>
	 * <ol>
	 * <li>If it has not then call {@link RowMapper#mapRow(ResultSet, int)}</li>
	 * <li>If it has then call
	 * {@link MapRowMapper#existingObject(Object, ResultSet, int)} and pass
	 * back the Object associcated with the key</li>
	 * </ol>
	 * <li>Repeat until the result set is finished</li>
	 * <li>Return the generated map</li>
	 * </ol>
	 *
	 * Because you are given such control over what happens when this method
	 * runs the generated map can be anything, you can throw exceptions if you
	 * encounter more than one instance of the key or just add it to a Java
	 * collection.
	 *
	 * @param <K>
	 *            The target key type
	 * @param <T>
	 *            The target value type
	 * @param sql
	 *            The SQL to run to generate this
	 * @param mapRowMapper
	 *            The instance of the row mapper
	 * @param args
	 *            Arguments to send to the target server
	 * @return A map which should be of the given above type
	 * @throws SqlServiceUncheckedException
	 *             Thrown in the event of problems with the mappings
	 */
	<K, T> Map<K, T> queryForMap(String sql, MapRowMapper<K, T> mapRowMapper,
			Object... args);

}