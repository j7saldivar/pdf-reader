/**
 * @author jorge.saldivar
 *
 */

package com.saldivar.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.saldivar.beans.Person;

@Component("personDao")
public class PersonDao {

	private NamedParameterJdbcTemplate jdbc;

	@Autowired
	public void setDataSource(DataSource jdbc) {
		this.jdbc = new NamedParameterJdbcTemplate(jdbc);
	}

	public List<Person> search(List<String> id) {

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("id", id);

		return jdbc
				.query("select first_name, last_name from person where id in (:id)", parameters, new RowMapper<Person>() {

					@Override
					public Person mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return mapPersonBean(rs);
					}

				});

	}

	protected Person mapPersonBean(ResultSet rs) throws SQLException {

		Person person = new Person();
		person.setId(rs.getInt("id"));
		person.setFirstName(rs.getString("first_name"));
		person.setLastName(rs.getString("last_name"));
		return person;

	}

}