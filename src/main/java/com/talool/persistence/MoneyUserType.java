package com.talool.persistence;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import com.talool.core.Money;

/**
 * 
 * @author clintz
 * 
 */
public class MoneyUserType implements UserType
{
	private static final int[] sqlTypes = new int[] { Types.NUMERIC };

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException
	{
		return cached;
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException
	{
		return value;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException
	{
		return (Serializable) value;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException
	{
		if (x == y)
			return true;
		if (x == null && y != null)
			return false;
		if (y == null && x != null)
			return false;

		return x.equals(y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException
	{
		return x.hashCode();
	}

	@Override
	public boolean isMutable()
	{
		return false;
	}

	// @Override
	// public Object nullSafeGet(ResultSet rs, String[] names, Object arg2) throws
	// HibernateException,
	// SQLException
	// {
	// final Float valueInUSD = (Float) rs.getFloat(names[0]);
	// if (rs.wasNull())
	// return null;
	// // Here is where we would convert currency !
	// // Currency userCurrency=User.getPreferences().getCurrency();
	// final BigDecimal value = new BigDecimal(valueInUSD.toString());
	// final Money amount = new Money(value.setScale(2, BigDecimal.ROUND_HALF_UP),
	// Currency.getInstance("USD"));
	// return amount;
	// }

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException
	{
		if (value == null)
		{
			st.setNull(index, Hibernate.FLOAT.sqlType());
		}
		else
		{
			final Money amount = (Money) value;
			// Here is where you would convert the amount from any currency to USD
			st.setFloat(index, amount.getValue().floatValue());
		}

	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException
	{
		return original;
	}

	@Override
	public Class returnedClass()
	{
		return Money.class;
	}

	@Override
	public int[] sqlTypes()
	{
		return sqlTypes;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
