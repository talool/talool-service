package com.talool.hibernate.dialect;

import java.sql.Types;

import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;
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
		registerFunction("ex_hstore", new StandardSQLFunction("exists", StandardBasicTypes.STRING));
		// registerFunction("ex_exists", new SQLFunctionTemplate("exists",
		// StandardBasicTypes.BOOLEAN));

		registerFunction("hs_key_exist", new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, "exist(?1, ?2)"));
		registerFunction("hs_value", new SQLFunctionTemplate(StandardBasicTypes.STRING, "?1 -> ?2"));

	}
}
