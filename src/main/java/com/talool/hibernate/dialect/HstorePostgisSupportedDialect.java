package com.talool.hibernate.dialect;

import java.sql.Types;

import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernatespatial.postgis.PostgisDialect;

/**
 * 
 * @author clintz
 * 
 */
public class HstorePostgisSupportedDialect extends PostgisDialect
{
	private static final long serialVersionUID = -3539114552844007208L;

	public HstorePostgisSupportedDialect()
	{
		super();
		register();
	}

	protected void register()
	{
		registerColumnType(Types.OTHER, "hstore");
		registerFunction("ex_hstore", new StandardSQLFunction("hstore"));

	}

}
