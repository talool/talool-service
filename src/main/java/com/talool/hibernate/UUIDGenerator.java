package com.talool.hibernate;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * 
 * @author clintz
 * 
 */
public class UUIDGenerator implements IdentifierGenerator
{
	private static Logger log = Logger.getLogger(UUIDGenerator.class);

	public Serializable generate(SessionImplementor session, Object object) throws HibernateException
	{
		final Connection connection = session.connection();
		try
		{
			final PreparedStatement ps = connection.prepareStatement("SELECT uuid_generate_v4() as uuid");

			ResultSet rs = ps.executeQuery();
			if (rs.next())
			{
				final String uuid = rs.getString("uuid");
				log.debug("Generated UUID: " + uuid);
				return uuid;
			}

		}
		catch (SQLException e)
		{
			log.error(e);
			throw new HibernateException("Unable to generate UUID");
		}
		return null;
	}
}