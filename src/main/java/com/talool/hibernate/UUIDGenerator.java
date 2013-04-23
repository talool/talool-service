package com.talool.hibernate;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

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
	private static Logger LOG = Logger.getLogger(UUIDGenerator.class);
	private static final String SELECT_UUID = "SELECT uuid_generate_v4() as uuid";
	private static final String UUID_PARAM_NAME = "uuid";

	public Serializable generate(final SessionImplementor session, final Object object)
			throws HibernateException
	{
		return generateDBUUID(session, object);
	}

	private static Serializable generateDBUUID(SessionImplementor session, Object object)
	{
		final Connection connection = session.connection();
		try
		{
			final PreparedStatement ps = connection.prepareStatement(SELECT_UUID);

			ResultSet rs = ps.executeQuery();
			if (rs.next())
			{
				final UUID uuid = UUID.fromString(rs.getString(UUID_PARAM_NAME));
				return uuid;
			}

		}
		catch (SQLException e)
		{
			LOG.error(e);
			throw new HibernateException("Unable to generate UUID");
		}
		return null;
	}
}